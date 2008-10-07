package de.desy.language.snl.parser.nodes;

import junit.framework.Assert;
import junit.framework.TestCase;

public class StateSetNode_Test extends TestCase {
	public void testStateNodeWithContentWithoutChildren() {
		final StateSetNode stateSetNode = new StateSetNode("AStateSet",
				"state init {" + "	when (delay(0.1)) {"
						+ "	    printf(\"sncExample: Startup delay over\n\");"
						+ "	} state low" + "    }", 23, 42);

		Assert.assertEquals("AStateSet", stateSetNode.getSourceIdentifier());
		Assert.assertTrue(stateSetNode.hasOffsets());
		Assert.assertEquals(23, stateSetNode.getStatementStartOffset());
		Assert.assertEquals(42, stateSetNode.getStatementEndOffset());
		Assert.assertEquals("state set", stateSetNode.getNodeTypeName());
		Assert.assertFalse(stateSetNode.hasChildren());
		Assert.assertTrue(stateSetNode.hasContent());
		Assert.assertEquals("state init {" + "	when (delay(0.1)) {"
				+ "	    printf(\"sncExample: Startup delay over\n\");"
				+ "	} state low" + "    }", stateSetNode.getContent());
		Assert.assertEquals("state set: AStateSet", stateSetNode
				.humanReadableRepresentation());
	}
}
