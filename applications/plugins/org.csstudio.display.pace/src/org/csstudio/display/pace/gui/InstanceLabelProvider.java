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
 */
public class InstanceLabelProvider extends CellLabelProvider
{
     /** Column of the model that this label provider handles */
     final private int column;

     /** Construct label provider for Model elements
      *  @param column Column of the model that this label provider handles.
      *                -1 for the instance name, &ge;0 for cells
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
         String tip = NLS.bind(Messages.InstanceLabelProvider_PVValueFormat,
                               cell.getPV(), cell.getValue());
         if (cell.isEdited())
             tip = tip + Messages.InstanceLabelProvider_OrigAppendix + cell.getUserValue();
         if (cell.isReadOnly())
             tip = tip + Messages.InstanceLabelProvider_ReadOnlyAppendix;
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

         // Special name column?
         if (column < 0)
         {
             gui_cell.setText(instance.getName());
             return;
         }
         // Cell column
         final Cell cell = instance.getCell(column);
         gui_cell.setText(cell.getValue());

         // Highlight edited cells
         if (cell.isEdited())
         {
             final Display display = Display.getCurrent();
             gui_cell.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
         }
         else
             gui_cell.setBackground(null);
         // Highlight read-only cells
         if (cell.isReadOnly())
         {
             final Display display = Display.getCurrent();
             gui_cell.setBackground(display.getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
         }
     }
}
