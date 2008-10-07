package de.desy.language.snl.parser.nodes;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

public class StringNode_Test extends TestCase {

	@Test
	public void testNode() {
		final StringNode node = new StringNode("\"Hallo\"", 23, 42);

		Assert.assertEquals("\"Hallo\"", node.getSourceIdentifier());
		Assert.assertTrue(node.hasOffsets());
		Assert.assertEquals(23, node.getStatementStartOffset());
		Assert.assertEquals(42, node.getStatementEndOffset());
		Assert.assertEquals("string", node.getNodeTypeName());
		Assert.assertFalse(node.hasChildren());
		Assert.assertFalse(node.hasContent());
		Assert.assertEquals("\"Hallo\": string", node
				.humanReadableRepresentation());
	}
}
