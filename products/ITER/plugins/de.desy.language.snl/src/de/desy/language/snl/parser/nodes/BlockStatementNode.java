package de.desy.language.snl.parser.nodes;

/**
 * This node represents a block statement.
 * 
 * @author C1 WPS / KM, MZ
 */
public class BlockStatementNode extends AbstractBraceBlockNode {

	public BlockStatementNode(final String content, final int startOffset,
			final int endOffset) {
		super("statement block", "{}", content, startOffset, endOffset);
	}
}
