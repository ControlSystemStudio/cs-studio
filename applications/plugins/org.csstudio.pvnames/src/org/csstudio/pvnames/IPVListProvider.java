/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.pvnames;

import java.util.regex.Pattern;


/**
* Interface for providers extension point
* 
* @author Fred Arnaud (Sopra Group)
* 
*/
public interface IPVListProvider {
	
	/** ID of the extension point, defined in plugin.xml */
    final public static String EXTENSION_POINT = "org.csstudio.pvnames";
    
	public PVListResult listPVs(Pattern pattern, int limit);

}
