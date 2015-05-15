/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.rdbtable.ui;

import org.csstudio.display.rdbtable.Activator;
import org.csstudio.display.rdbtable.Messages;
import org.csstudio.display.rdbtable.model.RDBTableModel;
import org.eclipse.jface.action.Action;
import org.eclipse.osgi.util.NLS;

/** Action for deleting a row in the RDBTable
 *  @author Kay Kasemir
 */
public class AddRowAction extends Action
{
    final private RDBTableModel model;

    /** Initialize
     *  @param model TableViewer for RDBTableRow entries
     */
    public AddRowAction(final RDBTableModel model)
    {
        super(Messages.AddRow, Activator.getImageDescriptor("html/add.gif")); //$NON-NLS-1$
        setToolTipText(Messages.AddRow_TT);
        this.model = model;
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        final String empty_row[] = new String[model.getColumnCount()];
        for (int c=0; c<model.getColumnCount(); ++c)
            empty_row[c] = NLS.bind(Messages.NewColumnDataFmt, model.getHeader(c));
        model.addRow(empty_row);
    }
}
