package net.styx.generator.parse

class Group extends Symbol {
    Group(Node node, Map symbolTable, Map<String, String> overrideAttrs) {
        super(node, symbolTable, overrideAttrs)
    }
}

class Message extends Symbol {
    def msgtype, msgcat

    Message(Node node, Map symbolTable, Map<String, String> overrideAttrs) {
        super(node, symbolTable, overrideAttrs)
        this.msgtype = node.attribute("msgtype")
        this.msgcat = node.attribute("msgcat")
    }
}