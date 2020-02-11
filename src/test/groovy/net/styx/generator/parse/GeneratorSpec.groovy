package net.styx.generator.parse

import net.styx.generator.CodeGen
import net.styx.generator.DictionaryParser
import spock.lang.Specification

class GeneratorSpec extends Specification {

    def "parse dictionaries"() {
        given:
        def parser = new DictionaryParser()
        Map<String, Symbol> symbolTable = parser.parseDictionaries(
                ["/FIX50.xml", "/DomainModel-FIX50.xml"], ["/DomainModel-meta.xml"]
        )

        expect:
        assert symbolTable.get("GTBookingInst").longName() == "GoodTillBookingInstruction"
    }

    def "generate code"() {
        given:
        CodeGen gen = new CodeGen()
        Set<String> generatedClasses = gen.generateSources()
        Set<String> classesWrittenToDir = new File(gen.fullPath()).list().collect {it.toString().split("\\.")[0]}.toSet()

        expect:
        assert !generatedClasses.isEmpty()
        assert generatedClasses.size() == classesWrittenToDir.size()
    }


    def "parse meta FIX dictionary"() {
        given:
        MetaDict parser = new MetaDict("/DomainModel-meta.xml")

        expect:
        assert [ref               : "Parties", longName: "CounterParties",
                containerInterface: "Set", containerImplementation: "HashSet"] == parser.getMeta("Parties")

        assert [ref: "ExecInst", longName: "ExecutionInstruction"] == parser.getMeta("ExecInst")
    }
}
