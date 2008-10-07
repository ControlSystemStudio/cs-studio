package de.desy.language.snl.parser.nodes;

public class CharNode extends AbstractSNLNode {

	private final String _sourceId;

	public CharNode(final String sourceId, final int startOffsetLastFound,
			final int endOffsetLastFound) {
		super();
		this.setStatementOffsets(startOffsetLastFound, endOffsetLastFound);
		this._sourceId = sourceId;
	}

	@Override
	protected String doGetSourceIdentifier() {
		return this._sourceId;
	}

	@Override
	public String getNodeTypeName() {
		return "char";
	}

	@Override
	public String humanReadableRepresentation() {
		return this.getSourceIdentifier() + ": " + this.getNodeTypeName();
	}

}
