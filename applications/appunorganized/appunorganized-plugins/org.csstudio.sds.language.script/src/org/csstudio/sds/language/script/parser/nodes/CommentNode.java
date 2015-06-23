package org.csstudio.sds.language.script.parser.nodes;


public class CommentNode extends AbstractScriptNode {

    private static final String COMMENT = "Comment";
    private final String _commentedSource;

    public CommentNode(final String commentedSource, final int statementsStartOffsetInSource,    final int statementsEndOffsetInSource) {
        _commentedSource = commentedSource;
        this.setStatementOffsets(statementsStartOffsetInSource,
                statementsEndOffsetInSource);
    }

    @Override
    protected String doGetSourceIdentifier() {
        return COMMENT;
    }

    @Override
    public String getNodeTypeName() {
        return "comment";
    }

    @Override
    public String humanReadableRepresentation() {
        return _commentedSource;
    }

}
