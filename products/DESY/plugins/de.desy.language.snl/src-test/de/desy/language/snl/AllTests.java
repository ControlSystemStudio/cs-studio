package de.desy.language.snl;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.desy.language.snl.codeElements.FindWithRegExInStringExperiment_Test;
import de.desy.language.snl.codeElements.RegExToMatchAssignDeclaration_Test;
import de.desy.language.snl.codeElements.RegExToMatchEventFlagDeclaration_Test;
import de.desy.language.snl.codeElements.RegExToMatchMonitorDeclaration_Test;
import de.desy.language.snl.codeElements.RegExToMatchMultiLineComment_Test;
import de.desy.language.snl.codeElements.RegExToMatchMultiLineEmbeddedC_Test;
import de.desy.language.snl.codeElements.RegExToMatchProgramStatement_Test;
import de.desy.language.snl.codeElements.RegExToMatchSingleComment_Test;
import de.desy.language.snl.codeElements.RegExToMatchSingleEmbeddedC_Test;
import de.desy.language.snl.codeElements.RegExToMatchStateSetStatements_Test;
import de.desy.language.snl.codeElements.RegExToMatchStateStatements_Test;
import de.desy.language.snl.codeElements.RegExToMatchSyncDeclaration_Test;
import de.desy.language.snl.codeElements.RegExToMatchTextWithoutSubsequent_Test;
import de.desy.language.snl.codeElements.RegExToMatchVariableDeclaration_Test;
import de.desy.language.snl.codeElements.RegExToMatchWhenStatements_Test;
import de.desy.language.snl.parser.SNLParser_Test;
import de.desy.language.snl.parser.nodes.AssignStatementNode_Test;
import de.desy.language.snl.parser.nodes.BlockConditionNode_Test;
import de.desy.language.snl.parser.nodes.BlockStatementNode_Test;
import de.desy.language.snl.parser.nodes.CharNode_Test;
import de.desy.language.snl.parser.nodes.EntryNode_Test;
import de.desy.language.snl.parser.nodes.EventFlagNode_Test;
import de.desy.language.snl.parser.nodes.ExitNode_Test;
import de.desy.language.snl.parser.nodes.MonitorStatementNode_Test;
import de.desy.language.snl.parser.nodes.OptionStatementNode_Test;
import de.desy.language.snl.parser.nodes.SingleLineCommentNode_Test;
import de.desy.language.snl.parser.nodes.SingleLineEmbeddedCNode_Test;
import de.desy.language.snl.parser.nodes.StateNode_Test;
import de.desy.language.snl.parser.nodes.StateSetNode_Test;
import de.desy.language.snl.parser.nodes.StringNode_Test;
import de.desy.language.snl.parser.nodes.SyncStatementNode_Test;
import de.desy.language.snl.parser.nodes.VariableNode_Test;
import de.desy.language.snl.parser.nodes.WhenNode_Test;
import de.desy.language.snl.parser.parser.AssignStatementParser_Test;
import de.desy.language.snl.parser.parser.BlockStatementParser_Test;
import de.desy.language.snl.parser.parser.CharParser_Test;
import de.desy.language.snl.parser.parser.ConditionStatementParser_Test;
import de.desy.language.snl.parser.parser.DefineConstantStatementParser_Test;
import de.desy.language.snl.parser.parser.DefineFunctionStatementParser_Test;
import de.desy.language.snl.parser.parser.EntryParser_Test;
import de.desy.language.snl.parser.parser.EventFlagParser_Test;
import de.desy.language.snl.parser.parser.ExitParser_Test;
import de.desy.language.snl.parser.parser.MonitorStatementParser_Test;
import de.desy.language.snl.parser.parser.OptionStatementParser_Test;
import de.desy.language.snl.parser.parser.ProgramParser_Test;
import de.desy.language.snl.parser.parser.SingleLineCommentParser_Test;
import de.desy.language.snl.parser.parser.SingleLineEmbeddedCParser_Test;
import de.desy.language.snl.parser.parser.StateParser_Test;
import de.desy.language.snl.parser.parser.StateSetParser_Test;
import de.desy.language.snl.parser.parser.StringParser_Test;
import de.desy.language.snl.parser.parser.SyncStatementParser_Test;
import de.desy.language.snl.parser.parser.VariableParser_Test;
import de.desy.language.snl.parser.parser.WhenParser_Test;


@RunWith(Suite.class)
@SuiteClasses({
        FindWithRegExInStringExperiment_Test.class,
        RegExToMatchAssignDeclaration_Test.class,
        RegExToMatchEventFlagDeclaration_Test.class,
        RegExToMatchMonitorDeclaration_Test.class,
        RegExToMatchMultiLineComment_Test.class,
        RegExToMatchMultiLineEmbeddedC_Test.class,
        RegExToMatchProgramStatement_Test.class,
        RegExToMatchSingleComment_Test.class,
        RegExToMatchSingleEmbeddedC_Test.class,
        RegExToMatchStateSetStatements_Test.class,
        RegExToMatchStateStatements_Test.class,
        RegExToMatchSyncDeclaration_Test.class,
        RegExToMatchTextWithoutSubsequent_Test.class,
        RegExToMatchVariableDeclaration_Test.class,
        RegExToMatchWhenStatements_Test.class,

        AssignStatementNode_Test.class,
        BlockConditionNode_Test.class,
        BlockStatementNode_Test.class,
        CharNode_Test.class,
        EntryNode_Test.class,
        EventFlagNode_Test.class,
        ExitNode_Test.class,
        MonitorStatementNode_Test.class,
        OptionStatementNode_Test.class,
        SingleLineCommentNode_Test.class,
        SingleLineEmbeddedCNode_Test.class,
        StateNode_Test.class,
        StateSetNode_Test.class,
        StringNode_Test.class,
        SyncStatementNode_Test.class,
        VariableNode_Test.class,
        WhenNode_Test.class,

        AssignStatementParser_Test.class,
        BlockStatementParser_Test.class,
        CharParser_Test.class,
        ConditionStatementParser_Test.class,
        DefineConstantStatementParser_Test.class,
        DefineFunctionStatementParser_Test.class,
        EntryParser_Test.class,
        EventFlagParser_Test.class,
        ExitParser_Test.class,
        MonitorStatementParser_Test.class,
        OptionStatementParser_Test.class,
        ProgramParser_Test.class,
        SingleLineCommentParser_Test.class,
        SingleLineEmbeddedCParser_Test.class,
        StateParser_Test.class,
        StateSetParser_Test.class,
        StringParser_Test.class,
        SyncStatementParser_Test.class,
		WhenParser_Test.class,
		VariableParser_Test.class,

		SNLParser_Test.class
})
public class AllTests {
    // EMPTY
}
