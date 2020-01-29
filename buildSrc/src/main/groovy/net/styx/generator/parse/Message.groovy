package net.styx.generator.parse

class Message extends Symbol {
    def msgtype, msgcat

    Message(Node node, Map symbolTable) {
        super(node, symbolTable)
        this.msgtype = node.attribute("msgtype")
        this.msgcat = node.attribute("msgcat")
    }
}