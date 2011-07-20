/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.swt;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.apputil.ui.swt.messages"; //$NON-NLS-1$
	public static String AddApplicationScreenshot;
	public static String AddApplicationScreenshotTT;
	public static String AddFullScreenshot;
	public static String AddFullScreenshotTT;
    public static String AddImage;
    public static String AddImageTT;
    public static String ImageTabFmt;
    public static String RemoveImage;
    public static String RemoveImageTT;
    public static String ImagePreview_ImageError;

    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    { /* prevent instantiation */ }
}
