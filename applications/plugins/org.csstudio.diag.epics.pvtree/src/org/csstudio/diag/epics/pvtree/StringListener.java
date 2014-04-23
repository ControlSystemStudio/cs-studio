/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import java.util.logging.Level;

import org.csstudio.vtype.pv.PV;
import org.csstudio.vtype.pv.PVListenerAdapter;
import org.epics.vtype.VType;

/** {@link IPVListener} that extracts text from value.
 * 
 *  <p>Derived class determines how to handle the text.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
abstract public class StringListener extends PVListenerAdapter
{
	/** @param error Error to handle */
    public void handleError(final String error)
	{
		handleText("Error: " + error);
	}

	/** @param text Text to handle */
	abstract public void handleText(final String text);

    /** {@inheritDoc} */
    @Override
    public void valueChanged(final PV pv, final VType value)
    {
        try
        {
            handleText(VTypeHelper.format(value));
        }
        catch (Exception e)
        {
            Plugin.getLogger().log(Level.SEVERE,
                    "PV Listener error for '" + pv.getName() + "': " + e.getMessage(),
                    e);
        }
    }
    
    @Override
    public void disconnected(final PV pv)
    {
        handleError(pv.getName() + " disconnected");
    }
	
};
        