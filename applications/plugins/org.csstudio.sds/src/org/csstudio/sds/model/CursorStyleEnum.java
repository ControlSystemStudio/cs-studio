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
package org.csstudio.sds.model;

import org.eclipse.draw2d.Cursors;
import org.eclipse.swt.graphics.Cursor;

/**
 * An enum for the available borders.
 * @author Kai Meyer
 *
 */
public enum CursorStyleEnum {
    /**
     * Arrow Cursor.
     */
    ARROW(0, "Arrow", Cursors.ARROW),
    /**
     * Hand Cursor.
     */
    HAND(1, "Hand", Cursors.HAND),
    /**
     * No Cursor.
     */
    NO(2, "No", Cursors.NO),
    /**
     * Hand Cursor.
     */
    CROSS(3, "Cross", Cursors.CROSS),
    /**
     * Hand Cursor.
     */
    STARTING(4, "Starting", Cursors.APPSTARTING),
    /**
     * Hand Cursor.
     */
    IBEAM(5, "IBeam", Cursors.IBEAM),
    /**
     * Hand Cursor.
     */
    HELP(6, "Help", Cursors.HELP);

    /**
     * The index of his enum.
     */
    private int _index;
    /**
     * The display name of this enum.
     */
    private String _displayName;
    /**
     * The cursor of this enum.
     */
    private Cursor _cursor;

    /**
     * Constructor.
     * @param index The index of this value
     * @param displayName The name of this value
     * @param cursor The cursor
     */
    private CursorStyleEnum(final int index, final String displayName, final Cursor cursor) {
        _index = index;
        _displayName = displayName;
        _cursor = cursor;
    }

    /**
     * Returns the index of this {@link CursorStyleEnum}.
     * @return The index
     */
    public int getIndex() {
        return _index;
    }

    /**
     * Returns the display name of this {@link CursorStyleEnum}.
     * @return The display name
     */
    public String getDisplayName() {
        return _displayName;
    }

    /**
     * Returns the cursor.
     * @return The cursor
     */
    public Cursor getCursor() {
        return _cursor;
    }

    /**
     * Returns the display names of the all borders.
     * @return The display names
     */
    public static String[] getDisplayNames() {
        CursorStyleEnum[] enums = CursorStyleEnum.values();
        String[] result = new String[enums.length];
        for (int i=0;i<enums.length;i++) {
            result[i] = enums[i].getDisplayName();
        }
        return result;
    }

    /**
     * Returns the corresponding {@link CursorStyleEnum} to the given index or <code>null</code> if the index is unknown.
     * @param index The index of the enum
     * @return The corresponding {@link CursorStyleEnum} or <code>null</code> if the index is unknown
     */
    public static CursorStyleEnum getEnumForIndex(final int index) {
        for (CursorStyleEnum csenum : CursorStyleEnum.values()) {
            if (csenum.getIndex()==index) {
                return csenum;
            }
        }
        return CursorStyleEnum.ARROW;
    }

}
