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
 * This class defines a knob widget model.
 * @author Xihui Chen
 */
public class KnobModel extends AbstractMarkedWidgetModel{

    /** Color of the knob. */
    public static final String PROP_KNOB_COLOR = "knob_color"; //$NON-NLS-1$

    /** True if the widget is drawn with advanced graphics. In some platforms,
     * advance graphics may not be available, in which case the widget will not be drawn
     * with advanced graphics even this is set to true.*/
    public static final String PROP_EFFECT3D = "effect_3d"; //$NON-NLS-1$


    /**Show value label. */
    public static final String PROP_SHOW_VALUE_LABEL = "show_value_label"; //$NON-NLS-1$

    /** Color of the thumb. */
    public static final String PROP_THUMB_COLOR = "thumb_color"; //$NON-NLS-1$

    /** True if ramp is gradient. */
    public static final String PROP_RAMP_GRADIENT = "ramp_gradient"; //$NON-NLS-1$

    /**
     *The minimum increment step when dragging the thumb.
     */
    public static final String PROP_INCREMENT = "increment"; //$NON-NLS-1$



    /** The default value of the default knob color property. */
    private static final RGB DEFAULT_KNOB_COLOR = new RGB(150,150,150);

    /** The default value of the height property. */
    private static final int DEFAULT_HEIGHT = 173;

    /** The default value of the width property. */
    private static final int DEFAULT_WIDTH = 173;

    /** The default value of the thumb color property. */
    private static final RGB DEFAULT_THUMB_COLOR = new RGB(127, 127, 127);

    public static final int MINIMUM_SIZE = 100;


    /**
     * The ID of this widget model.
     */
    public static final String ID = "org.csstudio.opibuilder.widgets.knob"; //$NON-NLS-1$

    public KnobModel() {
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setForegroundColor(new RGB(0,0,0));
        setScaleOptions(true, true, true);
        isControlWidget = true;
    }

    @Override
    protected void configureProperties() {
        super.configureProperties();

//        addPVProperty(new StringProperty(PROP_CONTROL_PV, "Control PV", WidgetPropertyCategory.Basic,
//                ""), new PVValueProperty(PROP_CONTROL_PV_VALUE, null));
//
        addProperty(new ColorProperty(PROP_KNOB_COLOR, "Knob Color",
                WidgetPropertyCategory.Display, DEFAULT_KNOB_COLOR));

        addProperty(new BooleanProperty(PROP_EFFECT3D, "3D Effect",
                WidgetPropertyCategory.Display, true));

        addProperty(new BooleanProperty(PROP_SHOW_VALUE_LABEL, "Show Value Label",
                WidgetPropertyCategory.Display, true));

        addProperty(new ColorProperty(PROP_THUMB_COLOR, "Thumb Color",
                WidgetPropertyCategory.Display, DEFAULT_THUMB_COLOR));

        addProperty(new BooleanProperty(PROP_RAMP_GRADIENT, "Ramp Gradient",
                WidgetPropertyCategory.Display, true));

        addProperty(new DoubleProperty(PROP_INCREMENT, "Increment",
                WidgetPropertyCategory.Behavior, 1.0));

        setPropertyDescription(PROP_SHOW_MARKERS, "Show Ramp");
//        setPropertyDescription(PROP_PVNAME, "Readback PV");
    }

    @Override
    public String getTypeID() {
        return ID;
    }

    /**
     * @return the knob color
     */
    public Color getKnobColor() {
        return getSWTColorFromColorProperty(PROP_KNOB_COLOR);
    }

    /**
     * @return true if the widget would be painted with 3D effect, false otherwise
     */
    public boolean isEffect3D() {
        return (Boolean) getProperty(PROP_EFFECT3D).getPropertyValue();
    }

    /**
     * @return true if the widget would be painted with 3D effect, false otherwise
     */
    public boolean isShowValueLabel() {
        return (Boolean) getProperty(PROP_SHOW_VALUE_LABEL).getPropertyValue();
    }

    /**
     * Gets the RGB for thumb.
     * @return The thumb color
     */
    public Color getThumbColor() {
        return getSWTColorFromColorProperty(PROP_THUMB_COLOR);
    }

    /**
     * @return true if the ramp is gradient, false otherwise
     */
    public boolean isRampGradient() {
        return (Boolean) getProperty(PROP_RAMP_GRADIENT).getPropertyValue();
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
