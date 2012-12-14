package de.desy.language.snl.parser.nodes;

public class MultiLineCommentNode extends AbstractSNLNode {

	private final String _sourceId;

	public MultiLineCommentNode(final String sourceId,
			final int startOffsetLastFound, final int endOffsetLastFound) {
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
		return "comment";
	}

	@Override
	public String humanReadableRepresentation() {
		return this.getNodeTypeName() + ": " + this.getSourceIdentifier();
	}

}
