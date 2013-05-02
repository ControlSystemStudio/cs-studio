/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.propsheet;

import org.csstudio.apputil.time.RelativeTime;
import org.csstudio.apputil.ui.swt.TableColumnSortHelper;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.swt.xygraph.undo.XYGraphMemento;
import org.csstudio.swt.xygraph.util.XYGraphMediaFactory;
import org.csstudio.trends.sscan.Activator;
import org.csstudio.trends.sscan.Messages;
import org.csstudio.trends.sscan.model.AxesConfig;
import org.csstudio.trends.sscan.model.AxisConfig;
import org.csstudio.trends.sscan.model.Detector;
import org.csstudio.trends.sscan.model.Model;
import org.csstudio.trends.sscan.model.ModelItem;
import org.csstudio.trends.sscan.model.ModelListener;
import org.csstudio.trends.sscan.model.Positioner;
import org.csstudio.trends.sscan.model.TraceType;
import org.csstudio.trends.sscan.ui.TableHelper;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Shell;

/** Helper for a 'Trace' TableViewer that handles the Model's items.
 *  Each 'row' in the table is a ModelItem.
 *  @author Kay Kasemir
 */
public class TraceTableHandler implements IStructuredContentProvider
{
    /** Prompt for the 'raw request' warning? */
    static private boolean prompt_for_raw_data_request = true;

    /** Prompt for the 'hide trace' warning'? */
    static private boolean prompt_for_not_visible = true;

    final private XYGraphMediaFactory color_registry = XYGraphMediaFactory.getInstance();
    private Model model;
    private TableViewer trace_table;

    final private ModelListener model_listener = new ModelListener()
    {
        @Override
        public void changedColors() { /* Ignored */ }

        @Override
        public void changedAxis(AxisConfig axis)
        {
            // In case an axis _name_ changed, this needs to be shown
            // in the "Axis" column.
            trace_table.refresh();
        }
        
  //      @Override
  //      public void changedAxes(AxesConfig axes)
  //      {
            // In case an axis _name_ changed, this needs to be shown
            // in the "Axis" column.
  //          trace_table.refresh();
  //      }

        @Override
        public void itemAdded(final ModelItem item)
        {
            trace_table.cancelEditing();
            trace_table.refresh();
        }

        @Override
        public void itemRemoved(final ModelItem item)
        {
            // User will often click on an item,
            // which usually starts an editor, then press delete.
            // To get a clear table update, all of this seems to be required
            trace_table.cancelEditing();
            trace_table.setSelection(null);
            trace_table.refresh();
        }

        @Override
        public void changedItemVisibility(ModelItem item)
        {   // Update the item's row in table
            changedItemLook(item);
        }

        @Override
        public void changedItemLook(final ModelItem item)
        {
            trace_table.refresh(item);
        }

        @Override
        public void changedItemDataConfig(final ModelItem item)
        {
            trace_table.refresh(item);
        }
	
		@Override
		public void changedAnnotations() {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void changedXYGraphConfig() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void changedItemData(ModelItem item) {

			
		}
    };

    /** Create table columns: Auto-sizable, with label provider and editor
     *  @param table_layout
     *  @param operations_manager
     *  @param table_viewer
     */
    public void createColumns(final TableColumnLayout table_layout, final OperationsManager operations_manager,
            final TableViewer table_viewer)
    {
        final Shell shell = table_viewer.getTable().getShell();

        // Visible Column ----------
        TableViewerColumn view_col = TableHelper.createColumn(table_layout, table_viewer, Messages.TraceVisibility, 45, 1);
        view_col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ModelItem item = (ModelItem) cell.getElement();
                cell.setImage(item.isVisible()
                        ? Activator.getDefault().getImage(Activator.ICON_CHECKED)
                        : Activator.getDefault().getImage(Activator.ICON_UNCHECKED));
            }

            @Override
            public String getToolTipText(Object element)
            {
                return Messages.TraceVisibilityTT;
            }
        });
        new TableColumnSortHelper<ModelItem>(table_viewer, view_col)
        {
			@Override
            public int compare(final ModelItem item1, final ModelItem item2)
            {
				final int v1 = item1.isVisible() ? 1 : 0;
				final int v2 = item2.isVisible() ? 1 : 0;
				final int cmp = v1 - v2;
				if (cmp != 0)
					return cmp;
				return item1.getName().compareTo(item2.getName());
            }
        };
        view_col.setEditingSupport(new EditSupportBase(table_viewer)
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

        // Trace Scan/Formula Column ----------
        view_col = TableHelper.createColumn(table_layout, table_viewer, Messages.ItemName, 100, 100);
        view_col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ModelItem item = (ModelItem) cell.getElement();
                cell.setText(item.getName());
            }

            @Override
            public String getToolTipText(Object element)
            {
                return Messages.ItemNameTT;
            }
        });
        new TableColumnSortHelper<ModelItem>(table_viewer, view_col)
        {
			@Override
            public int compare(final ModelItem item1, final ModelItem item2)
            {
				return item1.getName().compareTo(item2.getName());
            }
        };
        view_col.setEditingSupport(new EditSupportBase(table_viewer)
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
        view_col = TableHelper.createColumn(table_layout, table_viewer, Messages.TraceDisplayName, 100, 100);
        view_col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ModelItem item = (ModelItem) cell.getElement();
                cell.setText(item.getDisplayName());
            }

            @Override
            public String getToolTipText(Object element)
            {
                return Messages.TraceDisplayNameTT;
            }
        });
        new TableColumnSortHelper<ModelItem>(table_viewer, view_col)
        {
			@Override
            public int compare(final ModelItem item1, final ModelItem item2)
            {
				return item1.getDisplayName().compareTo(item2.getDisplayName());
            }
        };
        view_col.setEditingSupport(new EditSupportBase(table_viewer)
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
        view_col = TableHelper.createColumn(table_layout, table_viewer, Messages.Color, 40, 10);
        view_col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ModelItem item = (ModelItem) cell.getElement();
                cell.setBackground(color_registry.getColor(item.getColor()));
            }

            @Override
            public String getToolTipText(Object element)
            {
                return Messages.ColorTT;
            }
        });
        view_col.setEditingSupport(new EditSupportBase(table_viewer)
        {
            @Override
            protected CellEditor getCellEditor(final Object element)
            {
                return new RGBCellEditor(table_viewer.getTable());
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

        // X Column ----------
        view_col = TableHelper.createColumn(table_layout, table_viewer, Messages.xAxis, 60, 30);
        view_col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ModelItem item = (ModelItem) cell.getElement();
                cell.setText(item.getPositioner().getPositionerPV());
            }

            @Override
            public String getToolTipText(Object element)
            {
                return Messages.xAxisTT;
            }
        });
        new TableColumnSortHelper<ModelItem>(table_viewer, view_col)
        {
			@Override
            public int compare(final ModelItem item1, final ModelItem item2)
            {
				final int cmp = item1.getPositioner().getPositionerPV().compareTo(item2.getPositioner().getPositionerPV());
				if (cmp != 0)
					return cmp;
				return item1.getDisplayName().compareTo(item2.getDisplayName());
            }
        };
        view_col.setEditingSupport(new EditSupportBase(table_viewer)
        {
            @Override
            protected CellEditor getCellEditor(final Object element)
            {
            	final ModelItem item = (ModelItem)element;
                final String positioner_names[] = new String[item.getSscan().getPositionerCount()];
                for (int i=0; i<positioner_names.length; ++i)
                    positioner_names[i] = item.getSscan().getPositioner(i).getPositionerPV();
                final ComboBoxCellEditor combo = new ComboBoxCellEditor(table_viewer.getTable(),
                        positioner_names, SWT.READ_ONLY);
                combo.setValue(getValue(element));
                return combo;
            }
            @Override
            protected Object getValue(final Object element)
            {
            	final ModelItem item = (ModelItem)element;
                return item.getSscan().getPositionerIndex(((ModelItem) element).getPositioner());
            }
            @Override
            protected void setValue(final Object element, final Object value)
            {
            	final ModelItem item = (ModelItem)element;
                final int positioner_index = ((Integer)value).intValue();
                final Positioner positioner = item.getSscan().getPositioner(positioner_index);
                if (positioner != item.getPositioner())
                   new ChangePositionerCommand(operations_manager, item, positioner);
            }
        }); 
        
        // Y Column ----------
        view_col = TableHelper.createColumn(table_layout, table_viewer, Messages.yAxis, 60, 30);
        view_col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ModelItem item = (ModelItem) cell.getElement();
                cell.setText(item.getDetector().getDetectorPV());
            }

            @Override
            public String getToolTipText(Object element)
            {
                return Messages.yAxisTT;
            }
        });
        new TableColumnSortHelper<ModelItem>(table_viewer, view_col)
        {
			@Override
            public int compare(final ModelItem item1, final ModelItem item2)
            {
				final int cmp = item1.getDetector().getDetectorPV().compareTo(item2.getDetector().getDetectorPV());
				if (cmp != 0)
					return cmp;
				return item1.getDisplayName().compareTo(item2.getDisplayName());
            }
        };
        view_col.setEditingSupport(new EditSupportBase(table_viewer)
        {
            @Override
            protected CellEditor getCellEditor(final Object element)
            {
            	final ModelItem item = (ModelItem)element;
                final String detector_names[] = new String[item.getSscan().getDetectorCount()];
                for (int i=0; i<detector_names.length; ++i)
                    detector_names[i] = item.getSscan().getDetector(i).getDetectorPV();
                final ComboBoxCellEditor combo = new ComboBoxCellEditor(table_viewer.getTable(),
                        detector_names, SWT.READ_ONLY);
                combo.setValue(getValue(element));
                return combo;
            }
            @Override
            protected Object getValue(final Object element)
            {
            	final ModelItem item = (ModelItem)element;
                return item.getSscan().getDetectorIndex(((ModelItem) element).getDetector());
            }
            @Override
            protected void setValue(final Object element, final Object value)
            {
            	final ModelItem item = (ModelItem)element;
                final int detector_index = ((Integer)value).intValue();
                final Detector detector = item.getSscan().getDetector(detector_index);
                if (detector != item.getDetector())
                   new ChangeDetectorCommand(operations_manager, item, detector);
            }
        });

        // Line Width Column ----------
        view_col = TableHelper.createColumn(table_layout, table_viewer, Messages.TraceLineWidth, 40, 10);
        view_col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ModelItem item = (ModelItem) cell.getElement();
                cell.setText(Integer.toString(item.getLineWidth()));
            }

            @Override
            public String getToolTipText(Object element)
            {
                return Messages.TraceLineWidthTT;
            }

        });
        view_col.setEditingSupport(new EditSupportBase(table_viewer)
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
        view_col = TableHelper.createColumn(table_layout, table_viewer, Messages.Axis, 60, 30);
        view_col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ModelItem item = (ModelItem) cell.getElement();
                cell.setText(item.getAxes().getName());
            }

            @Override
            public String getToolTipText(Object element)
            {
                return Messages.AxisTT;
            }
        });
        new TableColumnSortHelper<ModelItem>(table_viewer, view_col)
        {
			@Override
            public int compare(final ModelItem item1, final ModelItem item2)
            {
				final int cmp = item1.getAxes().getName().compareTo(item2.getAxes().getName());
				if (cmp != 0)
					return cmp;
				return item1.getDisplayName().compareTo(item2.getDisplayName());
            }
        };
        view_col.setEditingSupport(new EditSupportBase(table_viewer)
        {
            @Override
            protected CellEditor getCellEditor(final Object element)
            {
                final String axis_names[] = new String[model.getAxesCount()];
                for (int i=0; i<axis_names.length; ++i)
                    axis_names[i] = model.getAxes(i).getName();
                final ComboBoxCellEditor combo = new ComboBoxCellEditor(table_viewer.getTable(),
                        axis_names, SWT.READ_ONLY);
                combo.setValue(getValue(element));
                return combo;
            }
            @Override
            protected Object getValue(final Object element)
            {
                return model.getAxesIndex(((ModelItem) element).getAxes());
            }
            @Override
            protected void setValue(final Object element, final Object value)
            {
                final int axis_index = ((Integer)value).intValue();
                final AxesConfig axes = model.getAxes(axis_index);
                final ModelItem item = (ModelItem)element;
                if (axes != item.getAxes())
                    new ChangeAxesCommand(operations_manager, item, axes);
            }
        });

        // Trace Type Column ----------
        view_col = TableHelper.createColumn(table_layout, table_viewer, Messages.TraceType, 75, 10);
        view_col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ModelItem item = (ModelItem) cell.getElement();
                cell.setText(item.getTraceType().toString());
            }

            @Override
            public String getToolTipText(Object element)
            {
                return Messages.TraceTypeTT;
            }
        });
        view_col.setEditingSupport(new EditSupportBase(table_viewer)
        {
            @Override
            protected CellEditor getCellEditor(final Object element)
            {
                final ComboBoxCellEditor combo = new ComboBoxCellEditor(table_viewer.getTable(),
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
        ColumnViewerToolTipSupport.enableFor(table_viewer);
    }

    /** {@inheritDoc} */
    @Override
    public void inputChanged(final Viewer viewer, final Object old_model, final Object new_model)
    {
        if (old_model != null)
            ((Model)old_model).removeListener(model_listener);

        trace_table = (TableViewer) viewer;
        model = (Model) new_model;
        if (trace_table == null  ||  model == null)
            return;

        model.addListener(model_listener);
    }

    /** {@inheritDoc} */
    @Override
    public Object[] getElements(final Object inputElement)
    {
    	final ModelItem[] items = new ModelItem[model.getItemCount()];
    	for (int i=0; i<items.length; ++i)
    		items[i] = model.getItem(i);
	    return items;
    }

	/** {@inheritDoc} */
    @Override
    public void dispose()
    {
        // NOP
    }
}
