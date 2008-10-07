package de.desy.language.snl.parser.nodes;

/**
 * The content of this node is the channel name without quotes.
 */
public class AssignStatementNode extends AbstractSNLNode {

	private final String _variableName;
	private final String _channel;

	public AssignStatementNode(final String variableName, final String channel,
			final int startOffset, final int endOffset) {
		super.setStatementOffsets(startOffset, endOffset);
		this._variableName = variableName;
		this._channel = channel;
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

	@Override
	public String humanReadableRepresentation() {
		return this.getSourceIdentifier() + " is assigned to \""
				+ this.getContent() + "\"";
	}

}
