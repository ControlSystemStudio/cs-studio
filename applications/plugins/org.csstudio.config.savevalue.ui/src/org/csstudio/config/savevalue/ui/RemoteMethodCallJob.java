/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.config.savevalue.ui;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Job which calls a remote method via RMI.
 * 
 * @author Joerg Rathlev
 */
public abstract class RemoteMethodCallJob extends Job {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteMethodCallJob.class);
    
	/**
	 * Creates a new job with the specified name.
	 * 
	 * @param name
	 *            the name of the job. Must not be <code>null</code>.
	 */
	public RemoteMethodCallJob(String name) {
		super(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final IStatus run(IProgressMonitor monitor) {
		IStatus result;
		monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);
		try {
			Registry reg = locateRmiRegistry();
			result = runWithRmiRegistry(reg);
		} catch (final RemoteException e) {
			LOG.error("Could not create reference to RMI registry", e); //$NON-NLS-1$
			final String message =
				Messages.SaveValueDialog_ERRMSG_NO_RMI_REGISTRY +
				e.getMessage();
			showErrorDialog(message);
			result = new Status(Status.ERROR, Activator.PLUGIN_ID, message, e);
		}
		monitor.done();
		return result;
	}

	/**
	 * Runs this job with the given RMI registry.
	 * 
	 * @param reg
	 *            the RMI registry.
	 */
	protected abstract IStatus runWithRmiRegistry(Registry reg);

	/**
	 * Displays an error dialog.
	 * 
	 * @param message
	 *            the error message.
	 */
	protected void showErrorDialog(final String message) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
            public void run() {
				MessageDialog.openError(null,
						Messages.SaveValueDialog_DIALOG_TITLE, message);
			}
		});
	}

	/**
	 * Returns the RMI registry from the host specified in the preferences.
	 * 
	 * @return the RMI registry.
	 * @throws RemoteException
	 *             if the reference to the remote registry could not be created.
	 */
	private Registry locateRmiRegistry() throws RemoteException {
		IPreferencesService prefs = Platform.getPreferencesService();
		String registryHost = prefs.getString(
				Activator.PLUGIN_ID,
				PreferenceConstants.RMI_REGISTRY_SERVER,
				null, null);
		LOG.debug("Connecting to RMI registry on host: {}", registryHost); //$NON-NLS-1$
		return LocateRegistry.getRegistry(registryHost);
	}

}
