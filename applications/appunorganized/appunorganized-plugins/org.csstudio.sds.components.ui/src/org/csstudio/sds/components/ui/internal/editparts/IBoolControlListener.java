package org.csstudio.sds.components.ui.internal.editparts;

/**
 * Definition of listeners that react on boolean control events.
 *
 * @author Xihui Chen
 *
 */
public interface IBoolControlListener {
    /**
     * React on a boolValue change event.
     *
     * @param newValue
     *            The new bool value.
     */
    void valueChanged(final boolean newValue);
}
