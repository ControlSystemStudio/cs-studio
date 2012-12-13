package de.desy.language.snl.parser.nodes;

import de.desy.language.editor.core.parser.Node;
import de.desy.language.libraries.utils.contract.Contract;

public abstract class AbstractSNLNode extends Node {

	/**
	 * Returns the name of the content, like a program name or a variable name.
	 * 
	 * Implemented by {@link #doGetSourceIdentifier()}.
	 * 
	 * @return The non-empty, not null string represents the content like it
	 *         appears in the source.
	 */
	public final String getSourceIdentifier() {
		final String result = this.doGetSourceIdentifier();

		Contract.ensureResultNotNull(result);
		return result;
	}

	/**
	 * Extracts the name of the content, like a program name or a variable name.
	 * 
	 * @return The non-empty, not null string represents the content like it
	 *         appears in the source.
	 */
	protected abstract String doGetSourceIdentifier();

	public boolean hasContent() {
		return false;
	}

	public final String getContent() {
		Contract.require(this.hasContent(), "hasContent()");

		final String result = this.doGetContent();

		Contract.ensureResultNotNull(result);
		return result;
	}

	protected String doGetContent() {
		return "";
	}

}
