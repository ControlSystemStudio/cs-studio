package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.ColorProperty;
import org.csstudio.sds.model.properties.DoubleProperty;
import org.csstudio.sds.model.properties.FontProperty;
import org.csstudio.sds.model.properties.IntegerProperty;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

public class ThumbWheelModel extends AbstractWidgetModel {

	public static final String PROP_FONT = "font"; //$NON-NLS-1$

	public static final String PROP_MIN = "min"; //$NON-NLS-1$

	public static final String PROP_MAX = "max"; //$NON-NLS-1$

	public static final String PROP_INTERNAL_FRAME_THICKNESS = "internalFrameSize"; //$NON-NLS-1$

	public static final String PROP_INTERNAL_FRAME_COLOR = "internalFrameColor"; //$NON-NLS-1$

	public static final String PROP_WHOLE_DIGITS_PART = "wholeDigits"; //$NON-NLS-1$

	public static final String PROP_DECIMAL_DIGITS_PART = "decimalDigits"; //$NON-NLS-1$

	public static final String ID = "org.csstudio.sds.components.ThumbWheel"; //$NON-NLS-1$

	public static final String PROP_VALUE = "value"; //$NON-NLS-1$

	private static final int DEFAULT_HEIGHT = 50;

	/**
	 * The default value of the width property.
	 */
	private static final int DEFAULT_WIDTH = 20;

	/**
	 * Standard constructor.
	 */
	public ThumbWheelModel() {
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
		addProperty(PROP_VALUE, new DoubleProperty("Value",
				WidgetPropertyCategory.Behaviour, 0));
		addProperty(PROP_WHOLE_DIGITS_PART, new IntegerProperty("Whole digits",
				WidgetPropertyCategory.Display, 5));
		addProperty(PROP_MIN, new DoubleProperty("Min",
				WidgetPropertyCategory.Behaviour, Double.NaN));
		addProperty(PROP_MAX, new DoubleProperty("Max",
				WidgetPropertyCategory.Behaviour, Double.NaN));
		addProperty(PROP_WHOLE_DIGITS_PART, new IntegerProperty(
				"Integer digits", WidgetPropertyCategory.Behaviour, 5));
		addProperty(PROP_DECIMAL_DIGITS_PART, new IntegerProperty(
				"Decimal digits", WidgetPropertyCategory.Behaviour, 5));

		addProperty(PROP_FONT, new FontProperty("Wheel Fonts",
				WidgetPropertyCategory.Display, new FontData("Arial", 9,
						SWT.NORMAL)));

		addProperty(PROP_INTERNAL_FRAME_COLOR, new ColorProperty(
				"Internal frame color", WidgetPropertyCategory.Display,
				ColorConstants.black.getRGB()));

		addProperty(PROP_INTERNAL_FRAME_THICKNESS, new IntegerProperty(
				"Internal frame thickness", WidgetPropertyCategory.Display, 0));

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

	public FontData getFont() {
		return getProperty(PROP_FONT).getPropertyValue();
	}

	public int getWholePartDigits() {
		return getProperty(PROP_WHOLE_DIGITS_PART).getPropertyValue();
	}

	public void setWholePartDigits(int val) {
		setPropertyValue(PROP_WHOLE_DIGITS_PART, val);
	}

	public int getDecimalPartDigits() {
		return getProperty(PROP_DECIMAL_DIGITS_PART).getPropertyValue();
	}

	public void setDecimalPartDigits(int val) {
		setPropertyValue(PROP_DECIMAL_DIGITS_PART, val);
	}

	public double getValue() {
		return getProperty(PROP_VALUE).getPropertyValue();
	}

	public int getInternalFrameThickness() {
		return getProperty(PROP_INTERNAL_FRAME_THICKNESS).getPropertyValue();
	}

	public RGB getInternalFrameColor() {
		return getProperty(PROP_INTERNAL_FRAME_COLOR).getPropertyValue();
	}

	public double getMin() {
		return getProperty(PROP_MIN).getPropertyValue();
	}

	public double getMax() {
		return getProperty(PROP_MAX).getPropertyValue();
	}

	public void setManualValue(double val) {
		getProperty(PROP_VALUE).setManualValue(val);
	}

	public RGB getInternalBorderColor() {
		return getProperty(PROP_INTERNAL_FRAME_COLOR).getPropertyValue();
	}

	public int getInternalBorderWidth() {
		return getProperty(PROP_INTERNAL_FRAME_THICKNESS).getPropertyValue();
	}
}
