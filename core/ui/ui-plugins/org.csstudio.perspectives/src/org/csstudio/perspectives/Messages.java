/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.perspectives;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "org.csstudio.perspectives.messages"; //$NON-NLS-1$

    public static String PerspectiveSaver_initFailed;
    public static String PerspectiveSaver_saveFailed;

    public static String PerspectiveLoader_selectFile;
    public static String PerspectiveLoader_loadFailed;
    public static String PerspectiveLoader_loadedPerspective;
    public static String PerspectiveLoader_fileNotUnderstood;

    public static String PerspectiveStartup_startupLoadFailed;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() { /* prevent instantiation */ }
}
