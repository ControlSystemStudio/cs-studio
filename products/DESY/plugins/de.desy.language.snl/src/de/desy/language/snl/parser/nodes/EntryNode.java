package de.desy.language.snl.parser.nodes;

public class EntryNode extends AbstractSNLNode {

	private final String _sourceId;

	public EntryNode(final String sourceId,
			final int startOffset, final int endOffset) {
		super();
		super.setStatementOffsets(startOffset, endOffset);
		this._sourceId = sourceId;
	}

	@Override
	protected String doGetSourceIdentifier() {
		return this._sourceId;
	}

	@Override
	public String getNodeTypeName() {
		return "entry";
	}

	@Override
	public String humanReadableRepresentation() {
		return this.getNodeTypeName() + ": " + this.getSourceIdentifier();
	}
	
	public void setOffsets(final int startOffset, final int endOffset) {
		super.setStatementOffsets(startOffset, endOffset);
	}

}
