package de.desy.language.snl.codeElements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class RegExToMatchSingleEmbeddedC_Test extends TestCase {
	private Pattern _pattern;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();

		this._pattern = Pattern.compile("(%%)([^\n\r]*)([\n|\r|\n\r|\r\n]?)");
	}

	@Test
	public void testsimpleRegEx() {
		final Matcher matcher = this._pattern
				.matcher("%% This is embedded C in a programm\n");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(3, matcher.groupCount());
		Assert.assertEquals("%%", matcher.group(1)); // the single line
														// embedded c beginning
		Assert.assertEquals(" This is embedded C in a programm", matcher
				.group(2)); // the
																					// c
																					// statement
		Assert.assertEquals("\n", matcher.group(3)); // the line break
	}

	@Test
	public void testsimpleRegExWithNoLineBreak() {
		final Matcher matcher = this._pattern
				.matcher("%% This is embedded C in a programm");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(3, matcher.groupCount());
		Assert.assertEquals("%%", matcher.group(1)); // the single line
														// embedded c beginning
		Assert.assertEquals(" This is embedded C in a programm", matcher
				.group(2)); // the
																					// c
																					// statement
		Assert.assertEquals("", matcher.group(3)); // the line break
	}

	@Test
	public void testsimpleRegExWithOnePercent() {
		final Matcher matcher = this._pattern
				.matcher("% This is embedded C in a programm\n");
		Assert.assertFalse(matcher.matches());

		Assert.assertEquals(3, matcher.groupCount());
	}

}