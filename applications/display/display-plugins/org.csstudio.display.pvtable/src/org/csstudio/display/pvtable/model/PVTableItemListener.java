/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.model;

/**
 * Listener to {@link PVTableItem}
 *
 * @author Kay Kasemir
 */
public interface PVTableItemListener {
    /**
     * @param item
     *            Item that was selected or de-selected
     */
    void tableItemSelectionChanged(PVTableItem item);

    /**
     * @param item
     *            Item that has new value
     */
    void tableItemChanged(PVTableItem item);
}
