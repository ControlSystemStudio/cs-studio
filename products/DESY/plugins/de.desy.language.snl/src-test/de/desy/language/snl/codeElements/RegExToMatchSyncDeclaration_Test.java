package de.desy.language.snl.codeElements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class RegExToMatchSyncDeclaration_Test extends TestCase {
	private Pattern _pattern;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();

		this._pattern = Pattern
				.compile("(sync\\s+)([a-zA-Z][0-9a-zA-Z]*)(\\s+to\\s+)([a-zA-Z][0-9a-zA-Z]*)(\\s*;)");
	}

	@Test
	public void testsimpleRegEx() {
		final Matcher matcher = this._pattern
				.matcher("sync testVariable to testFlag;");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(5, matcher.groupCount());
		Assert.assertEquals("sync ", matcher.group(1)); // the assign statement
		Assert.assertEquals("testVariable", matcher.group(2)); // variable name
		Assert.assertEquals(" to ", matcher.group(3)); // to with surrounding
														// whitespaces
		Assert.assertEquals("testFlag", matcher.group(4)); // flag name
		Assert.assertEquals(";", matcher.group(5)); // optional whitespace and
													// ';'
	}

	@Test
	public void testsimpleRegExWithNoVariable() {
		final Matcher matcher = this._pattern.matcher("sync to testFlag;");
		Assert.assertFalse(matcher.matches());

		Assert.assertEquals(5, matcher.groupCount());
	}

	@Test
	public void testsimpleRegExWithNoTo() {
		final Matcher matcher = this._pattern
				.matcher("sync testVariable testFlag;");
		Assert.assertFalse(matcher.matches());

		Assert.assertEquals(5, matcher.groupCount());
	}

	@Test
	public void testsimpleRegExWithQuotes() {
		final Matcher matcher = this._pattern
				.matcher("sync testVariable to \"testFlag\";");
		Assert.assertFalse(matcher.matches());

		Assert.assertEquals(5, matcher.groupCount());
	}

	@Test
	public void testsimpleRegExWithNoChannel() {
		final Matcher matcher = this._pattern.matcher("sync testVariable ;");
		Assert.assertFalse(matcher.matches());

		Assert.assertEquals(5, matcher.groupCount());
	}

}