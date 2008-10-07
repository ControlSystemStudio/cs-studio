package de.desy.language.snl.parser.parser;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import de.desy.language.snl.parser.nodes.CharNode;

public class CharParser_Test extends TestCase {

	@Test
	public void testParser() {
		final CharParser parser = new CharParser();

		parser.findNext("//...\nchar 'H';\n// usw...\n");
		Assert.assertTrue(parser.hasFoundElement());

		Assert.assertEquals("'H'", parser.getLastFoundStatement());
		final CharNode lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals("'H'", lastFoundAsNode.getSourceIdentifier());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(11, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(14, lastFoundAsNode.getStatementEndOffset());
		Assert.assertFalse(lastFoundAsNode.hasChildren());
		Assert.assertFalse(lastFoundAsNode.hasContent());

		Assert.assertEquals(parser.getEndOffsetLastFound(), lastFoundAsNode
				.getStatementEndOffset());
	}
}
