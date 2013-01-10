/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.util;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Utility methods to work with Display object
 * 
 * @author Xihui Chen
 */
public class DisplayUtils {
	
	/**
	 * Returns a non-null instance of Display object. Tries to find the Display
	 * object for the current thread first and if it fails tries to get:
	 * <li> Workbench display if the workbench running
	 * <li> Default display object
	 * 
	 * @return non-null Display object
	 * @since 1.2
	 */
	public static Display getDisplay() {
		Display display = Display.getCurrent();
		if (display == null && PlatformUI.isWorkbenchRunning()) {
			display = PlatformUI.getWorkbench().getDisplay();
		}
		return display != null ? display : Display.getDefault();
	}
	
	/**
	 * Attempts to return the default shell. If it cannot return the default
	 * shell, it returns the shell of the first workbench window that has shell.
	 * 
	 * @return The shell
	 * @since 1.2
	 */
	public static Shell getDefaultShell() {
		Shell shell = null;

		try {
			shell = Display.getDefault().getActiveShell();
		} catch (Exception e) {
			// ignore
		}

		try {
			if (shell == null) {
				IWorkbenchWindow activeWindow = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
				if (activeWindow != null)
					shell = activeWindow.getShell();
				
			}
		} catch (Exception e) {
			// ignore
		}

		if (shell == null) {
			IWorkbenchWindow[] windows = PlatformUI.getWorkbench()
				.getWorkbenchWindows();
			for (int i = 0; shell == null && i < windows.length; i++) {
				shell = windows[i].getShell();
			}
		}

		return shell;
	}
}