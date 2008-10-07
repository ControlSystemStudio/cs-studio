package de.desy.language.snl.parser.parser;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import de.desy.language.snl.parser.nodes.StringNode;

public class StringParser_Test extends TestCase {

	@Test
	public void testParser() {
		final StringParser parser = new StringParser();

		parser.findNext("//...\nstring \"Hallo\";\n// usw...\n");
		Assert.assertTrue(parser.hasFoundElement());

		Assert.assertEquals("\"Hallo\"", parser.getLastFoundStatement());
		final StringNode lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals("\"Hallo\"", lastFoundAsNode.getSourceIdentifier());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(13, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(20, lastFoundAsNode.getStatementEndOffset());
		Assert.assertFalse(lastFoundAsNode.hasChildren());
		Assert.assertFalse(lastFoundAsNode.hasContent());

		Assert.assertEquals(parser.getEndOffsetLastFound(), lastFoundAsNode
				.getStatementEndOffset());
	}
}
