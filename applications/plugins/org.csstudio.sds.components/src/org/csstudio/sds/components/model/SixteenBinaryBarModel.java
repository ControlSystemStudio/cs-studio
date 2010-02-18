package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.BooleanProperty;
import org.csstudio.sds.model.properties.IntegerProperty;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

/**
 * Represents the model for the Sixteen Binary Bar.
 * 
 * @author Alen Vrecko
 * 
 */
public class SixteenBinaryBarModel extends AbstractWidgetModel {

	public static final String PROP_ON_COLOR = "onColor"; //$NON-NLS-1$

	public static final String PROP_OFF_COLOR = "offColor"; //$NON-NLS-1$

	public static final String PROP_LABEL_FONT = "labelFont"; //$NON-NLS-1$

	public static final String PROP_HORIZONTAL = "horizontal"; //$NON-NLS-1$

	public static final String PROP_SHOW_LABELS = "showLabels"; //$NON-NLS-1$

	public static final String PROP_INTERNAL_FRAME_THICKNESS = "internalFrameSize"; //$NON-NLS-1$

	public static final String PROP_LABEL_COLOR = "labelColor"; //$NON-NLS-1$

	public static final String PROP_INTERNAL_FRAME_COLOR = "internalFrameColor"; //$NON-NLS-1$

	public static final String ID = "org.csstudio.sds.components.SixteenBinaryBar"; //$NON-NLS-1$

	public static final String PROP_VALUE = "value"; //$NON-NLS-1$
	
	public static final String PROP_BITS_FROM = "bitRangeFrom"; //$NON-NLS-1$
	
	public static final String PROP_BITS_TO = "bitRangeTo"; //$NON-NLS-1$ 

	private static final int DEFAULT_HEIGHT = 50;

	/**
	 * The default value of the orientation property.
	 */
	private static final boolean DEFAULT_ORIENTATION_HORIZONTAL = true;

	/**
	 * The default value of the width property.
	 */
	private static final int DEFAULT_WIDTH = 20;

	/**
	 * Standard constructor.
	 */
	public SixteenBinaryBarModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

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
		addProperty(PROP_VALUE, new IntegerProperty("Value",
				WidgetPropertyCategory.Behaviour, 0));
		addProperty(PROP_HORIZONTAL, new BooleanProperty(
				"Horizontal Orientation", WidgetPropertyCategory.Display,
				DEFAULT_ORIENTATION_HORIZONTAL));
		addProperty(PROP_SHOW_LABELS, new BooleanProperty("Show labels",
				WidgetPropertyCategory.Display, false));
		addFontProperty(PROP_LABEL_FONT, "Label fonts",
				WidgetPropertyCategory.Display, new FontData("Arial", 9,
						SWT.NORMAL));
		addProperty(PROP_INTERNAL_FRAME_THICKNESS, new IntegerProperty(
				"Internal frame thickness", WidgetPropertyCategory.Display, 1));
		
		// The maximum bit range that can be handled by this widget is 0..31.
		// More than 32 bits are not possible because the value property is an
		// integer property.
		addProperty(PROP_BITS_FROM, new IntegerProperty(
				"Bit range (from)", WidgetPropertyCategory.Behaviour, 0, 0,
				31));
		addProperty(PROP_BITS_TO, new IntegerProperty(
				"Bit range (to)", WidgetPropertyCategory.Behaviour, 15, 0, 31));

		addColorProperty(PROP_ON_COLOR, "On color",
				WidgetPropertyCategory.Display, "#00ff00");
		addColorProperty(PROP_OFF_COLOR, "Off color",
				WidgetPropertyCategory.Display, "#c0c0c0");
		addColorProperty(PROP_LABEL_COLOR, 
				"Label text color", WidgetPropertyCategory.Display,
				"#000000");
		addColorProperty(PROP_INTERNAL_FRAME_COLOR, 
				"Internal Frame Color", WidgetPropertyCategory.Display,
				"#000000");
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getDefaultToolTip() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(createTooltipParameter(PROP_ALIASES) + "\n");
		buffer.append("Value:\t");
		buffer.append(createTooltipParameter(PROP_VALUE));
		return buffer.toString();
	}

	public boolean getHorizontal() {
		return getBooleanProperty(PROP_HORIZONTAL).getPropertyValue();
	}

	public boolean getShowLabels() {
		return getBooleanProperty(PROP_SHOW_LABELS).getPropertyValue();
	}

	public int getValue() {
		return getIntegerProperty(PROP_VALUE).getPropertyValue();
	}

	public int getInternalFrameThickness() {
		return getIntegerProperty(PROP_INTERNAL_FRAME_THICKNESS).getPropertyValue();
	}

	public int getBitRangeFrom() {
		return getIntegerProperty(PROP_BITS_FROM).getPropertyValue();
	}
	
	public int getBitRangeTo() {
		return getIntegerProperty(PROP_BITS_TO).getPropertyValue();
	}

}
