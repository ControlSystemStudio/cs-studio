package org.csstudio.sds.components.model;

import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.BooleanProperty;
import org.csstudio.sds.model.properties.ColorProperty;
import org.csstudio.sds.model.properties.DoubleProperty;

/**
 * This class defines a scaled slider widget model.
 * 
 * @author Xihui Chen
 */
public class ScaledSliderModel extends AbstractMarkedWidgetModel {

	/** The ID of the fill color property. */
	public static final String PROP_FILL_COLOR = "fill_color"; //$NON-NLS-1$	

	/** The ID of the effect 3D property. */
	public static final String PROP_EFFECT3D = "effect3D"; //$NON-NLS-1$

	/** The ID of the horizontal property. */
	public static final String PROP_HORIZONTAL = "horizontal"; //$NON-NLS-1$

	/** The ID of the fillbackground-Color property. */
	public static final String PROP_FILLBACKGROUND_COLOR = "fillbackgroundColor"; //$NON-NLS-1$

	/** The ID of the thumb Color property. */
	public static final String PROP_THUMB_COLOR = "thumbColor"; //$NON-NLS-1$

	/**
	 * The ID of the increment property.
	 */
	public static final String PROP_INCREMENT = "increment"; //$NON-NLS-1$

	/** The default value of the default fill color property. */
	private static final String DEFAULT_FILL_COLOR = "#0000ff";

	/** The default value of the height property. */
	private static final int DEFAULT_HEIGHT = 200;

	/** The default value of the width property. */
	private static final int DEFAULT_WIDTH = 100;

	/** The default value of the fillbackground color property. */
	private static final String DEFAULT_FILLBACKGROUND_COLOR = "#C8C8C8";

	/** The default value of the thumb color property. */
	private static final String DEFAULT_THUMB_COLOR = "#ACACAC";
	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.sds.components.ScaledSlider"; //$NON-NLS-1$	

	public ScaledSliderModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setForegroundColor("#000000");
	}

	@Override
	protected void configureProperties() {
		super.configureProperties();
		addProperty(PROP_FILL_COLOR, new ColorProperty("Fill Color", WidgetPropertyCategory.Display, DEFAULT_FILL_COLOR));

		addProperty(PROP_EFFECT3D, new BooleanProperty("3D Effect", WidgetPropertyCategory.Display, true));

		addProperty(PROP_HORIZONTAL, new BooleanProperty("Horizontal", WidgetPropertyCategory.Display, false));

		addProperty(PROP_FILLBACKGROUND_COLOR, new ColorProperty("Color Fillbackground", WidgetPropertyCategory.Display,
				DEFAULT_FILLBACKGROUND_COLOR));

		addProperty(PROP_THUMB_COLOR, new ColorProperty("Thumb Color", WidgetPropertyCategory.Display, DEFAULT_THUMB_COLOR));

		addProperty(PROP_INCREMENT, new DoubleProperty("Increment", WidgetPropertyCategory.Behaviour, 1.0));

		setPropertyValue(PROP_LO_COLOR, "#FF8000");
		setPropertyValue(PROP_HI_COLOR, "#FF8000");

	}

	@Override
	public String getTypeID() {
		return ID;
	}

	/**
	 * @return the fill color
	 */
	public ColorProperty getFillColor() {
		return (ColorProperty) getProperty(PROP_FILL_COLOR);
	}

	/**
	 * @return true if the widget would be painted with 3D effect, false
	 *         otherwise
	 */
	public boolean isEffect3D() {
		return (Boolean) getProperty(PROP_EFFECT3D).getPropertyValue();
	}

	/**
	 * @return true if the widget is in horizontal orientation, false otherwise
	 */
	public boolean isHorizontal() {
		return (Boolean) getProperty(PROP_HORIZONTAL).getPropertyValue();
	}

	/**
	 * Gets the RGB for fillbackground.
	 * 
	 * @return The fillbackground color
	 */
	public ColorProperty getFillbackgroundColor() {
		return (ColorProperty) getProperty(PROP_FILLBACKGROUND_COLOR);
	}

	/**
	 * Gets the RGB for thumb.
	 * 
	 * @return The thumb color
	 */
	public ColorProperty getThumbColor() {
		return (ColorProperty) getProperty(PROP_THUMB_COLOR);
	}

	/**
	 * Return the increment value.
	 * 
	 * @return The increment value.
	 */
	public double getIncrement() {
		return (Double) getProperty(PROP_INCREMENT).getPropertyValue();
	}
}
