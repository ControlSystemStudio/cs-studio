package de.desy.language.snl.parser.nodes;

/**
 * The outline node of an SNL state.
 * 
 * @author C1 WPS / KM, MZ
 */
public class StateNode extends AbstractSNLNode {

	private final String _stateName;
	private final String _content;

	public StateNode(final String stateName, final String content,
			final int statementsStartOffsetInSource,
			final int statementsEndOffsetInSource) {
		this._stateName = stateName;
		this._content = content;
		this.setStatementOffsets(statementsStartOffsetInSource,
				statementsEndOffsetInSource);
	}

	@Override
	public String humanReadableRepresentation() {
		return this.getNodeTypeName() + " " + this.getSourceIdentifier();
	}

	@Override
	public String getNodeTypeName() {
		return "state";
	}

	@Override
	protected String doGetSourceIdentifier() {
		return this._stateName;
	}

	@Override
	public boolean hasContent() {
		return true;
	}

	@Override
	protected String doGetContent() {
		return this._content;
	}

	public void setStartOffsets(final int startOffset, final int endOffset) {
		super.setStatementOffsets(startOffset, endOffset);
	}
}
