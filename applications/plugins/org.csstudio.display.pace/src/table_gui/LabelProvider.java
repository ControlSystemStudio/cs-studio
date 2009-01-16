package table_gui;


import org.csstudio.display.pace.model.old.Cell;
import org.csstudio.display.pace.model.old.Model;
import org.csstudio.display.pace.model.old.Rows;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;


/** Label provider for one column of the the table.
 *  <p>
 *  Gets called by the TableViewer with a rows (Instances) of the Model
 *  and a cell of the table; has to populate that cell with the appropriate
 *  info from the Instance.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LabelProvider extends CellLabelProvider
{
    /** Column of the model that this label provider handles */
    final private int column;
    final private Model model;

    /** Construct label provider for Model elements
     *  @param column Column of the model that this label provider handles
     */
    public LabelProvider(final int column, final Model model)
    {
        this.column = column;
        this.model = model;
    }

    /** @return Tool tip string for given element (row of table, Instance of Model */
    @Override
    public String getToolTipText(final Object element)
    {
        // ModelProvider should always provide "Row" elements
        final Rows rows = (Rows) element;
        final Cell cell = rows.getCell(column);
        String tip = cell.getPvName() + " (" + cell.getAccess() + ")";
        if (cell.hasUserValue())
           tip = tip + ", orig: " + cell.getCurrentValue();
        return tip;
    }

    /** Update one cell of the table */
    @Override
    public void update(final ViewerCell gui_cell)
    {
       
        // ModelInstanceProvider should always provide "Instance" elements
        final Rows rows = (Rows) gui_cell.getElement();
        
        // The cell should be in the expected column
        if (gui_cell.getColumnIndex() != column)
            throw new Error("Expected column " + column
                    + ", got " + gui_cell.getColumnIndex());
                
        // This is the code updating the Table
        final Cell model_cell = rows.getCell(column);
        final String value = model_cell.hasUserValue()
           ? model_cell.getUserValue() : model_cell.getCurrentValue();
        //   System.out.println("value " + value);
        gui_cell.setText(value);
        
        // Highlight edited cells
        if (model_cell.hasUserValue())
        {
           final Display display = Display.getCurrent();
           gui_cell.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
        }
        
        // Highlight read-only cells
        if (model_cell.getAccess()==Cell.Access.ReadOnly)
        {
           final Display display = Display.getCurrent();
           if(gui_cell.getBackground()!=display.getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND))
              gui_cell.setBackground(display.getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
        }
    }
}
