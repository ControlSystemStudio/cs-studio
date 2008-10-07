package de.desy.language.snl.parser.parser;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import de.desy.language.snl.parser.nodes.EntryNode;

public class EntryParser_Test extends TestCase {

	@Test
	public void testFindNextCharSequence() {
		final String source = "entry {\n" 
				+ "    pvPut(str, \"hallo\"); \n    }";

		final EntryParser entryParser = new EntryParser();
		entryParser.findNext(source);

		Assert.assertTrue(entryParser.hasFoundElement());
		Assert.assertEquals("entry {\n" 
				+ "    pvPut(str, \"hallo\"); \n    }",
				entryParser.getLastFoundStatement());
		Assert.assertEquals(0, entryParser.getStartOffsetLastFound());
		Assert.assertEquals(38, entryParser.getEndOffsetLastFound());
		final EntryNode node = entryParser.getLastFoundAsNode();
		Assert.assertNotNull(node);
		Assert.assertFalse(node.hasChildren());
		Assert.assertFalse(node.hasContent());
		Assert.assertEquals("\n    pvPut(str, \"hallo\"); \n    ", node
				.getSourceIdentifier());
	}

}
