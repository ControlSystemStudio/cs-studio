/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.server;

import java.util.List;

import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.log.DataLog;
import org.csstudio.scan.server.internal.ExecutableScan;
import org.epics.pvdata.pv.PVStructure;

/** Context in which the {@link ScanCommandImpl}s of a {@link ExecutableScan} are executed.
 *
 *  <ul>
 *  <li>{@link Device}s with which commands can interact.
 *  <li>Methods to execute commands, supporting Pause/Continue/Abort
 *  <li>Data logger for {@link ScanSample}s
 *  <li>Progress information
 *  </ul>
 *
 *  <p>Scan commands can only interact with this restricted API
 *  of the actual {@link ExecutableScan}.
 *
 *  @author Kay Kasemir
 */
public interface ScanContextListener
{
    
	/** Inform scan context that log has been performed.
	 *  Meant to be called by {@link ScanCommandImpl}s
	 *  @param datalog log data
	 */
    public void logPerformed(DataLog datalog);
}
