package net.styx.generator.parse

import groovy.util.logging.Slf4j

@Slf4j
class MetaDictParser {

    def fixMeta

    static void main(String[] args) {
        new MetaDictParser("/DomainModel-meta.xml")
    }

    MetaDictParser(String classPathToXml) {
        InputStream is = MetaDictParser.class.getResourceAsStream(classPathToXml)
        fixMeta = new XmlSlurper().parse(is)
    }

    Map<String, String> getMeta(String refName) {
        def result = fixMeta.componentsMeta.'*'.findAll { it.@refName = "$refName" }
        log.info("=========> result: " + result)
        [:]
    }
}
