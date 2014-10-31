package de.desy.language.snl.parser.nodes;

public class DefineStatementNode extends AbstractSNLNode {

	private final String _sourceId;
	private String _value;

	public DefineStatementNode(final String sourceId, final String value,
			final int startOffset, final int endOffset) {
		super();
		super.setStatementOffsets(startOffset, endOffset);
		this._sourceId = sourceId;
		_value = value;
	}

	@Override
	protected String doGetSourceIdentifier() {
		return this._sourceId;
	}
	
	public String getValue() {
		return _value;
	}

	@Override
	public String getNodeTypeName() {
		return "define";
	}

	@Override
	public String humanReadableRepresentation() {
		return this.getNodeTypeName() + ": " + this.getSourceIdentifier();
	}

}
