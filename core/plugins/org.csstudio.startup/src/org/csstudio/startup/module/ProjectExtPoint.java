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
 * <code>ProjectExtPoint</code> defines methods which will open or close the projects
 * that belong to the application context.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public interface ProjectExtPoint extends CSSStartupExtensionPoint {

	/** The name of this extension point element */
	public static final String NAME = "project"; //$NON-NLS-1$
	
	/** The tag under which the opened projects should be stored in the parameters */
	public static final String PROJECTS = "css.projects"; //$NON-NLS-1$
	
	/**
	 * Opens the projects defined by the given application context. The 
	 * implementation should also take care of initialization of projects
	 * and any other actions required for them to run properly. 
	 * The projects that were run should be placed inside the parameters map under the
	 * tag {@value #PROJECTS} to enable their usage by other extension point.
	 * 
	 * @param context the application's context
	 * @param parameters contains additional parameters, which can define
	 * 			some special behaviour during the execution of this method (the keys
	 * 			are parameters names and the values are parameters values)
	 * 
	 * @return the exit code if something happened which requires to exit or restart 
	 * 			application or null if everything is alright
	 * 
	 * @throws Exception if an error occurred during the operation
	 */
	public Object openProjects(Display display, IApplicationContext context, Map<String, Object> parameters) throws Exception;
	
	/**
	 * Closes all the projects specified by the given context. In principle
	 * this method should close all those projects that were previously opened
	 * by {@link #openProjects(IApplicationContext)} method but it is not mandatory
	 * that the implementation follows the rules. However, the implementor should
	 * be aware of the risk if certain projects are not properly closed.
	 * 
	 * @param context the application's context
	 * @param parameters contains additional parameters, which can define
	 * 			some special behaviour during the execution of this method (the keys
	 * 			are parameters names and the values are parameters values)
	 * 
	 * @return the exit code if something happened which requires to exit or restart 
	 * 			application or null if everything is alright
	 * 
	 * @throws Exception if an error occurred during the operation
	 */
	public Object closeProjects(Display display, IApplicationContext context, Map<String, Object> parameters) throws Exception;
}
