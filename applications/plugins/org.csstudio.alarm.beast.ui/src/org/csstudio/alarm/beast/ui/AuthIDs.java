/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui;

/** Authorization IDs for various actions
 *  @author Kay Kasemir, Xihui Shen
 */
@SuppressWarnings("nls")
public interface AuthIDs
{
    /** ID used to (un-)acknowledge alarms */
    public static final String ACKNOWLEDGE = "alarm_ack";

    /** ID used for changes to the alarm configuration */
    public static final String CONFIGURE = "alarm_config";
}
