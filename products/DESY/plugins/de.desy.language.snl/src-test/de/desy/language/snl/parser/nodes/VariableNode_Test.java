package de.desy.language.snl.parser.nodes;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import de.desy.language.editor.core.parser.Node;

public class VariableNode_Test extends TestCase {
	@Test
	public void testNode() {
		final VariableNode node = new VariableNode("variableName", "long", 23,
				42, true);

		Assert.assertEquals("variableName", node.getSourceIdentifier());
		Assert.assertEquals("long", node.getTypeName());
		Assert.assertTrue(node.hasOffsets());
		Assert.assertEquals(23, node.getStatementStartOffset());
		Assert.assertEquals(42, node.getStatementEndOffset());
		Assert.assertEquals("Variable", node.getNodeTypeName());
		Assert.assertFalse(node.hasChildren());
		Assert.assertFalse(node.hasContent());
		Assert.assertEquals("Variable: variableName (Type: long)", node
				.humanReadableRepresentation());
		Assert.assertTrue(node.isArray());
	}

	public void testAssignVariable() {
		final VariableNode node = new VariableNode("variableName", "long", 23,
				42, false);
		final AssignStatementNode assignStatementNode = new AssignStatementNode(
				"variableName", "epics://krykWeather.temp_ai.VAL", 23, 42, false);

		Assert.assertFalse(node.isAssigned());
		node.setAssignedChannel(assignStatementNode);

		Assert.assertTrue(node.isAssigned());
		Assert.assertEquals("epics://krykWeather.temp_ai.VAL", node
				.getAssignedChannelName());
		Assert.assertTrue(node.hasChildren());
		final Node[] childrenNodes = node.getChildrenNodesAsArray();
		Assert.assertEquals(1, childrenNodes.length);
		Assert.assertEquals("epics://krykWeather.temp_ai.VAL",
				((AssignStatementNode) childrenNodes[0]).getContent());

		Assert
				.assertEquals(
						"Variable: variableName (Type: long, assigned to \"epics://krykWeather.temp_ai.VAL\")",
						node.humanReadableRepresentation());
		Assert.assertFalse(node.isArray());
	}

	public void testMonitoredVariable() {
		final VariableNode node = new VariableNode("variableName", "long", 23,
				42, false);
		final MonitorStatementNode monitorNode = new MonitorStatementNode(
				"variableName", 23, 42);

		Assert.assertFalse(node.isMonitored());

		node.setMonitored(monitorNode);

		Assert.assertTrue(node.isMonitored());

		Assert.assertEquals("Variable: variableName (Type: long, monitored)",
				node.humanReadableRepresentation());
	}

	public void testAssignAndMonitoredVariable() {
		final VariableNode node = new VariableNode("variableName", "long", 23,
				42, false);
		final AssignStatementNode assignStatementNode = new AssignStatementNode(
				"variableName", "epics://krykWeather.temp_ai.VAL", 23, 42, false);
		final MonitorStatementNode monitorNode = new MonitorStatementNode(
				"variableName", 23, 42);

		Assert.assertFalse(node.isAssigned());
		node.setAssignedChannel(assignStatementNode);

		Assert.assertTrue(node.isAssigned());
		Assert.assertEquals("epics://krykWeather.temp_ai.VAL", node
				.getAssignedChannelName());
		Assert.assertTrue(node.hasChildren());
		final Node[] childrenNodes = node.getChildrenNodesAsArray();
		Assert.assertEquals(1, childrenNodes.length);
		Assert.assertEquals("epics://krykWeather.temp_ai.VAL",
				((AssignStatementNode) childrenNodes[0]).getContent());

		Assert
				.assertEquals(
						"Variable: variableName (Type: long, assigned to \"epics://krykWeather.temp_ai.VAL\")",
						node.humanReadableRepresentation());

		Assert.assertFalse(node.isMonitored());

		node.setMonitored(monitorNode);

		Assert.assertTrue(node.isMonitored());

		Assert
				.assertEquals(
						"Variable: variableName (Type: long, assigned to \"epics://krykWeather.temp_ai.VAL\", monitored)",
						node.humanReadableRepresentation());
	}

}
