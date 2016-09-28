/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.properties.ActionsProperty;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.swt.graphics.RGB;


/**
 * The widget model for Boolean Button.
 * @author Xihui Chen
 *
 */
public class BoolButtonModel extends AbstractBoolControlModel {

    /** True if the widget is drawn with advanced graphics. In some platforms,
     * advance graphics may not be available, in which case the widget will not be drawn
     * with advanced graphics even this is set to true.*/
    public static final String PROP_EFFECT3D = "effect_3d"; //$NON-NLS-1$

    /** True if the widget is square button. */
    public static final String PROP_SQUARE_BUTTON = "square_button"; //$NON-NLS-1$

    /** True if the LED indicator is visible.*/
    public static final String PROP_SHOW_LED = "show_led"; //$NON-NLS-1$

    /** True if PV ONAM, ZNAM used instead of OnLabel and OffLabel */
    public static final String PROP_LABELS_FROM_PV = "labels_from_pv"; //$NON-NLS-1$

    /** The default value of the height property. */
    private static final int DEFAULT_HEIGHT = 50;

    /** The default value of the width property. */
    private static final int DEFAULT_WIDTH = 100;

    private static final RGB DEFAULT_FORE_COLOR = CustomMediaFactory.COLOR_BLACK;

    public BoolButtonModel() {
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setForegroundColor(DEFAULT_FORE_COLOR);
        setScaleOptions(true, true, true);
    }

    @Override
    protected void configureProperties() {
        super.configureProperties();

        addProperty(new BooleanProperty(PROP_EFFECT3D, "3D Effect",
                WidgetPropertyCategory.Display, true));

        addProperty(new BooleanProperty(PROP_SQUARE_BUTTON, "Square Button",
                WidgetPropertyCategory.Display, false));

        addProperty(new BooleanProperty(PROP_SHOW_LED, "Show LED",
                WidgetPropertyCategory.Display, true));

        addProperty(new BooleanProperty(PROP_LABELS_FROM_PV, "Labels From PV",
                WidgetPropertyCategory.Display, false));

        removeProperty(PROP_ACTIONS);
        addProperty(new ActionsProperty(PROP_ACTIONS, "Actions",
                WidgetPropertyCategory.Behavior, false));

        setPropertyVisible(PROP_BOOL_LABEL_POS, false);
    }
    /**
     * The ID of this widget model.
     */
    public static final String ID = "org.csstudio.opibuilder.widgets.BoolButton"; //$NON-NLS-1$

    @Override
    public String getTypeID() {
        return ID;
    }

    /**
     * @return true if the widget would be painted with 3D effect, false otherwise
     */
    public boolean isEffect3D() {
        return (Boolean) getProperty(PROP_EFFECT3D).getPropertyValue();
    }

    /**
     * @return true if the button is square, false otherwise
     */
    public boolean isSquareButton() {
        return (Boolean) getProperty(PROP_SQUARE_BUTTON).getPropertyValue();
    }

    /**
     * @return true if the LED should be shown, false otherwise
     */
    public boolean isShowLED() {
        return (Boolean) getProperty(PROP_SHOW_LED).getPropertyValue();
    }

    /**
     * @return true if on/off labels will be load from DB, false otherwise
     */
    public boolean isLabelsFromPV() {
        return (Boolean) getProperty(PROP_LABELS_FROM_PV).getPropertyValue();
    }

}
