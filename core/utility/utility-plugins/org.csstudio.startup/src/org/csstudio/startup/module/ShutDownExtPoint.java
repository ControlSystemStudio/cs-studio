/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.startup.module;

import java.util.Map;

/**
 * 
 * <code>ShutDownExtPoint</code> is used to provide the code that needs to be
 * executed just before the workbench closes. Such as for instance saving 
 * some data, closing connections or anything else which is not done automatically.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public interface ShutDownExtPoint extends CSSStartupExtensionPoint {

	/** The name of this extension point element */
	public static final String NAME = "shutdown"; //$NON-NLS-1$
	
	/**
	 * Is called just before the workbench is closed. The implementation should 
	 * handle all the things that need to be done before the application exits.
	 * 
	 * @param parameters contains additional parameters, which can define
	 * 			some special behaviour during the execution of this method (the keys
	 * 			are parameters names and the values are parameters values)
	 * 
	 */
	public void beforeShutDown(Map<String, Object> parameters);
}
