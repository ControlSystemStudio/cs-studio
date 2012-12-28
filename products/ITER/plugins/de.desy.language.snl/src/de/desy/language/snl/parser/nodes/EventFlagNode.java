package de.desy.language.snl.parser.nodes;

/**
 * The outline node of an SNL event flag.
 * 
 * @author C1 WPS / KM, MZ
 */
public class EventFlagNode extends AbstractSNLNode {

	private final String _eventFlagName;
	private SyncStatementNode _syncNode;

	public EventFlagNode(final String eventFlagName, final int startOffset,
			final int endOffset) {
		super();
		super.setStatementOffsets(startOffset, endOffset);
		this._eventFlagName = eventFlagName;
	}

	@Override
	public String humanReadableRepresentation() {
		final StringBuffer result = new StringBuffer(this.getNodeTypeName());
		result.append(": ");
		result.append(this.getSourceIdentifier());
		if (this.isSynchronized()) {
			result.append(" synchronized with ");
			result.append(this.getSynchronizedVariableName());
		}
		return result.toString();
	}

	@Override
	public String getNodeTypeName() {
		return "event flag";
	}

	@Override
	protected String doGetSourceIdentifier() {
		return this._eventFlagName;
	}

	public void setSynchronized(final SyncStatementNode syncNode) {
		this._syncNode = syncNode;
		this.addChild(syncNode);
	}

	public boolean isSynchronized() {
		return this._syncNode != null;
	}

	public String getSynchronizedVariableName() {
		return this._syncNode.getContent();
	}

}
