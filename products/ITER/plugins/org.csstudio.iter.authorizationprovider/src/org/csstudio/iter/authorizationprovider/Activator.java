/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.iter.authorizationprovider;

import java.util.logging.Logger;

/**
 * Plugin Activator
 * 
 * @author Davy Dequidt
 */
@SuppressWarnings("nls")
public class Activator {
	final public static String ID = "org.csstudio.iter.authorizationprovider";

	/** @return Logger for plugin ID */
	public static Logger getLogger() {
		return Logger.getLogger(ID);
	}
}
