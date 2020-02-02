package net.styx.generator.parse

import groovy.util.logging.Slf4j
import net.styx.generator.CodeGen
import spock.lang.Specification

@Slf4j
class GeneratorSpec extends Specification {

    def "Run generator"() {
        given:
        CodeGen gen = new CodeGen()
        def generatedClassCount = gen.start()

        expect:
        log.info("====================Generated $generatedClassCount=================")
        assert generatedClassCount > 0
    }


    def "parse meta FIX dictionary"() {
        given:
        MetaDictParser parser = new MetaDictParser("/DomainModel-meta.xml")

        expect:
        assert [refName: "Parties"] == parser.getMeta("Parties")
    }
}
