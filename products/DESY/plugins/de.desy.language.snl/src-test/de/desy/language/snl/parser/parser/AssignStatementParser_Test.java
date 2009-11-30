package de.desy.language.snl.parser.parser;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import de.desy.language.snl.parser.Interval;
import de.desy.language.snl.parser.nodes.AssignStatementNode;

public class AssignStatementParser_Test extends TestCase {

	@Test
	public void testParser() {
		final AssignStatementParser parser = new AssignStatementParser(new Interval[0]);

		parser
				.findNext("//...\nlong l;\nassign l to \"epics://krykWeather.temp_ai.VAL\";\n// usw...\n");
		Assert.assertTrue(parser.hasFoundElement());

		Assert.assertEquals("assign l to \"epics://krykWeather.temp_ai.VAL\";",
				parser.getLastFoundStatement());
		final AssignStatementNode lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals("l", lastFoundAsNode.getSourceIdentifier());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(14, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(60, lastFoundAsNode.getStatementEndOffset());
		Assert.assertFalse(lastFoundAsNode.hasChildren());
		Assert.assertTrue(lastFoundAsNode.hasContent());
		Assert.assertEquals("epics://krykWeather.temp_ai.VAL", lastFoundAsNode
				.getContent());

		Assert.assertEquals(parser.getEndOffsetLastFound(), lastFoundAsNode
				.getStatementEndOffset());
		Assert.assertFalse(lastFoundAsNode.isArray());
	}
	
	@Test
	public void testArrayAssignment() {
		final AssignStatementParser parser = new AssignStatementParser(new Interval[0]);

		parser
				.findNext("//...\nshort l[2];\nassign l[1] to \"epics://krykWeather.temp_ai.VAL\";\n// usw...\n");
		Assert.assertTrue(parser.hasFoundElement());

		Assert.assertEquals("assign l[1] to \"epics://krykWeather.temp_ai.VAL\";",
				parser.getLastFoundStatement());
		final AssignStatementNode lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals("l", lastFoundAsNode.getSourceIdentifier());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(18, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(67, lastFoundAsNode.getStatementEndOffset());
		Assert.assertFalse(lastFoundAsNode.hasChildren());
		Assert.assertTrue(lastFoundAsNode.hasContent());
		Assert.assertEquals("epics://krykWeather.temp_ai.VAL", lastFoundAsNode
				.getContent());

		Assert.assertEquals(parser.getEndOffsetLastFound(), lastFoundAsNode
				.getStatementEndOffset());
		Assert.assertTrue(lastFoundAsNode.isArray());
	}
}
