/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.rack.model;

/** Listener interface to RackDataAPI */
public interface RackModelListener
{
	/** value to indicate which data set changed  */
	public enum ChangeEvent
	{
		/** The list of racks has changed */
		RACKLIST,
		/** The contents of the rack have changed based on a specific rack name */
		DVCLIST,
		/** The contents of the rack have changed based on a child supported by the rack equipment*/
		PARENT
		
	}
    /** Something in the IOC model changed: New entries, deleted entries, renamed entries, ... */
    public void rackUtilChanged(ChangeEvent what);
}
