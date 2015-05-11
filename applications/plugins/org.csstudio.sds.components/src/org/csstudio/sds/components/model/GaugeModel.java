package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;

/**
 * This class defines a gauge widget model.
 *
 * @author Xihui Chen
 */
public class GaugeModel extends AbstractMarkedWidgetModel {

    /** The ID of the fill color property. */
    public static final String PROP_NEEDLE_COLOR = "needle_color"; //$NON-NLS-1$

    /** The ID of the effect 3D property. */
    public static final String PROP_EFFECT3D = "effect3D"; //$NON-NLS-1$

    /** The ID of the Ramp Gradient. */
    public static final String PROP_RAMP_GRADIENT = "ramp_gradient"; //$NON-NLS-1$

    /** The default value of the default fill color property. */
    private static final String DEFAULT_NEEDLE_COLOR = "#ff0000";

    /** The default value of the height property. */
    private static final int DEFAULT_HEIGHT = 200;

    /** The default value of the width property. */
    private static final int DEFAULT_WIDTH = 200;

    /**
     * The ID of this widget model.
     */
    public static final String ID = "org.csstudio.sds.components.Gauge"; //$NON-NLS-1$

    public GaugeModel() {
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND, "#000000");
    }

    @Override
    protected void configureProperties() {
        super.configureProperties();
        addColorProperty(PROP_NEEDLE_COLOR, "Needle Color", WidgetPropertyCategory.DISPLAY, DEFAULT_NEEDLE_COLOR, false);

        addBooleanProperty(PROP_EFFECT3D, "3D Effect", WidgetPropertyCategory.DISPLAY, true, false);

        addBooleanProperty(PROP_RAMP_GRADIENT, "Ramp Gradient", WidgetPropertyCategory.DISPLAY, true, false);

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
     * @return true if the ramp is gradient, false otherwise
     */
    public boolean isRampGradient() {
        return getBooleanProperty(PROP_RAMP_GRADIENT);
    }

}
