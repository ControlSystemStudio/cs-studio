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
 * <code>StartupParametersExtPoint</code> reads the input parameters and provides
 * access to those parameters to the application, which subsequently forwards the 
 * parameters to other extension points.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public interface StartupParametersExtPoint extends CSSStartupExtensionPoint {
	
	/** The name of the extension point element */
	public static final String NAME = "startupParameters"; //$NON-NLS-1$
	
	/** The tag which defines the exit code. */ 
	public static final String EXIT_CODE = "css.exit"; //$NON-NLS-1$
		
	/**
	 * Gather the applications startup parameters and returns them to the user. It is up
	 * to the implementation from where the parameters will be read (either from the 
	 * context, configuration file, system properties etc.). It is important that the
	 * returned parameters are properly place inside the map which is returned by this method.
	 * There is no general rule what the key and and the object should be. However, they
	 * should be of the type that is understood by the other {@link CSSStartupExtensionPoint}
	 * that are used afterwards (Some of the default implementation might use certain
	 * parameters. For details see the individual extension points.)
	 * <p>
	 * In the case when something happened what requires the application to exit or restart
	 * this method can put a parameter {@value #EXIT_CODE} in the returned map which
	 * should trigger the application to take appropriate actions. 
	 *  
	 * @param display the display of the application
	 * @param context the application's context
	 * @return the map of all parameters
	 * 
	 * @throws Exception if something unexpected happened
	 */
	public Map<String, Object> readStartupParameters(Display display, IApplicationContext context) throws Exception;
}
