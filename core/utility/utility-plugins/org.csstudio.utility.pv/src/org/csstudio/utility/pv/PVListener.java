/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv;

/** A listener for PV updates. 
 *  @author Kay Kasemir
 */
public interface PVListener
{
    /** Notification of a new value.
     *  <p>
     *  This event may be the immediate result of a
     *  control system library callback,
     *  i.e. it may arrive in a non-UI thread.
     *  
     *  @param pv The PV which has a new value
     */
    public void pvValueUpdate(PV pv);
    
    /** Notification of a PV disconnect.
     *  <p>
     *  This event may be the immediate result of a
     *  control system library callback,
     *  i.e. it may arrive in a non-UI thread.
     *  
     *  @param pv The disconnected PV
     */
    public void pvDisconnected(PV pv);
}
