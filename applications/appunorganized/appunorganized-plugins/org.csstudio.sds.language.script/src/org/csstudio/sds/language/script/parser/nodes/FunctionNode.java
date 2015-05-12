package org.csstudio.sds.language.script.parser.nodes;

import org.csstudio.sds.language.script.codeElements.PredefinedFunctions;

/**
 * The outline node of an Script-method.
 *
 * @author C1 WPS / KM, MZ
 */
public class FunctionNode extends AbstractScriptNode {

    private final String _variableName;
    private PredefinedFunctions _predefinedFunction;

    public FunctionNode(final String variableName, final PredefinedFunctions preFun,
            final int statementsStartOffsetInSource,
            final int statementsEndOffsetInSource) {
        this._variableName = variableName;
        this._predefinedFunction = preFun;

        this.setStatementOffsets(statementsStartOffsetInSource,
                statementsEndOffsetInSource);
    }

    @Override
    public String humanReadableRepresentation() {
        final StringBuffer result = new StringBuffer(this.getNodeTypeName());
        result.append(": ");
        result.append(this.getSourceIdentifier());
        return result.toString();
    }

    @Override
    public String getNodeTypeName() {
        return "function";
    }

    @Override
    protected String doGetSourceIdentifier() {
        return this._variableName;
    }

    public boolean isPredefined() {
        return _predefinedFunction!=null;
    }

    public PredefinedFunctions getPredefinedFunction() {
        return _predefinedFunction;
    }

}
