package org.csstudio.sds.language.script.codeElements;

import de.desy.language.editor.core.ILanguageElements;

public enum Keywords implements ILanguageElements {

    VAR("var"),
    RETURN("return"),
    IF("if"),
    ELSE("else"),
    FUNCTION("function"),
    NEW("new"),
    FOR("for"),
    WHILE("while"),
    DO("do"),
    SWITCH("switch"),
    CASE("case"),
    DEFAULT("default"),
    BREAK("break"),
    CATCH("catch"),
    CONST("const"),
    CONTINUE("continue"),
    DELETE("delete"),
    EXPORT("export"),
    FALSE("false"),
    FINALLY("finally"),
    IN("in"),
    INSTANCEOF("instanceof"),
    NULL("null"),
    THIS("this"),
    THROW("throw"),
    TRUE("true"),
    TRY("try"),
    TYPEOF("typeof"),
    UNDEFINED("undefined"),
    VOID("void"),
    WITH("with"),
    IMPORTPACKAGE("importPackage");

    private String _elementName;

    private Keywords(String elementName) {
        _elementName = elementName;
    }

    public String getElementName() {
        return _elementName;
    }

}
