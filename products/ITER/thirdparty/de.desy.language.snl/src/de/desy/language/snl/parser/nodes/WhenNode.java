package de.desy.language.snl.parser.nodes;

/**
 * The outline node of an SNL state when condition.
 * 
 * The source identifier will be the condition required to receive that state.
 * 
 * @author C1 WPS / KM, MZ
 */
public class WhenNode extends AbstractSNLNode {

	private final String _condition;
	private final String _followingState;
	private final String _content;

	@Deprecated
	public WhenNode(final String condition, final String followingState,
			final int statementsStartOffsetInSource,
			final int statementsEndOffsetInSource) {
		this(condition, null, followingState, statementsStartOffsetInSource,
				statementsEndOffsetInSource);
	}

	public WhenNode(final String condition, final String content,
			final String followingState,
			final int statementsStartOffsetInSource,
			final int statementsEndOffsetInSource) {
		this._condition = condition;
		this._followingState = followingState;
		this._content = content;

		this.setStatementOffsets(statementsStartOffsetInSource,
				statementsEndOffsetInSource);
	}

	@Override
	public boolean hasContent() {
		return this._content != null;
	}

	@Override
	protected String doGetContent() {
		return this._content;
	}

	@Override
	public String humanReadableRepresentation() {
		return this.getNodeTypeName() + " (" + this.getSourceIdentifier()
				+ ") -> " + this._followingState;
	}

	@Override
	public String getNodeTypeName() {
		return "when";
	}

	@Override
	protected String doGetSourceIdentifier() {
		return this._condition;
	}

	public String getFollowingState() {
		return this._followingState;
	}

	public void setOffsets(final int startOffset, final int endOffset) {
		super.setStatementOffsets(startOffset, endOffset);
	}

}
