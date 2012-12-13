package de.desy.language.snl.parser.parser;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import de.desy.language.snl.parser.nodes.StateSetNode;

public class StateSetParser_Test extends TestCase {
	private final String _source = "/*HAllo*/\nprogram sncExample;"
			+ "double v;" + "assign v to \"{user}:aiExample\";"
			+ "monitor v;\n\n\n" + "long l;\n\n" + "%{\n" + "   Embedded C\n"
			+ "}%\n" + "ss ss1 {" + "    state init {" + "	when (delay(0.1)) {"
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

	@Test
	public void testFindNextCharSequence() {
		final StateSetParser parser = new StateSetParser();

		parser.findNext(this._source);
		Assert.assertTrue(parser.hasFoundElement());
		Assert.assertEquals("ss ss1 {" + "    state init {"
				+ "	when (delay(0.1)) {"
				+ "	    printf(\"sncExample: Startup delay over\n\");"
				+ "	} state low" + "    }" + " /* Hallo Welt!*" + " ./. */"
				+ "    state low {" + "	    when (v > 50.0) {"
				+ "	        printf(\"sncExample: Changing to high\n\");"
				+ "/* +++" + "*/	    } state high" + "       "
				+ "       when ( delay(1.0) )" + "       {"
				+ "       } state low" + "   }" + "    state high {"
				+ "when (v <= 50.0) {"
				+ "	    printf(\"sncExample: Changing to low\n\");"
				+ "	} state low" + "        when ( delay(1.0) ) {"
				+ "       } state high" + "   }" + "}", parser
				.getLastFoundStatement());
		final StateSetNode lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals("ss1", lastFoundAsNode.getSourceIdentifier());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(111, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(558, lastFoundAsNode.getStatementEndOffset());

		Assert.assertEquals(parser.getEndOffsetLastFound(), lastFoundAsNode
				.getStatementEndOffset());
	}

	@Test()
	public void testMatchFoundWillFail() {
		try {
			final StateSetParser stateSetParser = new StateSetParser();
			stateSetParser.matchFound(null, null);
			Assert.fail("An UnsupportedOperationException was expected!");
		} catch (final UnsupportedOperationException uoe) {

		}
	}

	@Test()
	public void testMatchFoundWillFail2() {
		try {
			final StateSetParser stateSetParser = new StateSetParser();
			stateSetParser.getPrePatternString();
			Assert.fail("An UnsupportedOperationException was expected!");
		} catch (final UnsupportedOperationException uoe) {

		}
	}
}
