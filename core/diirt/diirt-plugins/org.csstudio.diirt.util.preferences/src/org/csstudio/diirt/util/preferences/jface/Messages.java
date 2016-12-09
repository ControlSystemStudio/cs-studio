/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.preferences.jface;

import org.eclipse.osgi.util.NLS;

/**
 * @author claudiorosati, European Spallation Source ERIC
 * @version 1.0.0 8 Dec 2016
 */
public class Messages extends NLS {

    private static final String BUNDLE_NAME = "org.csstudio.diirt.util.preferences.ui.messages"; //$NON-NLS-1$

    public static String DoubleFieldEditor_errorMessage;
    public static String DoubleFieldEditor_errorMessageRange;

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages ( ) {
    }

}
