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
package org.csstudio.opibuilder.properties.support;

import org.csstudio.autocomplete.ui.AutoCompleteTextCellEditor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 *
 * <code>WidgetClassPropertyDescriptor</code> is a property descriptor for the widget class, which supports a custom
 * auto complete that takes the widget type into account, when searching for options.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class WidgetClassPropertyDescriptor extends TextPropertyDescriptor {

    private final String widgetID;

    /**
     * @param widgetID the id of the widget model
     * @param propertyID id of the property
     * @param displayName the display name in the property sheet
     * @param detailedDescription the detailed description in tooltip and status line.
     */
    public WidgetClassPropertyDescriptor(String widgetID, String propertyID, String displayName,
        String detailedDescription) {
        super(propertyID, displayName);
        setDescription(detailedDescription);
        this.widgetID = widgetID;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.ui.views.properties.TextPropertyDescriptor#createPropertyEditor(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public CellEditor createPropertyEditor(final Composite parent) {
        AutoCompleteTextCellEditor editor = new AutoCompleteTextCellEditor(parent, widgetID);
        editor.getControl().setToolTipText(getDescription());
        return editor;
    }
}
