package org.csstudio.ui.fx.util;

import javafx.scene.control.CheckBox;

/**
 * <code>UnfocusableCheckBox</code> is an extension of the checkbox, which cannot receive focus.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class UnfocusableCheckBox extends CheckBox {

    /**
     * Constructs a new unfocusable checkbox without any text.
     */
    public UnfocusableCheckBox() {
        super();
    }

    /**
     * Constructs a new unfocusable checkbox.
     *
     * @param text the text to display next the checkbox
     */
    public UnfocusableCheckBox(String text) {
        super(text);
    }

    /*
     * (non-Javadoc)
     *
     * @see javafx.scene.Node#requestFocus()
     */
    @Override
    public void requestFocus() {
        // ignore focus
    }
}
