package de.desy.language.snl.parser.parser;

import de.desy.language.snl.parser.nodes.BlockConditionNode;

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
