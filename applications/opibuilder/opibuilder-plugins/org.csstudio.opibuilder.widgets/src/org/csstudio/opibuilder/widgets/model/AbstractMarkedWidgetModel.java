/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;


/**
 * This class defines a common widget model for any widget
 * which has one scale and standard markers.
 * Standard markers are comprised of LOLO, LO, HI, HIHI.
 * @author Xihui Chen
 */
public abstract class AbstractMarkedWidgetModel extends AbstractScaledWidgetModel {

    /** Show alarm markers(or ramp for round scale widgets).*/
    public static final String PROP_SHOW_MARKERS = "show_markers"; //$NON-NLS-1$

    /** Low low level.*/
    public static final String PROP_LOLO_LEVEL = "level_lolo"; //$NON-NLS-1$

    /** Low level. */
    public static final String PROP_LO_LEVEL = "level_lo"; //$NON-NLS-1$

    /** High Level. */
    public static final String PROP_HI_LEVEL = "level_hi"; //$NON-NLS-1$

    /** High High Level */
    public static final String PROP_HIHI_LEVEL = "level_hihi"; //$NON-NLS-1$


    /** Show low low marker.*/
    public static final String PROP_SHOW_LOLO = "show_lolo"; //$NON-NLS-1$

    /** Show low marker. */
    public static final String PROP_SHOW_LO = "show_lo"; //$NON-NLS-1$

    /** Show high marker. */
    public static final String PROP_SHOW_HI = "show_hi"; //$NON-NLS-1$

    /** Show high high marker. */
    public static final String PROP_SHOW_HIHI = "show_hihi"; //$NON-NLS-1$

    /** Color of low low marker. */
    public static final String PROP_LOLO_COLOR = "color_lolo"; //$NON-NLS-1$

    /** Color of low marker. */
    public static final String PROP_LO_COLOR = "color_lo"; //$NON-NLS-1$

    /** Color of high marker. */
    public static final String PROP_HI_COLOR = "color_hi"; //$NON-NLS-1$

    /** Color of high high marker. */
    public static final String PROP_HIHI_COLOR = "color_hihi"; //$NON-NLS-1$

    /** Load the Maximum, Minimum, HIHI, HI, LOLO, LO limits from PV. */
    public static final String PROP_LIMITS_FROM_PV = "limits_from_pv"; //$NON-NLS-1$

    /** The default value of the levels property. */
    private static final double[] DEFAULT_LEVELS = new double[]{10, 20, 80, 90};

    /** The default color of the lolo color property. */
    private static final RGB DEFAULT_LOLO_COLOR = new RGB(255,0,0);
    /** The default color of the lo color property. */
    private static final RGB DEFAULT_LO_COLOR = new RGB(255, 255 ,0);
    /** The default color of the hi color property. */
    private static final RGB DEFAULT_HI_COLOR = new RGB(255, 255,0);
    /** The default color of the hihi color property. */
    private static final RGB DEFAULT_HIHI_COLOR = new RGB(255,0,0);

    protected boolean isControlWidget = false;

    @Override
    protected void configureProperties() {

        super.configureProperties();
        addProperty(new BooleanProperty(PROP_SHOW_MARKERS, "Show Markers",
                WidgetPropertyCategory.Display, true));

        addProperty(new DoubleProperty(PROP_LOLO_LEVEL, "Level LOLO",
                WidgetPropertyCategory.Behavior, DEFAULT_LEVELS[0]));
        addProperty(new DoubleProperty(PROP_LO_LEVEL, "Level LO",
                WidgetPropertyCategory.Behavior, DEFAULT_LEVELS[1]));
        addProperty(new DoubleProperty(PROP_HI_LEVEL, "Level HI",
                WidgetPropertyCategory.Behavior, DEFAULT_LEVELS[2]));
        addProperty(new DoubleProperty(PROP_HIHI_LEVEL, "Level HIHI",
                WidgetPropertyCategory.Behavior, DEFAULT_LEVELS[3]));

        addProperty(new BooleanProperty(PROP_SHOW_LOLO, "Show LOLO",
                WidgetPropertyCategory.Display, true));
        addProperty(new BooleanProperty(PROP_SHOW_LO, "Show LO",
                WidgetPropertyCategory.Display, true));
        addProperty(new BooleanProperty(PROP_SHOW_HI, "Show HI",
                WidgetPropertyCategory.Display, true));
        addProperty(new BooleanProperty(PROP_SHOW_HIHI, "Show HIHI",
                WidgetPropertyCategory.Display, true));

        addProperty(new ColorProperty(PROP_LOLO_COLOR, "Color LOLO ",
                WidgetPropertyCategory.Display, DEFAULT_LOLO_COLOR));
        addProperty(new ColorProperty(PROP_LO_COLOR, "Color LO",
                WidgetPropertyCategory.Display, DEFAULT_LO_COLOR));
        addProperty(new ColorProperty(PROP_HI_COLOR, "Color HI",
                WidgetPropertyCategory.Display, DEFAULT_HI_COLOR));
        addProperty(new ColorProperty(PROP_HIHI_COLOR, "Color HIHI",
                WidgetPropertyCategory.Display, DEFAULT_HIHI_COLOR));
        addProperty(new BooleanProperty(PROP_LIMITS_FROM_PV, "Limits From PV",
                WidgetPropertyCategory.Behavior, true));


    }

    /**
     * Gets the lolo level for this model.
     * @return double
     *                 The lolo level
     */
    public double getLoloLevel() {
        return (Double) getProperty(PROP_LOLO_LEVEL).getPropertyValue();
    }

    /**
     * Gets the lo level for this model.
     * @return double
     *                 The lo level
     */
    public double getLoLevel() {
        return (Double) getProperty(PROP_LO_LEVEL).getPropertyValue();
    }

    /**
     * Gets the hi level for this model.
     * @return double
     *                 The hi level
     */
    public double getHiLevel() {
        return (Double) getProperty(PROP_HI_LEVEL).getPropertyValue();
    }

    /**
     * Gets the hihi level of this model.
     * @return double
     *                 The hihi level
     */
    public double getHihiLevel() {
        return (Double) getProperty(PROP_HIHI_LEVEL).getPropertyValue();
    }


    /**
     * @return the lolo color
     */
    public Color getLoloColor() {
        return getSWTColorFromColorProperty(PROP_LOLO_COLOR);
    }

    /**
     * @return the lo color
     */
    public Color getLoColor() {
        return getSWTColorFromColorProperty(PROP_LO_COLOR);
    }
    /**
     * @return the hi color
     */
    public Color getHiColor() {
        return getSWTColorFromColorProperty(PROP_HI_COLOR);
    }
    /**
     * @return the hihi color
     */
    public Color getHihiColor() {
        return getSWTColorFromColorProperty(PROP_HIHI_COLOR);
    }





    /**
     * @return true if the minor ticks should be shown, false otherwise
     */
    public boolean isShowMarkers() {
        return (Boolean) getProperty(PROP_SHOW_MARKERS).getPropertyValue();
    }

    /**
     * @return true if the lolo marker should be shown, false otherwise
     */
    public boolean isShowLolo() {
        return (Boolean) getProperty(PROP_SHOW_LOLO).getPropertyValue();
    }

    /**
     * @return true if the lo marker should be shown, false otherwise
     */
    public boolean isShowLo() {
        return (Boolean) getProperty(PROP_SHOW_LO).getPropertyValue();
    }

    /**
     * @return true if the hi marker should be shown, false otherwise
     */
    public boolean isShowHi() {
        return (Boolean) getProperty(PROP_SHOW_HI).getPropertyValue();
    }

    /**
     * @return true if the hihi marker should be shown, false otherwise
     */
    public boolean isShowHihi() {
        return (Boolean) getProperty(PROP_SHOW_HIHI).getPropertyValue();
    }

    /**
     * @return true if limits will be load from DB, false otherwise
     */
    public boolean isLimitsFromPV() {
        return (Boolean) getProperty(PROP_LIMITS_FROM_PV).getPropertyValue();
    }

    /**
     * @return true if this widget is a control widget, i.e. used to SET PV values
     */
    public boolean isControlWidget() {
        return isControlWidget;
    }
}
