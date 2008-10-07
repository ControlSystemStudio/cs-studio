package de.desy.language.snl.codeElements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class RegExToMatchAssignDeclaration_Test extends TestCase {
	private Pattern _pattern;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();

		this._pattern = Pattern
				.compile("(assign\\s+)([a-zA-Z][0-9a-zA-Z]*)(\\s+to\\s+)(\"[\\s\\S]*\")(\\s*;)");
	}

	@Test
	public void testsimpleRegEx() {
		final Matcher matcher = this._pattern
				.matcher("assign testVariable to \"test://ChannelName:VarName.VAL[hallo=welt]\";");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(5, matcher.groupCount());
		Assert.assertEquals("assign ", matcher.group(1)); // the assign
															// statement
		Assert.assertEquals("testVariable", matcher.group(2)); // variable name
		Assert.assertEquals(" to ", matcher.group(3)); // to with surrounding
														// whitespaces
		Assert.assertEquals("\"test://ChannelName:VarName.VAL[hallo=welt]\"",
				matcher.group(4)); // channel
																									// name
																									// with
																									// surrounding
																									// quotes
		Assert.assertEquals(";", matcher.group(5)); // optional whitespace and
													// ';'
	}

	@Test
	public void testsimpleRegExWithEmptyChannel() {
		final Matcher matcher = this._pattern
				.matcher("assign testVariable to \"\";");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(5, matcher.groupCount());
		Assert.assertEquals("assign ", matcher.group(1)); // the assign
															// statement
		Assert.assertEquals("testVariable", matcher.group(2)); // variable name
		Assert.assertEquals(" to ", matcher.group(3)); // to with surrounding
														// whitespaces
		Assert.assertEquals("\"\"", matcher.group(4)); // channel name with
														// surrounding quotes
		Assert.assertEquals(";", matcher.group(5)); // optional whitespace and
													// ';'
	}

	@Test
	public void testsimpleRegExWithNoVariable() {
		final Matcher matcher = this._pattern
				.matcher("assign to \"test://ChannelName:VarName.VAL[hallo=welt]\";");
		Assert.assertFalse(matcher.matches());

		Assert.assertEquals(5, matcher.groupCount());
	}

	@Test
	public void testsimpleRegExWithNoTo() {
		final Matcher matcher = this._pattern
				.matcher("assign testVariable \"test://ChannelName:VarName.VAL[hallo=welt]\";");
		Assert.assertFalse(matcher.matches());

		Assert.assertEquals(5, matcher.groupCount());
	}

	@Test
	public void testsimpleRegExWithNoQuotes() {
		final Matcher matcher = this._pattern
				.matcher("assign testVariable to test://ChannelName:VarName.VAL[hallo=welt];");
		Assert.assertFalse(matcher.matches());

		Assert.assertEquals(5, matcher.groupCount());
	}

	@Test
	public void testsimpleRegExWithNoChannel() {
		final Matcher matcher = this._pattern.matcher("assign testVariable ;");
		Assert.assertFalse(matcher.matches());

		Assert.assertEquals(5, matcher.groupCount());
	}

}