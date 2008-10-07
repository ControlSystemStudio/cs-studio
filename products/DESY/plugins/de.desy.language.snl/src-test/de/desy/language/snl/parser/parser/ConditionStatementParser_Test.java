package de.desy.language.snl.parser.parser;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import de.desy.language.snl.parser.nodes.BlockConditionNode;

public class ConditionStatementParser_Test extends TestCase {

	@Test
	public void testDoFindNext() {
		final ConditionStatementParser parser = new ConditionStatementParser();

		parser.findNext("(X < Y && pvGet(pv1) == 23)");

		Assert.assertTrue(parser.hasFoundElement());
		Assert.assertEquals("(X < Y && pvGet(pv1) == 23)", parser
				.getLastFoundStatement());
		final BlockConditionNode lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals("()", lastFoundAsNode.getSourceIdentifier());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(0, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(26, lastFoundAsNode.getStatementEndOffset());
		Assert.assertFalse(lastFoundAsNode.hasChildren());
		Assert.assertTrue(lastFoundAsNode.hasContent());
		Assert.assertEquals("X < Y && pvGet(pv1) == 23", lastFoundAsNode
				.getContent());

		Assert.assertEquals(parser.getEndOffsetLastFound(), lastFoundAsNode
				.getStatementEndOffset());
	}

	@Test
	public void testFindNextCharSequenceInt_FailNoBraces() {
		final ConditionStatementParser parser = new ConditionStatementParser();

		parser.findNext("abcdefg");
		Assert.assertFalse(parser.hasFoundElement());
	}

	@Test
	public void testFindNextCharSequenceInt_FailIncoorectBraces() {
		final ConditionStatementParser parser = new ConditionStatementParser();

		parser.findNext("))");
		Assert.assertFalse(parser.hasFoundElement());
	}

	@Test
	public void testFindNextCharSequenceInt_FailIncoorectBraces2() {
		final ConditionStatementParser parser = new ConditionStatementParser();

		parser.findNext("((())"); // 3 opens, 2 close, the other way would be
		// ok!
		Assert.assertFalse(parser.hasFoundElement());
	}

	@Test
	public void testFindNextCharSequenceInt_ComplexBraces() {
		final ConditionStatementParser parser = new ConditionStatementParser();

		parser.findNext("((()()))");
		Assert.assertTrue(parser.hasFoundElement());
		Assert.assertEquals("((()()))", parser.getLastFoundStatement());
		final BlockConditionNode lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals("()", lastFoundAsNode.getSourceIdentifier());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(0, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(7, lastFoundAsNode.getStatementEndOffset());
		Assert.assertFalse(lastFoundAsNode.hasChildren());
		Assert.assertTrue(lastFoundAsNode.hasContent());
		Assert.assertEquals("(()())", lastFoundAsNode.getContent());

		Assert.assertEquals(parser.getEndOffsetLastFound(), lastFoundAsNode
				.getStatementEndOffset());
	}

}
