/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.preferences.jface;


import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;


/**
 * A field editor for an double type preference.
 *
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 8 Dec 2016
 */
public class DoubleFieldEditor extends StringFieldEditor {

    private static final int DEFAULT_TEXT_LIMIT = 10;

    private double maxValidValue = Double.MAX_VALUE;
    private double minValidValue = Double.MIN_VALUE;

    /**
     * Creates a new double field editor.
     */
    protected DoubleFieldEditor ( ) {
    }

    /**
     * Creates an double field editor.
     *
     * @param name      The name of the preference this field editor works on.
     * @param labelText The label text of the field editor.
     * @param parent    The parent of the field editor's control.
     */
    public DoubleFieldEditor ( String name, String labelText, Composite parent ) {
        this(name, labelText, parent, DEFAULT_TEXT_LIMIT);
    }

    /**
     * Creates an double field editor.
     *
     * @param name      The name of the preference this field editor works on.
     * @param labelText The label text of the field editor.
     * @param parent    The parent of the field editor's control.
     * @param textLimit The maximum number of characters in the text.
     */
    public DoubleFieldEditor ( String name, String labelText, Composite parent, int textLimit ) {
        init(name, labelText);
        setTextLimit(textLimit);
        setEmptyStringAllowed(false);
        setErrorMessage(Messages.DoubleFieldEditor_errorMessage);
        createControl(parent);
    }

    /**
     * Returns this field editor's current value as a double.
     *
     * @return The value.
     * @exception NumberFormatException if the <code>String</code> does not
     *   contain a parsable double.
     */
    public double getDoubleValue() throws NumberFormatException {
        return new Double(getStringValue()).doubleValue();
    }

    /**
     * Sets the range of valid values for this field.
     *
     * @param min the minimum allowed value (inclusive)
     * @param max the maximum allowed value (inclusive)
     */
    public void setValidRange ( double min, double max ) {

        minValidValue = min;
        maxValidValue = max;

        setErrorMessage(NLS.bind(Messages.DoubleFieldEditor_errorMessageRange, min, max));

    }

    /*
     * (non-Javadoc)
     * Method declared on StringFieldEditor.
     * Checks whether the entered String is a valid integer or not.
     */
    @Override
    protected boolean checkState ( ) {

        Text text = getTextControl();

        if ( text == null ) {
            return false;
        }

        String numberString = text.getText();

        try {

            double number = Double.valueOf(numberString).doubleValue();

            if ( number >= minValidValue && number <= maxValidValue ) {
                clearErrorMessage();
                return true;
            }

            showErrorMessage();
            return false;

        } catch ( NumberFormatException e1 ) {
            showErrorMessage();
        }

        return false;

    }

    /*
     * (non-Javadoc)
     * Method declared on FieldEditor.
     */
    @Override
    protected void doLoad ( ) {

        Text text = getTextControl();

        if ( text != null ) {

            double value = getPreferenceStore().getDouble(getPreferenceName());

            text.setText("" + value);//$NON-NLS-1$

            oldValue = "" + value; //$NON-NLS-1$

        }

    }

    /*
     * (non-Javadoc)
     * Method declared on FieldEditor.
     */
    @Override
    protected void doLoadDefault ( ) {

        Text text = getTextControl();

        if ( text != null ) {

            double value = getPreferenceStore().getDefaultDouble(getPreferenceName());

            text.setText("" + value);//$NON-NLS-1$

        }

        valueChanged();

    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    @Override
    protected void doStore() {

        Text text = getTextControl();

        if (text != null) {

            Double d = new Double(text.getText());

            getPreferenceStore().setValue(getPreferenceName(), d.doubleValue());

        }

    }

}
