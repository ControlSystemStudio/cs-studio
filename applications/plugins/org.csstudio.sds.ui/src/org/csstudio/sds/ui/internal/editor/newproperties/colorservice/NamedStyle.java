package org.csstudio.sds.ui.internal.editor.newproperties.colorservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NamedStyle {

    private String _name;
    private Map<String, NamedColor> _colors;
    private Map<String, NamedFont> _fonts;
    private final String _description;

    public NamedStyle(String name, String description) {
        assert name != null : "name != null";
        assert name.trim().length() > 0 : "name.trim().length() > 0";
        assert description != null : "description != null";
        assert description.trim().length() > 0 : "description.trim().length() > 0";

        _name = name;
        _description = description;
        _colors = new HashMap<String, NamedColor>();
        _fonts = new HashMap<String, NamedFont>();
    }

    public void addColor(NamedColor color) {
        assert color != null : "color != null";

        _colors.put("${"+color.getName()+"}", color);
    }

    public void addFont(NamedFont font) {
        assert font != null : "font != null";

        _fonts.put("${"+font.getName()+"}", font);
    }

    public String getName() {
        return _name;
    }

    public String getDescription() {
        return _description;
    }

    public NamedColor getColor(String id) {
        assert id != null : "id != null";
        assert id.trim().length() > 0 : "id.trim().length() > 0";

        return _colors.get(id);
    }

    public List<NamedColor> listAllColors() {
        return new ArrayList<NamedColor>(_colors.values());
    }

    public NamedFont getFont(String id) {
        assert id != null : "id != null";
        assert id.trim().length() > 0 : "id.trim().length() > 0";

        return _fonts.get(id);
    }

    public List<NamedFont> listAllFonts() {
        return new ArrayList<NamedFont>(_fonts.values());
    }

}
