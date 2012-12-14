package de.desy.language.snl.parser.nodes;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

public class OptionStatementNode_Test extends TestCase {

	@Test
	public void testNode() {
		final OptionStatementNode node = new OptionStatementNode("+r", 23, 42);

		Assert.assertEquals("+r", node.getSourceIdentifier());
		Assert.assertTrue(node.hasOffsets());
		Assert.assertEquals(23, node.getStatementStartOffset());
		Assert.assertEquals(42, node.getStatementEndOffset());
		Assert.assertEquals("option", node.getNodeTypeName());
		Assert.assertFalse(node.hasChildren());
		Assert.assertFalse(node.hasContent());
		Assert.assertEquals("option: +r", node.humanReadableRepresentation());
	}
}
