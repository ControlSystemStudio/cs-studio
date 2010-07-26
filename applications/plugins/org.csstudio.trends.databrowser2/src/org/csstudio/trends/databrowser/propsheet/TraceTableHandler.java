/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser.propsheet;

import org.csstudio.apputil.time.RelativeTime;
import org.csstudio.platform.ui.swt.AutoSizeColumn;
import org.csstudio.platform.ui.swt.AutoSizeControlListener;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser.Activator;
import org.csstudio.trends.databrowser.Messages;
import org.csstudio.trends.databrowser.model.AxisConfig;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.ModelItem;
import org.csstudio.trends.databrowser.model.ModelListener;
import org.csstudio.trends.databrowser.model.PVItem;
import org.csstudio.trends.databrowser.model.RequestType;
import org.csstudio.trends.databrowser.model.TraceType;
import org.csstudio.trends.databrowser.ui.ColorRegistry;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Shell;

/** Helper for a 'Trace' TableViewer that handles the Model's items.
 *  Each 'row' in the table is a ModelItem.
 *  @author Kay Kasemir
 */
public class TraceTableHandler implements ILazyContentProvider
{
    /** Prompt for the 'raw request' warning? */
    static private boolean prompt_for_raw_data_request = true;
    
    /** Prompt for the 'hide trace' warning'? */
    static private boolean prompt_for_not_visible = true;

    final private ColorRegistry color_registry;
    private Model model;
    private TableViewer trace_table;
    
    final private ModelListener model_listener = new ModelListener()
    {
        public void changedUpdatePeriod() { /* Ignored */ }
        public void changedArchiveRescale() { /* Ignored */ }
        public void changedColors() { /* Ignored */ }
        public void changedTimerange() { /* Ignored */ }

        public void changedAxis(AxisConfig axis)
        {
            // In case an axis _name_ changed, this needs to be shown
            // in the "Axis" column.
            trace_table.refresh();
        }

        public void itemAdded(final ModelItem item)
        {
            trace_table.cancelEditing();
            trace_table.setItemCount(model.getItemCount());
        }
        
        public void itemRemoved(final ModelItem item)
        {
            // User will often click on an item,
            // which usually starts an editor, then press delete.
            // To get a clear table update, all of this seems to be required
            trace_table.cancelEditing();
            trace_table.setSelection(null);
            trace_table.setItemCount(model.getItemCount());
            trace_table.refresh();
        }

        public void changedItemVisibility(ModelItem item)
        {   // Update the item's row in table
            changedItemLook(item);
        }

        public void changedItemLook(final ModelItem item)
        {
            trace_table.refresh(item);
        }
        
        public void changedItemDataConfig(final PVItem item) { /* Ignored */ }
        public void scrollEnabled(final boolean scroll_enabled) { /* Ignored */ }
    };    
    
    /** Initialize
     *  @param color_registry ColorRegistry
     */
    public TraceTableHandler(final ColorRegistry color_registry)
    {
        this.color_registry = color_registry;
    }
    
    /** Create table columns: Auto-sizable, with label provider and editor
     *  @param trace_table
     */
    public void createColumns(final OperationsManager operations_manager,
            final TableViewer trace_table)
    {
        final Shell shell = trace_table.getTable().getShell();
        TableViewerColumn col;
        
        // Visible Column ----------
        col = AutoSizeColumn.make(trace_table, Messages.TraceVisibility, 45, 1);
        col.setLabelProvider(new CellLabelProvider()
        {
            @SuppressWarnings("nls")
            @Override
            public void update(final ViewerCell cell)
            {
                final ModelItem item = (ModelItem) cell.getElement();
                cell.setImage(item.isVisible()
                        ? Activator.getDefault().getImage("icons/checked.gif")
                        : Activator.getDefault().getImage("icons/unchecked.gif"));
            }
        });
        col.setEditingSupport(new EditSupportBase(trace_table)
        {
            @Override
            protected CellEditor getCellEditor(final Object element)
            {
                return new CheckboxCellEditor(((TableViewer)getViewer()).getTable());
            }

            @Override
            protected Object getValue(final Object element)
            {
                return ((ModelItem)element).isVisible();
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                final ModelItem item = (ModelItem)element;
                final boolean visible = ((Boolean) value).booleanValue();
                if (!visible  &&  prompt_for_not_visible)
                {
                    if (!MessageDialog.openQuestion(shell,
                            Messages.HideTraceWarning,
                            Messages.HideTraceWarningDetail))
                            return;
                    prompt_for_not_visible = false;
                }
                new ChangeVisibilityCommand(operations_manager, item, visible);
            }
        });
        
        // Trace PV/Formula Column ----------
        col = AutoSizeColumn.make(trace_table, Messages.ItemName, 100, 100);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ModelItem item = (ModelItem) cell.getElement();
                cell.setText(item.getName());
            }
        });
        col.setEditingSupport(new EditSupportBase(trace_table)
        {
            @Override
            protected Object getValue(final Object element)
            {
                return ((ModelItem) element).getName();
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                final String new_name = value.toString().trim();
                final ModelItem item = (ModelItem) element;
                if (new_name.equals(item.getName()))
                    return;
                new ChangeNameCommand(shell, operations_manager, item, new_name);
            }
        });
        
        // Display Name Column ----------
        col = AutoSizeColumn.make(trace_table, Messages.TraceDisplayName, 100, 100);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ModelItem item = (ModelItem) cell.getElement();
                cell.setText(item.getDisplayName());
            }
        });
        col.setEditingSupport(new EditSupportBase(trace_table)
        {
            @Override
            protected Object getValue(final Object element)
            {
                return ((ModelItem) element).getDisplayName();
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                final String new_name = value.toString().trim();
                final ModelItem item = (ModelItem) element;
                if (new_name.equals(item.getDisplayName()))
                    return;
                new ChangeDisplayNameCommand(operations_manager,
                        item, new_name);
            }
        });
        
        // Color Column ----------
        col = AutoSizeColumn.make(trace_table, Messages.Color, 40, 10);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ModelItem item = (ModelItem) cell.getElement();
                cell.setBackground(color_registry.getColor(item.getColor()));
            }
        });
        col.setEditingSupport(new EditSupportBase(trace_table)
        {
            @Override
            protected CellEditor getCellEditor(final Object element)
            {
                return new RGBCellEditor(trace_table.getTable());
            }

            @Override
            protected Object getValue(final Object element)
            {
                return ((ModelItem) element).getColor();
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                new ChangeColorCommand(operations_manager,
                        (ModelItem) element, (RGB)value);
            }
        });
        
        // Scan Period Column (only applies to PVItems) ----------
        col = AutoSizeColumn.make(trace_table, Messages.ScanPeriod, 70, 10);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ModelItem item = (ModelItem) cell.getElement();
                if (item instanceof PVItem)
                    cell.setText(Double.toString(((PVItem)item).getScanPeriod()));
                else
                    cell.setText(Messages.NotApplicable);
            }

            @Override
            public String getToolTipText(final Object element)
            {
                if (! (element instanceof PVItem))
                    return null;
                return Messages.ScanPeriodTT;
            }

        });
        col.setEditingSupport(new EditSupportBase(trace_table)
        {
            @Override
            protected boolean canEdit(Object element)
            {
                return element instanceof PVItem;
            }

            @Override
            protected Object getValue(final Object element)
            {
                if (element instanceof PVItem)
                    return Double.toString(((PVItem)element).getScanPeriod());
                else
                    return Messages.NotApplicable;
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                if (!(element instanceof PVItem))
                    return;
                final PVItem pv = (PVItem) element;
                try
                {
                    final double period = Double.parseDouble(value.toString().trim());
                    if (period != pv.getScanPeriod())
                        new ChangeSamplePeriodCommand(shell,
                                                operations_manager, pv, period);
                }
                catch (NumberFormatException ex)
                {
                    // Display will revert to original value
                    return;
                }
            }
        });
        
        // Buffer size Column (only applies to PVItems) ----------
        col = AutoSizeColumn.make(trace_table, Messages.LiveSampleBufferSize, 70, 10);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ModelItem item = (ModelItem) cell.getElement();
                if (item instanceof PVItem)
                    cell.setText(Integer.toString(((PVItem)item).getLiveCapacity()));
                else
                    cell.setText(Messages.NotApplicable);
            }

            @Override
            public String getToolTipText(final Object element)
            {
                if (! (element instanceof PVItem))
                    return null;
                final PVItem pv = (PVItem) element;
                return NLS.bind(Messages.LiveBufferSizeInfoFmt, pv.getLiveCapacity(), new RelativeTime(pv.getLiveCapacity()).toString());
            }
        });
        col.setEditingSupport(new EditSupportBase(trace_table)
        {
            @Override
            protected boolean canEdit(Object element)
            {
                return element instanceof PVItem;
            }

            @Override
            protected Object getValue(final Object element)
            {
                if (element instanceof PVItem)
                    return Integer.toString(((PVItem)element).getLiveCapacity());
                else
                    return Messages.NotApplicable;
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                if (!(element instanceof PVItem))
                    return;
                final PVItem pv = (PVItem) element;
                try
                {
                    final int size = Integer.parseInt(value.toString().trim());
                    if (size != pv.getLiveCapacity())
                        new ChangeLiveCapacityCommand(shell, operations_manager, pv, size);
                }
                catch (NumberFormatException ex)
                {
                    // Display will revert to original value
                    return;
                }
            }
        });
        
        // Line Width Column ----------
        col = AutoSizeColumn.make(trace_table, Messages.TraceLineWidth, 40, 10);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ModelItem item = (ModelItem) cell.getElement();
                cell.setText(Integer.toString(item.getLineWidth()));
            }
        });
        col.setEditingSupport(new EditSupportBase(trace_table)
        {
            @Override
            protected Object getValue(final Object element)
            {
                return Integer.toString(((ModelItem) element).getLineWidth());
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                int width;
                try
                {
                    width = Integer.parseInt(value.toString().trim());
                }
                catch (NumberFormatException ex)
                {
                    width = 0;
                }
                final ModelItem item = (ModelItem)element;
                if (width != item.getLineWidth())
                    new ChangeLineWidthCommand(operations_manager, item, width);
            }
        });
        
        // Axis Column ----------
        col = AutoSizeColumn.make(trace_table, Messages.Axis, 60, 30);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ModelItem item = (ModelItem) cell.getElement();
                cell.setText(item.getAxis().getName());
            }
        });
        col.setEditingSupport(new EditSupportBase(trace_table)
        {
            @Override
            protected CellEditor getCellEditor(final Object element)
            {
                final String axis_names[] = new String[model.getAxisCount()];
                for (int i=0; i<axis_names.length; ++i)
                    axis_names[i] = model.getAxis(i).getName();
                final ComboBoxCellEditor combo = new ComboBoxCellEditor(trace_table.getTable(),
                        axis_names, SWT.READ_ONLY);
                combo.setValue(getValue(element));
                return combo;
            }
            @Override
            protected Object getValue(final Object element)
            {
                return model.getAxisIndex(((ModelItem) element).getAxis());
            }
            @Override
            protected void setValue(final Object element, final Object value)
            {
                final int axis_index = ((Integer)value).intValue();
                final AxisConfig axis = model.getAxis(axis_index);
                final ModelItem item = (ModelItem)element;
                if (axis != item.getAxis())
                    new ChangeAxisCommand(operations_manager, item, axis);
            }
        });
        
        // Trace Type Column ----------
        col = AutoSizeColumn.make(trace_table, Messages.TraceType, 75, 10);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ModelItem item = (ModelItem) cell.getElement();
                cell.setText(item.getTraceType().toString());
            }
        });
        col.setEditingSupport(new EditSupportBase(trace_table)
        {
            @Override
            protected CellEditor getCellEditor(final Object element)
            {
                final ComboBoxCellEditor combo = new ComboBoxCellEditor(trace_table.getTable(),
                        TraceType.getDisplayNames(), SWT.READ_ONLY);
                combo.setValue(getValue(element));
                return combo;
            }
            @Override
            protected Object getValue(final Object element)
            {
                return ((ModelItem) element).getTraceType().ordinal();
            }
            @Override
            protected void setValue(final Object element, final Object value)
            {
                final TraceType trace_type =
                    TraceType.fromOrdinal(((Integer)value).intValue());
                final ModelItem item = (ModelItem)element;
                if (trace_type != item.getTraceType())
                    new ChangeTraceTypeCommand(operations_manager, item, trace_type);
            }
        });
        
        // Request Type Column ----------
        col = AutoSizeColumn.make(trace_table, Messages.RequestType, 75, 10);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ModelItem item = (ModelItem) cell.getElement();
                if (item instanceof PVItem)
                    cell.setText(((PVItem)item).getRequestType().toString());
                else
                    cell.setText(Messages.NotApplicable);
            }
        });
        // Edit as boolean: Raw == false,  Optimized == true
        col.setEditingSupport(new EditSupportBase(trace_table)
        {
            @Override
            protected boolean canEdit(final Object element)
            {
                return element instanceof PVItem;
            }

            @Override
            protected CellEditor getCellEditor(final Object element)
            {
                return new CheckboxCellEditor(((TableViewer)getViewer()).getTable());
            }

            @Override
            protected Object getValue(final Object element)
            {
                return ((PVItem)element).getRequestType() == RequestType.OPTIMIZED;
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                final PVItem item = (PVItem)element;
                final RequestType request_type = ((Boolean)value).booleanValue() ? RequestType.OPTIMIZED : RequestType.RAW;
                if (request_type == RequestType.RAW && prompt_for_raw_data_request)
                {
                    if (!MessageDialog.openQuestion(shell,
                            Messages.RequestTypeWarning,
                            Messages.RequestTypeWarningDetail))
                            return;
                    prompt_for_raw_data_request = false;
                }
                new ChangeRequestTypeCommand(operations_manager, item, request_type);
            }
        });
        
        ColumnViewerToolTipSupport.enableFor(trace_table, ToolTip.NO_RECREATE);

        new AutoSizeControlListener(trace_table.getTable());
    }

    /** Set input to a Model
     *  @see ILazyContentProvider#inputChanged(Viewer, Object, Object)
     */
    public void inputChanged(final Viewer viewer, final Object old_model, final Object new_model)
    {
        if (old_model != null)
            ((Model)old_model).removeListener(model_listener);
            
        trace_table = (TableViewer) viewer;
        model = (Model) new_model;
        if (trace_table == null  ||  model == null)
            return;

        trace_table.setItemCount(model.getItemCount());
        model.addListener(model_listener);
    }
    
    /** Called by ILazyContentProvider to get the ModelItem for a table row
     *  {@inheritDoc}
     */
    public void updateElement(int index)
    {
        trace_table.replace(model.getItem(index), index);
    }

    // ILazyContentProvider
    public void dispose()
    {
        // NOP
    }
}
