package de.desy.language.snl.parser.nodes;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

public class BlockConditionNode_Test extends TestCase {

	@Test
	public void testBlockConditionNode() {
		final BlockConditionNode blockConditionNode = new BlockConditionNode(
				"X < Y", 23, 42);
		Assert.assertTrue(blockConditionNode.hasContent());
		Assert.assertEquals("X < Y", blockConditionNode.getContent());
		Assert.assertEquals("()", blockConditionNode.getSourceIdentifier());
		Assert.assertEquals("condition block", blockConditionNode
				.getNodeTypeName());
		Assert.assertFalse(blockConditionNode.hasChildren());
		Assert.assertEquals(23, blockConditionNode.getStatementStartOffset());
		Assert.assertEquals(42, blockConditionNode.getStatementEndOffset());
	}

}
