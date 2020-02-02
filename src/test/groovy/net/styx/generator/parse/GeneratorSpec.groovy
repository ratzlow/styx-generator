package net.styx.generator.parse

import net.styx.generator.CodeGen
import spock.lang.Specification

class GeneratorSpec extends Specification {

    def "run code generator"() {
        given:
        CodeGen gen = new CodeGen()
        Set<String> generatedClasses = gen.generateSources()
        Set<String> classesWrittenToDir = new File(gen.fullPath()).list().collect {it.toString().split("\\.")[0]}.toSet()

        expect:
        assert !generatedClasses.isEmpty()
        assert classesWrittenToDir == generatedClasses
    }


    def "parse meta FIX dictionary"() {
        given:
        MetaDictParser parser = new MetaDictParser("/DomainModel-meta.xml")

        expect:
        assert [ref               : "Parties", longName: "CounterParties",
                containerInterface: "Set", containerImplementation: "HashSet"] == parser.getMeta("Parties")

        assert [ref: "ExecInst", longName: "ExecutionInstruction"] == parser.getMeta("ExecInst")
    }
}
