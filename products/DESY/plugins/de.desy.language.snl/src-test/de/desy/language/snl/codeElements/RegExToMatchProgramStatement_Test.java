package de.desy.language.snl.codeElements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

/**
 * A test to exam a pattern to match program statements.
 * 
 * Attention: This pattern does not match a statement with following chars
 * behind the statement close char (;), eg.
 * 
 * <pre>
 * program HalloWelt; daddel du da da! ;-)
 * </pre>
 * 
 * The pattern:
 * 
 * <pre>
 * (program)(\\s+)([a-zA-Z])([0-9a-zA-Z]*)(\\s*)([\\(][\\S\\s]*[\\)])?(\\s*)(;)
 * </pre>
 * 
 * @author C1 WPS / KM, MZ
 */
public class RegExToMatchProgramStatement_Test extends TestCase {

	private Pattern _pattern;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		this._pattern = Pattern
				.compile("(program\\s+)([a-zA-Z][0-9a-zA-Z]*)(\\s*)([\\(][\\S\\s]*[\\)])?(\\s*;)");
	}

	@Test
	public void testsimpleRegEx() {
		final Matcher matcher = this._pattern
				.matcher("program   HalloWelt (mit  , params ) ;");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(5, matcher.groupCount());
		Assert.assertEquals("program   ", matcher.group(1)); // the program
																// statement
																// with
																// whitespace
		Assert.assertEquals("HalloWelt", matcher.group(2)); // program name
		Assert.assertEquals(" ", matcher.group(3)); // optional whitespace
		Assert.assertEquals("(mit  , params )", matcher.group(4)); // optional
																	// param
		// block
		Assert.assertEquals(" ;", matcher.group(5)); // optional whitespace
														// with end of statement
	}

	@Test
	public void testsimpleRegExNoParams() {
		final Matcher matcher = this._pattern.matcher("program   HalloWelt  ;");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(5, matcher.groupCount());
		Assert.assertEquals("program   ", matcher.group(1)); // the program
																// statement
																// with
																// whitespace
		Assert.assertEquals("HalloWelt", matcher.group(2)); // programm name
		Assert.assertEquals("  ", matcher.group(3)); // optional whitespace
		Assert.assertEquals(null, matcher.group(4)); // no param block!
		Assert.assertEquals(";", matcher.group(5)); // optional whitespace with
													// end of statement
	}

	@Test
	public void testsimpleRegExNoParamsNoWhitespace() {
		final Matcher matcher = this._pattern.matcher("program HalloWelt;");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(5, matcher.groupCount());
		Assert.assertEquals("program ", matcher.group(1)); // the program
															// statement
		Assert.assertEquals("HalloWelt", matcher.group(2)); // additional and
															// optional
		// chars of program name
		Assert.assertEquals("", matcher.group(3)); // optional whitespace
		Assert.assertEquals(null, matcher.group(4)); // no param block!
		Assert.assertEquals(";", matcher.group(5)); // end of statement
	}

	@Test
	public void testsimpleRegExNoParamsNoWhitespace_Fail() {
		final Matcher matcher = this._pattern.matcher("programHalloWelt;");

		Assert.assertFalse(matcher.matches());
		Assert.assertEquals(5, matcher.groupCount());

		Assert.assertFalse(matcher.lookingAt());
	}

	@Test
	public void testsimpleRegExNoParamsNoWhitespace_FailNoName() {
		final Matcher matcher = this._pattern.matcher("program");

		Assert.assertFalse(matcher.matches());
		Assert.assertEquals(5, matcher.groupCount());

		Assert.assertFalse(matcher.lookingAt());
	}

	@Test
	public void testsimpleRegExNoParamsNoWhitespace_FailNoSemi() {
		final Matcher matcher = this._pattern.matcher("program HalloWelt");

		Assert.assertFalse(matcher.matches());
		Assert.assertEquals(5, matcher.groupCount());

		Assert.assertFalse(matcher.lookingAt());
	}
}
