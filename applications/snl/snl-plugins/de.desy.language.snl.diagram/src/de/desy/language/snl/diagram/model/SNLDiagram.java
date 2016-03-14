package de.desy.language.snl.diagram.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A container for multiple shapes. This is the "root" of the model data
 * structure.
 */
public class SNLDiagram extends SNLElement {

    private static final long serialVersionUID = 1;
    private List<SNLModel> _snlModels = new ArrayList<SNLModel>();

    /**
     * Add a shape to this diagram.
     *
     * @param child
     *            a non-null shape instance
     * @return true, if the shape was added, false otherwise
     */
    @Override
    public boolean addChild(SNLModel child) {
        if (child != null && _snlModels.add(child)) {
            return true;
        }
        return false;
    }

    /**
     * Return a List of Shapes in this diagram. The returned List should not be
     * modified.
     */
    @Override
    public List<SNLModel> getChildren() {
        return _snlModels;
    }

    /**
     * Remove a shape from this diagram.
     *
     * @param child
     *            a non-null shape instance;
     * @return true, if the shape was removed, false otherwise
     */
    @Override
    public boolean removeChild(SNLModel child) {
        if (child != null && _snlModels.remove(child)) {
            return true;
        }
        return false;
    }

    @Override
    protected boolean canHaveChildren() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "SNL-Diagram";
    }

}