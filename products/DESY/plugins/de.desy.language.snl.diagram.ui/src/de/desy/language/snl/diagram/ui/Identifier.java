package de.desy.language.snl.diagram.ui;

public enum Identifier {

    DIAGRAM_EDITOR_ID("de.desy.language.snl.diagram.ui.DiagramEditor");

    private final String _id;

    private Identifier(String id) {
        assert id != null : "id != null";
        assert id.trim().length() > 0 : "${param}.trim().length() > 0";

        _id = id;
    }

    public String getId() {
        return _id;
    }

}
