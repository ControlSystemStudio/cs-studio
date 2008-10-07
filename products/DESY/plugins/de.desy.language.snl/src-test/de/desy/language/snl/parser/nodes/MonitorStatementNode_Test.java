package de.desy.language.snl.parser.nodes;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

public class MonitorStatementNode_Test extends TestCase {

	@Test
	public void testNode() {
		final MonitorStatementNode node = new MonitorStatementNode(
				"variableName", 23, 42);

		Assert.assertEquals("variableName", node.getSourceIdentifier());
		Assert.assertTrue(node.hasOffsets());
		Assert.assertEquals(23, node.getStatementStartOffset());
		Assert.assertEquals(42, node.getStatementEndOffset());
		Assert.assertEquals("monitor", node.getNodeTypeName());
		Assert.assertFalse(node.hasChildren());
		Assert.assertFalse(node.hasContent());
		Assert.assertEquals("variableName is monitored", node
				.humanReadableRepresentation());
	}
}
