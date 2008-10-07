package de.desy.language.snl.parser.nodes;

import junit.framework.Assert;
import junit.framework.TestCase;

public class StateNode_Test extends TestCase {
	public void testStateNodeWithContentWithoutChildren() {
		final StateNode stateNode = new StateNode(
				"AState",
				"when (v > 50.0) {\n"
						+ "	        printf(\"sncExample: Changing to high\n\");\n"
						+ "       when ( delay(1.0) )\n"
						+ "       {} state low\n", 23, 42);

		Assert.assertEquals("AState", stateNode.getSourceIdentifier());
		Assert.assertTrue(stateNode.hasOffsets());
		Assert.assertEquals(23, stateNode.getStatementStartOffset());
		Assert.assertEquals(42, stateNode.getStatementEndOffset());
		Assert.assertEquals("state", stateNode.getNodeTypeName());
		Assert.assertFalse(stateNode.hasChildren());
		Assert.assertTrue(stateNode.hasContent());
		Assert.assertEquals("when (v > 50.0) {\n"
				+ "	        printf(\"sncExample: Changing to high\n\");\n"
				+ "       when ( delay(1.0) )\n" + "       {} state low\n",
				stateNode.getContent());
		Assert.assertEquals("state AState", stateNode
				.humanReadableRepresentation());
	}

	public void testStateNodeWithContentWithChildren() {
		final StateNode stateNode = new StateNode(
				"AState",
				"when (v > 50.0) {\n"
						+ "	        printf(\"sncExample: Changing to high\n\");} state Next;\n"
						+ "       when ( delay(1.0) )\n"
						+ "       {} state low\n", 23, 151);

		final WhenNode whenNode1 = new WhenNode("v > 50.0",
				"\n	        printf(\"sncExample: Changing to high\n\");",
				"Next", 0, 80);
		stateNode.addChild(whenNode1);
		final WhenNode whenNode2 = new WhenNode(" delay(1.0) ", "", "low", 89,
				127);
		stateNode.addChild(whenNode2);

		Assert.assertEquals("AState", stateNode.getSourceIdentifier());
		Assert.assertTrue(stateNode.hasOffsets());
		Assert.assertEquals(23, stateNode.getStatementStartOffset());
		Assert.assertEquals(151, stateNode.getStatementEndOffset());
		Assert.assertEquals("state", stateNode.getNodeTypeName());
		Assert.assertTrue(stateNode.hasChildren());
		Assert.assertEquals(2, stateNode.getChildrenNodesAsArray().length);
		Assert.assertEquals(whenNode1, stateNode.getChildrenNodesAsArray()[0]);
		Assert.assertEquals(whenNode2, stateNode.getChildrenNodesAsArray()[1]);
		Assert.assertTrue(stateNode.hasContent());
		Assert
				.assertEquals(
						"when (v > 50.0) {\n"
								+ "	        printf(\"sncExample: Changing to high\n\");} state Next;\n"
								+ "       when ( delay(1.0) )\n"
								+ "       {} state low\n", stateNode
								.getContent());
		Assert.assertEquals("state AState", stateNode
				.humanReadableRepresentation());
	}
}
