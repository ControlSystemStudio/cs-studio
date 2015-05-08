package org.csstudio.dct.ui.editor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simple bean acting as clipboard for parameter values.
 *
 * @author Sven Wende
 */
public class ParameterValuesClipboard {
    private Map<String, String> content;

    public void setContent(Map<String, String> parameters) {
        content=new LinkedHashMap<String, String>(parameters);
    }

    public Map<String, String> getContent() {
        return content!=null?new LinkedHashMap<String, String>(content):null;
    }

    public boolean isEmpty() {
        return content==null;
    }
}
