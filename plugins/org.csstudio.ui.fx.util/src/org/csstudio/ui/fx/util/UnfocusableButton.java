package org.csstudio.ui.fx.util;

import javafx.scene.Node;
import javafx.scene.control.Button;

/**
 * <code>UnfocusableButton</code> is an extension of the button, which cannot receive focus.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class UnfocusableButton extends Button {

    /**
     * Constructs a new unfocusable button.
     */
    public UnfocusableButton() {
        super();
    }

    /**
     * Constructs a new unfocusable button with the given text.
     *
     * @param text the button text
     */
    public UnfocusableButton(String text) {
        super(text);
    }

    /**
     * Constructs a new unfocusable button with the given text and graphics node.
     *
     * @param text the button text
     * @param graphics the graphics for the button
     */
    public UnfocusableButton(String text, Node graphics) {
        super(text, graphics);
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
