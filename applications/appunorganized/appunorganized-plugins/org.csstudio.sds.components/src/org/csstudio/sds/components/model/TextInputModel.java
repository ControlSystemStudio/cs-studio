/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, Member of the Helmholtz Association,
 * (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT,
 * THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF
 * WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED
 * HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
 * UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE
 * REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractTextTypeWidgetModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.CursorStyleEnum;
import org.csstudio.sds.model.TextAlignmentEnum;
import org.csstudio.sds.model.TextTypeEnum;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.util.ColorAndFontUtil;

/**
 * A widget model for text inputs.
 *
 * @author Alexander Will, Kai Meyer
 * @version $Revision: 1.35 $
 */
public class TextInputModel extends AbstractTextTypeWidgetModel {
    /**
     * The ID of the text input.
     */
    public static final String PROP_INPUT_TEXT = "inputText"; //$NON-NLS-1$

    /**
     * The ID of the font property.
     */
    public static final String PROP_FONT = "font"; //$NON-NLS-1$

    /**
     * The ID of the text alignment property.
     */
    public static final String PROP_TEXT_ALIGNMENT = "textAlignment"; //$NON-NLS-1$

    /**
     * The ID of this widget model.
     */
    public static final String ID = "org.csstudio.sds.components.Textinput"; //$NON-NLS-1$

    /**
     * The default value of the height property.
     */
    private static final int DEFAULT_HEIGHT = 20;

    /**
     * The default value of the width property.
     */
    private static final int DEFAULT_WIDTH = 80;
    /**
     * The ID of the <i>transparent</i> property.
     */
    public static final String PROP_TRANSPARENT = "transparent";
    /**
     * The ID of the <i>double type</i> property.
     */
    public static final int TYPE_DOUBLE = 1;

    /**
     * Standard constructor.
     */
    public TextInputModel() {
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setCursorId(CursorStyleEnum.IBEAM.name());
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
        addStringProperty(PROP_INPUT_TEXT, "Input Text", WidgetPropertyCategory.DISPLAY, "", true,PROP_TOOLTIP); //$NON-NLS-1$
        addArrayOptionProperty(PROP_TEXT_TYPE,
                               "Value Type",
                               WidgetPropertyCategory.DISPLAY,
                               TextTypeEnum.getDisplayNames(),
                               TextTypeEnum.DOUBLE.getIndex(), false, PROP_INPUT_TEXT);
        addIntegerProperty(PROP_PRECISION,
                           "Decimal places",
                           WidgetPropertyCategory.DISPLAY,
                           2,
                           0,
                           10, false,PROP_TEXT_TYPE);
        // Format
        addFontProperty(PROP_FONT,
                        "Font", WidgetPropertyCategory.FORMAT, ColorAndFontUtil.toFontString("Arial", 8), false, PROP_COLOR_FOREGROUND); //$NON-NLS-1$
        addArrayOptionProperty(PROP_TEXT_ALIGNMENT,
                               "Text Alignment",
                               WidgetPropertyCategory.FORMAT,
                               TextAlignmentEnum.getDisplayNames(),
                               TextAlignmentEnum.CENTER.getIndex(), false, PROP_FONT );
        addBooleanProperty(PROP_TRANSPARENT,
                           "Transparent Background",
                           WidgetPropertyCategory.FORMAT,
                           true,
                           true, AbstractWidgetModel.PROP_COLOR_BACKGROUND);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDefaultToolTip() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(createTooltipParameter(PROP_ALIASES) + "\n");
        buffer.append("Text:\t");
        buffer.append(createTooltipParameter(PROP_INPUT_TEXT));
        return buffer.toString();
    }

    /**
     * Gets the input text.
     *
     * @return the input text
     */
    public String getInputText() {
        return getStringProperty(PROP_INPUT_TEXT);
    }

    /**
     * Gets, if the marks should be shown or not.
     *
     * @return int 0 = Center, 1 = Top, 2 = Bottom, 3 = Left, 4 = Right
     */
    public int getTextAlignment() {
        return getArrayOptionProperty(PROP_TEXT_ALIGNMENT);
    }

    /**
     * Returns, if this widget should have a transparent background.
     *
     * @return boolean True, if it should have a transparent background, false otherwise
     */
    @Override
    public boolean getTransparent() {
        return getBooleanProperty(PROP_TRANSPARENT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringValueID() {
        return PROP_INPUT_TEXT;
    }

}
