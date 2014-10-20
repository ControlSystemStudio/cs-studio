package de.desy.language.snl.parser.nodes;

public class PlaceholderNode extends AbstractSNLNode {

	private final String _sourceId;

	public PlaceholderNode(final String sourceId) {
		super();
		this._sourceId = sourceId;
		this.setStatementOffsets(0, 1);
		this.addError(this._sourceId);
	}

	@Override
	protected String doGetSourceIdentifier() {
		return this._sourceId;
	}

	@Override
	public String getNodeTypeName() {
		return "Placeholder";
	}

	@Override
	public String humanReadableRepresentation() {
		return this.getNodeTypeName() + ": " + this.getSourceIdentifier();
	}

}
