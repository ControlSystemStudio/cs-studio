package org.csstudio.sds.language.script.codeElements;

import de.desy.language.editor.core.ILanguageElements;

public enum PredefinedMethods implements ILanguageElements {

    EXECUTE("execute");

    private String _elementName;

    private PredefinedMethods(String elementName) {
        _elementName = elementName;
    }

    public String getElementName() {
        return _elementName;
    }

}
