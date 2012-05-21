/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.clientmodel;

/** Listener to the AlarmClientModel configuration changes
 *  <p>
 *  Compared to the {@link AlarmClientModelListener} that receives
 *  every PV state change, this listener is only notified when a
 *  new alarm model was loaded.
 *  
 *  @author Kay Kasemir
 */
public interface AlarmClientModelConfigListener
{
    /** Notification which indicates that the model
     *  loaded a new configuration, i.e. has new name and content.
     *  <p>
     *  May originate from non-UI thread
     *  
     *  @param model Model
     */
    void newAlarmConfiguration(AlarmClientModel model);
}
