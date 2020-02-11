package net.styx.generator.parse

import groovy.util.logging.Slf4j

@Slf4j
abstract class Symbol {
    String name
    def type
    def symbolTable = [:]
    def attributes = []
    Map<String, String> overrideAttrs


    Symbol(Node node, symbolTable, Map<String, String> overrideAttrs) {
        this.type = node.name()
        this.name = node.attribute("name")
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

    String longName() {
        overrideAttrs.containsKey("longName") ? overrideAttrs["longName"] : name
    }

    String attributeName() {
        longName().substring(0,1).toLowerCase() + longName().substring(1)
    }

    String javaType() {
        longName()
    }

    @Override
    String toString() {
        return "${name}{type=$type attributes=${getAttributes()}}"
    }
}
