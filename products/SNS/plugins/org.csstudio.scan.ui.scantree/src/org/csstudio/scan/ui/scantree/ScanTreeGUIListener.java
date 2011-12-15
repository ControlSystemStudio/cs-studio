/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import java.util.List;

import org.csstudio.scan.command.ScanCommand;

/** Listener to the {@link ScanTreeGUI}
 *  @author Kay Kasemir
 */
public interface ScanTreeGUIListener
{
    /** Invoked when the scan tree has been changed
     *  (commands moved, removed, added)
     */
    public void scanTreeChanged();

    /** Invoked when user wants to submit the scan
     *  @param commands Scan commands
     */
    public void submitScan(List<ScanCommand> commands);
}
