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
package org.csstudio.saverestore.ui.browser.periodictable;

import javafx.geometry.Insets;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.Region;

/**
 *
 * <code>TinySpinner</code> is a spinner that has narrower buttons, which take less space than the regular Java FX
 * spinner. In addition it also implements some common functionality required by the periodic table, such as applying
 * value as you type, editing etc.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class TinySpinner extends Spinner<Integer> {

    /**
     * Constructs a new editable spinner with the given value factory.
     *
     * @param factory the value factory to set on the spinner
     */
    TinySpinner(SpinnerValueFactory<Integer> factory) {
        super(factory);
        setEditable(true);
        setMinWidth(0);
        setMaxWidth(Double.MAX_VALUE);
        getEditor().setOnKeyReleased(e -> {
            int pos = getEditor().getCaretPosition();
            try {
                int val = Integer.parseInt(getEditor().getText());
                getValueFactory().setValue(val);
            } catch (NumberFormatException ex) {
                //ignore
            }
            getEditor().positionCaret(pos);
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see javafx.scene.control.Spinner#createDefaultSkin()
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        SkinBase<?> skin = (SkinBase<?>)super.createDefaultSkin();
        ((Region) skin.getChildren().get(1)).setPadding(new Insets(0, 3, 0, 3));
        ((Region) skin.getChildren().get(2)).setPadding(new Insets(0, 3, 0, 3));
        return skin;
    }
}
