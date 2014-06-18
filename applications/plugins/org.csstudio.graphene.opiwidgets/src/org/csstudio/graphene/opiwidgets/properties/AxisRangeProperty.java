package org.csstudio.graphene.opiwidgets.properties;

import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.properties.support.PropertySSHelper;
import org.csstudio.opibuilder.util.MediaService;
import org.csstudio.opibuilder.util.OPIColor;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.epics.graphene.AxisRange;
import org.epics.graphene.AxisRanges;
import org.epics.graphene.AxisRanges.Absolute;
import org.epics.graphene.AxisRanges.Data;
import org.epics.graphene.AxisRanges.Display;
import org.epics.graphene.AxisRanges.Integrated;
import org.epics.util.stats.Ranges;
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
	public static final String XML_VALUE_TYPE_ABSOLUTE = "absolute"; //$NON-NLS-1$
	public static final String XML_VALUE_TYPE_INTEGRATED = "integrated"; //$NON-NLS-1$
	public static final String XML_ATTRIBUTE_MINABSOLUTE = "min"; //$NON-NLS-1$
	public static final String XML_ATTRIBUTE_MAXABSOLUTE = "max"; //$NON-NLS-1$
	public static final String XML_ATTRIBUTE_MINUSAGE = "minUsage"; //$NON-NLS-1$

	private static final String QUOTE = "\""; //$NON-NLS-1$
	
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
		// TODO
		return null;
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
		} else if (value instanceof Integrated) {
			axisRangeElement.setAttribute(XML_ATTRIBUTE_TYPE, XML_VALUE_TYPE_INTEGRATED);
			Integrated integrated = (Integrated) value;
			axisRangeElement.setAttribute(XML_ATTRIBUTE_MINUSAGE, Double.toString(integrated.getMinUsage()));
		} else if (value instanceof Absolute) {
			axisRangeElement.setAttribute(XML_ATTRIBUTE_TYPE, XML_VALUE_TYPE_ABSOLUTE);
			Absolute abs = (Absolute) value;
			axisRangeElement.setAttribute(XML_ATTRIBUTE_MINABSOLUTE, abs.getAbsoluteRange().getMinimum().toString());
			axisRangeElement.setAttribute(XML_ATTRIBUTE_MAXABSOLUTE, abs.getAbsoluteRange().getMaximum().toString());
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
			case XML_VALUE_TYPE_ABSOLUTE:
				try {
					double min = Double.parseDouble(axisRangeElement.getAttributeValue(XML_ATTRIBUTE_MINABSOLUTE));
					double max = Double.parseDouble(axisRangeElement.getAttributeValue(XML_ATTRIBUTE_MAXABSOLUTE));
					return AxisRanges.absolute(min, max);
				} catch(RuntimeException ex) {
					break;
				}
			case XML_VALUE_TYPE_INTEGRATED:
				try {
					double minUsage = Double.parseDouble(axisRangeElement.getAttributeValue(XML_ATTRIBUTE_MINUSAGE));
					return AxisRanges.integrated(minUsage);
				} catch(RuntimeException ex) {
					break;
				}
			case XML_VALUE_TYPE_DISPLAY:
			default:
		}
		return AxisRanges.display();
	}
}
