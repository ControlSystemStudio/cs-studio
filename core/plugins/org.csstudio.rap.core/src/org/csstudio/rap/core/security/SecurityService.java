/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.rap.core.security;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IStatus;
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
								"css_rap", null, new CSSRAPCallbackHandler(display)); //$NON-NLS-1$

				try {
					secureContext.login();
					loggedIn.set(true);
				} catch (Exception exception) {
					Throwable cause = exception.getCause();
					if (cause != null
							&& cause.getCause() instanceof ThreadDeath) {
						throw (ThreadDeath) cause.getCause();
					}
					IStatus status = new Status(IStatus.ERROR,
							"org.csstudio.opibuilder.rap", "Login failed",
							exception);
					ErrorDialog
							.openError(null, "Error", "Login failed", status);
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
			return loggedIn.get();
		} catch (InterruptedException e) {
			return false;
		}
	}
}
