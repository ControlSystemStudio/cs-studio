package de.desy.language.snl.parser.nodes;

import junit.framework.Assert;
import junit.framework.TestCase;

public class EntryNode_Test extends TestCase {
	public void testStateNodeWithContentWithoutChildren() {
		final EntryNode stateSetNode = new EntryNode(
				"entry {	printf(\"sncExample: Startup delay over\n\"); \n}"
						, 23, 42);

		Assert.assertEquals("entry {	printf(\"sncExample: Startup delay over\n\"); \n}", stateSetNode.getSourceIdentifier());
		Assert.assertTrue(stateSetNode.hasOffsets());
		Assert.assertEquals(23, stateSetNode.getStatementStartOffset());
		Assert.assertEquals(42, stateSetNode.getStatementEndOffset());
		Assert.assertEquals("entry", stateSetNode.getNodeTypeName());
		Assert.assertFalse(stateSetNode.hasChildren());
		Assert.assertFalse(stateSetNode.hasContent());
		Assert.assertEquals("entry: entry {	printf(\"sncExample: Startup delay over\n\"); \n}", stateSetNode
				.humanReadableRepresentation());
	}
}
