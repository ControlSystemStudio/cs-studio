package org.csstudio.sds.language.script.parser.statementParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.sds.language.script.codeElements.Keywords;
import org.csstudio.sds.language.script.codeElements.PredefinedFunctions;
import org.csstudio.sds.language.script.parser.nodes.FunctionNode;

public class FunctionParser extends
        AbstractDefaultStatementParser<FunctionNode> {

    @Override
    protected String getPatternString() {
        final PredefinedFunctions[] predefinedFunctionNames = PredefinedFunctions
                .values();
        final StringBuffer typeBuffer = new StringBuffer(
                predefinedFunctionNames[0].getElementName());
        for (int i = 1; i < predefinedFunctionNames.length; i++) {
            final PredefinedFunctions predefinedFunctionName = predefinedFunctionNames[i];
            typeBuffer.append("|");
            typeBuffer.append(predefinedFunctionName.getElementName());
        }
        typeBuffer.append("|([a-zA-Z][0-9a-zA-Z]*)");
        return "("+Keywords.FUNCTION.getElementName()+"\\s+)(" + typeBuffer.toString() + ")(\\s*\\()";
    }

    @Override
    protected String getPrePatternString() {
        throw new UnsupportedOperationException(
                "In this parser a special behavior of doFindNext(CharSequence,int) is required; by this this method should not be called!");
    }

    private PredefinedFunctions findPredefinedFunction(String varName) {
        if (varName != null && varName.trim().length() > 0) {
            for (PredefinedFunctions function : PredefinedFunctions.values()) {
                if (varName.equals(function.getElementName())) {
                    return function;
                }
            }
        }
        return null;
    }

    @Override
    protected void doFindNext(final CharSequence input, final int startIndex) {
        this._found = false;
        final Pattern prePattern = Pattern.compile(this.getPatternString());
        final Matcher preMatcher = prePattern.matcher(input);
        int localStart = startIndex;
        while (preMatcher.find(localStart)) {
            final String conditionWithBraces = this.determineCondition(input,
                    preMatcher.end() - 1);
            final int end = preMatcher.end();
            if (conditionWithBraces != null) {
                final int startOffsetOfBlock = end - 1
                        + conditionWithBraces.length();
                final String bodyContentWithBraces = this.determineContent(input,
                        startOffsetOfBlock);
                if (bodyContentWithBraces != null) {
                    this._startOffSet = preMatcher.start();
                    this._endOffSet = startOffsetOfBlock+bodyContentWithBraces.length();
                    this._found = true;
                    this._statement = input.subSequence(
                            this.getStartOffsetLastFound(),
                            this.getEndOffsetLastFound()).toString();
                    String functioName = preMatcher.group(2);
                    this._node = new FunctionNode(functioName, this.findPredefinedFunction(functioName),
                            this.getStartOffsetLastFound(), this.getEndOffsetLastFound());
                    break;
                }

            }
            localStart = end;
            if (localStart > input.length()) {
                this._found = false;
                break;
            }
        }
    }

    private String determineContent(final CharSequence input,
            final int startOffsetOfBlock) {
        final BlockStatementParser parser = new BlockStatementParser();
        parser.findNext(input, startOffsetOfBlock);

        if (!parser.hasFoundElement()) {
            return null;
        }

        final String lastFoundStatement = parser.getLastFoundStatement();

        return lastFoundStatement;
    }

    private String determineCondition(final CharSequence input,
            final int startOffsetOfBlock) {
        assert input.charAt(startOffsetOfBlock) == '(';
        final ConditionStatementParser bodyParser = new ConditionStatementParser();
        bodyParser.findNext(input, startOffsetOfBlock);
        if (bodyParser.hasFoundElement()) {
            return bodyParser.getLastFoundStatement();
        }
        return null;
    }

    @Override
    protected void matchFound(final Matcher preMatcher,
            final Matcher mainMatcher) {
        throw new UnsupportedOperationException(
                "In this parser a specail behavior of doFindNext(CharSequence,int) is required; by this this method should not be called!");
    }

}
