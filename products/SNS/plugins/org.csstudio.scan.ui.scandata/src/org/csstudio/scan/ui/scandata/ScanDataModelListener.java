/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scandata;

import org.csstudio.scan.data.ScanData;

/** Listener for the {@link ScanDataModel}
 *  @author Kay Kasemir
 */
public interface ScanDataModelListener
{
	/** Invoked when scan has new data
	 *  @param data Current data of the scan
	 */
	void updateScanData(ScanData data);

}
