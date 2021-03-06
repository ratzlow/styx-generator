package net.styx.generator

import groovy.util.logging.Slf4j
import net.styx.generator.parse.Component
import net.styx.generator.parse.Field
import net.styx.generator.parse.Message
import net.styx.generator.parse.Symbol
import net.styx.generator.render.ClassGen
import net.styx.generator.render.EnumGen
import net.styx.generator.render.Generator

import java.nio.charset.StandardCharsets

@Slf4j
class CodeGen {

    static void main(String[] args) {
        log.info("Start code generation ...")
        CodeGen gen = new CodeGen()
        gen.start()
        log.info("Finished generation. Look at ${gen.generateTarget}")
    }

    //---------------------------------------------------------------------------
    // public API
    //---------------------------------------------------------------------------

    def packageName = "net.styx.model.v1"

    def generateTarget = new File("./target/generated-sources/java")

    def dictNames = ["/FIX50.xml", "/DomainModel-FIX50.xml"]

    def start() {
        def symbolTable = parseDictionaries(dictNames)

        log.info("Start writing files to $generateTarget ...")
        cleanDir()
        Symbol s_1 = symbolTable.get("Order") as Symbol
        Map<String, Generator> generators = [:]
        flatten(s_1, generators)

        log.info("---------------------------------------------------------------------")
        generators.collect {it.value}.each {writeFile(it.generate(), it.fileName())}
        log.info("---------------------------------------------------------------------")
        log.info("Finished code generation!")

        generators.size()
    }


    //---------------------------------------------------------------------------
    // internal implementation
    //---------------------------------------------------------------------------

    private Map parseDictionaries(List<String> dictionaryNames) {
        def symbolTable = [:]

        for (String dictName : dictionaryNames) {
            log.info("Parsing dictionary $dictName ...")
            InputStream is = CodeGen.class.getResourceAsStream(dictName)
            assert is != null : "No dict found with $dictName"

            def fix = new XmlParser().parse(is as InputStream)

            fix.fields.field
                    .collect { Node node -> new Field(node, symbolTable) }

            fix.components.component
                    .collect { Node node -> new Component(node, symbolTable) }

            fix.messages.message
                    .collect { Node node -> new Message(node, symbolTable) }
        }

        symbolTable
    }

    private void flatten(Symbol symbol, Map<String, Generator> generators) {
        assert symbol != null

        if (generators.containsKey(symbol.name)) return

        def isEnum = symbol.type == "field" && !((Field) symbol).literals.isEmpty()
        def isClass = symbol.type != "field"
        if (isEnum) {
            generators.put(symbol.name, new EnumGen(packageName, symbol.name, symbol as Field))
        } else if (isClass) {
            generators.put(symbol.name, new ClassGen(packageName, symbol.name, symbol.getAttributes()))
        }

        log.debug("Adding symbol $symbol.name")

        assert symbol.attributes.every {it != null} : symbol
        symbol.attributes.forEach{ Symbol sym -> flatten(sym, generators) }
    }

    private void cleanDir() {
        log.info("Cleanup target generation dir")
        if (generateTarget.exists()) {
            generateTarget.deleteDir()
        }

        File dir = new File(fullPath())
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }

    private void writeFile(String generatedCode, String fileName) {
        def fqFileName = "${fullPath()}/${fileName}.java"
        log.info("Write to File: $fqFileName")
        def outFile = new File(fqFileName)
        outFile.append(generatedCode, StandardCharsets.UTF_8.toString())
        log.debug(generatedCode)
    }

    private String fullPath() { "$generateTarget/" + packageName.toString().replace(".", "/") }
}