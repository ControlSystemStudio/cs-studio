package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.util.ColorAndFontUtil;

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
        // Display
        addIntegerProperty(PROP_VALUE, "Value",
                           WidgetPropertyCategory.DISPLAY, 0,
                           true, PROP_TOOLTIP);
        addBooleanProperty(PROP_HORIZONTAL, "Horizontal Orientation",
                           WidgetPropertyCategory.DISPLAY, DEFAULT_ORIENTATION_HORIZONTAL,
                           false,PROP_VALUE);
        addIntegerProperty(PROP_INTERNAL_FRAME_THICKNESS, "Internal frame thickness",
                           WidgetPropertyCategory.DISPLAY, 1,
                           false,PROP_HORIZONTAL);
        // The maximum bit range that can be handled by this widget is 0..31.
        // More than 32 bits are not possible because the value property is an
        // integer property.
        addIntegerProperty(PROP_BITS_FROM, "Bit range (from)",
                           WidgetPropertyCategory.DISPLAY, 0, 0, 31,
                           false,PROP_INTERNAL_FRAME_THICKNESS);
        addIntegerProperty(PROP_BITS_TO, "Bit range (to)",
                           WidgetPropertyCategory.DISPLAY, 15, 0, 31,
                           false,PROP_BITS_FROM);

        // Format
        addBooleanProperty(PROP_SHOW_LABELS, "Show labels", WidgetPropertyCategory.FORMAT, false, true,PROP_COLOR_FOREGROUND);
        addFontProperty(PROP_LABEL_FONT, "Label fonts", WidgetPropertyCategory.FORMAT, ColorAndFontUtil.toFontString("Arial", 9), false,PROP_SHOW_LABELS);
        addColorProperty(PROP_ON_COLOR, "On color", WidgetPropertyCategory.FORMAT, "#00ff00", false,PROP_LABEL_FONT);
        addColorProperty(PROP_OFF_COLOR, "Off color", WidgetPropertyCategory.FORMAT, "#c0c0c0", false,PROP_ON_COLOR);
        addColorProperty(PROP_LABEL_COLOR, "Label text color", WidgetPropertyCategory.FORMAT, "#000000", false,PROP_OFF_COLOR);
        addColorProperty(PROP_INTERNAL_FRAME_COLOR, "Internal Frame Color", WidgetPropertyCategory.FORMAT, "#000000", false,PROP_LABEL_COLOR);

        hideProperty(AbstractWidgetModel.PROP_COLOR_FOREGROUND, getTypeID());
        hideProperty(AbstractWidgetModel.PROP_COLOR_BACKGROUND, getTypeID());
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
        return getBooleanProperty(PROP_HORIZONTAL);
    }

    public boolean getShowLabels() {
        return getBooleanProperty(PROP_SHOW_LABELS);
    }

    public int getValue() {
        return getIntegerProperty(PROP_VALUE);
    }

    public int getInternalFrameThickness() {
        return getIntegerProperty(PROP_INTERNAL_FRAME_THICKNESS);
    }

    public int getBitRangeFrom() {
        return getIntegerProperty(PROP_BITS_FROM);
    }

    public int getBitRangeTo() {
        return getIntegerProperty(PROP_BITS_TO);
    }

}
