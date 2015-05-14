package org.csstudio.sds.components.model;

import org.csstudio.sds.model.WidgetPropertyCategory;

/**
 * This class defines a common widget model for any widget which has one scale
 * and standard markers. Standard markers are comprised of LOLO, LO, HI, HIHI.
 *
 * @author Xihui Chen
 */
public abstract class AbstractMarkedWidgetModel extends AbstractScaledWidgetModel {

    /** The ID of the show minor ticks property. */
    public static final String PROP_SHOW_MARKERS = "showMarkers"; //$NON-NLS-1$

    /** The ID of the lolo level property. */
    public static final String PROP_LOLO_LEVEL = "loloLevel"; //$NON-NLS-1$

    /** The ID of the lo level property. */
    public static final String PROP_LO_LEVEL = "loLevel"; //$NON-NLS-1$

    /** The ID of the hi level property. */
    public static final String PROP_HI_LEVEL = "hiLevel"; //$NON-NLS-1$

    /** The ID of the hihi level property. */
    public static final String PROP_HIHI_LEVEL = "hihiLevel"; //$NON-NLS-1$

    /** The ID of the show lolo property. */
    public static final String PROP_SHOW_LOLO = "showLOLO"; //$NON-NLS-1$

    /** The ID of the show lo property. */
    public static final String PROP_SHOW_LO = "showLO"; //$NON-NLS-1$

    /** The ID of the show hi property. */
    public static final String PROP_SHOW_HI = "showHI"; //$NON-NLS-1$

    /** The ID of the show hihi property. */
    public static final String PROP_SHOW_HIHI = "showHIHI"; //$NON-NLS-1$

    /** The ID of the lolo color property. */
    public static final String PROP_LOLO_COLOR = "loloColor"; //$NON-NLS-1$

    /** The ID of the lo color property. */
    public static final String PROP_LO_COLOR = "loColor"; //$NON-NLS-1$

    /** The ID of the hi color property. */
    public static final String PROP_HI_COLOR = "hiColor"; //$NON-NLS-1$

    /** The ID of the hihi color property. */
    public static final String PROP_HIHI_COLOR = "hihiColor"; //$NON-NLS-1$

    /** The default value of the levels property. */
    private static final double[] DEFAULT_LEVELS = new double[] { 10, 20, 80, 90 };

    /** The default color of the lolo color property. */
    private static final String DEFAULT_LOLO_COLOR = "#ff0000";
    /** The default color of the lo color property. */
    private static final String DEFAULT_LO_COLOR = "#ffff00";
    /** The default color of the hi color property. */
    private static final String DEFAULT_HI_COLOR = "#ffff00";
    /** The default color of the hihi color property. */
    private static final String DEFAULT_HIHI_COLOR = "#ff0000";

    @Override
    protected void configureProperties() {

        super.configureProperties();
        addBooleanProperty(PROP_SHOW_MARKERS, "Show Markers", WidgetPropertyCategory.SCALE, true, false);

        addDoubleProperty(PROP_LOLO_LEVEL, "Level LOLO", WidgetPropertyCategory.SCALE, DEFAULT_LEVELS[0], false);
        addBooleanProperty(PROP_SHOW_LOLO, "Show LOLO", WidgetPropertyCategory.SCALE, true, false);
        addColorProperty(PROP_LOLO_COLOR, "Color LOLO ", WidgetPropertyCategory.SCALE, DEFAULT_LOLO_COLOR, false);

        addDoubleProperty(PROP_LO_LEVEL, "Level LO", WidgetPropertyCategory.SCALE, DEFAULT_LEVELS[1], false);
        addBooleanProperty(PROP_SHOW_LO, "Show LO", WidgetPropertyCategory.SCALE, true, false);
        addColorProperty(PROP_LO_COLOR, "Color LO", WidgetPropertyCategory.SCALE, DEFAULT_LO_COLOR, false);

        addDoubleProperty(PROP_HI_LEVEL, "Level HI", WidgetPropertyCategory.SCALE, DEFAULT_LEVELS[2], false);
        addBooleanProperty(PROP_SHOW_HI, "Show HI", WidgetPropertyCategory.SCALE, true, false);
        addColorProperty(PROP_HI_COLOR, "Color HI", WidgetPropertyCategory.SCALE, DEFAULT_HI_COLOR, false);

        addDoubleProperty(PROP_HIHI_LEVEL, "Level HIHI", WidgetPropertyCategory.SCALE, DEFAULT_LEVELS[3], false);
        addBooleanProperty(PROP_SHOW_HIHI, "Show HIHI", WidgetPropertyCategory.SCALE, true, false);
        addColorProperty(PROP_HIHI_COLOR, "Color HIHI", WidgetPropertyCategory.SCALE, DEFAULT_HIHI_COLOR, false);
    }

    /**
     * Gets the lolo level for this model.
     *
     * @return double The lolo level
     */
    public double getLoloLevel() {
        return getDoubleProperty(PROP_LOLO_LEVEL);
    }

    /**
     * Gets the lo level for this model.
     *
     * @return double The lo level
     */
    public double getLoLevel() {
        return getDoubleProperty(PROP_LO_LEVEL);
    }

    /**
     * Gets the hi level for this model.
     *
     * @return double The hi level
     */
    public double getHiLevel() {
        return getDoubleProperty(PROP_HI_LEVEL);
    }

    /**
     * Gets the hihi level of this model.
     *
     * @return double The hihi level
     */
    public double getHihiLevel() {
        return getDoubleProperty(PROP_HIHI_LEVEL);
    }

    /**
     * @return true if the minor ticks should be shown, false otherwise
     */
    public boolean isShowMarkers() {
        return getBooleanProperty(PROP_SHOW_MARKERS);
    }

    /**
     * @return true if the lolo marker should be shown, false otherwise
     */
    public boolean isShowLolo() {
        return getBooleanProperty(PROP_SHOW_LOLO);
    }

    /**
     * @return true if the lo marker should be shown, false otherwise
     */
    public boolean isShowLo() {
        return getBooleanProperty(PROP_SHOW_LO);
    }

    /**
     * @return true if the hi marker should be shown, false otherwise
     */
    public boolean isShowHi() {
        return getBooleanProperty(PROP_SHOW_HI);
    }

    /**
     * @return true if the hihi marker should be shown, false otherwise
     */
    public boolean isShowHihi() {
        return getBooleanProperty(PROP_SHOW_HIHI);
    }

}
