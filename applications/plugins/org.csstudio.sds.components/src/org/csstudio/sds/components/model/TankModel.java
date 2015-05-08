package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;

/**
 * This class defines a tank widget model.
 *
 * @author Xihui Chen
 */
public class TankModel extends AbstractMarkedWidgetModel {

    /** The ID of the fill color property. */
    public static final String PROP_FILL_COLOR = "fill_color"; //$NON-NLS-1$

    /** The ID of the show minor ticks property. */
    public static final String PROP_EFFECT3D = "effect3D"; //$NON-NLS-1$

    /**
     * The ID of the fillbackground-Color property.
     */
    public static final String PROP_FILLBACKGROUND_COLOR = "fillbackgroundColor";

    /** The default value of the default fill color property. */
    private static final String DEFAULT_FILL_COLOR = "#0000ff";

    /** The default value of the height property. */
    private static final int DEFAULT_HEIGHT = 200;

    /** The default value of the width property. */
    private static final int DEFAULT_WIDTH = 100;

    /**
     * The default value of the fillbackground color property.
     */
    private static final String DEFAULT_FILLBACKGROUND_COLOR = "#C0C0C0";

    /**
     * The ID of this widget model.
     */
    public static final String ID = "org.csstudio.sds.components.Tank"; //$NON-NLS-1$

    public TankModel() {
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND, "#000000");
    }

    @Override
    protected void configureProperties() {
        super.configureProperties();
        addColorProperty(PROP_FILL_COLOR, "Fill Color", WidgetPropertyCategory.DISPLAY, DEFAULT_FILL_COLOR, false);

        addBooleanProperty(PROP_EFFECT3D, "3D Effect", WidgetPropertyCategory.DISPLAY, true, false);

        addColorProperty(PROP_FILLBACKGROUND_COLOR, "Color Fillbackground", WidgetPropertyCategory.DISPLAY, DEFAULT_FILLBACKGROUND_COLOR, false);

        setColor(PROP_LO_COLOR, "#FF8000");
        setColor(PROP_HI_COLOR, "#FF8000");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDefaultToolTip() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(createTooltipParameter(PROP_ALIASES) + "\n");
        buffer.append("Value:\t");
        buffer.append(createTooltipParameter(PROP_VALUE) + "\n");
        return buffer.toString();
    }

    @Override
    public String getTypeID() {
        return ID;
    }

    /**
     * @return true if the bulb should be shown, false otherwise
     */
    public boolean isEffect3D() {
        return getBooleanProperty(PROP_EFFECT3D);
    }
}
