package de.desy.language.snl.codeElements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

/**
 * A test to exam a pattern to match when statements.
 * 
 * Attention: This pattern does not match a statement with following chars
 * behind the statement.
 * 
 * The pattern:
 * 
 * <pre>
 * (when)(\\s*\\()([\\S\\s]*[0-9a-zA-Z]*)(\\)\\s*\\{)([\\S\\s]*)(\\}\\s*state\\s*)([\\S\\s]*[a-zA-Z][0-9a-zA-Z]*)([;]?)
 * </pre>
 * 
 * @author C1 WPS / KM, MZ
 */
public class RegExToMatchWhenStatements_Test extends TestCase {

	/**
	 * public cause used in other tests.
	 */
	public static final String WHEN_PATTERN = "(when)(\\s*\\()([\\S\\s]*[0-9a-zA-Z]*)(\\)\\s*\\{)([\\S\\s]*)(\\}\\s*state\\s*)([\\S\\s]*[a-zA-Z][0-9a-zA-Z]*)([;]?)";

	private Pattern _pattern;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		this._pattern = Pattern
				.compile(RegExToMatchWhenStatements_Test.WHEN_PATTERN);
	}

	@Test
	public void testSimpleRegEx() {
		final Matcher matcher = this._pattern
				.matcher("when (pvGet(xyz) == 42 ) { pvPut(computerOff, 1); pvPut(knockOffWork, TRUE); } state CallItADay");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(8, matcher.groupCount());
		Assert.assertEquals("when", matcher.group(1)); // the when statement
		Assert.assertEquals(" (", matcher.group(2)); // at least one or more
		// whitespaces and first
		// condition brace
		Assert.assertEquals("pvGet(xyz) == 42 ", matcher.group(3)); // the
																	// condition
		// part without
		// enclosing
		// brace but
		// with all
		// leading or
		// trailing
		// whitespaces!
		Assert.assertEquals(") {", matcher.group(4)); // closing brace of
														// condition
		// plus block begin
		Assert.assertEquals(
				" pvPut(computerOff, 1); pvPut(knockOffWork, TRUE); ", matcher
						.group(5)); // statement block without surrounding
		// braces
		Assert.assertEquals("} state ", matcher.group(6)); // end of state
															// statement
		// block
		Assert.assertEquals("CallItADay", matcher.group(7)); // target state
		Assert.assertEquals("", matcher.group(8)); // optional semicolon
	}

	@Test
	public void testSimpleRegEx_EmptyCondition() {
		final Matcher matcher = this._pattern
				.matcher("when () { pvPut(computerOff, 1); pvPut(knockOffWork, TRUE); } state CallItADay");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(8, matcher.groupCount());
		Assert.assertEquals("when", matcher.group(1)); // the when statement
		Assert.assertEquals(" (", matcher.group(2)); // at least one or more
		// whitespaces and first
		// condition brace
		Assert.assertEquals("", matcher.group(3)); // the condition
		// part without
		// enclosing
		// brace but
		// with all
		// leading or
		// trailing
		// whitespaces!
		Assert.assertEquals(") {", matcher.group(4)); // closing brace of
														// condition
		// plus block begin
		Assert.assertEquals(
				" pvPut(computerOff, 1); pvPut(knockOffWork, TRUE); ", matcher
						.group(5)); // statement block without surrounding
		// braces
		Assert.assertEquals("} state ", matcher.group(6)); // end of state
															// statement
		// block
		Assert.assertEquals("CallItADay", matcher.group(7)); // target state
		Assert.assertEquals("", matcher.group(8)); // optional semicolon
	}

	@Test
	public void testSimpleRegEx_EmptyBlock() {
		final Matcher matcher = this._pattern
				.matcher("when (pvGet(xyz) == 42 ) {} state CallItADay");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(8, matcher.groupCount());
		Assert.assertEquals("when", matcher.group(1)); // the when statement
		Assert.assertEquals(" (", matcher.group(2)); // at least one or more
		// whitespaces and first
		// condition brace
		Assert.assertEquals("pvGet(xyz) == 42 ", matcher.group(3)); // the
																	// condition
		// part without
		// enclosing
		// brace but
		// with all
		// leading or
		// trailing
		// whitespaces!
		Assert.assertEquals(") {", matcher.group(4)); // closing brace of
														// condition
		// plus block begin
		Assert.assertEquals("", matcher.group(5)); // statement block without
		// surrounding braces
		Assert.assertEquals("} state ", matcher.group(6)); // end of state
															// statement
		// block
		Assert.assertEquals("CallItADay", matcher.group(7)); // target state
		Assert.assertEquals("", matcher.group(8)); // optional semicolon
	}

	@Test
	public void testSimpleRegEx_FailureWithMissingStartConditionBrace() {
		final Matcher matcher = this._pattern
				.matcher("when pvGet(xyz) == 42 ) {} state CallItADay");
		Assert.assertFalse(matcher.matches());

		Assert.assertEquals(8, matcher.groupCount());
	}

	@Test
	public void testSimpleRegEx_FailureWithMissingStateStatement() {
		final Matcher matcher = this._pattern
				.matcher("when (pvGet(xyz) == 42 ) {} CallItADay");
		Assert.assertFalse(matcher.matches());

		Assert.assertEquals(8, matcher.groupCount());
	}
}
