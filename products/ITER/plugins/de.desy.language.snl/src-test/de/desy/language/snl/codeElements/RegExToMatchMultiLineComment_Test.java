package de.desy.language.snl.codeElements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class RegExToMatchMultiLineComment_Test extends TestCase {
	private Pattern _pattern;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();

		this._pattern = Pattern.compile("(/\\*)([\\S\\s]*)(\\*/)");
	}

	@Test
	public void testsimpleRegEx() {
		final Matcher matcher = this._pattern
				.matcher("/* This is a comment in a program\nHere it goes furher */");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(3, matcher.groupCount());
		Assert.assertEquals("/*", matcher.group(1)); // the single line
														// embedded c beginning
		Assert.assertEquals(
				" This is a comment in a program\nHere it goes furher ",
				matcher.group(2)); // the
																										// c
																										// statement
		Assert.assertEquals("*/", matcher.group(3)); // the line break
	}

	@Test
	public void testsimpleRegExEmptyComment() {
		final Matcher matcher = this._pattern.matcher("/**/");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(3, matcher.groupCount());
		Assert.assertEquals("/*", matcher.group(1)); // the single line
														// embedded c beginning
		Assert.assertEquals("", matcher.group(2)); // the c statement
		Assert.assertEquals("*/", matcher.group(3)); // the line break
	}

	@Test
	public void testsimpleRegExAstericsComment() {
		final Matcher matcher = this._pattern.matcher("/********/");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(3, matcher.groupCount());
		Assert.assertEquals("/*", matcher.group(1)); // the single line
														// embedded c beginning
		Assert.assertEquals("******", matcher.group(2)); // the c statement
		Assert.assertEquals("*/", matcher.group(3)); // the line break
	}

	@Test
	public void testsimpleRegEx_FailWithMissingStart() {
		final Matcher matcher = this._pattern
				.matcher("/ This is a comment in a programm */");
		Assert.assertFalse(matcher.matches());

		Assert.assertEquals(3, matcher.groupCount());
	}

	@Test
	public void testsimpleRegEx_FailWithMissingEnd() {
		final Matcher matcher = this._pattern
				.matcher("/* This is a comment in a programm /");
		Assert.assertFalse(matcher.matches());

		Assert.assertEquals(3, matcher.groupCount());
	}

}