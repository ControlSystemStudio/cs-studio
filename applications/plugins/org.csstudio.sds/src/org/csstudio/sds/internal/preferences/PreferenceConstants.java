/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
 package org.csstudio.sds.internal.preferences;


/**
 * This class contains the id for the preference page entries used within the sds.
 * @author Kai Meyer
 *
 */
public final class PreferenceConstants {

    /**
     * Constant, which is used in the preference page for enable antialiasing.
     */
    public static final String PROP_ANTIALIASING = "PROP_ANTIALIASING";
    /**
     * The ID for the grid spacing property on the preference page.
     */
    public static final String PROP_GRID_SPACING = "PROP_GRID_SPACING";
    /**
     * The id for the widgets, which are used in the SDS.
     */
    public static final String PROP_DESELECTED_WIDGETS = "PROP_DESELECTED_WIDGETS";

    /**
     * ID for the preferred cursor selection rule.
     */
    public static final String CURSOR_SELECTION_RULE = "PROP_CURSOR_SELECTION_RULE";

    /**
     * ID for the node which contains the cursor settings.
     */
    public static final String CURSOR_SETTINGS = "PROP_CURSOR_SETTINGS";
    /**
     * ID for the state, if the parent display should be closed.
     */
    public static final String PROP_CLOSE_PARENT_DISPLAY = "PROP_CLOSE_PARENT_DISPLAY";

    /**
     * ID for preference containing the rule folders.
     */
    public static final String PROP_RULE_FOLDERS = "PROP_RULE_FOLDERS";

    /**
     * ID for preference containing the categorization of the widgets within the palette of the SDS-Editor.
     */
    public static final String PROP_WIDGET_CATEGORIZATION = "PROP_WIDGET_CATEGORIZATION";

    public static final String PROP_WRITE_ACCESS_DENIED = "PROP_WRITE_ACCESS_DENIED";

    public static final String PROP_DEFAULT_BEHAVIOR_PREFIX = "PROP_DEFAULT_BEHAVIOR_";

    public static final String PROP_SELECTED_COLOR_AND_FONT_STYLE = "PROP_SELECTED_COLOR_AND_FONT_STYLE";

    public static final String PROP_CROSSED_WIDGET_COLOR = "PROP_CROSSED_WIDGET_COLOR";

    public static final String PROP_CROSSED_WIDGET_LINE_WIDTH = "PROP_CROSSED_WIDGET_LINE_WIDTH";

    /**
     * Private constructor to avoid instantiation.
     */
    private PreferenceConstants() {
        //do nothing.
    }

}
