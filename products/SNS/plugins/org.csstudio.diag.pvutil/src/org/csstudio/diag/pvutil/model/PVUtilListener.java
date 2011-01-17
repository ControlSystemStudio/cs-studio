/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvutil.model;

/** Listener interface to PVUtilModel */
public interface PVUtilListener
{
	/** value to indicate which data set changed  */
	public enum ChangeEvent
	{
		/** The FEC info changed */
		FEC_CHANGE,
		/** The FEC info changed */
		PV_CHANGE
	}
	
    /** Something in the model changed: New entries, deleted entries, renamed entries, ... */
    public void pvUtilChanged(ChangeEvent what);
}
