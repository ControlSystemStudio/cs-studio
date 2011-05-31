/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.rdbtable.model;

/** Listener to the RDBTableModel
 *  @author Kay Kasemir
 */
public interface RDBTableModelListener
{
    /** @param row Row that changed its values */
    public void rowChanged(RDBTableRow row);

    /** @param new_row Row that was added to the model */
    public void newRow(RDBTableRow new_row);
}
