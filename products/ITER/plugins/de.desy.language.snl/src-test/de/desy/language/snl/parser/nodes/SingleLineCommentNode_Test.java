package de.desy.language.snl.parser.nodes;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

public class SingleLineCommentNode_Test extends TestCase {

	@Test
	public void testNode() {
		final SingleLineCommentNode singleLineCommentNode = new SingleLineCommentNode(
				" hello this is a comment ", 23, 42);

		Assert.assertEquals("(single line comment)", singleLineCommentNode
				.getSourceIdentifier());
		Assert.assertTrue(singleLineCommentNode.hasOffsets());
		Assert
				.assertEquals(23, singleLineCommentNode
						.getStatementStartOffset());
		Assert.assertEquals(42, singleLineCommentNode.getStatementEndOffset());
		Assert.assertEquals("Single line comment", singleLineCommentNode
				.getNodeTypeName());
		Assert.assertFalse(singleLineCommentNode.hasChildren());
		Assert.assertTrue(singleLineCommentNode.hasContent());
		Assert.assertEquals(" hello this is a comment ", singleLineCommentNode
				.getContent());
		Assert.assertEquals("Single line comment", singleLineCommentNode
				.humanReadableRepresentation());
	}

}
