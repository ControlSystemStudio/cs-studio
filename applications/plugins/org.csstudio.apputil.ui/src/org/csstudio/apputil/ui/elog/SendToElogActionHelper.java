/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.elog;

import org.csstudio.apputil.ui.Activator;
import org.csstudio.logbook.LogbookFactory;
import org.eclipse.jface.action.Action;

/** Helper for creating Action to send image of plot to logbook.
 *  <p>
 *  This action provides a common icon and text.
 *  It can also help to test if elog support is available at all.
 *  <p>
 *  Derived classes should override <code>run()</code> to display
 *  the <code>ExportToElogDialog</code> and perform the actual
 *  elog entry.
 *  
 *  @author Kay Kasemir
 */
public class SendToElogActionHelper extends Action
{
    /** @return <code>true</code> if elog support is available */
    public static boolean isElogAvailable()
    {
        try
        {
            if (LogbookFactory.getInstance() != null)
                return true;
        }
        catch (Exception ex)
        {
            // Ignore
        }
        return false;
    }

    /** Constructor */
    public SendToElogActionHelper()
    {
        super(Messages.ELog_ActionName,
              Activator.getImageDescriptor("icons/logentry-add-16.png")); //$NON-NLS-1$
        setToolTipText(Messages.ELog_ActionName_TT);
    }
}
