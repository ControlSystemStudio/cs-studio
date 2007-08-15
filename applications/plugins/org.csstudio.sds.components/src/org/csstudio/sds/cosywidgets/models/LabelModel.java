package org.csstudio.sds.cosywidgets.models;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.FontProperty;
import org.csstudio.sds.model.properties.OptionProperty;
import org.csstudio.sds.model.properties.StringProperty;
import org.csstudio.sds.model.properties.BooleanProperty;
import org.csstudio.sds.model.properties.DoubleProperty;
import org.csstudio.sds.model.properties.IntegerProperty;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

/**
 * A label widget model.
 * 
 * @author jbercic
 * 
 */
public final class LabelModel extends AbstractWidgetModel {
	/**
	 * Unique identifier.
	 */
	public static final String ID = "cosywidgets.label";
	
	/**
	 * The IDs of the properties.
	 */
	public static final String PROP_FONT = "font";
	public static final String PROP_TEXT_ALIGN = "textAlignment";
	public static final String PROP_TRANSPARENT = "transparent_background";
	public static final String PROP_ROTATION = "text_rotation";
	public static final String PROP_XOFF = "offset.x";
	public static final String PROP_YOFF = "offset.y";
	
	/**
	 * The ID of the precision property.
	 */
	public static final String PROP_PRECISION = "precision"; //$NON-NLS-1$
	
	/**
	 * Type of the displayed text.
	 */
	public static final String PROP_TYPE = "value_type";
	
	/**
	 * Text value.
	 */
	public static final String PROP_TEXTVALUE = "value.text";
	
	/**
	 * Double value and its output formatting.
	 */
	public static final String PROP_DOUBLEVALUE = "value.double";
	//public static final String PROP_DOUBLEVALUEFORMAT = "value.double.format";
	
	/**
	 * Currently available value types.
	 */
	public static final String [] VALUE_TYPES=new String [] {"Text","Double Value"};
	public static final int TYPE_TEXT = 0;
	public static final int TYPE_DOUBLE = 1;

	public LabelModel() {
		setWidth(100);
		setHeight(30);
	}
	
	/**
	 * Labels for the text alignment property.
	 */
	private static final String[] SHOW_LABELS = new String[] { "Center", "Top",
			"Bottom", "Left", "Right" };

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeID() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureProperties() {
		addProperty(PROP_FONT, new FontProperty("Font",WidgetPropertyCategory.Display, new FontData("Arial", 8, SWT.NONE)));
		addProperty(PROP_TEXT_ALIGN, new OptionProperty("Text Alignment",WidgetPropertyCategory.Display, SHOW_LABELS,0));
		addProperty(PROP_TRANSPARENT, new BooleanProperty("Transparent Background",WidgetPropertyCategory.Display,true));
		addProperty(PROP_ROTATION, new DoubleProperty("Text Rotation Angle",WidgetPropertyCategory.Display,90.0,0.0,360.0));
		addProperty(PROP_XOFF, new IntegerProperty("X Offset",WidgetPropertyCategory.Display,0));
		addProperty(PROP_YOFF, new IntegerProperty("Y Offset",WidgetPropertyCategory.Display,0));
		
		//value properties
		addProperty(PROP_TYPE, new OptionProperty("Value Type",WidgetPropertyCategory.Behaviour,VALUE_TYPES,TYPE_DOUBLE));
		addProperty(PROP_TEXTVALUE, new StringProperty("Text Value",WidgetPropertyCategory.Display,""));
		addProperty(PROP_DOUBLEVALUE, new DoubleProperty("Double Value",WidgetPropertyCategory.Display,0.0));
		//addProperty(PROP_DOUBLEVALUEFORMAT, new StringProperty("Double Value Format",WidgetPropertyCategory.Display,"%.3f"));
		addProperty(PROP_PRECISION, new IntegerProperty("Decimal places",
				WidgetPropertyCategory.Behaviour, 2, 0, 5));
	}

	public FontData getFont() {
		return (FontData) getProperty(PROP_FONT).getPropertyValue();
	}
	
	/**
	 * Return the precision.
	 * 
	 * @return The precision.
	 */
	public int getPrecision() {
		return (Integer) getProperty(PROP_PRECISION).getPropertyValue();
	}

	public int getTextAlignment() {
		return (Integer) getProperty(PROP_TEXT_ALIGN).getPropertyValue();
	}
	
	public boolean getTransparent() {
		return (Boolean) getProperty(PROP_TRANSPARENT).getPropertyValue();
	}
	
	public double getRotation() {
		return (Double) getProperty(PROP_ROTATION).getPropertyValue();
	}
	
	public int getXOff() {
		return (Integer) getProperty(PROP_XOFF).getPropertyValue();
	}
	
	public int getYOff() {
		return (Integer) getProperty(PROP_YOFF).getPropertyValue();
	}
	
	public int getType() {
		return (Integer) getProperty(PROP_TYPE).getPropertyValue();
	}
	
	public String getTextValue() {
		return (String) getProperty(PROP_TEXTVALUE).getPropertyValue();
	}
	
	public double getDoubleValue() {
		return (Double) getProperty(PROP_DOUBLEVALUE).getPropertyValue();
	}
	
//	public String getDoubleValueFormat() {
//		return (String) getProperty(PROP_DOUBLEVALUEFORMAT).getPropertyValue();
//	}
}
