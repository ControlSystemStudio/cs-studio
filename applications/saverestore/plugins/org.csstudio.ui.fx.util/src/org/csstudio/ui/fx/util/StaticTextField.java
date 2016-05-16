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
