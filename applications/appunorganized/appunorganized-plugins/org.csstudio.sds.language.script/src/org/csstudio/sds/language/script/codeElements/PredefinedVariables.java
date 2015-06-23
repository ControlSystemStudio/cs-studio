package org.csstudio.sds.language.script.codeElements;

import de.desy.language.editor.core.ILanguageElements;

public enum PredefinedVariables implements ILanguageElements {
    RETURNS("returns", true),
    COMPATIBLE_PROPERTIES("compatibleProperties", false),
    DESCRIPTION("description", false),
    PARAMETERS("parameters", false),
    PARAMETERTYPES("parameterTypes", true);

    private String _elementName;
    private boolean _optional;

    private PredefinedVariables(String elementName, boolean optional) {
        _elementName = elementName;
        _optional = optional;
    }

    public String getElementName() {
        return _elementName;
    }

    public boolean isOptional() {
        return _optional;
    }

}
