package de.desy.language.snl.parser.parser;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import de.desy.language.snl.parser.nodes.ProgramNode;

public class ProgramParser_Test extends TestCase {

	private final String _source = "/*HAllo*/\nprogram sncExample;"
			+ "double v;" + "assign v to \"{user}:aiExample\";" + "monitor v;"
			+ "%{\n" + "   Embedded C\n" + "}%\n" + "ss ss1 {"
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

	private final String _secondSource = "program sncGliu\n" + "\n"
			+ "/* options */\n" + "option  +r;\n" + "//....\n";

	private final String _thirdSource = "program level_check\n" + "\n"
			+ "float v;";

	private final String _fourthSource = "program ; \n\nprogram 1level\n;";

	@Test
	public void testFindNextCharSequenceInt() {
		final ProgramParser parser = new ProgramParser();
		parser.findNext(this._source);
		Assert.assertTrue(parser.hasFoundElement());
		Assert.assertEquals("program sncExample;", parser
				.getLastFoundStatement());
		final ProgramNode lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals(ProgramNode.class, lastFoundAsNode.getClass());
		Assert.assertEquals("sncExample", lastFoundAsNode.getProgramName());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(10, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(28, lastFoundAsNode.getStatementEndOffset());
	}

	@Test
	public void testFindNextCharSequenceIntSecondSource() {
		final ProgramParser parser = new ProgramParser();
		parser.findNext(this._secondSource);
		Assert.assertTrue(parser.hasFoundElement());
		Assert
				.assertEquals("program sncGliu\n", parser
						.getLastFoundStatement());
		final ProgramNode lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals(ProgramNode.class, lastFoundAsNode.getClass());
		Assert.assertEquals("sncGliu", lastFoundAsNode.getProgramName());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(0, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(15, lastFoundAsNode.getStatementEndOffset());
	}

	@Test
	public void testFindNextCharSequenceIntThirdSource() {
		final ProgramParser parser = new ProgramParser();
		parser.findNext(this._thirdSource);
		Assert.assertTrue(parser.hasFoundElement());
		Assert.assertEquals("program level_check\n", parser
				.getLastFoundStatement());
		final ProgramNode lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals(ProgramNode.class, lastFoundAsNode.getClass());
		Assert.assertEquals("level_check", lastFoundAsNode.getProgramName());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(0, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(19, lastFoundAsNode.getStatementEndOffset());
	}

	@Test
	public void testFindNextCharSequenceIntFourthSource() {
		final ProgramParser parser = new ProgramParser();
		parser.findNext(this._fourthSource);
		Assert.assertFalse(parser.hasFoundElement());
		parser.findNext(this._fourthSource, 11);
		Assert.assertFalse(parser.hasFoundElement());
	}

}
