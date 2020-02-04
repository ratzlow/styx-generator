package net.styx.generator.parse

import groovy.util.logging.Slf4j

@Slf4j
class MetaDict {

    def fixMeta

    MetaDict(String classPathToXml) {
        log.info("Reading meta dictionary from classpath=$classPathToXml")
        InputStream is = MetaDict.class.getResourceAsStream(classPathToXml)
        fixMeta = new XmlParser().parse(is as InputStream)
    }

    /**
     * Find in meta data for FIX dictionary depth first all elements with attribute 'ref' == supplied name
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
