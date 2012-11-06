/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields.model;

import java.util.List;
import java.util.Map;

import org.csstudio.diag.pvfields.PVField;

/** Listener to {@link PVModel}
 *  @author Kay Kasemir
 */
public interface PVModelListener extends PVFieldListener
{
	/** @param properties New properties */
    public void updateProperties(Map<String, String> properties);

	/** @param fields New fields */
    public void updateFields(List<PVField> fields);
}
