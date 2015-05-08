package org.csstudio.sds.language.script.parser.statementParser;

import org.csstudio.sds.language.script.parser.nodes.BlockConditionNode;

public class ConditionStatementParser extends
        AbstractBraceParser<BlockConditionNode> {

    public ConditionStatementParser() {
        super('(', ')');
    }

    @Override
    protected BlockConditionNode doCreateNode(final String content,
            final int startOffset, final int endOffset) {
        return new BlockConditionNode(content, startOffset, endOffset);
    }

}
