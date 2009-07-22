/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.config.savevalue.internal.changelog;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.config.savevalue.service.ChangelogEntry;

/**
 * Contains functions for serializing and deserializing changelog entries into a
 * line-based file format. Each line of the changelog file contains five
 * columns: channel name, value, user name, host name, modification date.
 * 
 * @author Joerg Rathlev
 */
final class ChangelogFileFormat {

	/**
	 * The character used to separate the columns of changelog file entries.
	 */
	private static final char SEPARATOR = ' ';
	
	/**
	 * The character used as an escape character for escaping separator and
	 * newline characters in changelog entries.
	 */
	private static final char ESCAPE_CHAR = '\\';

	/**
	 * Serializes a changelog entry into a string that can be written into a
	 * changelog file. The returned string contains the trailing newline
	 * character.
	 * 
	 * @param entry
	 *            a changelog entry.
	 * @return a string.
	 */
	static String serialize(ChangelogEntry entry) {
		StringBuilder result = new StringBuilder();
		result
			.append(escape(entry.getPvName())).append(SEPARATOR)
			.append(escape(entry.getValue())).append(SEPARATOR)
			.append(escape(entry.getUsername())).append(SEPARATOR)
			.append(escape(entry.getHostname())).append(SEPARATOR)
			.append(escape(entry.getLastModified())).append('\n');
		return result.toString();
	}

	/**
	 * Deserializes a changelog entry from a string.
	 * 
	 * @param serialized
	 *            the serialized changelog entry.
	 * @return the changelog entry.
	 */
	static ChangelogEntry deserialize(String serialized) {
		String[] tokens = splitAndUnescape(serialized);
		if (tokens.length != 5) {
			throw new IllegalArgumentException("invalid number of tokens");
		}
		return new ChangelogEntry(tokens[0], tokens[1], tokens[2], tokens[3], tokens[4]);
	}

	/**
	 * Escapes a string.
	 * 
	 * @param string
	 *            a string.
	 * @return the escaped string.
	 */
	static String escape(String string) {
		StringBuilder escaped = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			switch (c) {
			case SEPARATOR:
				escaped.append(ESCAPE_CHAR).append(SEPARATOR);
				break;
			case '\n':
				escaped.append(ESCAPE_CHAR).append('n');
				break;
			case '\r':
				escaped.append(ESCAPE_CHAR).append('r');
				break;
			case ESCAPE_CHAR:
				escaped.append(ESCAPE_CHAR).append(ESCAPE_CHAR);
				break;
			default:
				escaped.append(c);
				break;
			}
		}
		return escaped.toString();
	}

	/**
	 * Splits a string around the separator character and unescapes the tokens.
	 * 
	 * @param string
	 *            a string.
	 * @return the sequence of tokens.
	 */
	static String[] splitAndUnescape(String string) {
		List<String> tokens = new ArrayList<String>();
		
		boolean previousCharWasEscapeChar = false;
		StringBuilder currentToken = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if (previousCharWasEscapeChar) {
				// If the previous character was an escape character, append
				// the literal character that was escaped by it.
				switch (c) {
				case SEPARATOR:
					currentToken.append(SEPARATOR);
					break;
				case 'n':
					currentToken.append('\n');
					break;
				case 'r':
					currentToken.append('\r');
					break;
				case ESCAPE_CHAR:
					currentToken.append(ESCAPE_CHAR);
					break;
				default:
					throw new IllegalArgumentException("illegal escape sequence: \\" + c);
				}
				previousCharWasEscapeChar = false;
			} else {
				if (c == ESCAPE_CHAR) {
					// The character starts an escape sequence
					previousCharWasEscapeChar = true;
				} else if (c == SEPARATOR) {
					// Start a new token
					tokens.add(currentToken.toString());
					currentToken = new StringBuilder();
				} else {
					// Normal character, simply append it to the current token
					currentToken.append(c);
				}
			}
		}
		if (previousCharWasEscapeChar) {
			throw new IllegalArgumentException("unfinished escape sequence at end of string");
		} else {
			tokens.add(currentToken.toString());
		}
		
		return (String[]) tokens.toArray(new String[tokens.size()]);
	}

}
