package de.desy.language.snl.codeElements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class RegExToMatchMultiLineEmbeddedC_Test extends TestCase {
	private Pattern _pattern;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();

		this._pattern = Pattern
				.compile("(%\\{\\s*[\n\r]+)([\\S\\s]*)([\n\r]+\\s*\\}%\\s*[\n\r]+)"); // ([\\S\\s]*)([\n\r]+\\s*}%\\s*[\\n|\\r|\\r\\n|\\n\\r])");
	}

	@Test
	public void testsimpleRegEx() {
		final Matcher matcher = this._pattern
				.matcher("%{  \nThis is embedded C \nDaddel Du...\n  }%  \n");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(3, matcher.groupCount());
		Assert.assertEquals("%{  \n", matcher.group(1)); // the single line
															// embedded c
															// beginning
		Assert.assertEquals("This is embedded C \nDaddel Du...", matcher
				.group(2)); // the
																					// c
																					// statement
		Assert.assertEquals("\n  }%  \n", matcher.group(3)); // the line
																// break
	}

	@Test
	public void testsimpleRegExEmptyC() {
		final Matcher matcher = this._pattern.matcher("%{  \n\n  }%  \n");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(3, matcher.groupCount());
		Assert.assertEquals("%{  \n", matcher.group(1)); // the single line
															// embedded c
															// beginning
		Assert.assertEquals("", matcher.group(2)); // the c statement
		Assert.assertEquals("\n  }%  \n", matcher.group(3)); // the line
																// break
	}

	@Test
	public void testsimpleRegExEmbeddedCStatementInEmbeddedC() {
		final Matcher matcher = this._pattern
				.matcher("%{  \nThis is embedded }% C \nDaddel Du...\n  }%  \n");
		Assert.assertTrue(matcher.matches());

		Assert.assertEquals(3, matcher.groupCount());
		Assert.assertEquals("%{  \n", matcher.group(1)); // the single line
															// embedded c
															// beginning
		Assert.assertEquals("This is embedded }% C \nDaddel Du...", matcher
				.group(2)); // the
																						// c
																						// statement
		Assert.assertEquals("\n  }%  \n", matcher.group(3)); // the line
																// break
	}

	@Test
	public void testsimpleRegEx_FailWithMissingStart() {
		final Matcher matcher = this._pattern
				.matcher("%  \nThis is embedded }% C \nDaddel Du...\n  }%  \n");
		Assert.assertFalse(matcher.matches());

		Assert.assertEquals(3, matcher.groupCount());
	}

	@Test
	public void testsimpleRegEx_FailWithMissingEnd() {
		final Matcher matcher = this._pattern
				.matcher("%{  \nThis is embedded }% C \nDaddel Du...  }%  \n");
		Assert.assertFalse(matcher.matches());

		Assert.assertEquals(3, matcher.groupCount());
	}

}