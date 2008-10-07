package de.desy.language.snl.codeElements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class RegExToMatchStateSetStatements_Test extends TestCase {
	private Pattern _pattern;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();

		this._pattern = Pattern
				.compile("(ss\\s+)([a-zA-Z][0-9a-zA-Z]*)(\\s*\\{)([\\s\\S]*)(\\}[;]?)");
	}

	@Test
	public void testsimpleRegEx() {
		final String state1 = "state Feierabend { when (pvGet(xyz) == 42 ) { pvPut(computerOff, 1); pvPut(knockOffWork, TRUE); } state CallItADay  }";
		final Matcher matcher = this._pattern.matcher("ss WorkingStates {"
				+ state1 + "}");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(5, matcher.groupCount());
		Assert.assertEquals("ss ", matcher.group(1)); // the state statement
		Assert.assertEquals("WorkingStates", matcher.group(2)); // the name of
																// the stateset
		Assert.assertEquals(" {", matcher.group(3)); // open brace
		Assert.assertEquals(state1, matcher.group(4)); // contents
		Assert.assertEquals("}", matcher.group(5)); // closing brace and
													// optional ';'
	}

	@Test
	public void testsimpleRegExWithFinishingSemikolon() {
		final String state1 = "state Feierabend { when (pvGet(xyz) == 42 ) { pvPut(computerOff, 1); pvPut(knockOffWork, TRUE); } state CallItADay  }";
		final Matcher matcher = this._pattern.matcher("ss WorkingStates {"
				+ state1 + "};");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(5, matcher.groupCount());
		Assert.assertEquals("ss ", matcher.group(1)); // the state statement
		Assert.assertEquals("WorkingStates", matcher.group(2)); // the name of
																// the stateset
		Assert.assertEquals(" {", matcher.group(3)); // open brace
		Assert.assertEquals(state1, matcher.group(4)); // contents
		Assert.assertEquals("};", matcher.group(5)); // closing brace and
														// optional ';'
	}

	@Test
	public void testsimpleRegExWithEmptyContent() {
		final Matcher matcher = this._pattern.matcher("ss WorkingStates {"
				+ "}");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(5, matcher.groupCount());
		Assert.assertEquals("ss ", matcher.group(1)); // the state statement
		Assert.assertEquals("WorkingStates", matcher.group(2)); // the name of
																// the stateset
		Assert.assertEquals(" {", matcher.group(3)); // open brace
		Assert.assertEquals("", matcher.group(4)); // contents
		Assert.assertEquals("}", matcher.group(5)); // closing brace and
													// optional ';'
	}

	@Test
	public void testsimpleRegExWithTwoStates() {
		final String state1 = "state Feierabend { when (pvGet(xyz) == 42 ) { pvPut(computerOff, 1); pvPut(knockOffWork, TRUE); } state CallItADay  }";
		final String state2 = " state Wochenende { when (pvGet(xyz) == 42 ) { pvPut(knockOffWork, TRUE); } state CallItHappyDays  }";
		final Matcher matcher = this._pattern.matcher("ss WorkingStates {"
				+ state1 + state2 + "}");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(5, matcher.groupCount());
		Assert.assertEquals("ss ", matcher.group(1)); // the state statement
		Assert.assertEquals("WorkingStates", matcher.group(2)); // the name of
																// the stateset
		Assert.assertEquals(" {", matcher.group(3)); // open brace
		Assert.assertEquals(state1 + state2, matcher.group(4)); // contents
		Assert.assertEquals("}", matcher.group(5)); // closing brace and
													// optional ';'
	}
}
