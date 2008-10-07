package de.desy.language.snl.parser.nodes;

import junit.framework.Assert;
import junit.framework.TestCase;

public class WhenNode_Test extends TestCase {
	public void testWhenNodeWithoutContent() {
		final WhenNode whenNode = new WhenNode("x < y", "NEXT_ONE", 23, 42);

		Assert.assertEquals("x < y", whenNode.getSourceIdentifier());
		Assert.assertEquals("NEXT_ONE", whenNode.getFollowingState());
		Assert.assertTrue(whenNode.hasOffsets());
		Assert.assertEquals(23, whenNode.getStatementStartOffset());
		Assert.assertEquals(42, whenNode.getStatementEndOffset());
		Assert.assertEquals("when", whenNode.getNodeTypeName());
		Assert.assertFalse(whenNode.hasChildren());
		Assert.assertFalse(whenNode.hasContent());
		Assert.assertEquals("when (x < y) -> NEXT_ONE", whenNode
				.humanReadableRepresentation());
	}

	public void testWhenNodeWithContent() {
		final WhenNode whenNode = new WhenNode("x < y", "pvPut(x,1);",
				"NEXT_ONE", 23, 42);

		Assert.assertEquals("x < y", whenNode.getSourceIdentifier());
		Assert.assertEquals("NEXT_ONE", whenNode.getFollowingState());
		Assert.assertTrue(whenNode.hasOffsets());
		Assert.assertEquals(23, whenNode.getStatementStartOffset());
		Assert.assertEquals(42, whenNode.getStatementEndOffset());
		Assert.assertEquals("when", whenNode.getNodeTypeName());
		Assert.assertFalse(whenNode.hasChildren());
		Assert.assertTrue(whenNode.hasContent());
		Assert.assertEquals("pvPut(x,1);", whenNode.getContent());
		Assert.assertEquals("when (x < y) -> NEXT_ONE", whenNode
				.humanReadableRepresentation());
	}
}
