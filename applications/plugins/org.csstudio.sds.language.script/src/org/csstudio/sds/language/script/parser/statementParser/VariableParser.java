package org.csstudio.sds.language.script.parser.statementParser;

import java.util.regex.Matcher;

import org.csstudio.sds.language.script.codeElements.Keywords;
import org.csstudio.sds.language.script.codeElements.PredefinedVariables;
import org.csstudio.sds.language.script.parser.nodes.VariableNode;

public class VariableParser extends
        AbstractDefaultStatementParser<VariableNode> {

    @Override
    protected String getPatternString() {
        final PredefinedVariables[] predefinedVariableNames = PredefinedVariables
                .values();
        final StringBuffer typeBuffer = new StringBuffer(
                predefinedVariableNames[0].getElementName());
        for (int i = 1; i < predefinedVariableNames.length; i++) {
            final PredefinedVariables predefinedVariableName = predefinedVariableNames[i];
            typeBuffer.append("|");
            typeBuffer.append(predefinedVariableName.getElementName());
        }
        typeBuffer.append("|([a-zA-Z][0-9a-zA-Z]*)");
        return "("+Keywords.VAR.getElementName()+"\\s+)(" + typeBuffer.toString() + ")(\\s*=\\s*)([\\S\\s]*)"
                + this.getPrePatternString();
    }

    @Override
    protected String getPrePatternString() {
        return "(\\s*;)";
    }

    @Override
    protected void matchFound(final Matcher preMatcher,
            final Matcher mainMatcher) {
        this._statement = mainMatcher.group();
        this._startOffSet = mainMatcher.start();
        this._endOffSet = preMatcher.end() - 1;
        final String varName = mainMatcher.group(2);
        this._found = true;
        this._node = new VariableNode(varName, this.findPredefinedVariable(varName),
                this.getStartOffsetLastFound(), this.getEndOffsetLastFound());
    }

    private PredefinedVariables findPredefinedVariable(String varName) {
        if (varName != null && varName.trim().length() > 0) {
            for (PredefinedVariables var : PredefinedVariables.values()) {
                if (varName.equals(var.getElementName())) {
                    return var;
                }
            }
        }
        return null;
    }

}
