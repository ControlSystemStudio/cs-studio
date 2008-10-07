package de.desy.language.snl.parser.nodes;

public class StateSetNode extends AbstractSNLNode {

	private final String _content;
	private final String _sourceId;

	public StateSetNode(final String sourceId, final String content,
			final int startOffset, final int endOffset) {
		super();
		super.setStatementOffsets(startOffset, endOffset);
		this._content = content;
		this._sourceId = sourceId;
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
		return this._sourceId;
	}

	@Override
	public String getNodeTypeName() {
		return "state set";
	}

	@Override
	public String humanReadableRepresentation() {
		return this.getNodeTypeName() + ": " + this.getSourceIdentifier();
	}

}
