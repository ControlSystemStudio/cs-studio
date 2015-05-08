package org.csstudio.utility.pvmanager.widgets;

import org.epics.vtype.VType;

/**
 * An object that can be adapted to a vType.
 *
 * @author carcassi
 */
public interface VTypeAdaptable {

    /**
     * Provides a vType view of the object.
     * <p>
     * The view must be immutable: if the object changes, the returned
     * value must not.
     *
     * @return the adapted value
     */
    public VType toVType();
}
