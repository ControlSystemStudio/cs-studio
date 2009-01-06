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

package org.csstudio.config.savevalue.internal.dbfile;

/**
 * An input token read from a record instance database file.
 * 
 * @author Joerg Rathlev
 */
final class Token {

	/**
	 * The type of this token.
	 */
	private final TokenType _type;
	
	/**
	 * The text of this token.
	 */
	private final String _text;

	/**
	 * Creates a new token.
	 * 
	 * @param type
	 *            the token type.
	 * @param text
	 *            the text that was parsed into this token. May be
	 *            <code>null</code> if this token is a synthetic token.
	 */
	public Token(final TokenType type, final String text) {
		_type = type;
		_text = text;
	}

	/**
	 * Returns the type of this token.
	 * 
	 * @return the type of this token.
	 */
	public TokenType getType() {
		return _type;
	}
	
	/**
	 * Returns the text of this token.
	 * 
	 * @return the text of this token, or <code>null</code> if this token is a
	 * synthetic token.
	 */
	public String getText() {
		return _text;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Token(type=");
		stringBuilder.append(_type);
		stringBuilder.append("; text=");
		stringBuilder.append(_text);
		stringBuilder.append(")");
		return stringBuilder.toString();
	}

}
