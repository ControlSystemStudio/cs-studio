package de.desy.language.snl.parser.nodes;

/**
 * The content of this node is the channel name without quotes.
 */
public class AssignStatementNode extends AbstractSNLNode {

	private final String _variableName;
	private final String _channel;
	private final boolean _isArray;

	public AssignStatementNode(final String variableName, final String channel,
			final int startOffset, final int endOffset, boolean isArray) {
		super.setStatementOffsets(startOffset, endOffset);
		this._variableName = variableName;
		this._channel = channel;
		this._isArray = isArray;
	}

	@Override
	protected String doGetSourceIdentifier() {
		return this._variableName;
	}

	@Override
	public String getNodeTypeName() {
		return "assign";
	}

	@Override
	public boolean hasContent() {
		return true;
	}

	@Override
	protected String doGetContent() {
		return this._channel;
	}
	
	public boolean isArray() {
		return _isArray;
	}

	@Override
	public String humanReadableRepresentation() {
		return this.getSourceIdentifier() + " is assigned to \""
				+ this.getContent() + "\"";
	}

}
