package de.desy.language.snl.parser.parser;

import de.desy.language.libraries.utils.contract.Contract;
import de.desy.language.snl.parser.nodes.AbstractSNLNode;

/**
 * Abstract base class for all SNL specific element parser (not the root
 * parser!). All of that parsers will not be able to handle comments and
 * embedded C; these parts have to be removed (offsets may have to be fixed
 * after processing) or replaced (recommended) with whitespace chars by the root
 * parser.
 * 
 * @author C1 WPS / KM, MZ
 * 
 * @param <N>
 *            The Type of parsed SNL element node.
 */
public abstract class AbstractStatementParser<N extends AbstractSNLNode> {
	private CharSequence _input;

	/**
	 * The last character index of found statement.
	 */
	public final int getEndOffsetLastFound() {
		Contract.require(this.hasFoundElement(), "hasFoundElement()");

		final int result = this.doEndOffsetLastFound();

		Contract.ensure(result >= 0, "result >= 0");
		Contract.ensure(result < this.getInput().length(),
				"result < getInput().length()");
		return result;
	}

	public final void findNext(final CharSequence input) {
		this.findNext(input, 0);
	}

	public final void findNext(final CharSequence input, final int startIndex) {
		Contract.requireNotNull("input", input);
		Contract.require(startIndex >= 0, "startIndex >= 0");
		Contract.require(startIndex < input.length(),
				"startIndex < input.length()");

		this._input = input;

		this.doFindNext(input, startIndex);
	}

	public final CharSequence getInput() {
		return this._input;
	}

	public final N getLastFoundAsNode() {
		Contract.require(this.hasFoundElement(), "hasFoundElement()");

		final N result = this.doGetLastFoundAsNode();

		Contract.ensureResultNotNull(result);
		return result;
	}

	/**
	 * Returns the statement like it placed in the source. The returned String
	 * will be trimmed.
	 */
	public final String getLastFoundStatement() {
		Contract.require(this.hasFoundElement(), "hasFoundElement()");

		final String result = this.doGetLastFoundStatement();

		Contract.ensureResultNotNull(result);
		return result;
	}

	public abstract boolean hasFoundElement();

	public final int getStartOffsetLastFound() {
		Contract.require(this.hasFoundElement(), "hasFoundElement()");

		final int result = this.doStartOffsetLastFound();

		Contract.ensure(result >= 0, "result >= 0");
		Contract.ensure(result < this.getInput().length(),
				"result < getInput().length()");
		return result;
	}

	protected abstract int doEndOffsetLastFound();

	protected abstract void doFindNext(CharSequence input, int startIndex);

	protected abstract N doGetLastFoundAsNode();

	protected abstract String doGetLastFoundStatement();

	protected abstract int doStartOffsetLastFound();

}
