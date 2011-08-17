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
 * <code>LoginExtPoint</code> defines the credentials for the login to the
 * application and logins the user to the application.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
@SuppressWarnings("nls")
public interface LoginExtPoint extends CSSStartupExtensionPoint {
	
	/** The name of this extension point element */
	public static final String NAME = "login";
	
	/** The tag under which the username should be stored in the parameters map */
	public static final String USERNAME = "css.username";
	
	/** The tag under which the password should be stored in the parameters map */
	public static final String PASSWORD = "css.password";
	
	/**
	 * Prompts the user to enter the credentials and logins to the application. The 
	 * credentials should be placed in the parameters map since they might be used
	 * by other extension points. Use the {@link #USERNAME} and {@link #PASSWORD} tags
	 * to store the data into the map.
	 *  
	 * @param display the display of the application
	 * @param context the application's context
	 * @param parameters contains additional parameters, which can define
	 * 			some special behavior during the execution of this method (the keys
	 * 			are parameters names and the values are parameters values)
	 * 
	 * @return the exit code if something happened which requires to exit or restart 
	 * 			application or null if everything is alright
	 * @throws Exception if an error occurred during the operation
	 */
	public Object login(Display display, IApplicationContext context, Map<String, Object> parameters) throws Exception;
}
