package org.csstudio.graphene.opiwidgets.properties;

import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.epics.graphene.AxisRange;
import org.epics.graphene.AxisRanges;
import org.epics.graphene.AxisRanges.Auto;
import org.epics.graphene.AxisRanges.Data;
import org.epics.graphene.AxisRanges.Display;
import org.epics.graphene.AxisRanges.Fixed;
import org.jdom.Element;

/**
 * Widget property for range of a plot.
 *
 */
public class AxisRangeProperty extends AbstractWidgetProperty {

    public static final String XML_ELEMENT_AXISRANGE = "axisRange"; //$NON-NLS-1$
    public static final String XML_ATTRIBUTE_TYPE = "type"; //$NON-NLS-1$
    public static final String XML_VALUE_TYPE_DISPLAY = "display"; //$NON-NLS-1$
    public static final String XML_VALUE_TYPE_DATA = "data"; //$NON-NLS-1$
    public static final String XML_VALUE_TYPE_ABSOLUTE = "fixed"; //$NON-NLS-1$
    public static final String XML_VALUE_TYPE_AUTO = "auto"; //$NON-NLS-1$
    public static final String XML_ATTRIBUTE_MINFIXED = "min"; //$NON-NLS-1$
    public static final String XML_ATTRIBUTE_MAXFIXED = "max"; //$NON-NLS-1$
    public static final String XML_ATTRIBUTE_MINUSAGE = "minUsage"; //$NON-NLS-1$

    /**
     * AxisRange Property Constructor. The property value type is {@link AxisRange}.
     *
     * @param prop_id the property id which should be unique in a widget model.
     * @param description the description of the property,
     * which will be shown as the property name in property sheet.
     * @param category the category of the widget.
     * @param defaultValue the default value when the widget is first created.
     */
    public AxisRangeProperty(String prop_id, String description,
            WidgetPropertyCategory category, AxisRange defaultValue) {
        super(prop_id, description, category, defaultValue);
    }


    @Override
    public Object checkValue(Object value) {
        if (value instanceof AxisRange) {
            return value;
        } else {
            return null;
        }
    }

    @Override
    protected PropertyDescriptor createPropertyDescriptor() {
        return new AxisRangePropertyDescriptor(prop_id, description);
    }

    @Override
    public void writeToXML(Element propElement) {
        Object value = getPropertyValue();
        if (value == null) {
            value = AxisRanges.display();
        }

        Element axisRangeElement = new Element(XML_ELEMENT_AXISRANGE);

        if (value instanceof Display) {
            axisRangeElement.setAttribute(XML_ATTRIBUTE_TYPE, XML_VALUE_TYPE_DISPLAY);
        } else if (value instanceof Data) {
            axisRangeElement.setAttribute(XML_ATTRIBUTE_TYPE, XML_VALUE_TYPE_DATA);
        } else if (value instanceof Auto) {
            axisRangeElement.setAttribute(XML_ATTRIBUTE_TYPE, XML_VALUE_TYPE_AUTO);
            Auto integrated = (Auto) value;
            axisRangeElement.setAttribute(XML_ATTRIBUTE_MINUSAGE, Double.toString(integrated.getMinUsage()));
        } else if (value instanceof Fixed) {
            axisRangeElement.setAttribute(XML_ATTRIBUTE_TYPE, XML_VALUE_TYPE_ABSOLUTE);
            Fixed abs = (Fixed) value;
            axisRangeElement.setAttribute(XML_ATTRIBUTE_MINFIXED, abs.getFixedRange().getMinimum().toString());
            axisRangeElement.setAttribute(XML_ATTRIBUTE_MAXFIXED, abs.getFixedRange().getMaximum().toString());
        }
        propElement.addContent(axisRangeElement);
    }


    @Override
    public Object readValueFromXML(Element propElement) {
        Element axisRangeElement = propElement.getChild(XML_ELEMENT_AXISRANGE);
        if (axisRangeElement == null) {
            return AxisRanges.display();
        }
        String type = axisRangeElement.getAttributeValue(XML_ATTRIBUTE_TYPE);
        if (type == null) {
            return AxisRanges.display();
        }

        switch(type) {
            case XML_VALUE_TYPE_DATA:
                return AxisRanges.data();
            case "absolute":
            case XML_VALUE_TYPE_ABSOLUTE:
                try {
                    double min = Double.parseDouble(axisRangeElement.getAttributeValue(XML_ATTRIBUTE_MINFIXED));
                    double max = Double.parseDouble(axisRangeElement.getAttributeValue(XML_ATTRIBUTE_MAXFIXED));
                    return AxisRanges.fixed(min, max);
                } catch(RuntimeException ex) {
                    break;
                }
            case "integrated":
            case XML_VALUE_TYPE_AUTO:
                try {
                    double minUsage = Double.parseDouble(axisRangeElement.getAttributeValue(XML_ATTRIBUTE_MINUSAGE));
                    return AxisRanges.auto(minUsage);
                } catch(RuntimeException ex) {
                    break;
                }
            case XML_VALUE_TYPE_DISPLAY:
            default:
        }
        return AxisRanges.display();
    }
}
