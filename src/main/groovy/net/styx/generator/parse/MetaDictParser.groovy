package net.styx.generator.parse

import groovy.util.logging.Slf4j

@Slf4j
class MetaDictParser {

    def fixMeta

    MetaDictParser(String classPathToXml) {
        log.info("Reading meta dictionary from classpath=$classPathToXml")
        InputStream is = MetaDictParser.class.getResourceAsStream(classPathToXml)
        fixMeta = new XmlParser().parse(is as InputStream)
    }

    /**
     * Find meta data for FIX dictionary element referenced by "ref=???" attribute.
     *
     * @param refName ref attribute name to lookup corresponding attributes of element labeled with this ref=???
     * @return map of xml attributes in KV form
     */
    Map getMeta(String refName) {
        def attributes = fixMeta.'**'.find { it.@ref=="$refName" }?.attributes() ?: [:]
        log.debug("Searched for ref=$refName and found attributes=$attributes")
        attributes
    }
}
