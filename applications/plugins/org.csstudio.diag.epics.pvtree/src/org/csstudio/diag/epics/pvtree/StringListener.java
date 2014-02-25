/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import java.util.logging.Level;

import org.csstudio.simplepv.IPV;
import org.csstudio.simplepv.IPVListener;
import org.epics.vtype.VType;

/** {@link IPVListener} that extracts text from value.
 * 
 *  <p>Derived class determines how to handle the text.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
abstract public class StringListener extends IPVListener.Stub
{
	/** @param error Error to handle */
    public void handleError(final Exception error)
	{
		handleText("Error: " + error.getMessage());
	}

	/** @param text Text to handle */
	abstract public void handleText(final String text);

    /** {@inheritDoc} */
    @Override
    public void exceptionOccurred(IPV pv, Exception exception)
    {
        Plugin.getLogger().log(Level.WARNING,
                "PV Listener error for '" + pv.getName() + "': " + exception.getMessage(),
                exception);
        handleError(exception);
    }
	
	/** {@inheritDoc} */
	@Override
	public void valueChanged(final IPV pv)
    {
        try
        {
            final VType value = pv.getValue();
            // Ignore possible initial null
            if (value != null)
	            handleText(VTypeHelper.format(value));
        }
        catch (Exception e)
        {
            Plugin.getLogger().log(Level.SEVERE,
            		"PV Listener error for '" + pv.getName() + "': " + e.getMessage(),
            		e);
        }
    }
};
        