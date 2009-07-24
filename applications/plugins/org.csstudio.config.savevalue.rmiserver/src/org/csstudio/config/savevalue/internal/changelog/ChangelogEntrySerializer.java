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
 * <p>
 * Contains functions for serializing and deserializing changelog entries into
 * strings. The serialization format is designed to be used in a line-based
 * changelog file.
 * </p>
 * 
 * <p>
 * Clients should use the methods {@link #serialize} and {@link #deserialize}
 * serialize and deserialize changelog entries.
 * </p>
 * 
 * <h2>Serialization format</h2>
 * 
 * <p>
 * The format of a serialized changelog entry is a string consisting of five
 * tokens: channel name, value, user name, host name, modification date. The
 * tokens are separated by a single space character. For example, if on July
 * 1st, 2009, at 15:00 user alice saved a value of 0 for the record
 * EXAMPLE:counter from the computer named host123, the changelog entry will be
 * serialized as follows:
 * </p>
 * 
 * <pre>
 * EXAMPLE:counter 0 alice host123 2009-07-01T15:00:00
 * </pre>
 * 
 * <p>
 * If one of the values contains a space, it is escaped by preceding it with a
 * backslash. Line feeds and carriage returns are replaced by \n and \r,
 * respectively. A literal backslash in a value is escaped by a preceding
 * backslash. For example, if user alice sets EXAMPLE:path to &quot;C:\Program
 * Files\&quot;, the respective changelog entry will look like this:
 * </p>
 * 
 * <pre>
 * EXAMPLE:path C:\\Program\ Files\\ alice host123 2009-07-01T15:00:00
 * </pre>
 * 
 * @author Joerg Rathlev
 */
final class ChangelogEntrySerializer {

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
	 * Private constructor to prevent instantiation.
	 */
	private ChangelogEntrySerializer() {
	}

	/**
	 * Serializes a changelog entry into a string that can be written into a
	 * changelog file. Note that the returned string does <strong>not</strong>
	 * contain a trailing newline character.
	 * 
	 * @param entry
	 *            a changelog entry.
	 * @return a string.
	 * @see #deserialize(String)
	 */
	static String serialize(ChangelogEntry entry) {
		StringBuilder result = new StringBuilder();
		result
			.append(escape(entry.getPvName())).append(SEPARATOR)
			.append(escape(entry.getValue())).append(SEPARATOR)
			.append(escape(entry.getUsername())).append(SEPARATOR)
			.append(escape(entry.getHostname())).append(SEPARATOR)
			.append(escape(entry.getLastModified()));
		return result.toString();
	}

	/**
	 * Deserializes a changelog entry from a string. Note that the string
	 * <strong>must not</strong> contain a trailing newline character.
	 * 
	 * @param serialized
	 *            the serialized changelog entry.
	 * @return the changelog entry.
	 * @see #serialize(ChangelogEntry)
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
	 * Note: This method is accessible in the package for use in unit tests.
	 * Clients should not call this method directly to serialize a changelog
	 * entry. Use {@link #serialize(ChangelogEntry)} instead.
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
	 * Note: This method is accessible in the package for use in unit tests.
	 * Clients should not call this method directly to deserialize a changelog
	 * entry. Use {@link #deserialize(String)} instead.
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
