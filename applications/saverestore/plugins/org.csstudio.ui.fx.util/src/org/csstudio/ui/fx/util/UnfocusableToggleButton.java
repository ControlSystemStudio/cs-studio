/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.ui.fx.util;

import javafx.scene.Node;
import javafx.scene.control.ToggleButton;

/**
 * <code>UnfocusableToggleButton</code> is an extension of the toggle button, which cannot receive focus.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class UnfocusableToggleButton extends ToggleButton {

    /**
     * Constructs a new unfocusable toggle button.
     */
    public UnfocusableToggleButton() {
        super();
    }

    /**
     * Constructs a new unfocusable toggle button with the given text.
     *
     * @param text the button text
     */
    public UnfocusableToggleButton(String text) {
        super(text);
    }

    /**
     * Constructs a new unfocusable toggle button with the given text and graphics node.
     *
     * @param text the button text
     * @param graphics the graphics for the button
     */
    public UnfocusableToggleButton(String text, Node graphics) {
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
