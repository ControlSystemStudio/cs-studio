/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

/** Listener for the {@link GlobalAlarmUpdate}
 *  @author Kay Kasemir
 */
public interface GlobalAlarmListener
{
    /** Invoked when the GlobalAlarmUpdate expires */
    public void updateGlobalState();

}
