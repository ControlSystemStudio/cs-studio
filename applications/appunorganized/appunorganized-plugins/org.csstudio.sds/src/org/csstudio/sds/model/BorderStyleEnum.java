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

/**
 * An enum for the available borders.
 * @author Kai Meyer
 *
 */
public enum BorderStyleEnum {
    /**
     * No border.
     */
    NONE(0, "None"),
    /**
     * A line border.
     */
    LINE(1, "Line Style"),
    /**
     * A labeled border.
     */
    LABELED(2, "Labeled Style"),
    /**
     * A raised border.
     */
    RAISED(3, "Raised Style"),
    /**
     * A lowered border.
     */
    LOWERED(4, "Lowered Style"),
    /**
     * A dotted border.
     */
    DOTTED(5, "Dot Style"),
    /**
     * A shape border.
     */
    SHAPE(6, "Shape Style"),
    /**
     * A dashed border.
     */
    DASHED(7, "Dash Style"),
    /**
     * A dashed dotted border.
     */
    DASH_DOT(8, "Dash Dot Style"),

    /**
     * A dashed dotted dotted border.
     */
    DASH_DOT_DOT(9, "Dash Dot Dot Style");

    /**
     * The index of his enum.
     */
    private int _index;
    /**
     * The display name of this enum.
     */
    private String _displayName;

    /**
     * Constructor.
     * @param index The index of this value
     * @param displayName The name of this value
     */
    private BorderStyleEnum(final int index, final String displayName) {
        _index = index;
        _displayName = displayName;
    }

    /**
     * Returns the index of this {@link BorderStyleEnum}.
     * @return The index
     */
    public int getIndex() {
        return _index;
    }

    /**
     * Returns the display name of this {@link BorderStyleEnum}.
     * @return The display name
     */
    public String getDisplayName() {
        return _displayName;
    }

    /**
     * Returns the display names of the all borders.
     * @return The display names
     */
    public static String[] getDisplayNames() {
        BorderStyleEnum[] enums = BorderStyleEnum.values();
        String[] result = new String[enums.length];
        for (int i=0;i<enums.length;i++) {
            result[i] = enums[i].getDisplayName();
        }
        return result;
    }

    /**
     * Returns the corresponding {@link BorderStyleEnum} to the given index or <code>null</code> if the index is unknown.
     * @param index The index of the enum
     * @return The corresponding {@link BorderStyleEnum} or <code>null</code> if the index is unknown
     */
    public static BorderStyleEnum getEnumForIndex(final int index) {
        for (BorderStyleEnum ttenum : BorderStyleEnum.values()) {
            if (ttenum.getIndex()==index) {
                return ttenum;
            }
        }
        return null;
    }

}
