/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.core.preferences;


import org.eclipse.osgi.util.NLS;


/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 3 Nov 2016
 */
class Messages extends NLS {

    private static final String BUNDLE_NAME = "org.csstudio.diirt.util.core.preferences.messages"; //$NON-NLS-1$

    public static String DIIRTPreferences_verifyDIIRTPath_blankPath_message;
    public static String DIIRTPreferences_verifyDIIRTPath_nullPath_message;
    public static String DIIRTPreferences_verifyDIIRTPath_pathNotExists_message;
    public static String DIIRTPreferences_verifyDIIRTPath_pathNotValid_message;

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages ( ) {
    }

}
