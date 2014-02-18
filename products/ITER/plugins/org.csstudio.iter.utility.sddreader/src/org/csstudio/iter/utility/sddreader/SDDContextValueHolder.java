/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.iter.utility.sddreader;

public class SDDContextValueHolder {

	private static final SDDContext context = new SDDContext();

	/**
	 * Singleton = private constructor
	 */
	private SDDContextValueHolder() {
		// Singleton
	}

	public static SDDContext get() {
		return context;
	}
}
