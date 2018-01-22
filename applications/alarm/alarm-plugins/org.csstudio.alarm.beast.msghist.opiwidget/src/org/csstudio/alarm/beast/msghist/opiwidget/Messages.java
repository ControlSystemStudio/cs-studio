/*******************************************************************************
 * Copyright (c) 2010-2017 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.opiwidget;

import org.eclipse.osgi.util.NLS;

/**
 * <code>Messages</code> takes care of string externalisation for the message history OPI widget plugin.
 *
 * @author Borut Terpinc
 *
 */
public final class Messages extends NLS {

    private static final String BUNDLE_NAME = "org.csstudio.alarm.beast.msghist.opiwidget.messages"; //$NON-NLS-1$

    public static String TimeFormat;
    public static String Columns;
    public static String Filter;
    public static String ModelCreationError;
    public static String ModelUpdateError;
    public static String EditColumns;
    public static String PreferenceReadError;
    public static String MaxMessages;
    public static String SortingColumn;
    public static String SortAscending;
    public static String ColumnHeaders;

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
