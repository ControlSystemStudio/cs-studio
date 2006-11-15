package org.csstudio.platform.util;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A transportable unit for a control system item, which is used in the process
 * of converting items to string representations and back.
 * 
 * @author Sven Wende
 * 
 */
public final class ControlSystemItemPath implements Serializable {
	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 4959241842467380642L;

	/**
	 * Placeholder, which is used in PORTABLE_FORMAT.
	 */
	private static final String TYPE_PART = "TYPE";

	/**
	 * Placeholder, which is used in PORTABLE_FORMAT.
	 */
	private static final String OBJECT_PART = "OBJECT_PART";

	/**
	 * Defines the format of the String representation. Should contain the
	 * placeholders TYPE_PART and OBJECT_PART.
	 */
	private static final String PORTABLE_FORMAT = "##" + TYPE_PART + "###"
			+ OBJECT_PART + "##";

	/**
	 * The type identifier for the control system item, which should be
	 * transferred to a String representation.
	 */
	private String _typeId;

	/**
	 * The part of the String representation, which contains the individual data
	 * of the control system item, that should be transferred to a String
	 * representation.
	 */
	private String _itemData;

	/**
	 * Privat default constructur. Used only for serialization.
	 */
	private ControlSystemItemPath() {
		_itemData = "";
		_typeId = "";
	}

	/**
	 * Constructs a path.
	 * 
	 * @param typeId
	 *            the type identification of the control system item
	 * @param itemData
	 *            a String representation of the control system item´s data
	 * 
	 */
	public ControlSystemItemPath(final String typeId, final String itemData) {
		assert typeId != null;
		assert itemData != null;
		_typeId = typeId;
		_itemData = itemData;
	}

	/**
	 * @return the type id
	 */
	public String getTypeId() {
		return _typeId;
	}

	/**
	 * @return the item data in String format
	 */
	public String getItemData() {
		return _itemData;
	}

	/**
	 * @return a portable String representation a the control system, which can
	 *         be used externally
	 */
	public String toPortableString() {
		String portable = PORTABLE_FORMAT.replace(TYPE_PART, _typeId).replace(
				OBJECT_PART, _itemData);
		return portable;
	}

	/**
	 * Creates a path object from a String representation.
	 * 
	 * @param portableString
	 *            the portable String, which must use the same format as it is
	 *            provided by {@link #toPortableString()}
	 * 
	 * @return a path object or null, if the provided String does not satisfy
	 *         the format needs
	 */
	public static ControlSystemItemPath createFromPortableString(
			final String portableString) {
		ControlSystemItemPath path = null;

		String regex = PORTABLE_FORMAT.replace(TYPE_PART, "(.*)").replace(
				OBJECT_PART, "(.*)");

		Pattern pattern = Pattern.compile(regex);
		// Get a Matcher based on the target string.
		Matcher matcher = pattern.matcher(portableString);

		// Find all the matches.
		if (matcher.find()) {
			String typeId = matcher.group(1);
			String objecPart = matcher.group(2);

			if (typeId != null && !typeId.equals("") && objecPart != null) {
				path = new ControlSystemItemPath(typeId, objecPart);
			}
		}

		return path;
	}
}
