package net.styx.generator.parse

import groovy.util.logging.Slf4j

@Slf4j
abstract class Symbol {
    String name, attributeName
    def type, javaType
    def symbolTable = [:]
    def attributes = []
    Map<String, String> overrideAttrs;


    Symbol(Node node, symbolTable, Map<String, String> overrideAttrs) {
        this.type = node.name()
        this.name = node.attribute("name")
        this.attributeName = name.substring(0,1).toLowerCase() + name.substring(1)
        this.javaType = name
        this.symbolTable = symbolTable
        this.attributes = node.children().collect { Node child -> child.attribute("name") }.findAll {it != null}
        this.symbolTable.put(name, this)
        this.overrideAttrs = overrideAttrs
        log.debug("Created symbol for type=$type name=$name")
    }

    def getAttributes() {
        attributes.collect { attributeName -> symbolTable.get(attributeName) }
    }

    boolean isCollectionItem() {
        false
    }

    @Override
    String toString() {
        return "${name}{type=$type attributes=${getAttributes()}}"
    }
}
