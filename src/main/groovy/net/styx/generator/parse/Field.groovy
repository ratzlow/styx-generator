package net.styx.generator.parse

/**
 * Field is a value configured in complex types. It is mapped to specific target language types.
 */
class Field extends Symbol {

    // FIX dict mapping
    static Types types = new Types(Arrays.asList(
            new TypeResolver(["INT", "LENGTH", "NUMINGROUP", "TAGNUM", "SEQNUM"], "int", "-1"),
            new TypeResolver(["DAY-OF-MONTH", "MONTH-YEAR"], "short", "-1"),
            new TypeResolver(["BOOLEAN"], "boolean", "false"),
            new TypeResolver(["STRING", "MULTIPLESTRINGVALUE", "MULTIPLECHARVALUE", "EXCHANGE"], "String", "null"),
            new TypeResolver(["CHAR"], "char", "''"),
            new TypeResolver(["FLOAT", "QTY", "PRICE", "PRICEOFFSET", "AMT", "PERCENTAGE"], "BigDecimal", "null",
                    "java.math.BigDecimal"),
            new TypeResolver(["LOCALMKTDATE"], "LocalDate","null",
                    "java.time.LocalDate"),
            new TypeResolver(["CURRENCY"], "Currency", "null","java.util.Currency"),
            new TypeResolver(["COUNTRY"], "Locale", "null", "java.util.Locale"),
            new TypeResolver(["DATA"], "char[]", "new char[0]"),
            // how to imply UTC in type?
            new TypeResolver(["UTCTIMESTAMP", "TZTIMESTAMP"], "LocalDateTime", "null",
                    "java.time.LocalDateTime"),
            new TypeResolver(["UTCTIMEONLY", "TZTIMEONLY"], "LocalTime","null",
                    "java.time.LocalTime"),
            new TypeResolver(["UTCDATEONLY"], "LocalDate", "null",
                    "java.time.LocalDate")
    ))

    int number
    Collection<Literal> literals
    TypeResolver typeResolver

    Field(Node node, Map symbolTable, Map<String, String> overrideAttrs) {
        super(node, symbolTable, overrideAttrs)
        number = Integer.parseInt(node.attribute("number").toString())
        literals = node.children().collect { Node child -> new Literal(child) }
        String declaredType = node.attribute("type")
        typeResolver = types.find(declaredType)
        assert attributes.every {it != null} : name
    }

    Optional<String> getFqClassName() { typeResolver.fqTargetClassName }

    @Override
    String toString() {
        "${name}{type=$type" + (!literals.isEmpty() ? " literals=${literals}}" : "}")
    }

    @Override
    String javaType() {
        !literals.empty ? longName() : typeResolver.targetType
    }

    //------------------------------------------------------------------------------------------------------------------
    // inner classes to associate types with Fields
    //------------------------------------------------------------------------------------------------------------------

    private static class TypeResolver {
        Set<String> sourceTypes
        String targetType
        Optional<String> fqTargetClassName
        String unsetValue

        TypeResolver(def sourceTypes, String targetType, String unsetValue, String fqTargetClassName) {
            this(sourceTypes, targetType, unsetValue, Optional.of(fqTargetClassName))
        }

        TypeResolver(def sourceTypes, String targetType, String unsetValue) {
            this(sourceTypes, targetType, unsetValue, Optional.empty())
        }

        private TypeResolver(def sourceTypes, String targetType, String unsetValue, Optional<String> fqTargetClassName) {
            this.sourceTypes = new HashSet<>(sourceTypes)
            this.targetType = targetType
            this.unsetValue = unsetValue
            this.fqTargetClassName = fqTargetClassName
        }
    }

    private static class Types {
        Set<TypeResolver> typeMappings

        Types(Collection<TypeResolver> typeMappings) {
            this.typeMappings = new HashSet<>(typeMappings)
        }

        def find(String sourceType) {
            def resolver = typeMappings.find { resolver -> resolver.sourceTypes.contains(sourceType) }
            assert resolver != null : "sourceType = $sourceType"
            resolver
        }
    }

    /**
     * Some fields are enums having list of literals
     */
    private static class Literal {
        def code, description

        Literal(Node node) {
            code = node.attribute("enum")
            description = node.attribute("description")
            assert !code.toString().contains("\\s+")
            assert !description.toString().contains("\\s+")
        }

        @Override
        String toString() { return "{type=enum code=$code desc=$description}" }
    }
}