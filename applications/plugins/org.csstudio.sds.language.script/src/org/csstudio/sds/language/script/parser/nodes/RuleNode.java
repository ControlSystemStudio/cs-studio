package org.csstudio.sds.language.script.parser.nodes;

public class RuleNode extends AbstractScriptNode {

    private String _ruleName;

    public RuleNode(final String ruleName,
            final int statementsStartOffsetInSource,
            final int statementsEndOffsetInSource) {
        this._ruleName = ruleName;

        this.setStatementOffsets(statementsStartOffsetInSource,
                statementsEndOffsetInSource);
    }

    @Override
    protected String doGetSourceIdentifier() {
        return _ruleName;
    }

    @Override
    public String getNodeTypeName() {
        return "scripted rule";
    }

    @Override
    public String humanReadableRepresentation() {
        StringBuffer resultBuffer = new StringBuffer(this.getNodeTypeName());
        resultBuffer.append(": ");
        resultBuffer.append(this.getSourceIdentifier());
        return resultBuffer.toString();
    }

}
