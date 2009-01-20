package org.csstudio.display.pace.gui;

import org.csstudio.apputil.ui.swt.AutoSizeColumn;
import org.csstudio.apputil.ui.swt.AutoSizeControlListener;
import org.csstudio.display.pace.Messages;
import org.csstudio.display.pace.model.Cell;
import org.csstudio.display.pace.model.Column;
import org.csstudio.display.pace.model.Instance;
import org.csstudio.display.pace.model.Model;
import org.csstudio.display.pace.model.ModelListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchPartSite;

/** GUI for the Model
 * 
 *  @author Delphy Nypaver Armstrong
 *  @author Kay Kasemir
 */
public class GUI implements ModelListener, IMenuListener
{
    /** Minimum column width */
    private static final int MIN_SIZE = 100;
    
    /** Table Viewer for Model's "Instance" rows */
    private TableViewer table_viewer;
    
    /** Currently selected Cell in Model or <code>null</code> */
    private Cell selected_cell = null;

    /** Initialize
     *  @param parent Parent widget
     *  @param model Model to display
     *  @param site Workbench site or <code>null</code>
     */
    public GUI(final Composite parent, final Model model,
            final IWorkbenchPartSite site)
    {
        createComponents(parent, model);
        addCellTracker();
        model.addListener(this);
        // TODO context menu
        createContextMenu(site);
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

    /** Update <code>selected_cell</code> from mouse position */
    private void addCellTracker()
    {
        table_viewer.getTable().addListener(SWT.MouseDown, new Listener()
        {
            @SuppressWarnings("nls")
            public void handleEvent(final Event event)
            {
                final Point point = new Point(event.x, event.y);
                final ViewerCell viewer_cell = table_viewer.getCell(point);
                if (viewer_cell == null)
                {   // Didn't hit table??
                    selected_cell = null;
                    return;
                }
                final Instance instance = (Instance) viewer_cell.getElement();
                final int col_idx = viewer_cell.getColumnIndex();
                if (col_idx <= 0)
                {   // Special first column with instance name
                    selected_cell = null;
                    return;
                }
                selected_cell = instance.getCell(col_idx-1);
                System.out.println("Cell " + selected_cell);
            }
        });
    }

    private void createContextMenu(final IWorkbenchPartSite site)
    {
        final MenuManager manager = new MenuManager();
        manager.setRemoveAllWhenShown(true);
        manager.addMenuListener(this);
        final Table table = table_viewer.getTable();
        table.setMenu(manager.createContextMenu(table));
        // Allow extensions to add to the context menu
        if (site != null)
            site.registerContextMenu(manager, table_viewer);
    }

    // IMenuListener
    public void menuAboutToShow(final IMenuManager manager)
    {
        if (selected_cell != null)
        manager.add(new RestoreCellAction(selected_cell));
        // Placeholder for CSS PV contributions
        manager.add(new GroupMarker("additions")); //$NON-NLS-1$
        manager.add(new Separator());
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
