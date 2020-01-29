package net.styx.generator.render

import net.styx.generator.parse.Field
import net.styx.generator.parse.Symbol

class ClassGen implements Generator {
    def pkgName, className
    Set<String> importFqClassNames
    List<Symbol> attributes

    ClassGen(pkgName, className, List<Symbol> attributes) {
        this.pkgName = pkgName
        this.className = className
        this.attributes = attributes
        this.importFqClassNames = attributes.any{it.type == "component" && it.collectionItem} ? ["java.util.Collection"] : []
    }

    @Override
    String fileName() { className }

    @Override
    String generate() {
//------------------------------Template:class file----------------
"""package $pkgName;

${imports()}

public class $className {
    // attributes
${attributeDeclaration()}

    // accessors & mutators
${gettersAndSetters()}
}
"""
//----------------------------------------------------------------
    }


    def gettersAndSetters() {

        attributes.collect { attribute ->
//------------------------------Template:get/set methods----------
"""
    
    public ${resolveJavaType(attribute)} get$attribute.name() {
        return $attribute.attributeName;
    }
    
    public void set$attribute.name(${resolveJavaType(attribute)} $attribute.attributeName) {
        this.$attribute.attributeName = $attribute.attributeName;
    }
"""
//----------------------------------------------------------------
        }.join()
    }


    def imports() {
        importFqClassNames = attributes
                .findAll { it -> it.type == "field" }
                .findAll { Field field -> field.getFqClassName().isPresent() }
                .collect { Field field -> field.getFqClassName().get() }
                .toSet()

        if (attributes.any {it.isCollectionItem()}) {
            importFqClassNames << "java.util.Collection"
        }

        new TreeSet<>(importFqClassNames).collect{ "import $it;\n"}.join()
    }

    private String attributeDeclaration() {
        attributes
                .collect{ symbol -> resolveJavaType(symbol) + " " + symbol.attributeName }
                .collect{ "    private $it;\n"}.join()
    }

    private String resolveJavaType(Symbol attribute) {
        attribute.isCollectionItem() ? "Collection<$attribute.name>" : attribute.javaType
    }
}