/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pace.gui;

import org.csstudio.display.pace.Messages;
import org.csstudio.display.pace.model.Cell;
import org.csstudio.display.pace.model.Instance;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

/** Label provider for one column of the the table.
 *  <p>
 *  Gets called by the TableViewer with a rows (Instances) of the Model
 *  and a cell of the table; has to populate that cell with the appropriate
 *  info from the Instance.
 *  
 *  @author Delphy Nypaver Armstrong
 *  @author Kay Kasemir
 *  
 *    reviewed by Delphy 01/29/09
 */
public class InstanceLabelProvider extends CellLabelProvider
{
     /** Column of the model that this label provider handles */
     final private int column;

     /** Construct label provider for Model elements
      *  <p>
      *  There's a shift in column indices between GUI and Model:
      *  The left-most GUI column uses -1 in this argument to display the
      *  name of the Instance (row).
      *  Remaining GUI columns use column &ge;0 to display
      *  Model cells at the given column index.
      *  <p>
      *  Meaning:
      *  <ul>
      *  <li>GUI Column 0 = Model's Instance name for each row
      *  <li>GUI Column 1 = Model's Column 0 
      *  <li>GUI Column 2 = Model's Column 1
      *  <li>... 
      *  </ul>
      *  @param column Column of the model that this label provider handles.
      */
     InstanceLabelProvider(final int column)
     {
         this.column = column;
     }

     /** @return Tool tip string for given element (row of table, Instance of Model */
     @Override
     public String getToolTipText(final Object element)
     {
         // ModelInstanceProvider should always provided "Instance" elements
         final Instance instance = (Instance) element;
         // Special name column?
         if (column < 0)
             return instance.getName();
         // Cell column
         final Cell cell = instance.getCell(column);
         // Creating basic PV name, value tooltip
         String tip = NLS.bind(Messages.InstanceLabelProvider_PVValueFormat,
                               cell.getName(), cell.getValue());

         // Extend if 'edited'
         if (cell.isEdited())
             tip = tip + Messages.InstanceLabelProvider_OrigAppendix + cell.getCurrentValue();
         // Extend if 'read-only'
         if (cell.isReadOnly() || !cell.isPVWriteAllowed())
             tip = tip + Messages.InstanceLabelProvider_ReadOnlyAppendix;
         // If the cell has meta information, add the person who made the change,
         // the date of the change and the comment to the tool-tip.
         if (cell.hasMetaInformation())
         {
            final String meta = NLS.bind(Messages.InstanceLabelProvider_PVCommentTipFormat,
                  new Object[]
                  {
                        cell.getLastUser(),
                        cell.getLastDate(),
                        cell.getLastComment()
                  });
            tip = tip + meta;
         }
         return tip;
     }

     /** @param gui_cell Cell in GUI that we need to update with info
      *                  from the corresponding cell in the Model
      */
     @Override
     public void update(final ViewerCell gui_cell)
     {
         // ModelInstanceProvider should always provided "Instance" elements
         final Instance instance = (Instance) gui_cell.getElement();

         // Special name column? See comments in constructor
         if (column < 0)
         {
             gui_cell.setText(instance.getName());
             return;
         }
         // Cell column
         final Cell cell = instance.getCell(column);
         gui_cell.setText(cell.getValue());

         // Highlight edited cells with yellow background
         if (cell.isEdited())
         {
             final Display display = Display.getCurrent();
             gui_cell.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
         }
         else
             gui_cell.setBackground(null);
         // Highlight read-only cells with "inactive" background, usually
         // a light grey.
         // This is for cells configured to be read-only.
         // Those meant to be writable, but PV doesn't allow it,
         // for example because of Channel Access security,
         // say so in the tool tip.
         if (cell.isReadOnly())
         {
             final Display display = Display.getCurrent();
             gui_cell.setBackground(display.getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
         }
     }
}
