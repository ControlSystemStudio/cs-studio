package de.desy.language.snl.parser.parser;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import de.desy.language.snl.parser.Interval;
import de.desy.language.snl.parser.nodes.DefineStatementNode;

public class DefineConstantStatementParser_Test extends TestCase {
	
	private final static String text = 	"//...\n" +
										"#define OK 1\n" +
										"#define NOT_OK 0\n" +
										"#define R_KEINER 0 // Reiniger nicht verlangt\n" +
										"#define DBG_D100 (DBG_D100_EIN|debugNextStep)\n" +
										"#define SET_MAN(pv) pv = PID_MANUELL; pvPut(pv); // Test Kommentar\n" +
										"#define DBG_TTSTOP_EIN	FALSE //TRUE=aktiviert   FALSE=deaktiviert\n" +
										"#define DBG_TTSTOP (DBG_TTSTOP_EIN|debugNextStep)\n" +
										"// usw...\n"; 

	@Test
	public void testParser() {
		final DefineConstantStatementParser parser = new DefineConstantStatementParser(new Interval[0]);
		
		//OK
		parser.findNext(text);
		Assert.assertTrue(parser.hasFoundElement());

		Assert.assertEquals("#define OK 1", parser.getLastFoundStatement());
		DefineStatementNode lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals("OK", lastFoundAsNode.getSourceIdentifier());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(6, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(18, lastFoundAsNode.getStatementEndOffset());
		Assert.assertFalse(lastFoundAsNode.hasChildren());
		Assert.assertFalse(lastFoundAsNode.hasContent());
		Assert.assertEquals("1", lastFoundAsNode.getValue());

		Assert.assertEquals(parser.getEndOffsetLastFound(), lastFoundAsNode
				.getStatementEndOffset());
		
		//NOT_OK
		parser.findNext(text, 18);
		Assert.assertTrue(parser.hasFoundElement());

		Assert.assertEquals("#define NOT_OK 0", parser.getLastFoundStatement());
		lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals("NOT_OK", lastFoundAsNode.getSourceIdentifier());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(19, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(35, lastFoundAsNode.getStatementEndOffset());
		Assert.assertFalse(lastFoundAsNode.hasChildren());
		Assert.assertFalse(lastFoundAsNode.hasContent());
		Assert.assertEquals("0", lastFoundAsNode.getValue());

		Assert.assertEquals(parser.getEndOffsetLastFound(), lastFoundAsNode
				.getStatementEndOffset());
		
		//R_KEINER
		parser.findNext(text, 35);
		Assert.assertTrue(parser.hasFoundElement());

		Assert.assertEquals("#define R_KEINER 0 // Reiniger nicht verlangt", parser.getLastFoundStatement());
		lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals("R_KEINER", lastFoundAsNode.getSourceIdentifier());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(36, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(81, lastFoundAsNode.getStatementEndOffset());
		Assert.assertFalse(lastFoundAsNode.hasChildren());
		Assert.assertFalse(lastFoundAsNode.hasContent());
		Assert.assertEquals("0", lastFoundAsNode.getValue());

		Assert.assertEquals(parser.getEndOffsetLastFound(), lastFoundAsNode
				.getStatementEndOffset());
		
		//DBG_D100
		parser.findNext(text, 81);
		Assert.assertTrue(parser.hasFoundElement());

		Assert.assertEquals("#define DBG_D100 (DBG_D100_EIN|debugNextStep)", parser.getLastFoundStatement());
		lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals("DBG_D100", lastFoundAsNode.getSourceIdentifier());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(82, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(127, lastFoundAsNode.getStatementEndOffset());
		Assert.assertFalse(lastFoundAsNode.hasChildren());
		Assert.assertFalse(lastFoundAsNode.hasContent());
		Assert.assertEquals("(DBG_D100_EIN|debugNextStep)", lastFoundAsNode.getValue());

		Assert.assertEquals(parser.getEndOffsetLastFound(), lastFoundAsNode
				.getStatementEndOffset());
		
		//DBG_TTSTOP_EIN
		parser.findNext(text, 127);
		Assert.assertTrue(parser.hasFoundElement());

		Assert.assertEquals("#define DBG_TTSTOP_EIN	FALSE //TRUE=aktiviert   FALSE=deaktiviert", parser.getLastFoundStatement());
		lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals("DBG_TTSTOP_EIN", lastFoundAsNode.getSourceIdentifier());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(195, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(260, lastFoundAsNode.getStatementEndOffset());
		Assert.assertFalse(lastFoundAsNode.hasChildren());
		Assert.assertFalse(lastFoundAsNode.hasContent());
		Assert.assertEquals("FALSE", lastFoundAsNode.getValue());

		Assert.assertEquals(parser.getEndOffsetLastFound(), lastFoundAsNode
				.getStatementEndOffset());
		
		//DBG_TTSTOP
		parser.findNext(text, 260);
		Assert.assertTrue(parser.hasFoundElement());

		Assert.assertEquals("#define DBG_TTSTOP (DBG_TTSTOP_EIN|debugNextStep)", parser.getLastFoundStatement());
		lastFoundAsNode = parser.getLastFoundAsNode();
		Assert.assertEquals("DBG_TTSTOP", lastFoundAsNode.getSourceIdentifier());
		Assert.assertTrue(lastFoundAsNode.hasOffsets());
		Assert.assertEquals(261, lastFoundAsNode.getStatementStartOffset());
		Assert.assertEquals(310, lastFoundAsNode.getStatementEndOffset());
		Assert.assertFalse(lastFoundAsNode.hasChildren());
		Assert.assertFalse(lastFoundAsNode.hasContent());
		Assert.assertEquals("(DBG_TTSTOP_EIN|debugNextStep)", lastFoundAsNode.getValue());

		Assert.assertEquals(parser.getEndOffsetLastFound(), lastFoundAsNode
				.getStatementEndOffset());
		
		//Rest
		parser.findNext(text, 310);
		Assert.assertFalse(parser.hasFoundElement());
		
	}
}
