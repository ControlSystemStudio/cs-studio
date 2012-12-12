package de.desy.language.snl.codeElements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class RegExToMatchMonitorDeclaration_Test extends TestCase {
	private Pattern _pattern;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();

		this._pattern = Pattern
				.compile("(monitor\\s+)([a-zA-Z][0-9a-zA-Z]*)(\\s*;)");
	}

	@Test
	public void testsimpleRegExAsLong() {
		final Matcher matcher = this._pattern.matcher("monitor testMonitor ;");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(3, matcher.groupCount());
		Assert.assertEquals("monitor ", matcher.group(1)); // the type
		Assert.assertEquals("testMonitor", matcher.group(2)); // variable name
		Assert.assertEquals(" ;", matcher.group(3)); // optional whitespace
														// and ';'
	}

	@Test
	public void testsimpleRegExWithNoName() {
		final Matcher matcher = this._pattern.matcher("monitor;");
		Assert.assertFalse(matcher.matches());

		Assert.assertEquals(3, matcher.groupCount());
	}

	@Test
	public void testsimpleRegExWithIllegalName() {
		final Matcher matcher = this._pattern.matcher("monitor 123;");
		Assert.assertFalse(matcher.matches());

		Assert.assertEquals(3, matcher.groupCount());
	}

}