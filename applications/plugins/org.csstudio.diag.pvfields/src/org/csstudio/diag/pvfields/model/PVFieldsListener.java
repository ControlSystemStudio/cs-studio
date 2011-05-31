/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields.model;

/** Listener interface to PVFieldsModel */
public interface PVFieldsListener
{
	/** Something changed in the model
	 *  @param field Field that changed (new 'live' value)
	 *               or <code>null</code> if the whole model changed
	 */
	public void fieldChanged(PVInfo field);
}
