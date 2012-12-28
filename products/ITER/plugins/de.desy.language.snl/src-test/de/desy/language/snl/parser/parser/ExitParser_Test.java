package de.desy.language.snl.parser.parser;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import de.desy.language.snl.parser.nodes.ExitNode;

public class ExitParser_Test extends TestCase {

	@Test
	public void testFindNextCharSequence() {
		final String source = "exit {\n" 
				+ "    pvPut(str, \"hallo\"); \n    }";

		final ExitParser entryParser = new ExitParser();
		entryParser.findNext(source);

		Assert.assertTrue(entryParser.hasFoundElement());
		Assert.assertEquals("exit {\n" 
				+ "    pvPut(str, \"hallo\"); \n    }",
				entryParser.getLastFoundStatement());
		Assert.assertEquals(0, entryParser.getStartOffsetLastFound());
		Assert.assertEquals(37, entryParser.getEndOffsetLastFound());
		final ExitNode node = entryParser.getLastFoundAsNode();
		Assert.assertNotNull(node);
		Assert.assertFalse(node.hasChildren());
		Assert.assertFalse(node.hasContent());
		Assert.assertEquals("\n    pvPut(str, \"hallo\"); \n    ", node
				.getSourceIdentifier());
	}

}
