package de.desy.language.snl.codeElements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class RegExToMatchTextWithoutSubsequent_Test extends TestCase {
	private Pattern _pattern;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();

		this._pattern = Pattern.compile("(assign\\s+)([\\s\\S]*)(test)");
	}

	@Test
	public void testsimpleRegEx() {
		final Matcher matcher = this._pattern
				.matcher("assign testVariable to \"test://ChannelName:VarName.VAL[hallo=welt]\";test");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(3, matcher.groupCount());
		Assert.assertEquals("assign ", matcher.group(1)); // the assign
															// statement
		Assert
				.assertEquals(
						"testVariable to \"test://ChannelName:VarName.VAL[hallo=welt]\";",
						matcher.group(2)); // variable
																													// name
		Assert.assertEquals("test", matcher.group(3)); // to with surrounding
														// whitespaces
	}

}