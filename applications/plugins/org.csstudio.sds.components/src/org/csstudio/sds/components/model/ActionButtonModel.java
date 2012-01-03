/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.components.model;

import static org.csstudio.sds.model.WidgetPropertyCategory.ACTIONS;
import static org.csstudio.sds.model.WidgetPropertyCategory.DISPLAY;
import static org.csstudio.sds.model.WidgetPropertyCategory.FORMAT;

import org.csstudio.sds.model.AbstractTextTypeWidgetModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.TextAlignmentEnum;
import org.csstudio.sds.model.TextTypeEnum;
import org.csstudio.sds.util.ColorAndFontUtil;

/**
 * An action button widget model.
 *
 * @author Sven Wende
 * @version $Revision: 1.46 $
 *
 */
public class ActionButtonModel extends AbstractTextTypeWidgetModel {
    /**
     * The ID of the label property.
     */
    public static final String PROP_LABEL = "label"; //$NON-NLS-1$
    /**
     * The ID of the font property.
     */
    public static final String PROP_FONT = "font"; //$NON-NLS-1$
    /**
     * The ID of the text alignment property.
     */
    public static final String PROP_TEXT_ALIGNMENT = "textAlignment"; //$NON-NLS-1$
    /**
     * The ID of the ActionData property.
     */
    public static final String PROP_ACTION_PRESSED_INDEX = "action_pressed_index"; //$NON-NLS-1$
    /**
     * The ID of the ActionData property.
     */
    public static final String PROP_ACTION_RELEASED_INDEX = "action_released_index"; //$NON-NLS-1$
    /**
     * The ID of the ToggleButton property.
     */
    public static final String PROP_TOGGLE_BUTTON = "toggleButton"; //$NON-NLS-1$
    /**
     * The ID of the ToggleState property.
     */
    public static final String PROP_TOGGLE_STATE = "toggleState"; //$NON-NLS-1$
    /**
     * The ID of this widget model.
     */
    public static final String ID = "org.csstudio.sds.components.ActionButton"; //$NON-NLS-1$

    /**
     * The default value of the height property.
     */
    private static final int DEFAULT_HEIGHT = 20;

    /**
     * The default value of the width property.
     */
    private static final int DEFAULT_WIDTH = 80;

    /**
     * The default value of the Button style.
     */
    private static final boolean DEFAULT_TOGGLE_BUTTON = false;

    /**
     * The default value of the toggle state.
     */
    private static final boolean DEFAULT_TOGGLE_STATE = false;

    /**
     * Standard constructor.
     */
    public ActionButtonModel() {
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
        addStringProperty(PROP_LABEL, "Label Text", DISPLAY, "", true, PROP_TOOLTIP); //$NON-NLS-1$
        addBooleanProperty(PROP_TOGGLE_BUTTON,
                           "Toggle Button",
                           DISPLAY,
                           DEFAULT_TOGGLE_BUTTON,
                           false,
                           PROP_LABEL);
        addBooleanProperty(PROP_TOGGLE_STATE,
                           "Pressed",
                           DISPLAY,
                           DEFAULT_TOGGLE_STATE,
                           false,
                           PROP_TOGGLE_BUTTON);

        addArrayOptionProperty(PROP_TEXT_TYPE,
                               "Value Type",
                               DISPLAY,
                               TextTypeEnum.getDisplayNames(),
                               TextTypeEnum.DOUBLE.getIndex(),
                               false,
                               PROP_TOGGLE_STATE);
        addIntegerProperty(PROP_PRECISION,
                           "Decimal places",
                           DISPLAY,
                           2,
                           0,
                           10,
                           false,
                           PROP_TEXT_TYPE);

        // Format
        addFontProperty(PROP_FONT,
                        "Font",
                        FORMAT,
                        ColorAndFontUtil.toFontString("Arial", 8),
                        false,
                        PROP_COLOR_FOREGROUND); //$NON-NLS-1$
        addArrayOptionProperty(PROP_TEXT_ALIGNMENT,
                               "Text Alignment",
                               FORMAT,
                               TextAlignmentEnum.getDisplayNames(),
                               TextAlignmentEnum.CENTER.getIndex(),
                               false,
                               PROP_FONT);

        // Action
        addIntegerProperty(PROP_ACTION_PRESSED_INDEX,
                           "Action Index (pressed)",
                           ACTIONS,
                           -1,
                           -1,
                           Integer.MAX_VALUE,
                           false,
                           AbstractWidgetModel.PROP_ACTIONDATA);
        addIntegerProperty(PROP_ACTION_RELEASED_INDEX,
                           "Action Index (released)",
                           ACTIONS,
                           0,
                           -1,
                           Integer.MAX_VALUE,
                           false,
                           PROP_ACTION_PRESSED_INDEX);

        // .. hide properties
        hideProperty(PROP_BORDER_COLOR, getTypeID());
        hideProperty(PROP_BORDER_STYLE, getTypeID());
        hideProperty(PROP_BORDER_WIDTH, getTypeID());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDefaultToolTip() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(createTooltipParameter(PROP_ALIASES) + "\n");
        buffer.append(createTooltipParameter(PROP_ACTIONDATA) + "\n");
        buffer.append("Performed Action: ");
        buffer.append(createTooltipParameter(PROP_ACTION_PRESSED_INDEX));
        buffer.append(createTooltipParameter(PROP_ACTION_RELEASED_INDEX));
        return buffer.toString();
    }

    /**
     * Return the index of the selected WidgetAction from the ActionData. The
     * Action is running when the button is released.
     *
     * @return The index
     */
    public int getChoosenReleasedActionIndex() {
        return getIntegerProperty(PROP_ACTION_RELEASED_INDEX);
    }

    /**
     * Return the index of the selected WidgetAction from the ActionData. The
     * Action is running when the button is pressed.
     *
     * @return The index
     */
    public int getChoosenPressedActionIndex() {
        return getIntegerProperty(PROP_ACTION_PRESSED_INDEX);
    }

    /**
     * Return the label text.
     *
     * @return The label text.
     */
    public String getLabel() {
        return getStringProperty(PROP_LABEL);
    }

    /**
     * Returns the alignment for the text.
     *
     * @return int 0 = Center, 1 = Top, 2 = Bottom, 3 = Left, 4 = Right
     */
    public int getTextAlignment() {
        return getArrayOptionProperty(PROP_TEXT_ALIGNMENT);
    }

    /**
     * Returns whether the button is a toggle button.
     *
     * @return false = Push, true=Toggle
     */
    public boolean isToggleButton() {
        return getBooleanProperty(PROP_TOGGLE_BUTTON);
    }

    /**
     * Returns whether the button is a toggle button AND pressed.
     *
     * @return boolean
     */
    public boolean isPressed() {
        return isToggleButton() && getBooleanProperty(PROP_TOGGLE_STATE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringValueID() {
        return PROP_LABEL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getTransparent() {
        return false;
    }
}
