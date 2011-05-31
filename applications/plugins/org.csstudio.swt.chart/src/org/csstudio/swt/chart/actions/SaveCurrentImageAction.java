/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart.actions;

import org.csstudio.swt.chart.Activator;
import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.Messages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;

/** An Action for saving the current image to a file.
 *  <p>
 *  Suggested use is in the context menu of an editor or view that
 *  uses the InteractiveChart.
 *  
 *  @author Kay Kasemir
 */
public class SaveCurrentImageAction extends Action
{
    private final Chart chart;

    /** Constructor */
    public SaveCurrentImageAction(Chart chart)
    {
        super(Messages.SaveImage_ActionName,
              Activator.getImageDescriptor("icons/snapshot.gif")); //$NON-NLS-1$
        this.chart = chart;
        setToolTipText(Messages.SaveImage_ActionName_TT);
    }
    
    /** {@inheritDoc} */
    @Override
    public void run()
    {
        final String filename = ImageFileName.get(chart.getShell());
        if (filename == null)
            return;
        try
        {
            chart.createSnapshotFile(filename);
        }
        catch (Exception ex)
        {
            MessageDialog.openError(chart.getShell(), Messages.SaveImage_ErrorTitle,
            NLS.bind(Messages.SaveImage_ErrorMessage,
            filename, ex.getMessage()));
        }
    }
}
