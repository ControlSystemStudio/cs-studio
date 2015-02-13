/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.askap.product;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.askap.product.messages"; //$NON-NLS-1$

    public static String Project_SharedFolderName;
    public static String Project_ShareError;
    public static String Project_ShareErrorDetail;
	public static String StartupAuthenticationHelper_Login;
	public static String StartupAuthenticationHelper_LoginTip;
	public static String Window_Title;

    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    { /* prevent instantiation */ }
}
