package net.styx.generator.parse

class Group extends Symbol {
    Group(Node node, Map symbolTable) {
        super(node, symbolTable)
    }
}

class Message extends Symbol {
    def msgtype, msgcat

    Message(Node node, Map symbolTable) {
        super(node, symbolTable)
        this.msgtype = node.attribute("msgtype")
        this.msgcat = node.attribute("msgcat")
    }
}