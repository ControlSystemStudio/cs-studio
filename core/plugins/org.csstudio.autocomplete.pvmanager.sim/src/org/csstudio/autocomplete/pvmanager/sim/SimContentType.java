/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.pvmanager.sim;

import org.csstudio.autocomplete.parser.ContentType;

/**
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class SimContentType extends ContentType {

	public static SimContentType SimFunction = new SimContentType("SimFunction");

	private SimContentType(String value) {
		super(value);
	}

}
