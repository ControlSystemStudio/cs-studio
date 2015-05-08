package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;

/**
 * This class defines a knob widget model.
 *
 * @author Xihui Chen
 */
public class KnobModel extends AbstractMarkedWidgetModel {

    /** The ID of the knob color property. */
    public static final String PROP_KNOB_COLOR = "bulb_color"; //$NON-NLS-1$

    /** The ID of the effect 3D property. */
    public static final String PROP_EFFECT3D = "effect3D"; //$NON-NLS-1$

    /** The ID of the effect show value label property. */
    public static final String PROP_SHOW_VALUE_LABEL = "show_value_label"; //$NON-NLS-1$

    /** The ID of the thumb Color property. */
    public static final String PROP_THUMB_COLOR = "thumbColor"; //$NON-NLS-1$

    /** The ID of the Ramp Gradient. */
    public static final String PROP_RAMP_GRADIENT = "ramp_gradient"; //$NON-NLS-1$

    /**
     * The ID of the increment property.
     */
    public static final String PROP_INCREMENT = "increment"; //$NON-NLS-1$

    /** The default value of the default knob color property. */
    private static final String DEFAULT_KNOB_COLOR = "#969669";

    /** The default value of the height property. */
    private static final int DEFAULT_HEIGHT = 173;

    /** The default value of the width property. */
    private static final int DEFAULT_WIDTH = 173;

    /** The default value of the thumb color property. */
    private static final String DEFAULT_THUMB_COLOR = "#7F7F7F";
    /**
     * The ID of this widget model.
     */
    public static final String ID = "org.csstudio.sds.components.Knob"; //$NON-NLS-1$

    public KnobModel() {
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND, "#000000");
    }

    @Override
    protected void configureProperties() {
        super.configureProperties();
        addColorProperty(PROP_KNOB_COLOR, "Knob Color", WidgetPropertyCategory.DISPLAY, DEFAULT_KNOB_COLOR, false);

        addBooleanProperty(PROP_EFFECT3D, "3D Effect", WidgetPropertyCategory.DISPLAY, true, false);

        addBooleanProperty(PROP_SHOW_VALUE_LABEL, "Show Value Label", WidgetPropertyCategory.DISPLAY, true, false);

        addColorProperty(PROP_THUMB_COLOR, "Thumb Color", WidgetPropertyCategory.DISPLAY, DEFAULT_THUMB_COLOR, false);

        addBooleanProperty(PROP_RAMP_GRADIENT, "Ramp Gradient", WidgetPropertyCategory.DISPLAY, true, false);

        addDoubleProperty(PROP_INCREMENT, "Increment", WidgetPropertyCategory.BEHAVIOR, 1.0, false);

        setPropertyDescription(PROP_SHOW_MARKERS, "Show Ramp");
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
     * @return true if the widget would be painted with 3D effect, false
     *         otherwise
     */
    public boolean isShowValueLabel() {
        return getBooleanProperty(PROP_SHOW_VALUE_LABEL);
    }

    /**
     * @return true if the ramp is gradient, false otherwise
     */
    public boolean isRampGradient() {
        return getBooleanProperty(PROP_RAMP_GRADIENT);
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
