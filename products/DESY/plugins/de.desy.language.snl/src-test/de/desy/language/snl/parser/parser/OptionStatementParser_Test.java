package de.desy.language.snl.parser.parser;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import de.desy.language.snl.parser.nodes.OptionStatementNode;

public class OptionStatementParser_Test extends TestCase {

	@Test
	public void testParser() {
		final OptionStatementParser parser = new OptionStatementParser();

		parser.findNext("//...\nlong l;\noption +r;\n// usw...\n");
		Assert.assertTrue(parser.hasFoundElement());

		Assert.assertEquals("option +r;", parser.getLastFoundStatement());
		final OptionStatementNode lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals("+r", lastFoundAsNode.getSourceIdentifier());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(14, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(24, lastFoundAsNode.getStatementEndOffset());
		Assert.assertFalse(lastFoundAsNode.hasChildren());
		Assert.assertFalse(lastFoundAsNode.hasContent());

		Assert.assertEquals(parser.getEndOffsetLastFound(), lastFoundAsNode
				.getStatementEndOffset());
	}
}
