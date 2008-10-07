package de.desy.language.snl.parser.parser;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import de.desy.language.snl.parser.nodes.BlockStatementNode;

public class BlockStatementParser_Test extends TestCase {

	private final String _source = "ss ABC { \n    state XY {\n        when () {\n         pvPut(X, 1);\n   }\n }\n}";

	private final String _source2 = "state init {\n"
			+ "		when (delay(0.1)) {\n"
			+ "			printf(\"sncExample: Startup delay over\");\n"
			+ "		} state low    \n" + "} /* Hallo Welt!* ./. */    \n"
			+ "state low {\n" + "	    when (v > 50.0) {\n"
			+ "	        printf(\"sncExample: Changing to high\");\n"
			+ "/* +++*/\n" + "	    }\n" + "state high {\n"
			+ "     when ( delay(1.0) )       {\n" + "     } state low  \n"
			+ "}\n" + "state high {\n" + "		when (v <= 50.0) {\n"
			+ "	    	printf(\"sncExample: Changing to low\");\n"
			+ "		} state low\n" + "     when ( delay(1.0) ) {\n"
			+ "     } state high\n" + "}\n" + "}";

	@Test
	public void testFindNextCharSequenceInt() {
		final BlockStatementParser parser = new BlockStatementParser();

		parser.findNext(this._source);
		Assert.assertTrue(parser.hasFoundElement());
		Assert
				.assertEquals(
						"{ \n    state XY {\n        when () {\n         pvPut(X, 1);\n   }\n }\n}",
						parser.getLastFoundStatement());
		final BlockStatementNode lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals(BlockStatementNode.class, lastFoundAsNode
				.getClass());
		Assert.assertEquals("{}", lastFoundAsNode.getSourceIdentifier());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(7, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(73, lastFoundAsNode.getStatementEndOffset());
		Assert.assertFalse(lastFoundAsNode.hasChildren());
		Assert.assertTrue(lastFoundAsNode.hasContent());
		Assert
				.assertEquals(
						" \n    state XY {\n        when () {\n         pvPut(X, 1);\n   }\n }\n",
						lastFoundAsNode.getContent());

		Assert.assertEquals(parser.getEndOffsetLastFound(), lastFoundAsNode
				.getStatementEndOffset());
	}

	@Test
	public void testFindNextCharSequenceWithinSource2() {
		final BlockStatementParser parser = new BlockStatementParser();

		parser.findNext(this._source2);
		Assert.assertTrue(parser.hasFoundElement());
		Assert.assertEquals("{\n" + "		when (delay(0.1)) {\n"
				+ "			printf(\"sncExample: Startup delay over\");\n"
				+ "		} state low    \n" + "}", parser.getLastFoundStatement());
		final BlockStatementNode lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals(BlockStatementNode.class, lastFoundAsNode
				.getClass());
		Assert.assertEquals("{}", lastFoundAsNode.getSourceIdentifier());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(11, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(98, lastFoundAsNode.getStatementEndOffset());
		Assert.assertFalse(lastFoundAsNode.hasChildren());
		Assert.assertTrue(lastFoundAsNode.hasContent());
		Assert.assertEquals("\n" + "		when (delay(0.1)) {\n"
				+ "			printf(\"sncExample: Startup delay over\");\n"
				+ "		} state low    \n", lastFoundAsNode.getContent());

		Assert.assertEquals(parser.getEndOffsetLastFound(), lastFoundAsNode
				.getStatementEndOffset());
	}

	@Test
	public void testFindNextCharSequenceInt_FailNoBraces() {
		final BlockStatementParser parser = new BlockStatementParser();

		parser.findNext("abcdefg");
		Assert.assertFalse(parser.hasFoundElement());
	}

	@Test
	public void testFindNextCharSequenceInt_FailIncoorectBraces() {
		final BlockStatementParser parser = new BlockStatementParser();

		parser.findNext("}}");
		Assert.assertFalse(parser.hasFoundElement());
	}

	@Test
	public void testFindNextCharSequenceInt_FailIncoorectBraces2() {
		final BlockStatementParser parser = new BlockStatementParser();

		parser.findNext("{{{}}"); // 3 opens, 2 close, the other way would be
		// ok!
		Assert.assertFalse(parser.hasFoundElement());
	}

	@Test
	public void testFindNextCharSequenceInt_ComplexBraces() {
		final BlockStatementParser parser = new BlockStatementParser();

		parser.findNext("{{{}{}}}");
		Assert.assertTrue(parser.hasFoundElement());
		Assert.assertEquals("{{{}{}}}", parser.getLastFoundStatement());
		final BlockStatementNode lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals(BlockStatementNode.class, lastFoundAsNode
				.getClass());
		Assert.assertEquals("{}", lastFoundAsNode.getSourceIdentifier());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(0, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(7, lastFoundAsNode.getStatementEndOffset());
		Assert.assertFalse(lastFoundAsNode.hasChildren());
		Assert.assertTrue(lastFoundAsNode.hasContent());
		Assert.assertEquals("{{}{}}", lastFoundAsNode.getContent());

		Assert.assertEquals(parser.getEndOffsetLastFound(), lastFoundAsNode
				.getStatementEndOffset());
	}
}
