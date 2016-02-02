package org.csstudio.ui.fx.util;

import javafx.scene.control.TextField;

/**
 *
 * <code>StaticTextField</code> is a non editable and non focusable text field.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class StaticTextField extends TextField {

    /**
     * Constructs a new static text field.
     */
    public StaticTextField() {
        setEditable(false);
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
