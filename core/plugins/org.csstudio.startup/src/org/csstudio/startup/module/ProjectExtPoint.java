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
import org.eclipse.ui.application.WorkbenchAdvisor;

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
	 * Called by {@link org.csstudio.startup.application.Application} to
	 * allow implementors of the {@link CSSStartupExtensionPoint} to perform
	 * project cleanup in case that is considered necessary.
	 *
     * <p>In principle this method could close all those projects that were previously opened
     * by {@link #openProjects(IApplicationContext)} method but it is not mandatory
     * that the implementation follows the rules.
	 *
	 * <p>In most cases, one should actually NOT close any projects in here.
	 * The suggested way to assert that all resources are properly saved
	 * on exiting CSS is to call
	 * <code>ResourcesPlugin.getWorkspace().save(true, new NullProgressMonitor());</code>
	 * from the {@link WorkbenchAdvisor}'s preShutdown() method.
	 *
	 * RCP will invoke that preShutdown() when the workbench is about to close
	 * as part of the workbench run cycle.
	 *
	 * This closeProjects() method is actually called later from the Application
	 * code and in fact results in an "exited with unsaved changes" warning
	 * on the following startup of CSS.
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
