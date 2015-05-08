package org.csstudio.sds.cursorservice;

import org.csstudio.sds.model.IOption;

/**
 * A mouse cursor. Instances of this class represent mouse cursor graphics that
 * can be used as the cursor graphics for SDS widgets.
 *
 * @author swende, Joerg Rathlev
 */
public abstract class AbstractCursor implements IOption {
    /**
     * Identifier of this cursor.
     */
    private String _id;

    /**
     * The name of this cursor.
     */
    private String _title;

    /**
     * Creates a new cursor descriptor.
     *
     * @param id
     *            the id of the cursor.
     * @param title
     *            the name of the cursor.
     */
    AbstractCursor(final String id, final String title) {
        assert id != null;
        assert title != null;
        _id = id;
        _title = title;
    }

    /**
     * Returns the name of this cursor.
     *
     * @return the name of this cursor.
     */
    public final String getTitle() {
        return _title;
    }

    /**
     * Returns the identifier of this cursor.
     *
     * @return the identifier of this cursor.
     */
    public final String getIdentifier() {
        return _id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return _title;
    }
}
