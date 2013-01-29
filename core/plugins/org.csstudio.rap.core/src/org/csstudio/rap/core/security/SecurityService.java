/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.rap.core.security;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.csstudio.rap.core.RAPCorePlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.security.auth.ILoginContext;
import org.eclipse.equinox.security.auth.LoginContextFactory;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;

/**
 * The service that provides security related feature to CSS RAP.
 * 
 * @author Xihui Chen
 * 
 */
public class SecurityService {

	private static final String LOGGEDIN_KEY = "org.csstudio.rap.core.loggedin"; //$NON-NLS-1$

	/**
	 * Authenticate user with the registered login module. This method must be
	 * called in UI thread.
	 * 
	 * @param display
	 *            display of the session, must not be null.
	 * @param retry
	 *            the allowed number of retries.
	 * @return true if login successfully.
	 */
	public static boolean authenticate(final Display display) {

		if (display == null)
			throw new NullPointerException("display is null");
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicBoolean loggedIn = new AtomicBoolean(false);
		
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				// Since we are using Configuration which is set on plugin
				// startup,
				// the name and URL doesn't matter.
				ILoginContext secureContext = LoginContextFactory
						.createContext(
								"css_rap", null, new LoginDialogCallbackHandler(display)); //$NON-NLS-1$

				try {
					secureContext.login();
					loggedIn.set(true);
				} catch (Exception exception) {
					Throwable cause = exception.getCause();
					if (cause != null
							&& cause.getCause() instanceof ThreadDeath) {
						throw (ThreadDeath) cause.getCause();
					}

					Throwable t = exception;
					if(cause !=null)
						t = cause;
					IStatus status = new Status(IStatus.ERROR,
							RAPCorePlugin.PLUGIN_ID, t.getMessage(), t);
					ErrorDialog
							.openError(null, "Login Failed", "Login failed.", status);
				} finally {
					latch.countDown();
				}

			}
		};
		// only execute it with async if this is not in UI thread.
		// Otherwise, it will block forever.
		if (Display.getCurrent() == null)
			display.asyncExec(runnable);
		else
			runnable.run();

		try {
			latch.await();
			if (loggedIn.get()) {
				display.setData(LOGGEDIN_KEY, true);
				return true;
			} else
				return false;
		} catch (InterruptedException e) {
			return false;
		}
	}

	/**Check if a session has logged in.
	 * @param display display of the session.
	 * @return true if the session has logged in.
	 */
	public static boolean isLoggedIn(Display display) {
		if(display != null && display.getData(LOGGEDIN_KEY)!=null)
			return true;
		return false;
	}
	
	/**
	 * Shows JFace ErrorDialog but improved by constructing full stack trace in
	 * detail area.
	 */
	public static void openErrorDialogWithStackTrace(String title, String msg, Throwable t) {

	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    t.printStackTrace(pw);

	    final String trace = sw.toString(); // stack trace as a string

	    // Temp holder of child statuses
	    List<Status> childStatuses = new ArrayList<>();

	    // Split output by OS-independend new-line
	    for (String line : trace.split(System.getProperty("line.separator"))) {
	        // build & add status
	        childStatuses.add(new Status(IStatus.ERROR, RAPCorePlugin.PLUGIN_ID, line));
	    }

	    MultiStatus ms = new MultiStatus(RAPCorePlugin.PLUGIN_ID, IStatus.ERROR,
	            childStatuses.toArray(new Status[] {}), // convert to array of statuses
	            t.getLocalizedMessage(), t);

	    ErrorDialog.openError(null, title, msg, ms);
	}
}
