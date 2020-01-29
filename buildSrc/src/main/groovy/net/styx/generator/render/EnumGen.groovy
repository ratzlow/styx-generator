package net.styx.generator.render

import net.styx.generator.parse.Field

class EnumGen implements Generator {

    def pkgName, className
    Field field

    EnumGen(pkgName, className, Field field) {
        assert !field.literals.isEmpty() : field
        this.pkgName = pkgName
        this.className = className
        this.field = field
    }


    @Override
    String generate() {
//------------------------------Template:enum file----------------
"""package $pkgName;

public enum $className {
${literals()}

    private String code;

    ${className}(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
"""
//----------------------------------------------------------------
    }

    @Override
    String fileName() { className }

    private String literals() {
        field.literals.collect { "    $it.description(\"$it.code\")" }.join(",\n") + ";"
    }
}
