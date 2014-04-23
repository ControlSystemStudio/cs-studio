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
package org.csstudio.startup.application;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.startup.Plugin;
import org.csstudio.startup.module.CSSStartupExtensionPoint;
import org.csstudio.startup.module.LocaleSettingsExtPoint;
import org.csstudio.startup.module.LoginExtPoint;
import org.csstudio.startup.module.ProjectExtPoint;
import org.csstudio.startup.module.ServicesStartupExtPoint;
import org.csstudio.startup.module.ShutDownExtPoint;
import org.csstudio.startup.module.StartupParametersExtPoint;
import org.csstudio.startup.module.WorkbenchExtPoint;
import org.csstudio.startup.module.WorkspaceExtPoint;
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
public class Application implements IApplication {

	/** The list of all parameters read at start-up and any other parameters
	 * which were created later on during the execution of this application */
	protected Map<String, Object> parameters;

	/** The map of all loaded extension points. When an extension point is requested
	 * this map is first searched for the particular type of extension point. If found
	 * it is returned otherwise the extension points are loaded using the eclipse
	 * loaded (@see {@link #getExtensionPoints(Class, String)}).*/
	protected HashMap<Class<? extends CSSStartupExtensionPoint>,
	                  CSSStartupExtensionPoint[]> configurationElements =
	                      new HashMap<Class<? extends CSSStartupExtensionPoint>, CSSStartupExtensionPoint[]>(8);

    /** {@inheritDoc} */
    public Object start(final IApplicationContext context) throws Exception
	{
		// Display configuration info
		final String version = (String) context.getBrandingBundle().getHeaders().get("Bundle-Version");
		final String app_info = context.getBrandingName() + " " + version;

		// Create parser for arguments and run it.
		final String args[] = (String[]) context.getArguments().get("application.args");

		int pos = 0;
		while (pos < args.length) {
			if (args[pos].equals("-help")) {
				System.out
						.println(app_info + "\n\nOptions:\n"
								+ "  -help                         : Display help\n"
								+ "  -version                      : Display version info\n"
								+ "  -data /home/fred/Workspace    : Eclipse workspace location\n"
								+ "  -pluginCustomization /path/to/my/settings.ini: Eclipse plugin defaults\n"
								+ "  -share_link /absolute/system/path=relative/workspace/path[,another_link]*: Set shared links\n");
				return IApplication.EXIT_OK;
			}
			if (args[pos].equals("-version")) {
				System.out.println(app_info);
				return IApplication.EXIT_OK;
			}
			pos++;
		}
    	
    	// Create the display
	    final Display display = PlatformUI.createDisplay();

		if (display == null) {
			System.err.println("No display"); //$NON-NLS-1$
			return EXIT_OK;
		}

        final OpenDocumentEventProcessor openDocProcessor
        	= new OpenDocumentEventProcessor(display);

		handleDisplayDebugging(display);

		try
		{
			return startApplication(context, display, openDocProcessor);
		}
		catch (Throwable ex)
		{
			Logger.getLogger(Plugin.ID).log(Level.SEVERE, "Application Main Loop Error", ex); //$NON-NLS-1$
			if (ex instanceof Exception)
				throw (Exception) ex;
		}
		return EXIT_OK;
	}

	/** Show and tweak SWT debug options.
     *  See eclipse docs and org.csstudio.sns.product/debug_options.txt
     *  for fundamental SWT debugging.
     *  This code then tweaks some X11/GTK settings.
     *  @param display SWT Display
     */
    @SuppressWarnings({ "nls" })
    private void handleDisplayDebugging(final Display display)
    {
        if (display.getDeviceData().debug)
        {
            System.out.println("Enabled SWT debugging");
            // Workbench.createDisplay turned this off. Re-enable!
            display.setWarnings(true);
            System.out.println("Re-enabled display warnings");

            // For GTK/X11, Eclipse sets XSynchronize(true) when debugging,
            // which gives better error location detail but is also
            // about 30 times slower.
            if ("true".equalsIgnoreCase(Platform.getDebugOption(Plugin.ID + "/debug/async_x")))
                enableXASynchronous();
        }
        if (display.getDeviceData().tracking)
            System.out.println("Enabled SWT resource tracking");
    }

    /** Disable XSynchronize which Eclipse enabled by default for debugging.
     *  Code works only on SWT/GTK, and uses reflection to avoid compile
     *  errors on other OS.
     */
    @SuppressWarnings({ "rawtypes", "nls", "unchecked" })
    private void enableXASynchronous()
    {
        try
        {
            final Class os = Class.forName("org.eclipse.swt.internal.gtk.OS");

            // int xdisplay = OS.GDK_DISPLAY ();
            final Method gdk_display = os.getDeclaredMethod("GDK_DISPLAY");
            final Integer xdisplay = (Integer) gdk_display.invoke(os);

            // OS.XSynchronize(xdisplay, false);
            final Method xsynchronize = os.getDeclaredMethod("XSynchronize",
                            new Class[] { Integer.TYPE, Boolean.TYPE });
            xsynchronize.invoke(os, new Object[] { xdisplay, Boolean.FALSE });

            System.out.println("XSynchronize(false), back to async. X11");
        }
        catch (ClassNotFoundException ex)
        {   // Not on Linux/GTK, so just ignore
            return;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
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
     * @param openDocProcessor
	 * @return the exit code of any of the first executed segment that provided one.
	 * 			If no other exit code is given the code returned by
	 * 			{@link #startWorkbench(Display, IApplicationContext)} is returned by
	 * 			this method.
	 *
	 * @throws Exception if anything went wrong during the execution of any of the segments
	 */
	protected Object startApplication(final IApplicationContext context,
	        final Display display, OpenDocumentEventProcessor openDocProcessor) throws Exception
    {
		parameters = readStartupParameters(display,context);
		parameters.put(OpenDocumentEventProcessor.OPEN_DOC_PROCESSOR, openDocProcessor);
		Object exitCode = parameters.get(StartupParametersExtPoint.EXIT_CODE);
        if (exitCode != null)
            return exitCode;

		exitCode = applyLocaleSetting(context);
        if (exitCode != null)
            return exitCode;

		exitCode = promptForLogin(display, context);
        if (exitCode != null)
            return exitCode;

		exitCode = promptForWorkspace(display, context);
        if (exitCode != null)
            return exitCode;

		exitCode = startServices(display,context);
        if (exitCode != null)
            return exitCode;

		exitCode = openProjects(display,context);
        if (exitCode != null)
            return exitCode;

		final WorkbenchExtPoint wb = getWorkbenchExtPoint();
		exitCode = wb.beforeWorkbenchCreation(display, context, parameters);
		if (exitCode != null)
			return exitCode;

		//if everything is ok and working and no code arrives later,
		//this is the code to return
		final Object workbenchCode =
		    wb.runWorkbench(display, context, parameters);

		exitCode = wb.afterWorkbenchCreation(display, context, parameters);
		if (exitCode != null)
			return exitCode;

		exitCode = closeProjects(display,context);
        if (exitCode != null)
            return exitCode;

        return workbenchCode;
	}

	/** {@inheritDoc} */
	public void stop()
	{
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
		for (LocaleSettingsExtPoint p : points)
		{
			final Object error = p.applyLocaleSettings(context, parameters);
			if (error != null)
			    return error;
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
		for (StartupParametersExtPoint p : points)
		{
			parameters.putAll(p.readStartupParameters(display, context));
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
		for (LoginExtPoint p : points)
		{
		    final Object error = p.login(display, context, parameters);
			if (error != null)
			    return error;
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
	@SuppressWarnings("nls")
    protected Object promptForWorkspace(Display display,
	        IApplicationContext context) throws Exception
    {
        WorkspaceExtPoint[] points = getExtensionPoints(WorkspaceExtPoint.class, WorkspaceExtPoint.NAME);
		if (points.length > 1)
		{
		    System.err.println("At most one " + WorkspaceExtPoint.NAME +
		            " extension point, allowed, but found " + points.length);
			return IApplication.EXIT_OK;
		}
		if (points.length == 0)
			return null;
		else
			return points[0].promptForWorkspace(display, context, parameters);
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
	protected Object startServices(Display display, IApplicationContext context)
	    throws Exception
    {
		ServicesStartupExtPoint[] points = getExtensionPoints(ServicesStartupExtPoint.class, ServicesStartupExtPoint.NAME);
		for (ServicesStartupExtPoint p : points)
		{
		    final Object o = p.startServices(display, context, parameters);
			if (o != null)
			    return o;
		}
		return null;
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
	protected Object openProjects(Display display, IApplicationContext context) throws Exception
	{
		ProjectExtPoint[] points = getExtensionPoints(ProjectExtPoint.class, ProjectExtPoint.NAME);
		for (ProjectExtPoint p : points)
		{
		    final Object o = p.openProjects(display, context, parameters);
			if (o != null)
			    return o;
		}
		return null;
	}

	/** @return The one and only WorkbenchExtPoint
	 *  @throws Exception when not finding exactly one
	 */
	@SuppressWarnings("nls")
    private WorkbenchExtPoint getWorkbenchExtPoint() throws Exception
	{
	    final WorkbenchExtPoint[] points =
	        getExtensionPoints(WorkbenchExtPoint.class, WorkbenchExtPoint.NAME);
	    if (points.length != 1)
	        throw new Exception("Need exactly one " +
	                    WorkbenchExtPoint.NAME + " extension point, found " +
	                    points.length);
	    return points[0];
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
		for (ProjectExtPoint p : points)
		{
		    final Object o = p.closeProjects(display, context, parameters);
			if (o != null)
			    return o;
		}
		return null;
	}

	/**
	 * Loads all {@link ShutDownExtPoint}s and executes the
	 * {@link ShutDownExtPoint#beforeShutDown()} method.
	 *
	 */
	protected void stopApplication()
	{
		ShutDownExtPoint[] points = getExtensionPoints(ShutDownExtPoint.class, ShutDownExtPoint.NAME);
		for (ShutDownExtPoint p : points)
		{
			try
			{
				p.beforeShutDown(parameters);
			}
			catch (Exception e)
			{
			    // Don't quit, since we're shutting down anyway
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
	 * @param name the name of the extension point element ("locale", "project", "startupParameters", ...)
	 * @return the array of extension points for that element that have the correct type
	 */
	@SuppressWarnings({ "unchecked", "nls" })
	protected <T extends CSSStartupExtensionPoint> T[] getExtensionPoints(
            final Class<T> type, final String name)
    {
		// Check cache
		final CSSStartupExtensionPoint[] points = configurationElements.get(type);
		if (points != null)
            return (T[]) points;
		
		// Query registry
        final IConfigurationElement[] config = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(CSSStartupExtensionPoint.NAME);
        final List<T> list = new ArrayList<T>();
        for (IConfigurationElement e : config)
        {
        	// Only use extensions for the requested element
        	if (! name.equals(e.getName()))
        		continue;
            try
            {
            	final Object o = e.createExecutableExtension("class");
            	// Check for correct type
            	// (somewhat redundant because schema description
            	//  already requires types for the various elements
            	//  of the extension point)
                if (type.isAssignableFrom(o.getClass()))
                {
                    list.add((T) o);
                }
            }
            catch (Exception ex)
            {
                errorExecutingExtensionPoint("Error loading " + name
                        + " extension points.", ex);
            }
        }
        final T[] array = (T[]) Array.newInstance(type, list.size());
        list.toArray(array);
        // Add to cache
        configurationElements.put(type, array);
        return array;
    }

	/**
	 * This is a utility method which logs the error that happens during the
	 * execution of one of the extension points.
	 *
	 * @param name the name of the extension point
	 * @param t the exception that occurred during execution (could be null)
	 */
	@SuppressWarnings("nls")
    protected void errorExecutingExtensionPoint(String name, Throwable t)
	{
	    System.err.println("Error executing " + name + " extension point.");
	    if (t != null)
	        t.printStackTrace(System.err);
	}


}