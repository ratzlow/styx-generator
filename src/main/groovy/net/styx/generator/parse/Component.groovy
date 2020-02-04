package net.styx.generator.parse


class Component extends Symbol {

    Component(Node node, Map symbolTable, Closure<Map<String, String>> overrideAttrs) {
        super(node, symbolTable, overrideAttrs(node))
        parse(node.children(), symbolTable, overrideAttrs)
    }

    def parse(Collection<Node> children, Map symbolTable, Closure<Map<String, String>> overrideAttrs) {
        for (Node child : children) {
            def elemName = child.name()
            assert ["field", "component", "group"].contains(elemName)
            if (elemName == "group") {
                new Group(child, symbolTable, overrideAttrs(child))
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
