package de.desy.language.snl.parser.parser;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import de.desy.language.snl.parser.nodes.SingleLineEmbeddedCNode;

public class SingleLineEmbeddedCParser_Test extends TestCase {

	@Test
	public void testFindNextCharSequence() {
		final SingleLineEmbeddedCParser parser = new SingleLineEmbeddedCParser();
		parser.findNext("pvPut(x,2); \n %%Do embedded C \n pvGet(x);");
		Assert.assertTrue(parser.hasFoundElement());
		final SingleLineEmbeddedCNode lastFoundAsNode = parser
				.getLastFoundAsNode();
		Assert.assertNotNull(lastFoundAsNode);
		Assert.assertEquals("(embedded c line)", lastFoundAsNode
				.getSourceIdentifier());
		Assert.assertFalse(lastFoundAsNode.hasChildren());
		Assert.assertTrue(lastFoundAsNode.hasContent());
		Assert.assertEquals("Do embedded C ", lastFoundAsNode.getContent());
		Assert.assertEquals("%%Do embedded C \n", parser
				.getLastFoundStatement());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(14, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(30, lastFoundAsNode.getStatementEndOffset());
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
