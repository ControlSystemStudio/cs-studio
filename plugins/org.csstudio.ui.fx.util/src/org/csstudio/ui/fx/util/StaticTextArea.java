package org.csstudio.ui.fx.util;

import javafx.scene.control.TextArea;

/**
 *
 * <code>StaticTextArea</code> is a non editable and non focusable text area.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class StaticTextArea extends TextArea {

    /**
     * Constructs a new static text area.
     */
    public StaticTextArea() {
        setEditable(false);
        setWrapText(true);
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
