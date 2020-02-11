package net.styx.generator

import groovy.util.logging.Slf4j
import net.styx.generator.parse.Component

import net.styx.generator.parse.Field
import net.styx.generator.parse.Message
import net.styx.generator.parse.MetaDict
import net.styx.generator.parse.Symbol
import net.styx.generator.render.ClassGen
import net.styx.generator.render.EnumGen
import net.styx.generator.render.Generator

import java.nio.charset.StandardCharsets

@Slf4j
class CodeGen {

    static void main(String[] args) {
        CodeGen gen = new CodeGen()
        gen.generateSources()
    }

    //---------------------------------------------------------------------------
    // public API
    //---------------------------------------------------------------------------

    def packageName = "net.styx.model.v1"

    def generateTarget = new File("./target/generated-sources/java")

    /**
     * Latter override former dictionaries. Order matters!
     */
    def dictNames = ["/FIX50.xml", "/DomainModel-FIX50.xml"]

    /**
     * List of meta dictionaries decorating #dictNames
     */
    def metaDictionaries = ["/DomainModel-meta.xml"]

    /**
     * List of classes to generate along with all the constituents.
     */
    def aggregateRoots = ["Order", "Fill"]

    /**
     * @return path in file system where classes were written to.
     */
    def fullPath() { "$generateTarget/" + packageName.toString().replace(".", "/") }

    /**
     * Start generation process as instructed by given properties.
     *
     * @return names of classes that were generated
     */
    Set<String> generateSources() {

        def parser = new DictionaryParser()
        Map<String, Symbol> symbolTable = parser.parseDictionaries(dictNames, metaDictionaries)

        log.info("Down select unique set of symbols to build object graphs starting at $aggregateRoots")
        Collection<Symbol> rootSymbols = symbolTable.findAll {aggregateRoots.contains(it.key)} collect { it.value }
        assert aggregateRoots.size() == rootSymbols.size() : "Could not find all requested rootEntities!"

        Map<String, Generator> generators = [:]
        distinct(rootSymbols, generators)

        log.info("Start writing files to $generateTarget ...")
        log.info("---------------------------------------------------------------------")
        cleanDir()
        generators.collect {it.value}.each {writeFile(it.generate(), it.fileName())}
        log.info("---------------------------------------------------------------------")
        log.info("Finished code generation! Wrote ${generators.size()} files to ${fullPath()}")

        generators.keySet()
    }


    //---------------------------------------------------------------------------
    // internal implementation
    //---------------------------------------------------------------------------

    private void distinct(Collection<Symbol> symbols, Map<String, Generator> generators) {
        assert symbols != null

        for (Symbol symbol : symbols) {
            if ( !generators.containsKey(symbol.name) ) {
                def isEnum = symbol.type == "field" && !((Field) symbol).literals.isEmpty()
                def isClass = symbol.type != "field"
                def longName = symbol.longName()
                if (isEnum) {
                    generators.put(symbol.name, new EnumGen(packageName, longName, symbol as Field))
                } else if (isClass) {
                    generators.put(symbol.name, new ClassGen(packageName, longName, symbol.getAttributes()))
                }

                log.debug("Adding symbol $longName")

                assert symbol.attributes.every {it != null} : symbol

                // enter into recursion
                distinct(symbol.getAttributes() as Collection<Symbol>, generators)
            }
        }
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
}

@Slf4j
class DictionaryParser {


    Map<String, Symbol> parseDictionaries(List<String> dictionaryNames, List<String> metaDictNames) {

        // TODO: add support for parsing multiple metaDicts
        MetaDict metaDict = new MetaDict(metaDictNames.get(0))

        Map<String, Symbol> symbolTable = [:]

        for (String dictName : dictionaryNames) {
            log.info("Parsing dictionary $dictName ...")
            InputStream is = DictionaryParser.class.getResourceAsStream(dictName)
            assert is != null : "No dict found with $dictName"

            def fix = new XmlParser().parse(is as InputStream)
            def overrideAttrs = { Node node -> metaDict.getMeta(node.attribute("name") as String)}

            fix.fields.field
                    .collect { Node node -> new Field(node, symbolTable, overrideAttrs(node)) }

            fix.components.component
                    .collect { Node node -> new Component(node, symbolTable, overrideAttrs) }

            fix.messages.message
                    .collect { Node node -> new Message(node, symbolTable, overrideAttrs(node)) }
        }

        symbolTable
    }
}