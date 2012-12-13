package de.desy.language.snl.parser.nodes;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import de.desy.language.editor.core.parser.Node;

public class EventFlagNode_Test extends TestCase {
	@Test
	public void testNode() {
		final EventFlagNode node = new EventFlagNode("eventFlagName", 23, 42);

		Assert.assertEquals("eventFlagName", node.getSourceIdentifier());
		Assert.assertTrue(node.hasOffsets());
		Assert.assertEquals(23, node.getStatementStartOffset());
		Assert.assertEquals(42, node.getStatementEndOffset());
		Assert.assertEquals("event flag", node.getNodeTypeName());
		Assert.assertFalse(node.hasChildren());
		Assert.assertFalse(node.hasContent());
		Assert.assertEquals("event flag: eventFlagName", node
				.humanReadableRepresentation());
	}

	public void testSynchronizedVariable() {
		final EventFlagNode node = new EventFlagNode("eventFlagName", 23, 42);
		final SyncStatementNode syncNode = new SyncStatementNode(
				"eventFlagName", "variableName", 23, 42);

		Assert.assertFalse(node.isSynchronized());
		node.setSynchronized(syncNode);

		Assert.assertTrue(node.isSynchronized());
		Assert.assertEquals("variableName", node.getSynchronizedVariableName());
		Assert.assertTrue(node.hasChildren());
		final Node[] childrenNodes = node.getChildrenNodesAsArray();
		Assert.assertEquals(1, childrenNodes.length);
		Assert.assertEquals("variableName",
				((SyncStatementNode) childrenNodes[0]).getContent());

		Assert.assertEquals(
				"event flag: eventFlagName synchronized with variableName",
				node.humanReadableRepresentation());
	}

}
