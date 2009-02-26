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

package org.csstudio.diag.interconnectionServer.internal.iocmessage;

import java.util.regex.Pattern;

/**
 * Parses a message of tag value pairs received from the IOC over the network.
 * The format parsed by this parser is a list of tag value pairs transmitted as
 * plain text. Each tag value pair (including the last) must be terminated by
 * a semicolon. The tag and the value in a pair must be separated by an equals
 * sign.
 * 
 * Example message: ID=1;TYPE=event;TEXT=example;
 * 
 * @author Joerg Rathlev
 */
public class IocMessageParser {
	
	/**
	 * The character that terminates each tag value pair.
	 */
	private static final String ITEM_TERMINATOR = ";";
	
	/**
	 * The character that separates tag and value within a pair.
	 */
	private static final String TAG_VALUE_SEPARATOR = "=";

	private final Pattern _itemRegex;
	private final Pattern _tagValueRegex;

	/**
	 * Creates a new parser.
	 */
	public IocMessageParser() {
		// Compile the regular expression patterns used for parsing
		_itemRegex = Pattern.compile(Pattern.quote(ITEM_TERMINATOR));
		_tagValueRegex = Pattern.compile(Pattern.quote(TAG_VALUE_SEPARATOR));
	}

	/**
	 * Parses the specified message.
	 * 
	 * @param unparsedMessage
	 *            the message received from the IOC.
	 * @return the parsed message.
	 */
	public IocMessage parse(String unparsedMessage) {
		if (unparsedMessage == null) {
			throw new NullPointerException("unparsedMessage must not be null");
		}
		
		IocMessage message = new IocMessage();

		String[] items = _itemRegex.split(unparsedMessage);
		for (String item : items) {
			String[] tagValue = _tagValueRegex.split(item, 2);
			if (tagValue.length == 2) {
				TagValuePair tv = new TagValuePair(tagValue[0], tagValue[1]);
				message.addItem(tv);
			} else {
				// TODO: error handling. The old parser code simply ignored
				// invalid tag value pairs. Maybe we should at least log a
				// warning instead?
			}
		}
		
		return message;
	}
}
