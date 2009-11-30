package de.desy.language.snl.parser.parser;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import de.desy.language.snl.parser.Interval;
import de.desy.language.snl.parser.nodes.MonitorStatementNode;

public class MonitorStatementParser_Test extends TestCase {

	@Test
	public void testParser() {
		final MonitorStatementParser parser = new MonitorStatementParser(new Interval[0]);

		parser.findNext("//...\nlong l;\nmonitor l;\n// usw...\n");
		Assert.assertTrue(parser.hasFoundElement());

		Assert.assertEquals("monitor l;", parser.getLastFoundStatement());
		final MonitorStatementNode lastFoundAsNode = parser
				.getLastFoundAsNode();
		Assert.assertEquals("l", lastFoundAsNode.getSourceIdentifier());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(14, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(23, lastFoundAsNode.getStatementEndOffset());
		Assert.assertFalse(lastFoundAsNode.hasChildren());
		Assert.assertFalse(lastFoundAsNode.hasContent());

		Assert.assertEquals(parser.getEndOffsetLastFound(), lastFoundAsNode
				.getStatementEndOffset());
	}
}
