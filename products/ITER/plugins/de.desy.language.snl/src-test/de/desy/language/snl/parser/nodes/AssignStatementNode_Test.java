package de.desy.language.snl.parser.nodes;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

public class AssignStatementNode_Test extends TestCase {

	@Test
	public void testNode() {
		final AssignStatementNode node = new AssignStatementNode(
				"variableName", "epics://krykWeather.temp_ai.VAL", 23, 42, false);

		Assert.assertEquals("variableName", node.getSourceIdentifier());
		Assert.assertTrue(node.hasOffsets());
		Assert.assertEquals(23, node.getStatementStartOffset());
		Assert.assertEquals(42, node.getStatementEndOffset());
		Assert.assertEquals("assign", node.getNodeTypeName());
		Assert.assertFalse(node.hasChildren());
		Assert.assertTrue(node.hasContent());
		Assert.assertEquals("epics://krykWeather.temp_ai.VAL", node
				.getContent());
		Assert
				.assertEquals(
						"variableName is assigned to \"epics://krykWeather.temp_ai.VAL\"",
						node.humanReadableRepresentation());
		Assert.assertFalse(node.isArray());
	}
}
