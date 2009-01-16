package org.csstudio.display.pace.gui;

import org.csstudio.apputil.ui.swt.AutoSizeColumn;
import org.csstudio.apputil.ui.swt.AutoSizeControlListener;
import org.csstudio.display.pace.Messages;
import org.csstudio.display.pace.model.Cell;
import org.csstudio.display.pace.model.Column;
import org.csstudio.display.pace.model.Model;
import org.csstudio.display.pace.model.ModelListener;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

/** GUI for the Model
 * 
 *  @author Delphy Nypaver Armstrong
 *  @author Kay Kasemir
 */
public class GUI implements ModelListener
{
    private static final int MIN_SIZE = 100;
    private TableViewer table_viewer;

    /** Initialize
     *  @param parent Parent widget
     *  @param model Model to display
     */
    public GUI(final Composite parent, final Model model)
    {
        createComponents(parent, model);
        model.addListener(this);
    }

    /** Create GUI elements
     *  @param parent Parent widget
     *  @param model Model to display
     */
    private void createComponents(final Composite parent, final Model model)
    {
        final GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        parent.setLayout(layout);
        
        // Create TableViewer that displays Model in Table
        table_viewer = new TableViewer(parent,
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.VIRTUAL);
        // Some tweaks to the underlying table widget
        final Table table = table_viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        table.setLayoutData(gd);
        
        ColumnViewerToolTipSupport.enableFor(table_viewer, ToolTip.NO_RECREATE);

        // Connect TableViewer to the Model: Provide content from model...
        table_viewer.setContentProvider(new ModelInstanceProvider());

        // Create table columns
        TableViewerColumn col =
            AutoSizeColumn.make(table_viewer, Messages.SystemColumn, MIN_SIZE, 100);
        col.setLabelProvider(new InstanceLabelProvider(-1));
        for (int c=0;  c<model.getColumnCount();  ++c)
        {
            final Column model_col = model.getColumn(c);
            col = AutoSizeColumn.make(table_viewer,
                                model_col.getName(), MIN_SIZE, 100);
            // Tell column how to display the model elements
            col.setLabelProvider(new InstanceLabelProvider(c));
            if (! model_col.isReadonly())
                col.setEditingSupport(new ModelCellEditor(table_viewer, c));
        }
        new AutoSizeControlListener(table);
        
        table_viewer.setInput(model);
    }

    // ModelListener
    public void cellUpdate(final Cell cell)
    {
        final Table table = table_viewer.getTable();
        if (table.isDisposed())
            return;
        table.getDisplay().asyncExec(new Runnable()
        {
            public void run()
            {
                if (table.isDisposed())
                    return;
                table_viewer.update(cell.getInstance(), null);
            }
        });
    }
}
