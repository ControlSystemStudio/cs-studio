/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

/** Tags used to write/read XML alarm config
 *  @author Kay Kasemir, Xihui Chen
 */
@SuppressWarnings("nls")
public interface XMLTags
{
    final public static String NAME = "name";
    final public static String COMPONENT = "component";
    final public static String PV = "pv";
    final public static String COMMAND = "command";
    final public static String DISPLAY = "display";
    final public static String ENABLED = "enabled";
    final public static String GUIDANCE = "guidance";
    final public static String AUTOMATED_ACTION = "automated_action";
    final public static String TITLE = "title";
    final public static String DETAILS = "details";
    final public static String DESCRIPTION = "description";
    final public static String LATCHING = "latching";
    final public static String ANNUNCIATING = "annunciating";
    final public static String DELAY = "delay";
    final public static String COUNT = "count";
    final public static String FILTER = "filter";
}
