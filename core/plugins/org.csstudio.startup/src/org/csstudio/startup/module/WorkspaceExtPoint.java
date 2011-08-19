/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.startup.module;

import java.util.Map;

import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * <code>WorkspacePromptExtPoint</code> defines how the workspace for the application
 * is loaded. There can be only one extension point of this type in the application.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public interface WorkspaceExtPoint extends CSSStartupExtensionPoint {

	/** The name of this extension point element */
	public static final String NAME = "workspace"; //$NON-NLS-1$
	
	/** The tag under which the workspace URL should be stored in the parameters */
	public static final String WORKSPACE = "css.workspace"; //$NON-NLS-1$
	
	/**
	 * Selects the appropriate workspace according to the given parameters. If the
	 * the workspace cannot be selected automatically the implementation should ask
	 * the user to define it.
	 * 
	 * @param display the display of the application
	 * @param context the context of the application
	 * @param parameters contains additional parameters, which can define
	 * 			some special behaviour during the execution of this method (the keys
	 * 			are parameters names and the values are parameters values)
	 * 
	 * @return the exit code if something happened which requires to exit or restart 
	 * 			application or null if everything is alright
	 * @throws Exception if an error occurred during the operation
	 */
	public Object promptForWorkspace(Display display, IApplicationContext context, Map<String, Object> parameters) throws Exception;
}
