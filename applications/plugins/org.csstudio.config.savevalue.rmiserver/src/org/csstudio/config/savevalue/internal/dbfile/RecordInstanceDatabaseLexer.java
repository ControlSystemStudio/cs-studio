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

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

/**
 * Lexical analyzer for record instance database files.
 * 
 * @author Joerg Rathlev
 */
class RecordInstanceDatabaseLexer {

	/**
	 * The reader which is used as the input.
	 */
	private final Reader _reader;
	
	/**
	 * The stream tokenizer used to pre-tokenize the input.
	 */
	private final StreamTokenizer _tokenizer;

	/**
	 * Creates a new lexer.
	 * 
	 * @param reader
	 *            the reader which will be used as the input.
	 */
	public RecordInstanceDatabaseLexer(final Reader reader) {
		_reader = reader;
		_tokenizer = new StreamTokenizer(_reader);
		setupTokenizerSyntax();
	}

	/**
	 * Programs the syntax of the stream tokenizer used by this lexer.
	 */
	private void setupTokenizerSyntax() {
		_tokenizer.resetSyntax();
		
		// Characters which do not need to be quoted in strings:
		// a-z A-Z 0-9 _ - : . [ ] < > ;
		_tokenizer.wordChars('a', 'z');
		_tokenizer.wordChars('A', 'Z');
		_tokenizer.wordChars('0', '9');
		_tokenizer.wordChars('_', '_');
		_tokenizer.wordChars('-', '-');
		_tokenizer.wordChars(':', ':');
		_tokenizer.wordChars('.', '.'); // note: not legal in record names
		_tokenizer.wordChars('[', '[');
		_tokenizer.wordChars(']', ']');
		_tokenizer.wordChars('<', '<');
		_tokenizer.wordChars('>', '>');
		
		// Whitespace characters:
		// \n \r \t ' '
		_tokenizer.whitespaceChars('\n', '\n');
		_tokenizer.whitespaceChars('\r', '\r');
		_tokenizer.whitespaceChars('\t', '\t');
		_tokenizer.whitespaceChars(' ', ' ');
		
		// Quoted strings:
		// "..."
		_tokenizer.quoteChar('"');
		
		// End of line comments:
		// # ...
		_tokenizer.commentChar('#');
	}

	/**
	 * Returns the next token from the input.
	 * 
	 * @return the next token from the input.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public Token nextToken() throws IOException {
		// Get the next token from the stream tokenizer, than check its token
		// type with regard to the specific tokenization rules of database files
		// (recognize keywords, etc.).
		int token = _tokenizer.nextToken();
		String tokenText = _tokenizer.sval;
		switch (token) {
		case StreamTokenizer.TT_WORD:
			return recognizeToken(tokenText);
		case '"':
			return new Token(TokenType.QUOTED_STRING, tokenText);
		case '{':
			return new Token(TokenType.LCURLY, tokenText);
		case '}':
			return new Token(TokenType.RCURLY, tokenText);
		case '(':
			return new Token(TokenType.LPAREN, tokenText);
		case ')':
			return new Token(TokenType.RPAREN, tokenText);
		case ',':
			return new Token(TokenType.COMMA, tokenText);
		case StreamTokenizer.TT_EOF:
			return new Token(TokenType.END_OF_INPUT, null);
		default:
			// TODO: improve error handling
			throw new RuntimeException("panic: " + ((char) token));
		}
	}

	/**
	 * Recognizes a single token.
	 * 
	 * @param tokenText
	 *            the text of the token.
	 * @return the token.
	 */
	private Token recognizeToken(final String tokenText) {
		// Check for keywords; if it's not a keyword, it's an identifier
		if ("record".equals(tokenText)) {
			return new Token(TokenType.RECORD, tokenText);
		} else if ("field".equals(tokenText)) {
			return new Token(TokenType.FIELD, tokenText);
		} else {
			return new Token(TokenType.IDENTIFIER, tokenText);
		}
	}

}
