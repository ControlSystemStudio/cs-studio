package de.desy.language.snl.parser.parser;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import de.desy.language.snl.parser.nodes.WhenNode;

public class WhenParser_Test extends TestCase {

	@Test
	public void testFindNextCharSequence() {
		final String source = "\n\n    when ( X < Y && pvGet(pv1) == 23  )\n" // 38
				+ "    { pvPut(str, \"hallo\"); \n    } state NextOne\n\n"
				+ "when (X > Y || pvGet(pv1) == 42)\n"
				+ "    { pvPut(str, \"Moin\"); \n    } state LastOne;\n"
				+ "\n}";

		final WhenParser whenParser = new WhenParser();
		whenParser.findNext(source);

		Assert.assertTrue(whenParser.hasFoundElement());
		Assert.assertEquals("when ( X < Y && pvGet(pv1) == 23  )\n"
				+ "    { pvPut(str, \"hallo\"); \n    } state NextOne",
				whenParser.getLastFoundStatement());
		Assert.assertEquals(6, whenParser.getStartOffsetLastFound());
		Assert.assertEquals(89, whenParser.getEndOffsetLastFound());
		final WhenNode node = whenParser.getLastFoundAsNode();
		Assert.assertNotNull(node);
		Assert.assertFalse(node.hasChildren());
		Assert.assertTrue(node.hasContent());
		Assert
				.assertEquals(" pvPut(str, \"hallo\"); \n    ", node
						.getContent());
		Assert.assertEquals("X < Y && pvGet(pv1) == 23", node
				.getSourceIdentifier());
		Assert.assertEquals("NextOne", node.getFollowingState());

		// XXX Suggestion: Use getEndOffsetLastFound() for index-less call.
		whenParser.findNext(source, whenParser.getEndOffsetLastFound());
		Assert.assertTrue(whenParser.hasFoundElement());

		Assert.assertEquals("when (X > Y || pvGet(pv1) == 42)\n"
				+ "    { pvPut(str, \"Moin\"); \n    } state LastOne;",
				whenParser.getLastFoundStatement());
		Assert.assertEquals(91, whenParser.getStartOffsetLastFound());
		Assert.assertEquals(171, whenParser.getEndOffsetLastFound());
		final WhenNode node2 = whenParser.getLastFoundAsNode();
		Assert.assertNotNull(node2);
		Assert.assertFalse(node2.hasChildren());
		Assert.assertTrue(node2.hasContent());
		Assert
				.assertEquals(" pvPut(str, \"Moin\"); \n    ", node2
						.getContent());
		Assert.assertEquals("X > Y || pvGet(pv1) == 42", node2
				.getSourceIdentifier());
		Assert.assertEquals("LastOne", node2.getFollowingState());
	}

	/**
	 * A Test as result of found bug. The condition was parsed from the first
	 * brace up to the last (in code block!).
	 */
	public void testFindNextWithSourceOFFailureOnBraces() {
		final String source = "when()\n"
				+ "{\n"
				+ "pvGet(vin1);\n"
				+ "\n"
				+ "vout1 = vin1 *2;\n"
				+ "vout2 += 10;\n"
				+ "v     += 10;\n"
				+ "        for(i=0; i<4000; i++)\n"
				+ "        {\n"
				+ "           wf[i] += 10;\n"
				+ "           if(wf[i] > 200) wf[i] = 0;\n"
				+ "        }\n"
				+ "if(vout2 > 200) vout2 = 0;\n"
				+ "if(v     > 200) v     = 0;\n"
				+ "sprintf(vstrOut, \"vstrOut_%f\", vout2);\n"
				+ "%{\n"
				+ "	do something nice in c\n"
				+ "}%\n"
				+ "pvPut(vout1);\n"
				+ "pvPut(vout2);\n"
				+ "pvPut(vstrOut);\n"
				+ "\n"
				+ "printf(\"vin1=%f, vout1=%f, vout2=%f, vstrOut=%s, wf[2]=%f, wf[3000]=%f\n\",\n"
				+ "                vin1, vout1, vout2, vstrOut, wf[2], wf[3000]);\n"
				+ "} state delaySec;\n";

		final WhenParser whenParser = new WhenParser();
		whenParser.findNext(source);

		Assert.assertTrue(whenParser.hasFoundElement());
		Assert.assertEquals(source.substring(0, source.length() - 1),
				whenParser.getLastFoundStatement()); // In this example this
		// is the whole source
		// without closing line
		// break
		Assert.assertEquals(0, whenParser.getStartOffsetLastFound());
		Assert.assertEquals(500, whenParser.getEndOffsetLastFound());
		final WhenNode node = whenParser.getLastFoundAsNode();
		Assert.assertNotNull(node);
		Assert.assertFalse(node.hasChildren());
		Assert.assertEquals("", node.getSourceIdentifier());
		Assert.assertTrue(node.hasContent());
		Assert
				.assertEquals(
						"\n"
								+ "pvGet(vin1);\n"
								+ "\n"
								+ "vout1 = vin1 *2;\n"
								+ "vout2 += 10;\n"
								+ "v     += 10;\n"
								+ "        for(i=0; i<4000; i++)\n"
								+ "        {\n"
								+ "           wf[i] += 10;\n"
								+ "           if(wf[i] > 200) wf[i] = 0;\n"
								+ "        }\n"
								+ "if(vout2 > 200) vout2 = 0;\n"
								+ "if(v     > 200) v     = 0;\n"
								+ "sprintf(vstrOut, \"vstrOut_%f\", vout2);\n"
								+ "%{\n"
								+ "	do something nice in c\n"
								+ "}%\n"
								+ "pvPut(vout1);\n"
								+ "pvPut(vout2);\n"
								+ "pvPut(vstrOut);\n"
								+ "\n"
								+ "printf(\"vin1=%f, vout1=%f, vout2=%f, vstrOut=%s, wf[2]=%f, wf[3000]=%f\n\",\n"
								+ "                vin1, vout1, vout2, vstrOut, wf[2], wf[3000]);\n",
						node.getContent());
		Assert.assertEquals("", node.getSourceIdentifier());
		Assert.assertEquals("delaySec", node.getFollowingState());
		Assert.assertEquals("when () -> delaySec", node
				.humanReadableRepresentation());
	}

	@Test()
	public void testMatchFoundWillFail() {
		try {
			final WhenParser whenParser = new WhenParser();
			whenParser.matchFound(null, null);
			Assert.fail("An UnsupportedOperationException was expected!");
		} catch (final UnsupportedOperationException uoe) {

		}
	}

	@Test
	public void testInvalidSourceWithMissingClosingBraceInFirstWhenBody() {
		final String source = "\n\n    when (X < Y && pvGet(pv1) == 23)\n" // 38
				+ "    { pvPut(str, \"hallo\"); \n     state NextOne\n\n"
				+ "when (X > Y || pvGet(pv1) == 42)\n"
				+ "    { pvPut(str, \"Moin\"); \n    } state LastOne;\n" + "\n";

		final WhenParser whenParser = new WhenParser();
		whenParser.findNext(source);

		Assert.assertFalse(whenParser.hasFoundElement());
	}

	@Test
	public void testInvalidSourceWithMissingClosingBraceInFirstWhenCondition() {
		final String source = "\n\n    when (X < Y && pvGet(pv1) == 23\n" // 38
				+ "    { pvPut(str, \"hallo\"); \n  }   state NextOne\n\n"
				+ "when (X > Y || pvGet(pv1) == 42)\n"
				+ "    { pvPut(str, \"Moin\"); \n    } state LastOne;\n" + "\n";

		final WhenParser whenParser = new WhenParser();
		whenParser.findNext(source);

		Assert.assertFalse(whenParser.hasFoundElement());
	}

	@Test
	public void testInvalidSourceWithAdditionalOpeningBraceInFirstWhen() {
		final String source = "\n\n    when (X < Y && pvGet(pv1) == 23)\n" // 38
				+ "    { { pvPut(str, \"hallo\"); \n   }  state NextOne\n\n"
				+ "when (X > Y || pvGet(pv1) == 42)\n"
				+ "    { pvPut(str, \"Moin\"); \n    } state LastOne;\n" + "\n";

		final WhenParser whenParser = new WhenParser();
		whenParser.findNext(source);

		Assert.assertFalse(whenParser.hasFoundElement());

		// TODO 2008-01-14: km, mz: Make it possible to parse the second when,
		// if first is incorrect.
	}
}
