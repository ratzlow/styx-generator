package net.styx.generator.parse


class Component extends Symbol {

    Component(Node node, Map symbolTable) {
        super(node, symbolTable)
        parse(node.children(), symbolTable)
    }

    def parse(Collection<Node> children, Map symbolTable) {
        for (Node child : children) {
            def elemName = child.name()
            assert ["field", "component", "group"].contains(elemName)
            if (elemName == "group") {
                new Group(child, symbolTable)
            }
        }
    }

    @Override
    boolean isCollectionItem() {
        super.attributes.size() == 1 && super.attributes.first().type == "group"
    }

    @Override
    def getAttributes() {
        isCollectionItem() ? super.attributes.first().attributes : super.attributes
    }
}
