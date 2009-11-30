package de.desy.language.snl.parser.parser;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import de.desy.language.snl.parser.Interval;
import de.desy.language.snl.parser.nodes.EventFlagNode;

public class EventFlagParser_Test extends TestCase {

	private final String _source = "/*HAllo*/\nprogram sncExample;"
			+ "double v;" + "assign v to \"{user}:aiExample\";"
			+ "monitor v;\n\n\n" + "evflag vFlag;\n" + "long l;\n\n" + "%{\n"
			+ "   Embedded C\n" + "}%\n";

	@Test
	public void testFindNextCharSequence() {
		final EventFlagParser parser = new EventFlagParser(new Interval[0]);

		// vFlag
		parser.findNext(this._source);
		Assert.assertTrue(parser.hasFoundElement());
		Assert.assertEquals("evflag vFlag;", parser.getLastFoundStatement());
		final EventFlagNode lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals(EventFlagNode.class, lastFoundAsNode.getClass());
		Assert.assertEquals("vFlag", lastFoundAsNode.getSourceIdentifier());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(82, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(94, lastFoundAsNode.getStatementEndOffset());

		Assert.assertEquals(parser.getEndOffsetLastFound(), lastFoundAsNode
				.getStatementEndOffset());
	}

}
