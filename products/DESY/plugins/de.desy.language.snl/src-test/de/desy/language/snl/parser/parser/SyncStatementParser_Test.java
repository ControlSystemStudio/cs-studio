package de.desy.language.snl.parser.parser;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import de.desy.language.snl.parser.Interval;
import de.desy.language.snl.parser.nodes.SyncStatementNode;

public class SyncStatementParser_Test extends TestCase {

	@Test
	public void testParser() {
		final SyncStatemantParser parser = new SyncStatemantParser(new Interval[0]);

		parser.findNext("//...\nlong l;\nsync loLimit to loFlag;\n// usw...\n");
		Assert.assertTrue(parser.hasFoundElement());

		Assert.assertEquals("sync loLimit to loFlag;", parser
				.getLastFoundStatement());
		final SyncStatementNode lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals("loFlag", lastFoundAsNode.getSourceIdentifier());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(14, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(37, lastFoundAsNode.getStatementEndOffset());
		Assert.assertFalse(lastFoundAsNode.hasChildren());
		Assert.assertTrue(lastFoundAsNode.hasContent());
		Assert.assertEquals("loLimit", lastFoundAsNode.getContent());

		Assert.assertEquals(parser.getEndOffsetLastFound(), lastFoundAsNode
				.getStatementEndOffset());
	}
}
