package de.desy.language.snl.parser.nodes;

public class MonitorStatementNode extends AbstractSNLNode {

	private final String _sourceId;

	public MonitorStatementNode(final String sourceId, final int startOffset,
			final int endOffset) {
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
		return "monitor";
	}

	@Override
	public String humanReadableRepresentation() {
		return this.getSourceIdentifier() + " is monitored";
	}

}
