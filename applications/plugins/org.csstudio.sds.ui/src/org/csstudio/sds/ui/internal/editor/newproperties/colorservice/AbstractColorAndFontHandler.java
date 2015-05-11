package org.csstudio.sds.ui.internal.editor.newproperties.colorservice;

import java.util.List;

import org.xml.sax.helpers.DefaultHandler;

public abstract class AbstractColorAndFontHandler extends DefaultHandler{

    public abstract void reset();

    public abstract NamedStyle getStyle(String styleName);

    public abstract List<NamedStyle> getStyles();

}