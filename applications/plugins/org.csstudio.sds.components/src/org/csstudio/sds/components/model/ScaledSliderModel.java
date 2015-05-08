package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;

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
        setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND,"#000000");
    }

    @Override
    protected void configureProperties() {
        super.configureProperties();
        addColorProperty(PROP_FILL_COLOR, "Fill Color", WidgetPropertyCategory.DISPLAY, DEFAULT_FILL_COLOR, false);

        addBooleanProperty(PROP_EFFECT3D, "3D Effect", WidgetPropertyCategory.DISPLAY, true, false);

        addBooleanProperty(PROP_HORIZONTAL, "Horizontal", WidgetPropertyCategory.DISPLAY, false, false);

        addColorProperty(PROP_FILLBACKGROUND_COLOR, "Color Fillbackground", WidgetPropertyCategory.DISPLAY,
                DEFAULT_FILLBACKGROUND_COLOR, false);

        addColorProperty(PROP_THUMB_COLOR, "Thumb Color", WidgetPropertyCategory.DISPLAY, DEFAULT_THUMB_COLOR, false);

        addDoubleProperty(PROP_INCREMENT, "Increment", WidgetPropertyCategory.BEHAVIOR, 1.0, false);

        setColor(PROP_LO_COLOR, "#FF8000");
        setColor(PROP_HI_COLOR, "#FF8000");

    }

    @Override
    public String getTypeID() {
        return ID;
    }

    /**
     * @return true if the widget would be painted with 3D effect, false
     *         otherwise
     */
    public boolean isEffect3D() {
        return getBooleanProperty(PROP_EFFECT3D);
    }

    /**
     * @return true if the widget is in horizontal orientation, false otherwise
     */
    public boolean isHorizontal() {
        return getBooleanProperty(PROP_HORIZONTAL);
    }

    /**
     * Return the increment value.
     *
     * @return The increment value.
     */
    public double getIncrement() {
        return getDoubleProperty(PROP_INCREMENT);
    }
}
