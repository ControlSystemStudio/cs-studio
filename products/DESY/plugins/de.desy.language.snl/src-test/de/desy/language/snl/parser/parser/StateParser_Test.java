package de.desy.language.snl.parser.parser;

import junit.framework.Assert;

import org.junit.Test;

import de.desy.language.snl.parser.nodes.StateNode;

public class StateParser_Test {

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
		final StateParser parser = new StateParser();

		parser.findNext(this._source);
		Assert.assertTrue(parser.hasFoundElement());
		Assert.assertEquals("state init {" + "	when (delay(0.1)) {"
				+ "	    printf(\"sncExample: Startup delay over\n\");"
				+ "	} state low" + "    }", parser.getLastFoundStatement());
		final StateNode lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals(StateNode.class, lastFoundAsNode.getClass());
		Assert.assertEquals("init", lastFoundAsNode.getSourceIdentifier());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(123, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(218, lastFoundAsNode.getStatementEndOffset());

		Assert.assertEquals(parser.getEndOffsetLastFound(), lastFoundAsNode
				.getStatementEndOffset());
	}

	@Test
	public void testFindNextCharSequenceWithEmptyBlock() {
		final StateParser parser = new StateParser();

		parser.findNext("state init {}");
		Assert.assertTrue(parser.hasFoundElement());
		Assert.assertEquals("state init {}", parser.getLastFoundStatement());
		final StateNode lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals(StateNode.class, lastFoundAsNode.getClass());
		Assert.assertEquals("init", lastFoundAsNode.getSourceIdentifier());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(0, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(12, lastFoundAsNode.getStatementEndOffset());

		Assert.assertEquals(parser.getEndOffsetLastFound(), lastFoundAsNode
				.getStatementEndOffset());
	}

	@Test()
	public void testMatchFoundWillFail() {
		try {
			final StateParser stateParser = new StateParser();
			stateParser.matchFound(null, null);
			Assert.fail("An UnsupportedOperationException was expected!");
		} catch (final UnsupportedOperationException uoe) {

		}
	}

	@Test(timeout=1000)
	public void testBUG2136() {
	    final StateParser parser = new StateParser();
	
	    parser.findNext("state init ");
	    Assert.assertFalse(parser.hasFoundElement());
	}
}
