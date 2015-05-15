/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
 * TODO (hrickens) :
 *
 * @author hrickens
 * @since 21.12.2011
 */
public enum PrecisionEnum {

    PREC_SYSTEM(-1,"From CS"),
    PREC_ZERO(0,"0"),
    PREC_ONE(1,"1"),
    PREC_TWO(2,"2"),
    PREC_THREE(3,"3"),
    PREC_FOUR(4,"4"),
    PREC_FIVE(5,"5"),
    PREC_SIX(6,"6"),
    PREC_SEVEN(7,"7"),
    PREC_EIGTH(8,"8"),
    PREC_NINE(9,"9"),
    PREC_FULL(-2,"FULL");

   public static final int TYPE_TEXT = 0;

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
    *
    * @param index
    *            The index of this value
    * @param displayName
    *            The name of this value
    */
   private PrecisionEnum(final int index, final String displayName) {
       _index = index;
       _displayName = displayName;
   }
    /**
     * Returns the index of this {@link PrecisionEnum}.
     *
     * @return The index
     */
    public int getIndex() {
        return _index;
    }

    /**
     * Returns the display name of this {@link PrecisionEnum}.
     *
     * @return The display name
     */
    public String getDisplayName() {
        return _displayName;
    }

    /**
     * Returns the display names of the all text types.
     *
     * @return The display names
     */
    public static String[] getDisplayNames() {
        final PrecisionEnum[] PrecisionEnums = PrecisionEnum.values();
        final String[] result = new String[PrecisionEnums.length];
        for (int i = 0; i < PrecisionEnums.length; i++) {
            result[i] = PrecisionEnums[i].getDisplayName();
        }
        return result;
    }

//    /**
//     * Check if new input Character belongs to value format.
//     *
//     * @param chars
//     *            The new input characters.
//     * @param pos
//     *            position of new input character.
//     * @return only true if new value belongs to value format.
//     */
//    public boolean isValidChars(final char character, final CharSequence chars, final int pos) {
//        final PrecisionEnum propertyValue = this;
//        if (character == SWT.CR || character == SWT.KEYPAD_CR || character == SWT.DEL
//                || character == SWT.BS
//                || (pos == 1 || pos == 2) && (character == 'X' || character == 'x')) {
//            return true;
//        }
//        Pattern pattern;
//        switch (propertyValue) {
//            case HEX:
//            case EXP:
//            case DOUBLE:
//                pattern = Pattern.compile("[\\.\\p{XDigit}eE-]*");
//                return pattern.matcher(chars).matches();
//            default:
//                return true;
//        }
//    }

    /**
     * Returns the corresponding {@link PrecisionEnum} to the given index or <code>null</code> if the
     * index is unknown.
     *
     * @param index
     *            The index of the enum
     * @return The corresponding {@link PrecisionEnum} or <code>null</code> if the index is unknown
     */
    public static PrecisionEnum getEnumForIndex(final int index) {
        for (final PrecisionEnum pEnum : PrecisionEnum.values()) {
            if (pEnum.getIndex() == index) {
                return pEnum;
            }
        }
        return null;
    }
}
