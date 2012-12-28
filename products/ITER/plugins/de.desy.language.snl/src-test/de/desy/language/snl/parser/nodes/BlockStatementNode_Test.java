package de.desy.language.snl.parser.nodes;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

public class BlockStatementNode_Test extends TestCase {

	@Test
	public void testBlockStatementNode() {
		final BlockStatementNode blockStatementNode = new BlockStatementNode(
				" pvPut(); // XXX", 23, 42);
		Assert.assertTrue(blockStatementNode.hasContent());
		Assert
				.assertEquals(" pvPut(); // XXX", blockStatementNode
						.getContent());
		Assert.assertEquals("{}", blockStatementNode.getSourceIdentifier());
		Assert.assertEquals("statement block", blockStatementNode
				.getNodeTypeName());
		Assert.assertFalse(blockStatementNode.hasChildren());
		Assert.assertEquals(23, blockStatementNode.getStatementStartOffset());
		Assert.assertEquals(42, blockStatementNode.getStatementEndOffset());
	}

}
