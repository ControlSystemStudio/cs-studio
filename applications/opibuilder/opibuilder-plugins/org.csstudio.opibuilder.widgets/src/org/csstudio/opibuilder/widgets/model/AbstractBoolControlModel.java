/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.swt.widgets.figures.AbstractBoolControlFigure.ShowConfirmDialog;

/**
 * Abstract model for boolean controls.
 * @author Xihui Chen
 *
 */
public class AbstractBoolControlModel extends AbstractBoolWidgetModel {


    /**
     * If the button should be a toggle button.
     */
    public static final String PROP_TOGGLE_BUTTON= "toggle_button"; //$NON-NLS-1$

    /** If a confirm dialog should be shown before performing action. */
    public static final String PROP_CONFIRM_DIALOG = "show_confirm_dialog"; //$NON-NLS-1$

    /** The password needed to perform action*/
    public static final String PROP_PASSWORD = "password"; //$NON-NLS-1$

    /** The message which will be shown on confirm dialog. */
    public static final String PROP_CONFIRM_TIP = "confirm_message"; //$NON-NLS-1$


    public static final String DEFAULT_CONFIRM_TIP = "Are you sure you want to do this?";

    /**
     * The action which will be executed when widget is pushed. It is the index the actions in
     * actions property.
     */
    public static final String PROP_PUSH_ACTION_INDEX = "push_action_index"; //$NON-NLS-1$


    /**
     * The action which will be executed when widget is released. It is the index the action in
     * actions property.
     */
    public static final String PROP_RELEASED_ACTION_INDEX = "released_action_index"; //$NON-NLS-1$

    private static final boolean DEFAULT_TOGGLE_BUTTON = true;

    @Override
    protected void configureProperties() {
        super.configureProperties();
        addProperty(new BooleanProperty(PROP_TOGGLE_BUTTON, "Toggle Button",
                WidgetPropertyCategory.Behavior, DEFAULT_TOGGLE_BUTTON));
        addProperty(new ComboProperty(PROP_CONFIRM_DIALOG, "Show Confirm Dialog",
                WidgetPropertyCategory.Behavior, ShowConfirmDialog.stringValues(), 0));
        addProperty(new StringProperty(PROP_PASSWORD, "Password",
                WidgetPropertyCategory.Behavior, ""));
        addProperty(new StringProperty(PROP_CONFIRM_TIP, "Confirm Message",
                WidgetPropertyCategory.Behavior, DEFAULT_CONFIRM_TIP));

        addProperty(new IntegerProperty(PROP_PUSH_ACTION_INDEX, "Push Action Index",
                WidgetPropertyCategory.Behavior, 0, 0, Integer.MAX_VALUE));

        addProperty(new IntegerProperty(PROP_RELEASED_ACTION_INDEX, "Release Action Index",
                WidgetPropertyCategory.Behavior, 0, 0, Integer.MAX_VALUE));
    }
    /**
     * The ID of this widget model.
     */
    public static final String ID = "org.csstudio.sns.widgets.LED"; //$NON-NLS-1$

    @Override
    public String getTypeID() {
        return ID;
    }

    /**
     * @return true if the button is toggle.
     */
    public boolean isToggleButton() {
        return (Boolean) getProperty(PROP_TOGGLE_BUTTON).getPropertyValue();
    }

    /**
     * @return true if the confirm dialog should be shown, false otherwise
     */
    public ShowConfirmDialog getShowConfirmDialog() {
        return ShowConfirmDialog.values()[(Integer)getPropertyValue(PROP_CONFIRM_DIALOG)];
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return (String) getProperty(PROP_PASSWORD).getPropertyValue();
    }

    /**
     * @return the confirm tip
     */
    public String getConfirmTip() {
        return (String) getProperty(PROP_CONFIRM_TIP).getPropertyValue();
    }

    /**
     * Return the index of the selected WidgetAction from the ActionData.
     * The Action is running when the button is released.
     * @return The index
     */
    public int getPushActionIndex() {
        return (Integer) getProperty(PROP_PUSH_ACTION_INDEX).getPropertyValue();
    }


    /**
     * Return the index of the selected WidgetAction from the ActionData.
     * The Action is running when the button is released.
     * @return The index
     */
    public int getReleasedActionIndex() {
        return (Integer) getProperty(PROP_RELEASED_ACTION_INDEX).getPropertyValue();
    }
}
