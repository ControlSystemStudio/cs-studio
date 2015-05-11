package org.csstudio.sds.cursorservice;

/**
 * A widget state in which a cursor can be applied to the widget. Cursor states
 * are associated with {@link CursorSelectionRule}s: each rule declares a set
 * of states and determines the current state for a widget.
 *
 * @author Joerg Rathlev
 */
public final class CursorState {

    /**
     * The id of the preference in which the cursor for this state is stored.
     */
    private String _id;

    /**
     * The name of this state.
     */
    private String _name;

    /**
     * Creates a new cursor state.
     *
     * @param id
     *            the id of this state.
     * @param name
     *            the name of this state.
     */
    CursorState(final String id, final String name) {
        assert id != null;
        assert name != null;
        _id = id;
        _name = name;
    }

    /**
     * Returns the id of this state.
     *
     * @return the id of this state.
     */
    public String getId() {
        return _id;
    }

    /**
     * Returns the name of this state.
     *
     * @return the name of this state.
     */
    public String getName() {
        return _name;
    }

    /**
     * Indicates whether some other object is equal to this one. Returns
     * <code>true</code> if the other object is a <code>CursorState</code>
     * instance with the same id as this one.
     *
     * @param obj
     *            the object this object will be compared to.
     * @return <code>true</code> if this object is the same as the obj
     *         argument; <code>false</code> otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof CursorState) {
            CursorState other = (CursorState) obj;
            return other._id.equals(this._id);
        }
        return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return _id.hashCode();
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object.
     */
    @Override
    public String toString() {
        return "CursorState:" + _id;
    }
}
