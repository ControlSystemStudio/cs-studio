package de.desy.language.snl.codeElements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class RegExToMatchVariableDeclaration_Test extends TestCase {
	private Pattern _pattern;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();

		this._pattern = Pattern
				.compile("(long|double|char|string|int|short)(\\s+)([a-zA-Z][0-9a-zA-Z]*)(\\s*;)");
	}

	@Test
	public void testsimpleRegExAsLong() {
		final Matcher matcher = this._pattern.matcher("long longVariable ;");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(4, matcher.groupCount());
		Assert.assertEquals("long", matcher.group(1)); // the type
		Assert.assertEquals(" ", matcher.group(2)); // at least one whitespace
		Assert.assertEquals("longVariable", matcher.group(3)); // variable name
		Assert.assertEquals(" ;", matcher.group(4)); // optional whitespace
														// and ';'
	}

	@Test
	public void testsimpleRegExAsString() {
		final Matcher matcher = this._pattern
				.matcher("string   stringVariable;");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(4, matcher.groupCount());
		Assert.assertEquals("string", matcher.group(1)); // the type
		Assert.assertEquals("   ", matcher.group(2)); // at least one
														// whitespace
		Assert.assertEquals("stringVariable", matcher.group(3)); // variable
																	// name
		Assert.assertEquals(";", matcher.group(4)); // optional whitespace and
													// ';'
	}

	@Test
	public void testsimpleRegExWithIllegalType() {
		final Matcher matcher = this._pattern.matcher("work stringVariable;");
		Assert.assertFalse(matcher.matches());

		Assert.assertEquals(4, matcher.groupCount());
	}

}