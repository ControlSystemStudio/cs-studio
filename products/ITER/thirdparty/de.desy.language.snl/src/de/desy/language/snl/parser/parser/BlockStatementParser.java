package de.desy.language.snl.parser.parser;

import de.desy.language.snl.parser.nodes.BlockStatementNode;

public class BlockStatementParser extends
		AbstractBraceParser<BlockStatementNode> {

	public BlockStatementParser() {
		super('{', '}');
	}

	@Override
	protected BlockStatementNode doCreateNode(final String lastStatement,
			final int startOffset, final int endOffset) {
		return new BlockStatementNode(lastStatement, startOffset, endOffset);
	}

}
