package de.desy.language.snl.parser.nodes;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

public class CharNode_Test extends TestCase {

	@Test
	public void testNode() {
		final CharNode node = new CharNode("'H'", 23, 42);

		Assert.assertEquals("'H'", node.getSourceIdentifier());
		Assert.assertTrue(node.hasOffsets());
		Assert.assertEquals(23, node.getStatementStartOffset());
		Assert.assertEquals(42, node.getStatementEndOffset());
		Assert.assertEquals("char", node.getNodeTypeName());
		Assert.assertFalse(node.hasChildren());
		Assert.assertFalse(node.hasContent());
		Assert.assertEquals("'H': char", node.humanReadableRepresentation());
	}
}
