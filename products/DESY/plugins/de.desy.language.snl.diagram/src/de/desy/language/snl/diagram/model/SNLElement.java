package de.desy.language.snl.diagram.model;

import java.util.ArrayList;
import java.util.List;

public abstract class SNLElement extends ModelElement {

    private static final long serialVersionUID = 9220177984541119789L;
    /** Property ID to use when a child is added to this diagram. */
    public static final String CHILD_ADDED_PROP = "ShapesDiagram.ChildAdded";
    /** Property ID to use when a child is removed from this diagram. */
    public static final String CHILD_REMOVED_PROP = "ShapesDiagram.ChildRemoved";

    private List<SNLModel> _snlModels = new ArrayList<SNLModel>();

    /**
     * Add a shape to this diagram.
     *
     * @param child
     *            a non-null shape instance
     * @return true, if the shape was added, false otherwise
     */
    public boolean addChild(SNLModel child) {
        if (canHaveChildren()) {
            if (child != null && _snlModels.add(child)) {
                firePropertyChange(CHILD_ADDED_PROP, null, child);
                return true;
            }
        }
        return false;
    }

    /**
     * Return a List of Shapes in this diagram. The returned List should not be
     * modified.
     */
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
    public boolean removeChild(SNLModel child) {
        if (canHaveChildren()) {
            if (child != null && _snlModels.remove(child)) {
                firePropertyChange(CHILD_REMOVED_PROP, null, child);
                return true;
            }
        }
        return false;
    }

    public boolean hasChildren() {
        return !_snlModels.isEmpty();
    }

    protected abstract boolean canHaveChildren();

}
