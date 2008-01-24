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
package org.csstudio.sds.model.optionEnums;

/**
 * An enum for the available text types.
 * @author Kai Meyer
 *
 */
public enum TextTypeEnum {
	/**
	 * text type.
	 */
	TEXT(0, "Text Value"),
	/**
	 * double type.
	 */
	DOUBLE(1, "Double Value"),
	/**
	 * aliases type.
	 */
	ALIAS(2, "Alias Value"),
	/**
	 * hex type.
	 */
	HEX(3, "Hex Value");
	
	/**
	 * The ID for the <i>text</i> type.
	 */
	public static final int TYPE_TEXT = 0;
	/**
	 * The ID for the <i>double</i> type.
	 */
	public static final int TYPE_DOUBLE = 1;
	/**
	 * The ID for the <i>alias</i> type.
	 */
	public static final int TYPE_ALIAS = 2;
	/**
	 * The ID for the <i>alias</i> type.
	 */
	public static final int TYPE_HEX = 3;

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
	private TextTypeEnum(final int index, final String displayName) {
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
	 * Returns the display names of the all text types.
	 * @return The display names
	 */
	public static String[] getDisplayNames() {
		TextTypeEnum[] textTypeEnums = TextTypeEnum.values();
		String[] result = new String[textTypeEnums.length];
		for (int i=0;i<textTypeEnums.length;i++) {
			result[i] = textTypeEnums[i].getDisplayName();
		}
		return result;
	}
	
	/**
	 * Returns the corresponding {@link TextTypeEnum} to the given index or <code>null</code> if the index is unknown.
	 * @param index The index of the enum
	 * @return The corresponding {@link TextTypeEnum} or <code>null</code> if the index is unknown
	 */
	public static TextTypeEnum getEnumForIndex(final int index) {
		for (TextTypeEnum ttenum : TextTypeEnum.values()) {
			if (ttenum.getIndex()==index) {
				return ttenum;
			}
		}
		return null;
	}

}
