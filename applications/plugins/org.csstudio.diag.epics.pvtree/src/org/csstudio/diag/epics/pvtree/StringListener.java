/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import java.util.logging.Level;

import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVReaderListener;
import org.epics.vtype.VType;

/** {@link PVReaderListener} that extracts text from value.
 * 
 *  <p>Derived class determines how to handle the text.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
abstract public class StringListener implements PVReaderListener<VType>
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
    public void pvChanged(final PVReaderEvent<VType> event)
    {
    	final PVReader<VType> pv = event.getPvReader();
    	final Exception error = pv.lastException();
    	if (error != null)
    	{
            Plugin.getLogger().log(Level.WARNING,
            		"PV Listener error for '" + pv.getName() + "': " + error.getMessage(),
            		error);
            handleError(error);
            return;
    	}
        try
        {
            final VType value = pv.getValue();
            // Ignore possible initial null
            if (value == null)
            	return;
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
        