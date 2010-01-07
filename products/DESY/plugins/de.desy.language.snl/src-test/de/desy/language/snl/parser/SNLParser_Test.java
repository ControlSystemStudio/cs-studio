package de.desy.language.snl.parser;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Test;

import de.desy.language.editor.core.parser.Node;
import de.desy.language.snl.parser.nodes.AbstractSNLNode;
import de.desy.language.snl.parser.nodes.AllVariablesNode;
import de.desy.language.snl.parser.nodes.AssignStatementNode;
import de.desy.language.snl.parser.nodes.EventFlagNode;
import de.desy.language.snl.parser.nodes.MonitorStatementNode;
import de.desy.language.snl.parser.nodes.OptionStatementNode;
import de.desy.language.snl.parser.nodes.ProgramNode;
import de.desy.language.snl.parser.nodes.StateSetNode;
import de.desy.language.snl.parser.nodes.SyncStatementNode;
import de.desy.language.snl.parser.nodes.VariableNode;

public class SNLParser_Test extends TestCase {

	private final String _source = "program sncExample;"
			+ "double v;"
			+ "assign v to \"{user}:aiExample\";"
			+ "monitor v;"
			+ "evflag vFlag;\n"
			+ "sync v to vFlag;\n"
			+ "long l;"
			+ "%{\n"
			+ "   Embedded C\n"
			+ "}%\n"
			+ "ss ss1 {"
			+ "    state init {"
			+ "	when (delay(0.1)) {"
			+ "	    printf(\"sncExample: Startup delay over\n\");"
			+ "	} state low"
			+ "    }"
			+ " /* Hallo Welt!*"
			+ " ./. */"
			+ "    state low {"
			+ "	    when (v > 50.0) {"
			+ "	        printf(\"sncExample: Changing to high\n\");"
			+ "/* +++"
			+ "*/	    } state high"
			+ "       "
			+ "       when ( delay(1.0) )"
			+ "       {"
			+ "       } state low"
			+ "   }"
			+ "    state high {"
			+ "when (v <= 50.0) {"
			+ "	    printf(\"sncExample: Changing to low\n\");"
			+ "	} state low"
			+ "        when ( delay(1.0) ) {"
			+ "       } state high"
			+ "   }"
			+ "}"
			+ "option +r;\n"
			+ " %% Hello C!\n"
			+ "// more and more...\n";

	private final String _clearSource = "program sncExample;" // 0-18
			+ "double v;" // 19-27
			+ "assign v to \"{user}:aiExample\";" // 28-58
			+ "monitor v;"// 59-68
			+ "evflag vFlag;\n" // 69-82
			+ "sync v to vFlag;\n" // 83-99
			+ "long l;"// 100-106
			+ "   "// 107-110
			+ "              "// 111-124
			+ "   "// 125-127
			+ "ss ss1 {"// 128-135
			+ "    state init {"// 136-151
			+ "	when (delay(0.1)) {"// 152-171
			+ "	    printf(\"sncExample: Startup delay over\n\");"// 172-218
			+ "	} state low"// 219-230
			+ "    }"// 231-235
			+ "                "
			+ "       "
			+ "    state low {"
			+ "	    when (v > 50.0) {"
			+ "	        printf(\"sncExample: Changing to high\n\");"
			+ "      "
			+ "  	    } state high"
			+ "       "
			+ "       when ( delay(1.0) )"
			+ "       {"
			+ "       } state low"
			+ "   }"
			+ "    state high {"
			+ "when (v <= 50.0) {"
			+ "	    printf(\"sncExample: Changing to low\n\");"
			+ "	} state low"
			+ "        when ( delay(1.0) ) {"
			+ "       } state high"
			+ "   }"
			+ "}"
			+ "option +r;\n"
			+ "             "
			+ "                   \n";

	@Test
	public void testSNLParser() {
		final SNLParser parser = new SNLParser();

		IProgressMonitor progressMonitor = EasyMock.createNiceMock(IProgressMonitor.class);
		EasyMock.replay(progressMonitor);
		
		final Node programNode = parser.parse(this._source, null, progressMonitor);

		Assert.assertEquals(this._source, parser.getSequenceWorkingOn());
		Assert.assertEquals(this._clearSource, parser.getClearedInput());
		Assert.assertNotNull(programNode);
		Assert.assertEquals(ProgramNode.class, programNode.getClass());
		Assert.assertTrue(programNode.hasChildren());

		// Program node
//		final Node programNode = rootNode.getChildrenNodesAsArray()[0];
		Assert.assertEquals(ProgramNode.class, programNode.getClass());
		Assert.assertEquals("sncExample", ((ProgramNode) programNode)
				.getSourceIdentifier());

		Assert.assertTrue(programNode.hasChildren());
		Assert.assertEquals(4, programNode.getChildrenNodesAsArray().length);

		Node node;

		// State set ss1
		node = programNode.getChildrenNodesAsArray()[0];
		Assert.assertEquals(StateSetNode.class, node.getClass());
		final StateSetNode stateSetNode = ((StateSetNode) node);
		Assert.assertEquals(127, stateSetNode.getStatementStartOffset());
		Assert.assertEquals("ss1", stateSetNode.getSourceIdentifier());
		Assert.assertTrue(stateSetNode.hasContent());
		Assert.assertEquals("    state init {" + "	when (delay(0.1)) {"
				+ "	    printf(\"sncExample: Startup delay over\n\");"
				+ "	} state low" + "    }" + "                " + "       "
				+ "    state low {" + "	    when (v > 50.0) {"
				+ "	        printf(\"sncExample: Changing to high\n\");"
				+ "      " + "  	    } state high" + "       "
				+ "       when ( delay(1.0) )" + "       {"
				+ "       } state low" + "   }" + "    state high {"
				+ "when (v <= 50.0) {"
				+ "	    printf(\"sncExample: Changing to low\n\");"
				+ "	} state low" + "        when ( delay(1.0) ) {"
				+ "       } state high" + "   }", stateSetNode.getContent());
		Assert.assertTrue(stateSetNode.hasChildren());
		final Node[] stateSetChildrenNodes = stateSetNode.getChildrenNodesAsArray();
		Assert.assertEquals(3, stateSetChildrenNodes.length);
		Assert.assertEquals("init",
				((AbstractSNLNode) stateSetChildrenNodes[0])
						.getSourceIdentifier());
		final int stateStatementStartOffset = stateSetChildrenNodes[0]
				.getStatementStartOffset();
		Assert.assertEquals(139, stateStatementStartOffset);
		final int stateStatementEndOffset = stateSetChildrenNodes[0]
				.getStatementEndOffset();
		Assert.assertEquals(235, stateStatementEndOffset);
		Assert.assertEquals("state init {" + "	when (delay(0.1)) {"
				+ "	    printf(\"sncExample: Startup delay over\n\");"
				+ "	} state low" + "    }", this._clearSource.subSequence(
				stateStatementStartOffset, stateStatementEndOffset).toString());

		Assert.assertEquals("low", ((AbstractSNLNode) stateSetChildrenNodes[1])
				.getSourceIdentifier());
		Assert.assertEquals("high",
				((AbstractSNLNode) stateSetChildrenNodes[2])
						.getSourceIdentifier());
		
		// double v;
		node = programNode.getChildrenNodesAsArray()[1];
		Assert.assertEquals(AllVariablesNode.class, node.getClass());
		Assert.assertEquals(2, node.getChildrenNodes().size());
		
		VariableNode varNode = (VariableNode) node.getChildrenNodesAsArray()[0];
		Assert.assertEquals("v", varNode.getSourceIdentifier());
		Assert.assertTrue(varNode.isAssigned());
		Assert.assertTrue(varNode.isMonitored());
		Assert.assertEquals("{user}:aiExample", varNode
				.getAssignedChannelName());
		Assert.assertTrue(varNode.hasChildren());
		final Node[] childrenNodes = varNode.getChildrenNodesAsArray();
		Assert.assertEquals(2, childrenNodes.length);
		Assert.assertEquals(AssignStatementNode.class, childrenNodes[0]
				.getClass());
		Assert.assertEquals("{user}:aiExample",
				((AssignStatementNode) childrenNodes[0]).getContent());
		Assert.assertEquals(MonitorStatementNode.class, childrenNodes[1]
				.getClass());

		// long l;
		varNode = (VariableNode) node.getChildrenNodesAsArray()[1];
		Assert.assertEquals(VariableNode.class, varNode.getClass());
		Assert.assertEquals("l", varNode.getSourceIdentifier());
		
		// option +r;
		node = programNode.getChildrenNodesAsArray()[3];
		Assert.assertEquals(OptionStatementNode.class, node.getClass());
		Assert.assertEquals("+r", ((OptionStatementNode) node)
				.getSourceIdentifier());
		Assert.assertFalse(((OptionStatementNode) node).hasChildren());
		Assert.assertFalse(((OptionStatementNode) node).hasContent());

		// evFlag vFlag
		node = programNode.getChildrenNodesAsArray()[2];
		Assert.assertEquals(EventFlagNode.class, node.getClass());
		final EventFlagNode eventFlagNode = (EventFlagNode) node;
		Assert.assertEquals("vFlag", eventFlagNode.getSourceIdentifier());
		Assert.assertTrue(eventFlagNode.hasChildren());
		Assert.assertEquals(1, eventFlagNode.getChildrenNodesAsArray().length);

		final SyncStatementNode syncNode = (SyncStatementNode) eventFlagNode
				.getChildrenNodesAsArray()[0];
		Assert.assertEquals(SyncStatementNode.class, syncNode.getClass());
		Assert.assertEquals("vFlag", syncNode.getSourceIdentifier());
		Assert.assertTrue(syncNode.hasContent());
		Assert.assertFalse(syncNode.hasChildren());
		Assert.assertEquals("v", syncNode.getContent());

		

		// // assign v to "{user}:aiExample";
		// assertTrue(node.getChildrenNodes().length > 1);
		// node = node.getChildrenNodes()[1];
		// assertEquals(AssignNode.class, node.getClass());
		// assertEquals("v", ((AssignNode)node).getSourceIdentifier());
		// assertTrue(((AssignNode)node).hasContent());
		// assertEquals("\"{user}:aiExample\"",
		// ((AssignNode)node).getContent());
	}

	private final String _source2 = "program sncExample\n" + "double v;"
			+ "assign v to \"{user}:aiExample\";" + "monitor v;" + "long l;"
			+ "%{\n" + "   Embedded C\n" + "}%\n" + "ss ss1 {"
			+ "    state init {" + "	when (delay(0.1)) {"
			+ "	    printf(\"sncExample: Startup delay over\n\");"
			+ "	} state low" + "    }" + " /* Hallo Welt!*" + " ./. */"
			+ "    state low {" + "	    when (v > 50.0) {"
			+ "	        printf(\"sncExample: Changing to high\n\");" + "/* +++"
			+ "*/	    } state high" + "       " + "       when ( delay(1.0) )"
			+ "       {" + "       } state low" + "   }" + "    state high {"
			+ "when (v <= 50.0) {"
			+ "	    printf(\"sncExample: Changing to low\n\");"
			+ "	} state low" + "        when ( delay(1.0) ) {"
			+ "       } state high" + "   }" + "}";

	private final String _clearSource2 = "program sncExample\n" + "double v;"
			+ "assign v to \"{user}:aiExample\";" + "monitor v;" + "long l;"
			+ "   " + "              " + "   " + "ss ss1 {"
			+ "    state init {" + "	when (delay(0.1)) {"
			+ "	    printf(\"sncExample: Startup delay over\n\");"
			+ "	} state low" + "    }" + "                " + "       "
			+ "    state low {" + "	    when (v > 50.0) {"
			+ "	        printf(\"sncExample: Changing to high\n\");" + "      "
			+ "  	    } state high" + "       " + "       when ( delay(1.0) )"
			+ "       {" + "       } state low" + "   }" + "    state high {"
			+ "when (v <= 50.0) {"
			+ "	    printf(\"sncExample: Changing to low\n\");"
			+ "	} state low" + "        when ( delay(1.0) ) {"
			+ "       } state high" + "   }" + "}";

	@Test
	public void testSNLParserSource2ProgramWithoutSemicolon() {
		final SNLParser parser = new SNLParser();

		IProgressMonitor progressMonitor = EasyMock.createNiceMock(IProgressMonitor.class);
		EasyMock.replay(progressMonitor);
		
		final Node programNode = parser.parse(this._source2, null, progressMonitor);

		Assert.assertEquals(this._source2, parser.getSequenceWorkingOn());
		Assert.assertEquals(this._clearSource2, parser.getClearedInput());
		Assert.assertNotNull(programNode);
		Assert.assertEquals(ProgramNode.class, programNode.getClass());
		Assert.assertTrue(programNode.hasChildren());

		// Program node
//		final Node programNode = rootNode.getChildrenNodesAsArray()[0];
		Assert.assertEquals(ProgramNode.class, programNode.getClass());
		Assert.assertEquals("sncExample", ((ProgramNode) programNode)
				.getSourceIdentifier());

		Assert.assertTrue(programNode.hasChildren());
		Assert.assertEquals(2, programNode.getChildrenNodesAsArray().length);

		Node node;

		// double v;
		node = programNode.getChildrenNodesAsArray()[0];
		Assert.assertEquals(StateSetNode.class, node.getClass());
		Assert.assertEquals("ss1", ((StateSetNode) node).getSourceIdentifier());

		// long l;
		node = programNode.getChildrenNodesAsArray()[1];
		Assert.assertEquals(AllVariablesNode.class, node.getClass());
		Assert.assertEquals(2, ((AllVariablesNode) node).getChildrenNodes().size());
	}
}
