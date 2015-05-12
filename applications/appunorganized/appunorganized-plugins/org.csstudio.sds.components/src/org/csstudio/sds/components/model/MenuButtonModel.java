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

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.BorderStyleEnum;
import org.csstudio.sds.model.TextAlignmentEnum;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.util.ColorAndFontUtil;

/**
 *
 * @author Helge Rickens, Kai Meyer
 *
 */
public class MenuButtonModel extends AbstractWidgetModel {
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
     * The default value of the height property.
     */
    private static final int DEFAULT_HEIGHT = 20;

    /**
     * The default value of the width property.
     */
    private static final int DEFAULT_WIDTH = 80;
    /**
     * The ID of this widget model.
     */
    public static final String ID = "org.csstudio.sds.components.MenuButton";

    /**
     * Constructor.
     */
    public MenuButtonModel() {
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.setPropertyValue(PROP_BORDER_STYLE, BorderStyleEnum.RAISED.getIndex());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureProperties() {
        addStringProperty(PROP_LABEL, "Label Text", WidgetPropertyCategory.DISPLAY, "", false); //$NON-NLS-1$
        addFontProperty(PROP_FONT, "Font", WidgetPropertyCategory.DISPLAY, ColorAndFontUtil.toFontString("Arial", 8), false); //$NON-NLS-1$
        addArrayOptionProperty(PROP_TEXT_ALIGNMENT, "Text Alignment", WidgetPropertyCategory.DISPLAY, TextAlignmentEnum.getDisplayNames(),
                TextAlignmentEnum.CENTER.getIndex(), false);
        // addProperty(PROP_ACTIONDATA, new ActionDataProperty("Action Data",
        // WidgetPropertyCategory.Behaviour, new ActionData()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDefaultToolTip() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(createTooltipParameter(PROP_ALIASES) + "\n");
        buffer.append("Actions:\t");
        buffer.append(createTooltipParameter(PROP_ACTIONDATA));
        return buffer.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeID() {
        return ID;
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

}
