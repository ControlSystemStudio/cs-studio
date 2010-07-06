package de.desy.language.snl.codeElements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;
import junit.framework.TestCase;

public class FindWithRegExInStringExperiment_Test extends TestCase {
	private final String _source = "program sncExample;" + "double v;"
			+ "assign v to \"{user}:aiExample\";" + "monitor v;" + "ss ss1 {"
			+ "    state init {" + "	when (delay(0.1)) {"
			+ "	    printf(\"sncExample: Startup delay over\n\");"
			+ "	} state low" + "    }" + " /* Hallo Welt!*" + " ./. */"
			+ "    state low {" + "	    when (v > 50.0) {"
			+ "	        printf(\"sncExample: Changing to high\n\");" + "/* +++"
			+ "*/	    } state high" + "       " + "       when ( delay(1.0) )"
			+ "       {" + "       } state low" + "   }" + "    state high {"
			+ "when (v <= 50.0) {"
			+ "	    printf(\"sncExample: Changing to low\n\");"
			+ "	} state low" + "        when ( delay(1.0) ) {"
			+ "       } state high" + "   }" + "}";

	public void testFindWithRegEx() {
		final Pattern prePattern = Pattern.compile("(\\*/)");
		final Matcher preMatcher = prePattern.matcher(this._source);
		preMatcher.find();
		final Pattern pattern = Pattern.compile("(/\\*)([\\S\\s]*)(\\*/)");
		final Matcher matcher = pattern.matcher(this._source);
		matcher.region(0, preMatcher.end());
		Assert.assertTrue(matcher.find());
		Assert.assertEquals(178, matcher.start());
		Assert.assertEquals(200, matcher.end());
	}

	public void testFindWithRegExForWhen() {
		final Pattern prePattern = Pattern
				.compile("(\\}\\s*state\\s*)([a-zA-Z][0-9a-zA-Z]*)([;]?)");
		final Matcher preMatcher = prePattern.matcher(this._source);
		preMatcher.find();
		final Pattern pattern = Pattern
				.compile("(when)(\\s*\\()([\\S\\s]*[0-9a-zA-Z]*)(\\)\\s*\\{)([\\S\\s]*)(\\}\\s*state\\s*)([\\S\\s]*[a-zA-Z][0-9a-zA-Z]*)([;]?)");
		final Matcher matcher = pattern.matcher(this._source);
		matcher.region(0, preMatcher.end());
		Assert.assertTrue(matcher.find());
		Assert.assertEquals(94, matcher.start());
		Assert.assertEquals(172, matcher.end());
	}
}
