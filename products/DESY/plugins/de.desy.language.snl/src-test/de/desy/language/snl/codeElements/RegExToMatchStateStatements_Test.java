package de.desy.language.snl.codeElements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

/**
 * A test to exam a pattern to match state statements.
 */
public class RegExToMatchStateStatements_Test extends TestCase {
	private Pattern _pattern;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();

		this._pattern = Pattern
				.compile("(state\\s+)([a-zA-Z][0-9a-zA-Z]*)(\\s*\\{)([\\s\\S]*)(\\}[;]?)");
	}

	@Test
	public void testsimpleRegEx() {
		final String when1 = " when (pvGet(xyz) == 42 ) { pvPut(computerOff, 1); pvPut(knockOffWork, TRUE); } state CallItADay  ";
		final Matcher matcher = this._pattern.matcher("state Feierabend {"
				+ when1 + "}");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(5, matcher.groupCount());
		Assert.assertEquals("state ", matcher.group(1)); // the state
		// statement
		Assert.assertEquals("Feierabend", matcher.group(2)); // the name of
		// the state
		Assert.assertEquals(" {", matcher.group(3)); // open brace
		Assert.assertEquals(when1, matcher.group(4)); // contents
		Assert.assertEquals("}", matcher.group(5)); // closing brace and
		// optional ';'
	}

	@Test
	public void testsimpleRegExMultipipleWhens() {
		final String when1 = " when (pvGet(xyz) == 42 ) { pvPut(computerOff, 1); pvPut(knockOffWork, TRUE); } state CallItADay  ";
		final String when2 = " when () { pvPut(computerOn, TRUE);} state InTheMorning  ";
		final Matcher matcher = this._pattern.matcher("state Feierabend {"
				+ when1 + when2 + "}");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(5, matcher.groupCount());
		Assert.assertEquals("state ", matcher.group(1)); // the state
		// statement
		Assert.assertEquals("Feierabend", matcher.group(2)); // the name of
		// the state
		Assert.assertEquals(" {", matcher.group(3)); // open brace
		Assert.assertEquals(when1 + when2, matcher.group(4)); // contents
		Assert.assertEquals("}", matcher.group(5)); // closing brace and
		// optional ';'
	}

	@Test
	public void testsimpleRegExNoWhens() {
		final Matcher matcher = this._pattern.matcher("state Feierabend {"
				+ "}");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(5, matcher.groupCount());
		Assert.assertEquals("state ", matcher.group(1)); // the state
		// statement
		Assert.assertEquals("Feierabend", matcher.group(2)); // the name of
		// the state
		Assert.assertEquals(" {", matcher.group(3)); // open brace
		Assert.assertEquals("", matcher.group(4)); // contents
		Assert.assertEquals("}", matcher.group(5)); // closing brace and
		// optional ';'
	}

}
