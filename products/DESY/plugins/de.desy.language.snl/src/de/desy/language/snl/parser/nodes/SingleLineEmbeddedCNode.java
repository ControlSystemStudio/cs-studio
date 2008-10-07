package de.desy.language.snl.parser.nodes;

public class SingleLineEmbeddedCNode extends AbstractSNLNode {

	private final String _content;

	public SingleLineEmbeddedCNode(final String content, final int startOffset,
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
		return "(embedded c line)";
	}

	@Override
	public String getNodeTypeName() {
		return "Embedded c line";
	}

	@Override
	public String humanReadableRepresentation() {
		return this.getNodeTypeName();
	}

}
