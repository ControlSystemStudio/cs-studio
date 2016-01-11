/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtable.opiwidget;

import org.eclipse.osgi.util.NLS;

/**
 * <code>Messages</code> takes care of string externalisation for the alarm table OPI widget plugin.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public final class Messages extends NLS {

    private static final String BUNDLE_NAME = "org.csstudio.alarm.beast.ui.alarmtable.opiwidget.messages"; //$NON-NLS-1$

    public static String ModelCreationError;
    public static String Writable;
    public static String SortAscending;
    public static String SortingColumn;
    public static String SeparateTables;
    public static String UnacknowledgedBlinking;
    public static String TimeFormat;
    public static String Columns;
    public static String FilterItem;
    public static String MaxNumberOfAlarms;
    public static String AckTableWeight;
    public static String UnAckTableWeight;
    public static String TableHeaderVisible;
    public static String ColumnsHeadersVisible;

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
