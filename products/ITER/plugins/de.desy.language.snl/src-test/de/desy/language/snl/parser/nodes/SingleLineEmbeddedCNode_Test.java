package de.desy.language.snl.parser.nodes;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

public class SingleLineEmbeddedCNode_Test extends TestCase {

	@Test
	public void testNode() {
		final SingleLineEmbeddedCNode singleLineEmbeddedCNode = new SingleLineEmbeddedCNode(
				" hello this is c ", 23, 42);

		Assert.assertEquals("(embedded c line)", singleLineEmbeddedCNode
				.getSourceIdentifier());
		Assert.assertTrue(singleLineEmbeddedCNode.hasOffsets());
		Assert.assertEquals(23, singleLineEmbeddedCNode
				.getStatementStartOffset());
		Assert
				.assertEquals(42, singleLineEmbeddedCNode
						.getStatementEndOffset());
		Assert.assertEquals("Embedded c line", singleLineEmbeddedCNode
				.getNodeTypeName());
		Assert.assertFalse(singleLineEmbeddedCNode.hasChildren());
		Assert.assertTrue(singleLineEmbeddedCNode.hasContent());
		Assert.assertEquals(" hello this is c ", singleLineEmbeddedCNode
				.getContent());
		Assert.assertEquals("Embedded c line", singleLineEmbeddedCNode
				.humanReadableRepresentation());
	}

}
