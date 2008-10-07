package de.desy.language.snl.parser.nodes;

public class BlockConditionNode extends AbstractBraceBlockNode {

	public BlockConditionNode(final String content, final int startOffset,
			final int endOffset) {
		super("condition block", "()", content, startOffset, endOffset);
	}

}
