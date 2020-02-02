package net.styx.generator.parse


abstract class Symbol {
    String name, attributeName
    def type, javaType
    def symbolTable = [:]
    def attributes = []

    Symbol(Node node, symbolTable) {
        this.type = node.name()
        this.name = node.attribute("name")
        this.attributeName = name.substring(0,1).toLowerCase() + name.substring(1)
        this.javaType = name
        this.symbolTable = symbolTable
        this.attributes = node.children().collect { Node child -> child.attribute("name") }.findAll {it != null}
        this.symbolTable.put(name, this)
    }

    def getAttributes() {
        attributes.collect { String attributeName -> symbolTable.get(attributeName) }
    }

    boolean isCollectionItem() {
        false
    }

    @Override
    String toString() {
        return "${name}{type=$type attributes=${getAttributes()}}"
    }
}
