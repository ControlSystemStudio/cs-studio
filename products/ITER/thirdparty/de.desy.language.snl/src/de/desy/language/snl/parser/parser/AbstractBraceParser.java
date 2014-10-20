package de.desy.language.snl.parser.parser;

import de.desy.language.snl.parser.nodes.AbstractSNLNode;

public abstract class AbstractBraceParser<N extends AbstractSNLNode> extends
		AbstractStatementParser<N> {

	private boolean _found;
	private N _node;
	private String _lastStatement;
	private int _start;
	private int _end;
	private final char _openBraceChar;
	private final char _closeBraceChar;

	public AbstractBraceParser(final char openBraceChar,
			final char closeBraceChar) {
		super();
		this._openBraceChar = openBraceChar;
		this._closeBraceChar = closeBraceChar;
	}

	@Override
	protected void doFindNext(final CharSequence input, final int startIndex) {
		final String blockStatement = this
				.findBlockStatement(input, startIndex);
		this._found = blockStatement != null;
		if (this._found) {
			this._lastStatement = blockStatement;
			this._node = this.doCreateNode(this._lastStatement.substring(1,
					this._lastStatement.length() - 1), this._start, this._end);
		}
	}

	abstract protected N doCreateNode(String content, int startOffset,
			int endOffset);

	private String findBlockStatement(final CharSequence input,
			final int startIndex) {
		int braceCount = 0;
		this._start = -1;
		for (int i = startIndex; i < input.length(); i++) {
			if (this._openBraceChar == input.charAt(i)) {
				if (this._start < 0) {
					this._start = i;
				}
				braceCount++;
			}
			if (this._closeBraceChar == input.charAt(i)) {
				braceCount--;
			}
			if (braceCount < 0) {
				return null;
			}
			if ((braceCount == 0) && (this._start >= 0)) {
				this._end = i;
				return input.subSequence(this._start, this._end + 1).toString();
			}
		}
		return null;
	}

	@Override
	protected N doGetLastFoundAsNode() {
		return this._node;
	}

	@Override
	protected String doGetLastFoundStatement() {
		return this._lastStatement;
	}

	@Override
	protected int doStartOffsetLastFound() {
		return this._start;
	}

	@Override
	protected int doEndOffsetLastFound() {
		return this._end;
	}

	@Override
	public boolean hasFoundElement() {
		return this._found;
	}

}