package de.desy.language.snl.parser.parser;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import de.desy.language.snl.parser.nodes.SingleLineCommentNode;

public class SingleLineCommentParser_Test extends TestCase {

	@Test
	public void testFindNextCharSequence() {
		final SingleLineCommentParser parser = new SingleLineCommentParser();
		parser.findNext("pvPut(x,2); \n //This is a comment\n pvGet(x);");
		Assert.assertTrue(parser.hasFoundElement());
		final SingleLineCommentNode lastFoundAsNode = parser
				.getLastFoundAsNode();
		Assert.assertNotNull(lastFoundAsNode);
		Assert.assertEquals("(single line comment)", lastFoundAsNode
				.getSourceIdentifier());
		Assert.assertFalse(lastFoundAsNode.hasChildren());
		Assert.assertTrue(lastFoundAsNode.hasContent());
		Assert.assertEquals("This is a comment", lastFoundAsNode.getContent());
		Assert.assertEquals("//This is a comment", parser
				.getLastFoundStatement());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(14, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(33, lastFoundAsNode.getStatementEndOffset());
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
