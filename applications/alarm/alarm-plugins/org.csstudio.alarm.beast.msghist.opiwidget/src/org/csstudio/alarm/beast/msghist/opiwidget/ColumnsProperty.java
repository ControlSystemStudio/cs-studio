/*******************************************************************************
 * Copyright (c) 2010-2017 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.opiwidget;

import java.util.ArrayList;

import org.csstudio.alarm.beast.msghist.PropertyColumnPreference;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

/**
 *
 * <code>ColumnsProperty</code> defines the columns settings of the message history widget. The property prescribes which columns
 * are visible in the table in what order and what are their widths and weights.
 *
 *  @author Borut Terpinc
 */
public class ColumnsProperty extends AbstractWidgetProperty {

    private static final String XML_ELEMENT_COLUMN = "column"; //$NON-NLS-1$
    private static final String XML_ATTRIBUTE_NAME = "name"; //$NON-NLS-1$
    private static final String XML_ATTRIBUTE_WIDTH = "width"; //$NON-NLS-1$
    private static final String XML_ATTRIBUTE_WEIGHT = "weight"; //$NON-NLS-1$
    private static final String XML_ATTRIBUTE_VISIBLE = "visible"; //$NON-NLS-1$

    /**
     * Constructs a new columns property.
     *
     * @param propID
     *            the property ID
     * @param description
     *            the description of the property
     * @param category
     *            the property category
     * @param defaultValue
     *            default value to use
     */
    public ColumnsProperty(String propID, String description, WidgetPropertyCategory category,
            ColumnsInput defaultValue) {
        super(propID, description, category, defaultValue);
    }

    /*
     * @see org.csstudio.opibuilder.properties.AbstractWidgetProperty#checkValue(java .lang.Object)
     */
    @Override
    public Object checkValue(Object value) {
        if (value instanceof ColumnsInput) {
            return value;
        } else {
            return null;
        }
    }

    /*
     * @see org.csstudio.opibuilder.properties.AbstractWidgetProperty# createPropertyDescriptor()
     */
    @Override
    protected PropertyDescriptor createPropertyDescriptor() {
        return new ColumnsPropertyDescriptor(prop_id, description);
    }

    /*
     * @see org.csstudio.opibuilder.properties.AbstractWidgetProperty# readValueFromXML(org.jdom.Element)
     */
    @Override
    public ColumnsInput readValueFromXML(Element propElement) throws Exception {
        ArrayList<PropertyColumnPreference> cws = new ArrayList<>(20);
        for (Object o : propElement.getChildren(XML_ELEMENT_COLUMN)) {
            Element c = (Element) o;
            String name = c.getAttribute(XML_ATTRIBUTE_NAME).getValue();
            int width = c.getAttribute(XML_ATTRIBUTE_WIDTH).getIntValue();
            int weight = c.getAttribute(XML_ATTRIBUTE_WEIGHT).getIntValue();
            boolean visible = c.getAttribute(XML_ATTRIBUTE_VISIBLE).getBooleanValue();
            cws.add(new PropertyColumnPreference(name, width, weight, visible));
        }

        return new ColumnsInput(cws.toArray(new PropertyColumnPreference[cws.size()]));
    }

    /*
     * @see org.csstudio.opibuilder.properties.AbstractWidgetProperty#writeToXML(org. jdom.Element)
     */
    @Override
    public void writeToXML(Element propElement) {
        PropertyColumnPreference[] columns = ((ColumnsInput) getPropertyValue()).getColumns();
        for (PropertyColumnPreference c : columns) {
            Element e = new Element(XML_ELEMENT_COLUMN);
            e.setAttribute(XML_ATTRIBUTE_NAME, c.getName());
            e.setAttribute(XML_ATTRIBUTE_WEIGHT, String.valueOf(c.getWeight()));
            e.setAttribute(XML_ATTRIBUTE_WIDTH, String.valueOf(c.getSize()));
            e.setAttribute(XML_ATTRIBUTE_VISIBLE, String.valueOf(c.isVisible()));
            propElement.addContent(e);
        }
    }
}
