
package org.csstudio.nams.service.regelwerkbuilder.declaration;

import org.csstudio.nams.common.material.regelwerk.Regelwerk;

/**
 * This exception is thrown if loading or creating of {@link Regelwerk}-elements
 * failed.
 */
public class RegelwerksBuilderException extends Exception {

	private static final long serialVersionUID = -6257594513186459756L;

	public RegelwerksBuilderException() {
	    // Nothing to do
	}

	public RegelwerksBuilderException(final String arg0) {
		super(arg0);
	}

	public RegelwerksBuilderException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}

	public RegelwerksBuilderException(final Throwable arg0) {
		super(arg0);
	}
}
