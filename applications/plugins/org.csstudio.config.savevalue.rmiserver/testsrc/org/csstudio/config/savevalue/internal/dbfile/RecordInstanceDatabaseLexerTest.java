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

import static org.junit.Assert.*;

import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;


/**
 * @author Joerg Rathlev
 */
public class RecordInstanceDatabaseLexerTest {

	@Test
	public void testEmptyInput() throws Exception {
		// Expected: for an empty input, returns EOF the first token.
		Reader reader = new StringReader("");
		RecordInstanceDatabaseLexer lexer = new RecordInstanceDatabaseLexer(reader);
		Token t = lexer.nextToken();
		assertEquals(TokenType.END_OF_INPUT, t.getType());
	}
	
	@Test
	public void testRecordKeyword() throws Exception {
		// Expected: parse the record keyword as a keyword token, followed by
		// the end of the input.
		Reader reader = new StringReader("record");
		RecordInstanceDatabaseLexer lexer = new RecordInstanceDatabaseLexer(reader);
		Token t = lexer.nextToken();
		assertEquals(TokenType.RECORD, t.getType());
		assertEquals("record", t.getText());
		t = lexer.nextToken();
		assertEquals(TokenType.END_OF_INPUT, t.getType());
	}
	
	@Test
	public void testCommentsAndWhitespace() throws Exception {
		// Comments and whitespace should be ignored.
		Reader reader = new StringReader("#comment\n   record  \n#comment\n  ");
		RecordInstanceDatabaseLexer lexer = new RecordInstanceDatabaseLexer(reader);
		Token t = lexer.nextToken();
		assertEquals(TokenType.RECORD, t.getType());
		assertEquals("record", t.getText());
		t = lexer.nextToken();
		assertEquals(TokenType.END_OF_INPUT, t.getType());
	}
	
	@Test
	public void testParseComplexExample() throws Exception {
		// A complex test case with expected tokenization:
		// 
		// record(ai, "foo") {
		//     field
		// }
		// 
		// => RECORD, LPAREN, IDENTIFIER, COMMA, QUOTED_STRING, RPAREN,
		//    LCURLY, FIELD, RCURLY
		// 
		// (Note: the example is not syntactically valid, but that doesn't
		// matter here, because this tests only the lexer part!)
		Reader reader = new StringReader("record(ai, \"foo\") {\n    field\n}");
		RecordInstanceDatabaseLexer lexer = new RecordInstanceDatabaseLexer(reader);
		Token t = lexer.nextToken();
		assertEquals(TokenType.RECORD, t.getType());
		t = lexer.nextToken();
		assertEquals(TokenType.LPAREN, t.getType());
		t = lexer.nextToken();
		assertEquals(TokenType.IDENTIFIER, t.getType());
		assertEquals("ai", t.getText());
		t = lexer.nextToken();
		assertEquals(TokenType.COMMA, t.getType());
		t = lexer.nextToken();
		assertEquals(TokenType.QUOTED_STRING, t.getType());
		assertEquals("foo", t.getText());
		t = lexer.nextToken();
		assertEquals(TokenType.RPAREN, t.getType());
		t = lexer.nextToken();
		assertEquals(TokenType.LCURLY, t.getType());
		t = lexer.nextToken();
		assertEquals(TokenType.FIELD, t.getType());
		t = lexer.nextToken();
		assertEquals(TokenType.RCURLY, t.getType());
	}
}
