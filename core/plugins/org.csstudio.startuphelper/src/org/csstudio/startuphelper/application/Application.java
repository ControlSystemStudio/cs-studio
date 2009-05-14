/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.startuphelper.application;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.workbench.CssWorkbenchAdvisor;
import org.csstudio.startuphelper.extensions.CSSStartupExtensionPoint;
import org.csstudio.startuphelper.extensions.LocaleSettingsExtPoint;
import org.csstudio.startuphelper.extensions.LoginExtPoint;
import org.csstudio.startuphelper.extensions.ProjectExtPoint;
import org.csstudio.startuphelper.extensions.ServicesStartupExtPoint;
import org.csstudio.startuphelper.extensions.ShutDownExtPoint;
import org.csstudio.startuphelper.extensions.StartupParametersExtPoint;
import org.csstudio.startuphelper.extensions.WorkbenchExtPoint;
import org.csstudio.startuphelper.extensions.WorkspaceExtPoint;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * <code>Application</code> is the default implementation of the
 * {@link IApplication} interface which acts a an entry point for the Control
 * System Studio. This class uses several extension points through which the
 * user can provide his own code how certain aspects should be handled during
 * the star-up of the application or if they should be handled at all.
 * <p>
 * This implementation defines the action and the sequence in which the
 * extension points will be executed. For details see 
 * {@link #startApplication(IApplicationContext, Display)}.
 * </p> 
 * 
 * @author Alexander Will
 * @author Kay Kasemir
 * @author Xihui Chen
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a> (code
 *         unification, extension points)
 * 
 * @version $Revision$
 * 
 */
@SuppressWarnings("nls")
public class Application implements IApplication {

	/** Is the list of all parameters read at start-up and any other parameters
	 * which were created later on during the execution of this application */
	protected Map<String, Object> parameters;
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.
	 * IApplicationContext)
	 */
	public Object start(IApplicationContext context) throws Exception {
		//create the display 
		final Display display = PlatformUI.createDisplay();
		if (display == null) {
			CentralLogger.getInstance().getLogger(this).error("No display"); //$NON-NLS-1$
			return EXIT_OK;
		}

		try
		{
			return startApplication(context, display);
		}
		finally
		{
		    try
		    {
		        display.close();
		    }
		    catch (Throwable ex)
		    {
		        // On OS X, when using Command-Q to quit, we can get a
		        // "Widget is disposed" error.
		        // With the menu File/Exit that doesn't happen, only Cmd-Q.
		        // It's probably not a problem, and catching it here means
		        // the rest of the shutdown still works OK.
		        // Log it? Ignore it? Print it?
		        ex.printStackTrace();
		    }
		}
	}
	
	/**
	 * Initializes the application. Method consequently executes all other 'segments'
	 * of this class. The sequence is the following:
	 * <ul>
	 * 	<li> {@link #readStartupParameters(Display, IApplicationContext)} </li>
	 *  <li> {@link #applyLocaleSetting(IApplicationContext)} </li>
	 *  <li> {@link #promptForLogin(Display, IApplicationContext)} </li>
	 *  <li> {@link #promptForWorkspace(Display, IApplicationContext)} </li>
	 *  <li> {@link #startServices(Display, IApplicationContext)} </li>
	 *  <li> {@link #openProjects(Display, IApplicationContext)} </li>
	 *  <li> {@link #beforeWorkbenchStart(Display, IApplicationContext)} </li>
	 *  <li> {@link #startWorkbench(Display, IApplicationContext)} </li>
	 *  <li> {@link #afterWorkbenchStart(Display, IApplicationContext)} </li>
	 *  <li> {@link #closeProjects(Display, IApplicationContext)} </li>
	 * </ul>
	 * 
	 * To change the order in which the segments are executes override this method.
	 * <p>
	 * The default implementations of the methods listed above make the calls 
	 * to appropriate extension points. If one of the extension points returns
	 * an exit code this method will terminate the operation and return that 
	 * code immediately. If everything went the appropriate exit code as returned
	 * by the {@link #startWorkbench(Display, IApplicationContext)} is returned.
	 * </p>
	 * 
	 * @param context this application's context
	 * @param display the display of the application
	 * @return the exit code of any of the first executed segment that provided one. 
	 * 			If no other exit code is given the code returned by 
	 * 			{@link #startWorkbench(Display, IApplicationContext)} is returned by
	 * 			this method. 			
	 * 
	 * @throws Exception if anything went wrong during the execution of any of the segments
	 */
	protected Object startApplication(IApplicationContext context, Display display) throws Exception{
		
		parameters = readStartupParameters(display,context);
		Object exitCode = parameters.get(StartupParametersExtPoint.EXIT_CODE);
		if (exitCode != null) {
			return exitCode;
		}
		
		exitCode = applyLocaleSetting(context);
		if (exitCode != null) {
			return exitCode;
		}
		
		exitCode = promptForLogin(display, context);
		if (exitCode != null) {
			return exitCode;
		}
		
		exitCode = promptForWorkspace(display, context);
		if (exitCode != null) {
			return exitCode;
		}
		
		exitCode = startServices(display,context);
		if (exitCode != null) {
			return exitCode;
		}
		
		exitCode = openProjects(display,context);
		if (exitCode != null) {
			return exitCode;
		}
		
		exitCode = beforeWorkbenchStart(display,context);
		if (exitCode != null) {
			return exitCode;
		}
		
		//if everything is ok and working and no code arrives later, 
		//this is the code to return
		Object workbenchCode = startWorkbench(display,context);
		
		exitCode = afterWorkbenchStart(display,context);
		if (exitCode != null) {
			return exitCode;
		}
		
		exitCode = closeProjects(display,context);
		if (exitCode != null) {
			return exitCode;
		}
		return workbenchCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		stopApplication(); 
		closeWorkbench();
	}
		
	/**
	 * Applies the locale setting. Loads the {@link LocaleSettingsExtPoint} and executes
	 * the {@link LocaleSettingsExtPoint#applyLocaleSetting()} method. 
	 * 
	 * @param context the context of this application
	 * 
	 * @return potential exit code (null if everything is ok)
	 */
	protected Object applyLocaleSetting(IApplicationContext context) throws Exception{
		LocaleSettingsExtPoint[] points = getExtensionPoints(LocaleSettingsExtPoint.class, LocaleSettingsExtPoint.NAME);
		Object o = null;
		for (LocaleSettingsExtPoint p : points) {
			try {
				o = p.applyLocaleSettings(context, parameters);
				if (o != null) return o;
			} catch (Exception e) {
				errorExecutingExtensionPoint(LocaleSettingsExtPoint.NAME, e);
				throw e;
			}
		}
		return null;
	}
	
	/**
	 * Reads all the startup parameters and returns them in a map. This map will be passed
	 * to other extension points, which can use the parameters loaded by this extension
	 * point.
	 *  
	 * @param display the display of the application
	 * @param context the application's context
	 * 
	 * @return the map with all parameters 
	 */
	protected Map<String, Object> readStartupParameters(Display display, IApplicationContext context) throws Exception {
		StartupParametersExtPoint[] points = getExtensionPoints(StartupParametersExtPoint.class, StartupParametersExtPoint.NAME);
		Map<String, Object> parameters = new HashMap<String, Object>();
		for (StartupParametersExtPoint p : points) {
			try {
				parameters.putAll(p.readStartupParameters(display, context));
			} catch (Exception e) {
				errorExecutingExtensionPoint(StartupParametersExtPoint.NAME, e);
				throw e;
			}
		}
		return parameters;
	}
	
	/**
	 * Loads the {@link LoginExtPoint} and executes the 
	 * {@link LoginExtPoint#login(Display, IApplicationContext, Map)} method.
	 * It is expected that after the execution of this method the user is logged
	 * into the application and has the appropriate access rights requested by the
	 * setup of this application.
	 * 
	 * @param display the display of this application
	 * @param context the application's context
	 * 
	 * @return potential exit code (null if everything is ok)
	 */
	protected Object promptForLogin(Display display, IApplicationContext context) throws Exception {
		LoginExtPoint[] points = getExtensionPoints(LoginExtPoint.class, LoginExtPoint.NAME);
		Object o = null;
		for (LoginExtPoint p : points) {
			try {
				o = p.login(display, context, parameters);
				if (o != null) return o;
			} catch (Exception e) {
				errorExecutingExtensionPoint(LoginExtPoint.NAME, e);
				throw e;
			}
		}
		return null;
	}
	
	/**
	 * Loads the {@link WorkspaceExtPoint} and executes 
	 * {@link WorkspaceExtPoint#promptForWorkspace(Display, IApplicationContext, Map)}
	 * method. After the execution the workspace for the application should be selected.
	 * There can be only one extension point of this type in the application. If no
	 * extension points are defined the application will load the default workspace as
	 * specified and by this rcp. 
	 * 
	 * @param display the display of this application
	 * @param context this application's context
	 * 
	 * @return potential exit code (null if everything is ok)
	 */
    protected Object promptForWorkspace(Display display, IApplicationContext context) throws Exception {
		WorkspaceExtPoint[] points = getExtensionPoints(WorkspaceExtPoint.class, WorkspaceExtPoint.NAME);
		if (points.length > 1) {
		    CentralLogger.getInstance().getLogger(this).error("Cannot have more than one WorkspacePrompt extension point");
			return IApplication.EXIT_OK;
		}
		if (points.length == 0) {
			return null;
		} else {
			try {
				return points[0].promptForWorkspace(display, context, parameters);
			} catch (Exception e) {
				errorExecutingExtensionPoint(WorkspaceExtPoint.NAME, e);
				throw e;
			}
		}
	}
	
	/**
	 * Loads the {@link ServicesStartupExtPoint} and executes
	 * {@link ServicesStartupExtPoint#startServices(IApplicationContext)}.
	 * 
	 * @param display the display of this application
	 * @param context this application's context
	 * 
	 * @return potential exit code (null if everything is ok)
	 */
	protected Object startServices(Display display, IApplicationContext context) throws Exception {
		ServicesStartupExtPoint[] points = getExtensionPoints(ServicesStartupExtPoint.class, ServicesStartupExtPoint.NAME);
		Object o = null;
		for (ServicesStartupExtPoint p : points) {
			try {
				o = p.startServices(display, context, parameters);
				if (o != null) return o;
			} catch (Exception e) {
				errorExecutingExtensionPoint(ServicesStartupExtPoint.NAME, e);
				throw e;
			}
		}
		return o;
	}
	
	/**
	 * Loads the {@link ProjectExtPoint} and executes the 
	 * {@link ProjectExtPoint#openProjects(IApplicationContext)} method.
	 * 
	 * @param display the display of this application
	 * @param context this application's context
	 * 
	 * @return potential exit code (null if everything is ok)
	 */
	protected Object openProjects(Display display, IApplicationContext context) throws Exception {
		ProjectExtPoint[] points = getExtensionPoints(ProjectExtPoint.class, ProjectExtPoint.NAME);
		Object o = null;
		for (ProjectExtPoint p : points) {
			try {
				o = p.openProjects(display, context, parameters);
				if (o != null) return o;
			} catch (Exception e) {
				errorExecutingExtensionPoint(ProjectExtPoint.NAME, e);
				throw e;
			}
		}
		return null;
	}
	
	/**
	 * Called immediately before the workbench stops running. It loads the 
	 * {@link WorkbenchExtPoint} extension points and executes the
	 * {@link WorkbenchExtPoint#beforeWorkbenchCreation(IApplicationContext)} method.
	 * There can be only one extension point of this type in the application.
	 * 
	 * @param display the display of this application
	 * @param context this application's context
	 * @return potential exit code (null if everything is ok)
	 */
	protected Object beforeWorkbenchStart(Display display, IApplicationContext context) throws Exception {
		WorkbenchExtPoint[] points = getExtensionPoints(WorkbenchExtPoint.class, WorkbenchExtPoint.NAME);
		if (points.length > 1) {
		    CentralLogger.getInstance().getLogger(this).error("Cannot have more than one RunWorkbench extension point");
			return IApplication.EXIT_OK;
		}
		if (points.length == 0) {
			return null;
		} else {
			try {
				return points[0].beforeWorkbenchCreation(display, context, parameters);
			} catch (Exception e) {
				errorExecutingExtensionPoint(WorkbenchExtPoint.NAME, e);
				throw e;
			}
		}
	}
	
	/**
	 * Starts the workbench. Loads the {@link WorkbenchExtPoint} and executes
	 * {@link WorkbenchExtPoint#runWorkbench(Display, IApplicationContext, Map)} method.
	 * The workbench should be created and run by this method. There can be only one 
	 * extension point of this type in the application.
	 * 
	 * @param display the display of this application
	 * @param context this appication's context
	 * 
	 * @return the exit code
	 */
	protected Object startWorkbench(Display display, IApplicationContext context) throws Exception {
		WorkbenchExtPoint[] points = getExtensionPoints(WorkbenchExtPoint.class, WorkbenchExtPoint.NAME);
		if (points.length > 1) {
		    CentralLogger.getInstance().getLogger(this).error("Cannot have more than one RunWorkbench extension point");
			return IApplication.EXIT_OK;
		}
		if (points.length == 0) {
			return runDefaultWorkbench(display, context, parameters);
		} else {
			try {
				return points[0].runWorkbench(display, context, parameters);
			} catch (Exception e) {
				errorExecutingExtensionPoint(WorkbenchExtPoint.NAME, e);
				throw e;
			}
		}
	}
	
	/**
	 * Runs the default workbench which uses the {@link CssWorkbenchAdvisor}.
	 * 
	 * @param display the display that workbench should be created for
	 * @param context this application's context
	 * @param parameters the startup parameters which may define a specific behaviour of 
	 * 			this method
	 * @return the exit code
	 */
	protected Object runDefaultWorkbench(Display display, IApplicationContext context, Map<String, Object> parameters) {
		return PlatformUI.createAndRunWorkbench(display, new CssWorkbenchAdvisor());
	}
	
	/**
	 * Called immediately after the workbench stops running. It loads the 
	 * {@link WorkbenchExtPoint} extension points and executes the
	 * {@link WorkbenchExtPoint#afterWorkbenchCreation(IApplicationContext)} method.
	 * 
	 * @param display the display of this application
	 * @param context this application's context
	 * @return potential exit code (null if everything is ok)
	 */
	protected Object afterWorkbenchStart(Display display, IApplicationContext context) throws Exception {
		WorkbenchExtPoint[] points = getExtensionPoints(WorkbenchExtPoint.class, WorkbenchExtPoint.NAME);
		if (points.length > 1) {
		    CentralLogger.getInstance().getLogger(this).error("Cannot have more than one RunWorkbench extension point");
			return IApplication.EXIT_OK;
		}
		if (points.length == 0) {
			return null;
		} else {
			try { 
				return points[0].afterWorkbenchCreation(display, context, parameters);
			} catch (Exception e) {
				errorExecutingExtensionPoint(WorkbenchExtPoint.NAME, e);
				throw e;
			}
		}		
	}
	
	/**
	 * Loads the {@link ProjectExtPoint} and executes the 
	 * {@link ProjectExtPoint#closeProjects(IApplicationContext)} method.
	 * 
	 * @param display the display of this application
	 * @param context this application's context
	 * 
	 * @return potential exit code (null if everything is ok)
	 */
	protected Object closeProjects(Display display, IApplicationContext context) throws Exception {
		ProjectExtPoint[] points = getExtensionPoints(ProjectExtPoint.class, ProjectExtPoint.NAME);
		Object o = null;
		for (ProjectExtPoint p : points) {
			try {
				o = p.closeProjects(display, context, parameters);
				if (o != null) return o;
			} catch (Exception e) {
				errorExecutingExtensionPoint(ProjectExtPoint.NAME, e);
				throw e;
			}
		}
		return null;
	}
	
	/**
	 * Loads all {@link ShutDownExtPoint}s and executes the 
	 * {@link ShutDownExtPoint#beforeShutDown()} method.
	 * 
	 */
	protected void stopApplication() {
		ShutDownExtPoint[] points = getExtensionPoints(ShutDownExtPoint.class, ShutDownExtPoint.NAME);
		for (ShutDownExtPoint p : points) {
			try {
				p.beforeShutDown(parameters);
			} catch (Exception e) {
				errorExecutingExtensionPoint(ShutDownExtPoint.NAME, e);
			}
		}
	}
	
	/**
	 * This method is called as the last executed action when the application is 
	 * stopped. It closes the active workbench. This action is not made as an
	 * extension points because it should be executed at each exit of the application. 
	 */
	protected void closeWorkbench() {
		// IDEApplication copy
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null) {
			return;
		}
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}

	/**
	 * Gathers the loaded extension points which match the parameter criteria.
	 * 
	 * @param <T> the type of the extension point requested
	 * @param type the interface/implementation that defines this type
	 * @param name the name of the extension point
	 * @return the array of extension points
	 */
	@SuppressWarnings("unchecked")
	protected <T extends CSSStartupExtensionPoint> T[] getExtensionPoints(Class<T> type, String name) {
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(CSSStartupExtensionPoint.NAME);
		ArrayList<T> list = new ArrayList<T>();
		for (IConfigurationElement e : config) {
			try {
				Object o = e.createExecutableExtension("class");
				if (type.isAssignableFrom(o.getClass())) {
					list.add((T)o);
				}
			} catch (Exception ex) {
			    CentralLogger.getInstance().getLogger(this).error("Error loading " + name + " extension points.", ex);
			}
		}
		T[] array = (T[])Array.newInstance(type, list.size());
		return list.toArray(array);
	}
	
	/**
	 * This is a utility method which logs the error that happens during the
	 * execution of one of the extension points.
	 * 
	 * @param name the name of the extension point
	 * @param t the exception that occurred during execution (could be null) 
	 */
	protected void errorExecutingExtensionPoint(String name, Throwable t) {
	    CentralLogger.getInstance().getLogger(this).error("Error executing " + name
				+ " extension point.", t);
	}
}