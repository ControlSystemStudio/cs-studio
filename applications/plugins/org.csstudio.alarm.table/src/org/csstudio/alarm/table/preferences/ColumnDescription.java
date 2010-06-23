/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN
 * "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN
 * ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 * DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS
 * AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE
 * THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.alarm.table.preferences;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Description of the columns contain the title header, the column width, default value and the like.
 */
public enum ColumnDescription {
    IS_DEFAULT_ENTRY("Default", 40, MouseActionDescription.NO_ACTION),
    TOPIC_SET("Topics", 150, "Topics", MouseActionDescription.EDIT_STRING),
    NAME_FOR_TOPIC_SET("Name", 150, "Name", MouseActionDescription.EDIT_STRING),
    POPUP_MODE("PopUp Mode", 80, "false", MouseActionDescription.TOGGLE_BOOL),
    AUTO_START("Auto Start", 80, "false", MouseActionDescription.TOGGLE_BOOL),
    FONT("Font", 100, "Tahoma,0,8", MouseActionDescription.OPEN_FONT_DIALOGUE);


    /**
     * Description of the action on mouse double click
     */
    public enum MouseActionDescription {
        NO_ACTION, EDIT_STRING, TOGGLE_BOOL, OPEN_FONT_DIALOGUE
    }

    private final String _title;
    private final int _columnWidth;
    private final String _defaultValue;
    private final MouseActionDescription _mouseActionDescription;

    private ColumnDescription(@Nonnull final String title, final int columnWidth, @Nonnull final MouseActionDescription mouseActionDescription) {
        this(title, columnWidth, null, mouseActionDescription);
    }

    private ColumnDescription(@Nonnull final String title, final int columnWidth, @CheckForNull final String defaultValue,
                              @Nonnull final MouseActionDescription mouseActionDescription) {
        _title = title;
        _columnWidth = columnWidth;
        _defaultValue = defaultValue;
        _mouseActionDescription = mouseActionDescription;
    }

    @Nonnull
    public static ColumnDescription getColumnDescriptionForIndex(final int index) {
        return ColumnDescription.values()[index];
    }

    @Nonnull
    public String getTitle() {
        return _title;
    }

    public int getColumnWidth() {
        return _columnWidth;
    }

    @CheckForNull
    public String getDefaultValue() {
        return _defaultValue;
    }

    @Nonnull
    public MouseActionDescription getMouseActionDescription() {
        return _mouseActionDescription;
    }

    public int getColumnIndex() {
        return ordinal();
    }

    public boolean isLast() {
        return ordinal() == (values().length - 1);
    }

}
