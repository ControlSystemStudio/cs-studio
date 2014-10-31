package de.desy.language.snl.parser.nodes;

import de.desy.language.libraries.utils.contract.Contract;

public abstract class AbstractBraceBlockNode extends AbstractSNLNode {

	private final String _content;
	private final String _nodeTypeName;
	private final String _sourceId;

	public AbstractBraceBlockNode(final String nodeTypeName,
			final String sourceId, final String content, final int startOffset,
			final int endOffset) {
		super();
		Contract.requireNotNull("content", content);
		Contract.requireNotNull("nodeTypeName", nodeTypeName);
		Contract.requireNotNull("sourceId", sourceId);

		this._nodeTypeName = nodeTypeName;
		this._sourceId = sourceId;
		this.setStatementOffsets(startOffset, endOffset);
		this._content = content;
	}

	@Override
	protected String doGetSourceIdentifier() {
		return this._sourceId;
	}

	@Override
	public String getNodeTypeName() {
		return this._nodeTypeName;
	}

	@Override
	public String humanReadableRepresentation() {
		return this.getNodeTypeName() + " " + this.getContent();
	}

	@Override
	protected String doGetContent() {
		return this._content;
	}

	@Override
	public boolean hasContent() {
		return true;
	}

}