package org.csstudio.frib.product.startupmodule;

import java.util.Map;
import java.util.logging.Logger;

import org.csstudio.frib.product.ApplicationWorkbenchAdvisor;
import org.csstudio.frib.product.Messages;
import org.csstudio.logging.LogConfigurator;
import org.csstudio.frib.startuphelper.StartupAuthenticationHelper;
import org.csstudio.platform.workspace.RelaunchConstants;
import org.csstudio.startup.application.OpenDocumentEventProcessor;
import org.csstudio.startup.module.LoginExtPoint;
import org.csstudio.startup.module.ProjectExtPoint;
import org.csstudio.startup.module.WorkbenchExtPoint;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * <code>WorkbenchExtPointImpl</code> runs the workbench using the
 * {@link ApplicationWorkbenchAdvisor}. This class also implements the option to
 * link shared folder to the project folder. In this case the implementation
 * expects {@value StartupParameters#SHARE_LINK_PARAM} and
 * {@value ProjectExtPoint#PROJECTS} parameters. During the
 * {@link #runWorkbench(Display, IApplicationContext, Map)} execution
 * {@value LoginExtPoint#USERNAME} and {@value LoginExtPoint#PASSWORD} are
 * expected.
 * 
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * 
 */
public class Workbench implements WorkbenchExtPoint {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.csstudio.startup.extensions.RunWorkbenchExtPoint#afterWorkbenchCreation
	 * (org.eclipse.swt.widgets.Display,
	 * org.eclipse.equinox.app.IApplicationContext, java.util.Map)
	 */
	public Object afterWorkbenchCreation(Display display,
			IApplicationContext context, Map<String, Object> parameters) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.csstudio.startup.extensions.RunWorkbenchExtPoint#beforeWorkbenchCreation
	 * (org.eclipse.swt.widgets.Display,
	 * org.eclipse.equinox.app.IApplicationContext, java.util.Map)
	 */
	public Object beforeWorkbenchCreation(Display display,
			IApplicationContext context, Map<String, Object> parameters) {
		Object share_link = parameters.get(StartupParameters.SHARE_LINK_PARAM);
		Object o = parameters.get(ProjectExtPoint.PROJECTS);
		if (share_link != null && o != null) {
			IProject[] projects = (IProject[]) o;
			if (projects.length > 0) {
				linkSharedFolder(projects[0], (String) share_link);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.csstudio.startup.extensions.RunWorkbenchExtPoint#runWorkbench(org
	 * .eclipse.swt.widgets.Display,
	 * org.eclipse.equinox.app.IApplicationContext, java.util.Map)
	 */
	public Object runWorkbench(Display display, IApplicationContext context,
			Map<String, Object> parameters) {
		// Configure Logging
		try {
			LogConfigurator.configureFromPreferences();
		} catch (Exception ex) {
			ex.printStackTrace();
			// Continue without customized log configuration
		}
		final Logger logger = Logger.getLogger(getClass().getName());

		Object o = parameters.get(LoginExtPoint.USERNAME);
		String username = o != null ? (String) o : null;
		o = parameters.get(LoginExtPoint.PASSWORD);
		String password = o != null ? (String) o : null;

		// authenticate user
		StartupAuthenticationHelper.authenticate(username, password);

		OpenDocumentEventProcessor openDocProcessor = (OpenDocumentEventProcessor) parameters
				.get(OpenDocumentEventProcessor.OPEN_DOC_PROCESSOR);

		// Run the workbench
		final int returnCode = PlatformUI.createAndRunWorkbench(display,
				new ApplicationWorkbenchAdvisor(openDocProcessor));

		// Plain exit from IWorkbench.close()
		if (returnCode != PlatformUI.RETURN_RESTART)
			return IApplication.EXIT_OK;

		// Something called IWorkbench.restart().
		// Is this supposed to be a RESTART or RELAUNCH?
		final Integer exit_code = Integer
				.getInteger(RelaunchConstants.PROP_EXIT_CODE);
		if (IApplication.EXIT_RELAUNCH.equals(exit_code)) { // RELAUCH with new
															// command line
			logger.fine("RELAUNCH, command line:\n" //$NON-NLS-1$
					+ System.getProperty(RelaunchConstants.PROP_EXIT_DATA));
			return IApplication.EXIT_RELAUNCH;
		}
		// RESTART without changes
		return IApplication.EXIT_RESTART;
	}

	/**
	 * Assert/update link to common folder.
	 * 
	 * @param project
	 *            Project
	 * @param share_link
	 *            Folder to which the 'Share' entry should link
	 */
	private void linkSharedFolder(final IProject project,
			final String share_link) {
		final IFolder common = project.getFolder(new Path(
				Messages.Project_SharedFolderName));
		// if (common.exists()) ...? No. Re-create in any case
		// to assert that it has the correct link
		try {
			common.createLink(new Path(share_link), IResource.REPLACE,
					new NullProgressMonitor());
		} catch (CoreException ex) {
			MessageDialog.openError(
					null,
					Messages.Project_ShareError,
					NLS.bind(Messages.Project_ShareErrorDetail, share_link,
							ex.getMessage()));
		}
	}
}
