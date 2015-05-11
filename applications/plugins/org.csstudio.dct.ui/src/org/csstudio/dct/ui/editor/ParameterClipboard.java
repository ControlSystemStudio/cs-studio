package org.csstudio.dct.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.internal.Parameter;

/**
 * Simple bean acting as clipboard for parameters.
 *
 * @author Sven Wende
 */
public class ParameterClipboard {
    private List<Parameter> content;

    public ParameterClipboard() {
        content = null;
    }

    public void setContent(List<Parameter> parameters) {
        content=new ArrayList<Parameter>(parameters);
    }

    public List<Parameter> getContent() {
        return content!=null?new ArrayList<Parameter>(content):null;
    }

    public boolean isEmpty() {
        return content==null;
    }
}
