package org.csstudio.sds.language.script.parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.csstudio.sds.language.script.codeElements.PredefinedFunctions;
import org.csstudio.sds.language.script.codeElements.PredefinedVariables;
import org.csstudio.sds.language.script.parser.nodes.AbstractScriptNode;
import org.csstudio.sds.language.script.parser.nodes.CommentNode;
import org.csstudio.sds.language.script.parser.nodes.FunctionNode;
import org.csstudio.sds.language.script.parser.nodes.RuleNode;
import org.csstudio.sds.language.script.parser.nodes.VariableNode;
import org.csstudio.sds.language.script.parser.statementParser.CommentParser;
import org.csstudio.sds.language.script.parser.statementParser.FunctionParser;
import org.csstudio.sds.language.script.parser.statementParser.VariableParser;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;

import de.desy.language.editor.core.measurement.KeyValuePair;
import de.desy.language.editor.core.parser.AbstractLanguageParser;
import de.desy.language.editor.core.parser.Node;
import de.desy.language.libraries.utils.contract.Contract;

public class ScriptParser extends AbstractLanguageParser {

    protected Node doParse(CharSequence input, IResource sourceResource,
            IProgressMonitor progressMonitor) {
        if (input.length()>0) {
            RuleNode ruleNode = new RuleNode(sourceResource.getName(),0,input.length());
            List<CommentNode> allComments = this.findAndAddAllComments(input.toString());
            List<VariableNode> allVariables = this.findAndAddAllVariables(input.toString());
            List<FunctionNode> allFunctions = this.findAndAddAllFunctions(input.toString());

            //remove commented functions
            FunctionNode[] functionNodes = allFunctions.toArray(new FunctionNode[allFunctions.size()]);
            for (FunctionNode function : functionNodes) {
                if (isCommented(allComments, function)) {
                    allFunctions.remove(function);
                }
            }

            //remove commented variables
            VariableNode[] varNodes = allVariables.toArray(new VariableNode[allVariables.size()]);
            for (VariableNode var : varNodes) {
                if (isCommented(allComments, var)) {
                    allVariables.remove(var);
                }
            }

            checkPredefinedVariables(ruleNode, allVariables);
            checkPredefinedFunctions(ruleNode, allFunctions);

            List<VariableNode> globalVariables = new LinkedList<VariableNode>(allVariables);
            for (FunctionNode function : allFunctions) {
                for (VariableNode variable : allVariables) {
                    if (isSubNode(function, variable)) {
                        function.addChild(variable);
                        if (variable.isPredefined()) {
                            variable.addWarning("'"+variable.getSourceIdentifier()+"' should be a global variable for rules");
                        }
                        globalVariables.remove(variable);
                    }
                }
            }
            for (VariableNode globalVaribale : globalVariables) {
                ruleNode.addChild(globalVaribale);
            }
            for (FunctionNode function : allFunctions) {
                ruleNode.addChild(function);
            }

            return ruleNode;
        }
        return null;
    }

    private boolean isCommented(List<CommentNode> comments, AbstractScriptNode node) {
        for (CommentNode comment : comments) {
            if (comment.hasOffsets() && node.hasOffsets()) {
                return (comment.getStatementStartOffset()<node.getStatementStartOffset() && comment.getStatementEndOffset()>node.getStatementStartOffset());
            }
        }
        return false;
    }

    private List<CommentNode> findAndAddAllComments(final String input) {
        List<CommentNode> nodes = new ArrayList<CommentNode>();
        final CommentParser commentParser = new CommentParser();

        commentParser.findNext(input);

        while (commentParser.hasFoundElement()) {
            final CommentNode commentNode = commentParser.getLastFoundAsNode();
            nodes.add(commentNode);

            final int lastEndPosition = commentParser.getEndOffsetLastFound();

            //search next one
            commentParser.findNext(input, lastEndPosition);
        }

        return nodes;
    }

    private List<VariableNode> findAndAddAllVariables(final String input) {
        List<VariableNode> nodes = new LinkedList<VariableNode>();
        final VariableParser predefinedVariableParser = new VariableParser();

        predefinedVariableParser.findNext(input);

        while (predefinedVariableParser.hasFoundElement()) {
            final VariableNode varNode = predefinedVariableParser.getLastFoundAsNode();
            nodes.add(varNode);

            final int lastEndPosition = predefinedVariableParser.getEndOffsetLastFound();

            // search next one
            predefinedVariableParser.findNext(input, lastEndPosition);
        }
        return nodes;
    }

    private List<FunctionNode> findAndAddAllFunctions(final String input) {
        List<FunctionNode> nodes = new LinkedList<FunctionNode>();
        final FunctionParser predefinedFunctionParser = new FunctionParser();

        predefinedFunctionParser.findNext(input);

        while (predefinedFunctionParser.hasFoundElement()) {
            final FunctionNode functionNode = predefinedFunctionParser.getLastFoundAsNode();
            nodes.add(functionNode);

            final int lastEndPosition = predefinedFunctionParser.getEndOffsetLastFound();

            // search next one
            predefinedFunctionParser.findNext(input, lastEndPosition);
        }
        return nodes;
    }

    private boolean isSubNode(Node parentNode, Node subNode) {
        if (parentNode.hasOffsets() && subNode.hasOffsets()) {
            return (parentNode.getStatementStartOffset()<subNode.getStatementStartOffset() && parentNode.getStatementEndOffset()>subNode.getStatementEndOffset());
        }
        return false;
    }

    private void checkPredefinedVariables(Node parentNode, List<VariableNode> variables) {
        Contract.requireNotNull("variables", variables);
        List<String> errors = new ArrayList<String>(PredefinedVariables.values().length);
        List<String> warnings = new ArrayList<String>();
        boolean returnVariableFound = false;

        for (PredefinedVariables predefVar : PredefinedVariables.values()) {
            boolean found = false;
            for (VariableNode node : variables) {
                if (predefVar.getElementName().equals(node.getSourceIdentifier())) {
                    found = true;
                    if (PredefinedVariables.RETURNS.equals(predefVar)) {
                        returnVariableFound = true;
                    }
                    break;
                }
            }
            if (!found) {
                if (predefVar.isOptional()) {
                    StringBuffer messageBuffer = new StringBuffer("Missing variable declaration for '");
                    messageBuffer.append(predefVar.getElementName());
                    messageBuffer.append("'");
                    warnings.add(messageBuffer.toString());
                } else {
                    StringBuffer messageBuffer = new StringBuffer("Missing variable declaration for '");
                    messageBuffer.append(predefVar.getElementName());
                    messageBuffer.append("'");
                    errors.add(messageBuffer.toString());
                }
            }
        }
        if (returnVariableFound) {
            for (String error : errors) {
                parentNode.addError(error);
            }
            for (String warning : warnings) {
                parentNode.addWarning(warning);
            }
        }
    }

    private void checkPredefinedFunctions(Node parentNode, List<FunctionNode> functions) {
        Contract.requireNotNull("functions", functions);
        for (PredefinedFunctions predefFun : PredefinedFunctions.values()) {
            boolean found = false;
            for (FunctionNode node : functions) {
                if (predefFun.getElementName().equals(node.getSourceIdentifier())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                StringBuffer messageBuffer = new StringBuffer("Missing function declaration for '");
                messageBuffer.append(predefFun.getElementName());
                messageBuffer.append("'");
                parentNode.addError(messageBuffer.toString());
            }
        }
    }

    @Override
    public KeyValuePair[] getMeasurementData() {
        return new KeyValuePair[0];
    }

}
