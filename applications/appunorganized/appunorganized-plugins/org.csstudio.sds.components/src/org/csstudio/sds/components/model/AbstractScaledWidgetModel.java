package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;

/**
 * This class defines a common widget model for any widget which has a scale.
 *
 * @author Xihui Chen
 */
public abstract class AbstractScaledWidgetModel extends AbstractWidgetModel {

    /** The ID of the <i>transparent</i> property. */
    public static final String PROP_TRANSPARENT = "transparency";

    /** The ID of the value property. */
    public static final String PROP_VALUE = "value"; //$NON-NLS-1$

    /** The ID of the minimum property. */
    public static final String PROP_MIN = "minimum"; //$NON-NLS-1$

    /** The ID of the maximum property. */
    public static final String PROP_MAX = "maximum"; //$NON-NLS-1$

    /** The ID of the major tick step hint property. */
    public static final String PROP_MAJOR_TICK_STEP_HINT = "majorTickStepHint"; //$NON-NLS-1$

    /** The ID of the show minor ticks property. */
    public static final String PROP_SHOW_MINOR_TICKS = "showMinorTicks"; //$NON-NLS-1$

    /** The ID of the show minor ticks property. */
    public static final String PROP_SHOW_SCALE = "showScale"; //$NON-NLS-1$

    /** The ID of the log scale property. */
    public static final String PROP_LOG_SCALE = "logScale"; //$NON-NLS-1$

    /** The default value of the value property. */
    private static final double DEFAULT_VALUE = 50;

    /** The default value of the minimum property. */
    private static final double DEFAULT_MIN = 0;

    /** The default value of the maximum property. */
    private static final double DEFAULT_MAX = 100;

    /** The default value of the major tick step hint property. */
    private static final double DEFAULT_MAJOR_TICK_STEP_HINT = 50;

    @Override
    protected void configureProperties() {

        addDoubleProperty(PROP_VALUE, "Value", WidgetPropertyCategory.DISPLAY, DEFAULT_VALUE, true, PROP_TOOLTIP);
        addBooleanProperty(PROP_TRANSPARENT, "Transparent Background", WidgetPropertyCategory.FORMAT, true, true, PROP_COLOR_BACKGROUND);


        addDoubleProperty(PROP_MAX, "Maximum", WidgetPropertyCategory.SCALE, DEFAULT_MAX, false);

        addDoubleProperty(PROP_MIN, "Minimum", WidgetPropertyCategory.SCALE, DEFAULT_MIN, false);

        addBooleanProperty(PROP_SHOW_SCALE, "Show Scale", WidgetPropertyCategory.SCALE, true, false);

        addBooleanProperty(PROP_LOG_SCALE, "Log Scale", WidgetPropertyCategory.SCALE, false, false);

        addDoubleProperty(PROP_MAJOR_TICK_STEP_HINT, "Major Tick Step Hint", WidgetPropertyCategory.SCALE, DEFAULT_MAJOR_TICK_STEP_HINT, 1,
                          1000, false);

        addBooleanProperty(PROP_SHOW_MINOR_TICKS, "Show Minor Ticks", WidgetPropertyCategory.SCALE, true, false);


    }

    /**
     * @return the value
     */
    public Double getValue() {
        return getDoubleProperty(PROP_VALUE);
    }

    /**
     * @return the minimum value
     */
    public Double getMinimum() {
        return getDoubleProperty(PROP_MIN);
    }

    /**
     * @return the maximum value
     */
    public Double getMaximum() {
        return getDoubleProperty(PROP_MAX);
    }

    /**
     * @return the major tick step hint value
     */
    public Double getMajorTickStepHint() {
        return getDoubleProperty(PROP_MAJOR_TICK_STEP_HINT);
    }

    /**
     * @return true if the minor ticks should be shown, false otherwise
     */
    public boolean isShowMinorTicks() {
        return getBooleanProperty(PROP_SHOW_MINOR_TICKS);
    }

    /**
     * @return true if the scale should be shown, false otherwise
     */
    public boolean isShowScale() {
        return getBooleanProperty(PROP_SHOW_SCALE);
    }

    /**
     * @return true if log scale enabled, false otherwise
     */
    public boolean isLogScaleEnabled() {
        return getBooleanProperty(PROP_LOG_SCALE);
    }

    /**
     * Returns, if this widget should have a transparent background.
     *
     * @return boolean True, if it should have a transparent background, false
     *         otherwise
     */
    public boolean isTransparent() {
        return getBooleanProperty(PROP_TRANSPARENT);
    }

}
