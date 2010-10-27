/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.macros;

/**Infinite loop detected. 
 * @author Xihui Chen
 *
 */
public class InfiniteLoopException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 569430280936384743L;

	public InfiniteLoopException() { // NOP
	}

	public InfiniteLoopException(String message) {
		super(message);
	}

	public InfiniteLoopException(Throwable cause) {
		super(cause);
	}

	public InfiniteLoopException(String message, Throwable cause) {
		super(message, cause);
	}

}
