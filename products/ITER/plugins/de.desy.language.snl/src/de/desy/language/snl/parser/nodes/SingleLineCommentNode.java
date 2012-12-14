package de.desy.language.snl.parser.nodes;

public class SingleLineCommentNode extends AbstractSNLNode {

	private final String _content;

	public SingleLineCommentNode(final String content, final int startOffset,
			final int endOffset) {
		super.setStatementOffsets(startOffset, endOffset);
		this._content = content;
	}

	@Override
	public boolean hasContent() {
		return true;
	}

	@Override
	protected String doGetContent() {
		return this._content;
	}

	@Override
	protected String doGetSourceIdentifier() {
		return "(single line comment)";
	}

	@Override
	public String getNodeTypeName() {
		return "Single line comment";
	}

	@Override
	public String humanReadableRepresentation() {
		return this.getNodeTypeName();
	}

}
