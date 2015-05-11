package org.csstudio.sds.language.script.parser.nodes;

import org.csstudio.sds.language.script.codeElements.PredefinedVariables;

/**
 * The outline node of an SNL variable.
 *
 * @author C1 WPS / KM, MZ
 */
public class VariableNode extends AbstractScriptNode {

    private final String _variableName;
    private PredefinedVariables _predefinedVariable;

    public VariableNode(final String variableName, final PredefinedVariables preVar,
            final int statementsStartOffsetInSource,
            final int statementsEndOffsetInSource) {
        this._variableName = variableName;
        this._predefinedVariable = preVar;

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
        return "variable";
    }

    @Override
    protected String doGetSourceIdentifier() {
        return this._variableName;
    }

    public boolean isPredefined() {
        return _predefinedVariable!=null;
    }

    public PredefinedVariables getPredefinedVariable() {
        return _predefinedVariable;
    }

}
