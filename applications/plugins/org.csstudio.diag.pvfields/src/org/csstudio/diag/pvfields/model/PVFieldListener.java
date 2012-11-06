/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields.model;

import org.csstudio.diag.pvfields.PVField;

/** Listener to {@link PVField} updates
 *  @author Kay Kasemir
 */
public interface PVFieldListener
{
	/** @param field {@link PVField} that has new value */
    public void updateField(PVField field);
}
