package de.desy.language.snl.parser.parser;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import de.desy.language.snl.parser.Interval;
import de.desy.language.snl.parser.nodes.DefineStatementNode;

public class DefineFunctionStatementParser_Test extends TestCase {
	
	private final static String text = 	"//...\n" +
										"#define OK 1\n" +
										"#define DBG_D100 (DBG_D100_EIN|debugNextStep)\n" +
										"#define SET_MAN(pv) pv = PID_MANUELL; pvPut(pv); // Test Kommentar\n" +
										"#define PV_SET_SYNC(pv, val) pv = val; pvPut (pv, SYNC);\n" +
										"#define DBG_TTSTOP_EIN	FALSE //TRUE=aktiviert   FALSE=deaktiviert\n" +
										"#define DBG_TTSTOP (DBG_TTSTOP_EIN|debugNextStep)\n" +
										"// usw...\n"; 

	@Test
	public void testParser() {
		final DefineFunctionStatementParser parser = new DefineFunctionStatementParser(new Interval[0]);
		
		//SET_MAN(pv)
		parser.findNext(text);
		Assert.assertTrue(parser.hasFoundElement());

		Assert.assertEquals("#define SET_MAN(pv) pv = PID_MANUELL; pvPut(pv); // Test Kommentar\n", parser.getLastFoundStatement());
		DefineStatementNode lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals("SET_MAN(pv)", lastFoundAsNode.getSourceIdentifier());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(65, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(132, lastFoundAsNode.getStatementEndOffset());
		Assert.assertFalse(lastFoundAsNode.hasChildren());
		Assert.assertFalse(lastFoundAsNode.hasContent());
		Assert.assertEquals("pv = PID_MANUELL; pvPut(pv);", lastFoundAsNode.getValue());

		Assert.assertEquals(parser.getEndOffsetLastFound(), lastFoundAsNode
				.getStatementEndOffset());	
		
		//PV_SET_SYNC(pv, val)
		parser.findNext(text, 132);
		Assert.assertTrue(parser.hasFoundElement());

		Assert.assertEquals("#define PV_SET_SYNC(pv, val) pv = val; pvPut (pv, SYNC);\n", parser.getLastFoundStatement());
		lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals("PV_SET_SYNC(pv, val)", lastFoundAsNode.getSourceIdentifier());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(132, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(189, lastFoundAsNode.getStatementEndOffset());
		Assert.assertFalse(lastFoundAsNode.hasChildren());
		Assert.assertFalse(lastFoundAsNode.hasContent());
		Assert.assertEquals("pv = val; pvPut (pv, SYNC);", lastFoundAsNode.getValue());

		Assert.assertEquals(parser.getEndOffsetLastFound(), lastFoundAsNode
				.getStatementEndOffset());	
		
		//Rest
		parser.findNext(text, 189);
		Assert.assertFalse(parser.hasFoundElement());
	}
}
