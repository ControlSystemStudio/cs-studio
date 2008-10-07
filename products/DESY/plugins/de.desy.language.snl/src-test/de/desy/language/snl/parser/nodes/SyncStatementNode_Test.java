package de.desy.language.snl.parser.nodes;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

public class SyncStatementNode_Test extends TestCase {

	@Test
	public void testNode() {
		final SyncStatementNode node = new SyncStatementNode("eventFlagName",
				"variableName", 23, 42);

		Assert.assertEquals("eventFlagName", node.getSourceIdentifier());
		Assert.assertTrue(node.hasOffsets());
		Assert.assertEquals(23, node.getStatementStartOffset());
		Assert.assertEquals(42, node.getStatementEndOffset());
		Assert.assertEquals("sync", node.getNodeTypeName());
		Assert.assertFalse(node.hasChildren());
		Assert.assertTrue(node.hasContent());
		Assert.assertEquals("variableName", node.getContent());
		Assert.assertEquals(
				"sync: eventFlagName is synchronized with variableName", node
						.humanReadableRepresentation());
	}
}
