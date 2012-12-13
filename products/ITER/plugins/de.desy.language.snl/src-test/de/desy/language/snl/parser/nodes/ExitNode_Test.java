package de.desy.language.snl.parser.nodes;

import junit.framework.Assert;
import junit.framework.TestCase;

public class ExitNode_Test extends TestCase {
	public void testStateNodeWithContentWithoutChildren() {
		final ExitNode stateSetNode = new ExitNode(
				"exit {	printf(\"sncExample: Startup delay over\n\"); \n}"
						, 23, 42);

		Assert.assertEquals("exit {	printf(\"sncExample: Startup delay over\n\"); \n}", stateSetNode.getSourceIdentifier());
		Assert.assertTrue(stateSetNode.hasOffsets());
		Assert.assertEquals(23, stateSetNode.getStatementStartOffset());
		Assert.assertEquals(42, stateSetNode.getStatementEndOffset());
		Assert.assertEquals("exit", stateSetNode.getNodeTypeName());
		Assert.assertFalse(stateSetNode.hasChildren());
		Assert.assertFalse(stateSetNode.hasContent());
		Assert.assertEquals("exit: exit {	printf(\"sncExample: Startup delay over\n\"); \n}", stateSetNode
				.humanReadableRepresentation());
	}
}
