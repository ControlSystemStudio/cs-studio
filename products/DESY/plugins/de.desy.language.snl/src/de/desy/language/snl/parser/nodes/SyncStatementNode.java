package de.desy.language.snl.parser.nodes;

public class SyncStatementNode extends AbstractSNLNode {

	private final String _eventFlagName;
	private final String _variableName;

	public SyncStatementNode(final String eventFlagName,
			final String variableName, final int startOffset,
			final int endOffset) {
		super();
		super.setStatementOffsets(startOffset, endOffset);
		this._eventFlagName = eventFlagName;
		this._variableName = variableName;
	}

	@Override
	protected String doGetSourceIdentifier() {
		return this._eventFlagName;
	}

	@Override
	public String getNodeTypeName() {
		return "sync";
	}

	@Override
	public String humanReadableRepresentation() {
		return this.getNodeTypeName() + ": " + this.getSourceIdentifier()
				+ " is synchronized with " + this.getContent();
	}

	@Override
	public boolean hasContent() {
		return true;
	}

	@Override
	protected String doGetContent() {
		return this._variableName;
	}

}
