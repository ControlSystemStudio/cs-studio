/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.swt;

import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/** Get screenshot as SWT image
 *  @author Kay Kasemir
 */
public class Screenshot
{
	/** @return Image with screenshot of complete display */
	public static Image getFullScreenshot()
	{
		final Display display = Display.getCurrent();
		return getScreenshot(display, display, display.getBounds());
	}

	/** @return Image with screenshot of application window */
	public static Image getApplicationScreenshot()
	{
		final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		return getScreenshot(shell.getDisplay(), shell, shell.getBounds());
	}
	
	/** @param display Display
	 *  @param drawable Drawable
	 *  @param bounds bounds of that drawable
	 *  @return Image with screenshot of the drawable
	 */
	public static Image getScreenshot(
			final Display display,
			final Drawable drawable,
			final Rectangle bounds)
    {
        final GC gc = new GC(drawable);
		final Image image = new Image(display, bounds);
        gc.copyArea(image, 0, 0);
        gc.dispose();

        return image;
    }
}
