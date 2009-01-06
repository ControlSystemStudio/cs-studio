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
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Parser for record instance definition files.</p>
 * 
 * <p>Note that this parser parses only record instance definitions, and only
 * field definitions within those record instance definitions. It does not
 * currently support record information items.</p>
 * 
 * <p>The parser parses only the syntactical structure of the given input. It
 * does not attempt to validate the record instance definitions in any way. For
 * example, it would accept "A.B" as a legal record name because that is
 * syntactically recognizable as a name, but it is actually not a valid name,
 * because the "." character is not allowed within EPICS record names.</p>
 * 
 * <p>The grammar used by this parser is:</p>
 * 
 * <pre>
 * DatabaseFile ::= RecordInstance*
 * 
 * RecordInstance ::= 'record' '(' Str ',' Str ')' '{' Field* '}'
 * 
 * Field ::= 'field' '(' Str ',' Str ')'
 * 
 * Str ::= IDENTIFIER | QUOTED_STRING
 * </pre>
 * 
 * @author Joerg Rathlev
 */
public final class RecordInstanceDatabaseParser {
	
	/* Implementation note: The parser is implemented as a simple
	 * recursive-descent style parser, but without a real lookahead (there is
	 * no loookahead buffer) because there is nothing in the grammar which
	 * would require a lookahead buffer. The RecordInstance and Field
	 * non-terminals start with an unambiguous keyword token; the parsing of
	 * identifiers and quoted strings is simply inlined.
	 */

	/**
	 * The lexer which generates the tokens for this parser.
	 */
	private final RecordInstanceDatabaseLexer _lexer;
	
	/**
	 * The list of record instances.
	 */
	private final List<RecordInstance> _recordInstances;

	/**
	 * Creates a new parser.
	 * 
	 * @param reader
	 *            the reader from which the input to be parsed will be read.
	 */
	public RecordInstanceDatabaseParser(final Reader reader) {
		_lexer = new RecordInstanceDatabaseLexer(reader);
		_recordInstances = new ArrayList<RecordInstance>();
	}

	/**
	 * Parses the given input. Note that this method can be called only once.
	 * 
	 * @return a list of the record instances defined in the input file.
	 * @throws IOException if an IO error occurs.
	 */
	public List<RecordInstance> parse() throws IOException {
		parseDatabase();
		return _recordInstances;
	}

	/**
	 * Parse the <code>Database</code> non-terminal.
	 * 
	 * @throws IOException
	 *             if an IO error occurs.
	 */
	void parseDatabase() throws IOException {
		Token t = _lexer.nextToken();
		while (t.getType() != TokenType.END_OF_INPUT) {
			switch (t.getType()) {
			case RECORD:
				parseRecord();
				break;
			default:
				// TODO: error handling
				throw new RuntimeException();
			}
			t = _lexer.nextToken();
		}
	}

	/**
	 * Parse the <code>Record</code> non-terminal.
	 * 
	 * @throws IOException if an IO error occurs. 
	 */
	void parseRecord() throws IOException {
		String type;
		String name;
		
		// Note: the 'record' token was already consumed by parseDatbase().
		consumeToken(TokenType.LPAREN);
		Token t = _lexer.nextToken();
		switch (t.getType()) {
		case IDENTIFIER:
		case QUOTED_STRING:
			type = t.getText();
			break;
		default:
			// TODO error handling
			throw new RuntimeException();
		}
		consumeToken(TokenType.COMMA);
		t = _lexer.nextToken();
		switch (t.getType()) {
		case IDENTIFIER:
		case QUOTED_STRING:
			name = t.getText();
			break;
		default:
			// TODO error handling
			throw new RuntimeException();
		}
		consumeToken(TokenType.RPAREN);
		consumeToken(TokenType.LCURLY);
		t = _lexer.nextToken();
		List<Field> fields = new ArrayList<Field>();
		while (t.getType() != TokenType.RCURLY) {
			switch (t.getType()) {
			case FIELD:
				parseField(fields);
				break;
			default:
				// TODO error handling
				throw new RuntimeException();
			}
			t = _lexer.nextToken();
		}
		
		RecordInstance ri = new RecordInstance(type, name);
		_recordInstances.add(ri);
		for (Field f : fields) {
			ri.addField(f);
		}
	}

	/**
	 * Parse the <code>Field</code> non-terminal.
	 * 
	 * @param fields
	 *            the list to which the field will be added.
	 * 
	 * @throws IOException
	 *             if an IO error occurs.
	 */
	void parseField(final List<Field> fields) throws IOException {
		String name;
		String value;
		
		consumeToken(TokenType.LPAREN);
		Token t = _lexer.nextToken();
		switch (t.getType()) {
		case IDENTIFIER:
		case QUOTED_STRING:
			name = t.getText();
			break;
		default:
			// TODO error handling
			throw new RuntimeException();
		}
		consumeToken(TokenType.COMMA);
		t = _lexer.nextToken();
		switch (t.getType()) {
		case QUOTED_STRING:
			value = t.getText();
			break;
		default:
			// TODO error handling
			throw new RuntimeException();
		}
		consumeToken(TokenType.RPAREN);
		
		Field f = new Field(name, value);
		fields.add(f);
	}

	/**
	 * Consumes the next token from the lexer and ensures that it is of the
	 * expected type. If the token is not of the expected type, raises an error.
	 * 
	 * @param expected
	 *            the expected type for the next token.
	 * @throws IOException if an IO error occurs.
	 */
	private void consumeToken(final TokenType expected) throws IOException {
		Token t = _lexer.nextToken();
		if (t.getType() != expected) {
			// TODO: error handling
			throw new RuntimeException(t.toString() + ", expectd type: " + expected);
		}
	}

}
