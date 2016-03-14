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
package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.support.PropertySSHelper;
import org.csstudio.opibuilder.util.SchemaService;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 *
 * <code>WidgetClassProperty</code> identifies the widget property, which specifies the class of the particular widget.
 * Widget classes are defined in the OPI schema and they specify the property values for the widget.
 *
 *
 * @see SchemaService#applyWidgetClassProperties(org.csstudio.opibuilder.model.AbstractWidgetModel)
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class WidgetClassProperty extends PVNameProperty {

    private String detailedDescription;
    private String widgetID;

    /**
     * Construct a new widget class property.
     *
     * @param widgetID the unique widget type, which this property belongs too (needed for auto complete)
     * @param prop_id the property id (this is always {@link AbstractWidgetModel#PROP_WIDGET_CLASS})
     * @param description the property description
     * @param category the property category
     * @param defaultValue default property value
     */
    public WidgetClassProperty(String widgetID, String prop_id, String description, WidgetPropertyCategory category,
        String defaultValue) {
        super(prop_id, description, category, defaultValue);
        this.detailedDescription = description;
        this.widgetID = widgetID;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.opibuilder.properties.PVNameProperty#createPropertyDescriptor()
     */
    @Override
    protected PropertyDescriptor createPropertyDescriptor() {
        if (PropertySSHelper.getIMPL() == null) {
            return null;
        }
        return PropertySSHelper.getIMPL().getWidgetClassPropertyDescriptor(widgetID, prop_id, description,
            detailedDescription);
    }

}
