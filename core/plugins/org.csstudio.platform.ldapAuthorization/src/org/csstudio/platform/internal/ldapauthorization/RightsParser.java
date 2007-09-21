package org.csstudio.platform.internal.ldapauthorization;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.platform.security.Right;
import org.csstudio.platform.security.RightSet;

/**
 * Parser for {@link Right}s and {@link RightSet}s.
 * 
 * @author Joerg Rathlev
 */
final class RightsParser {

	/**
	 * Regular expression for matching rights. Matches the role in the first
	 * subgroup, the group in the second subgroup.
	 */
	private static final Pattern RIGHT_PATTERN = Pattern
			.compile("\\(\\s*(\\S+)\\s*,\\s*(\\S+)\\s*\\)");

	/**
	 * Regular expression for splitting right sets. Matches zero or more
	 * whitespace characters that are between a closing and an opening
	 * parentheses.
	 */
	private static final Pattern RIGHT_SET_PATTERN = Pattern
			.compile("(?<=\\))\\s*(?=\\()");

	/**
	 * Parses a string-encoded right into an instance of {@link Right}. The
	 * string must have the format <code>(role, group)</code>.
	 * 
	 * @param encodedRight
	 *            the string-encoded right.
	 * @return the right, or <code>null</code> if the string could not be
	 *         parsed.
	 */
	static Right parseRight(String encodedRight) {
		Matcher m = RIGHT_PATTERN.matcher(encodedRight.trim());
		if (m.matches()) {
			String role = m.group(1);
			String group = m.group(2);
			return new Right(role, group);
		} else {
			return null;
		}
	}

	/**
	 * Parses a string-encoded right set into an instance of {@link RightSet}.
	 * The string must have the format
	 * <code>(role, group) (role, group) ...</code>.
	 * 
	 * @param encodedRightSet
	 *            the right set encoded as a string.
	 * @param name
	 *            the name of the right set to return.
	 * @return the parsed right set.
	 */
	static RightSet parseRightSet(String encodedRightSet, String name) {
		RightSet result = new RightSet(name);
		String[] encodedRights = RIGHT_SET_PATTERN.split(encodedRightSet.trim());
		for (String encRight : encodedRights) {
			Right r = parseRight(encRight);
			if (r != null) {
				result.addRight(r);
			}
		}
		return result;
	}

}
