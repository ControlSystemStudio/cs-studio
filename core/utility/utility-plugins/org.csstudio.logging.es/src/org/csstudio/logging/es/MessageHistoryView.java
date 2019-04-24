/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging.es;

import org.csstudio.logging.es.archivedjmslog.PropertyFilter;
import org.csstudio.logging.es.gui.GUI;
import org.csstudio.logging.es.model.LogArchiveModel;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * Eclipse View for the Message History
 * 
 * @author Kay Kasemir
 * @author Xihui Chen
 */
public class MessageHistoryView extends ViewPart
{
    /** ID under which this view is registered in plugin.xml */
    public static final String ID = "org.csstudio.logging.es.MessageHistoryView"; //$NON-NLS-1$

    private LogArchiveModel model;

    @Override
    public void createPartControl(final Composite parent)
    {
        try
        {
            this.model = Helpers.createModel(parent.getShell());
            final GUI gui = new GUI(getSite(), parent, this.model);
            this.model.updateFromArchive();

            getSite().setSelectionProvider(gui.getSelectionProvider());
        }
        catch (Throwable ex)
        {
            MessageDialog.openError(parent.getShell(), Messages.Error,
                    ex.getMessage());
        }
    }

    public void setFilters(PropertyFilter[] filters) throws Exception
    {
        this.model.setFilters(filters);
    }

    @Override
    public void setFocus()
    {
        // NOP
    }

    public void setTimeRange(String from, String to) throws Exception
    {
        this.model.setTimerange(from, to);
    }
}
