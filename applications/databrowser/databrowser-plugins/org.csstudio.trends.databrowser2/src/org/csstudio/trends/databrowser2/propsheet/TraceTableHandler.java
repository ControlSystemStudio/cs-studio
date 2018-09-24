/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.propsheet;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.csstudio.apputil.time.RelativeTime;
import org.csstudio.apputil.ui.swt.TableColumnSortHelper;
import org.csstudio.archive.vtype.DefaultVTypeFormat;
import org.csstudio.swt.rtplot.PointType;
import org.csstudio.swt.rtplot.TraceType;
import org.csstudio.swt.rtplot.data.PlotDataItem;
import org.csstudio.swt.rtplot.undo.UndoableActionManager;
import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.AxisConfig;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.csstudio.trends.databrowser2.model.ModelListener;
import org.csstudio.trends.databrowser2.model.ModelListenerAdapter;
import org.csstudio.trends.databrowser2.model.PVItem;
import org.csstudio.trends.databrowser2.model.PlotSample;
import org.csstudio.trends.databrowser2.model.RequestType;
import org.csstudio.trends.databrowser2.model.TimeHelper;
import org.csstudio.trends.databrowser2.preferences.Preferences;
import org.csstudio.trends.databrowser2.ui.TableHelper;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/** Helper for a 'Trace' TableViewer that handles the Model's items.
 *  Each 'row' in the table is a ModelItem.
 *  @author Kay Kasemir
 */
public class TraceTableHandler implements IStructuredContentProvider
{

    private LocalResourceManager color_registry;
    private Model model;
    private TableViewer trace_table;
    private boolean editing = false;
    private ScopedPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID);

    final private ModelListener model_listener = new ModelListenerAdapter()
    {
        @Override
        public void changedAxis(final Optional<AxisConfig> axis)
        {
            // In case an axis _name_ changed, this needs to be shown
            // in the "Axis" column.
            trace_table.refresh();
        }

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
            // Change of 'units' can arrive on non-UI threads
            trace_table.getControl().getDisplay().asyncExec(() -> trace_table.refresh(item));
        }

        @Override
        public void changedItemDataConfig(final PVItem item)
        {
            trace_table.refresh(item);
        }

        @Override
        public void selectedSamplesChanged()
        {
            if (!editing)
                trace_table.getTable().getDisplay().asyncExec(() ->
            {
                if (!trace_table.getTable().isDisposed())
                    trace_table.refresh();
            });
        }
    };


    /** Create table columns: Auto-sizable, with label provider and editor
     *  @param table_layout
     *  @param operations_manager
     *  @param table_viewer
     */
    public void createColumns(final TableColumnLayout table_layout, final UndoableActionManager operations_manager,
            final TableViewer table_viewer)
    {
        color_registry = new LocalResourceManager(JFaceResources.getResources(), table_viewer.getTable());
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
                editing = true;
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
                final ModelItem item = (ModelItem) element;
                final boolean visible = ((Boolean) value).booleanValue();
                if (!visible && ! store.getBoolean(Preferences.ALLOW_HIDE_TRACE))
                {
                    MessageDialogWithToggle md = MessageDialogWithToggle.openOkCancelConfirm(shell,
                            Messages.HideTraceWarning,
                            Messages.HideTraceWarningDetail,
                            Messages.DoNotShowAgain,
                            false,
                            null,
                            null);
                    if (md.getToggleState())
                        store.setValue(Preferences.ALLOW_HIDE_TRACE, true);
                    if (md.getReturnCode() != MessageDialog.OK)
                        return;
                }
                new ChangeVisibilityCommand(operations_manager, item, visible);
                editing = false;
            }
        });

        // Trace PV/Formula Column ----------
        view_col = TableHelper.createColumn(table_layout, table_viewer, Messages.ItemName, 70, 70);
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
            protected CellEditor getCellEditor(Object element) {
                editing = true;
                return super.getCellEditor(element);
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                final String new_name = value.toString().trim();
                final ModelItem item = (ModelItem) element;
                if (new_name.equals(item.getName()))
                    return;
                new ChangeNameCommand(shell, operations_manager, item, new_name);
                editing = false;
            }
        });

        // Display Name Column ----------
        view_col = TableHelper.createColumn(table_layout, table_viewer, Messages.TraceDisplayName, 70, 70);
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
            protected CellEditor getCellEditor(Object element) {
                editing = true;
                return super.getCellEditor(element);
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
                editing = false;
            }
        });

        // Color Column ----------
        view_col = TableHelper.createColumn(table_layout, table_viewer, Messages.Color, 40, 10);
        view_col.setLabelProvider(new ColorCellLabelProvider<ModelItem>()
        {
            @Override
            protected Color getColor(ModelItem item)
            {
                return color_registry.createColor(item.getColor());
            }
        });
        view_col.setEditingSupport(new EditSupportBase(table_viewer)
        {
            @Override
            protected CellEditor getCellEditor(final Object element)
            {
                editing = true;
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
                editing = false;
            }
        });

        // Selected sample time stamp and value
        view_col = TableHelper.createColumn(table_layout, table_viewer, Messages.CursorTimestamp, 150, 30);
        view_col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ModelItem item = (ModelItem) cell.getElement();
                final Optional<PlotDataItem<Instant>> sample = item.getSelectedSample();
                if (sample.isPresent())
                    cell.setText(TimeHelper.format(sample.get().getPosition()));
                else
                    cell.setText(Messages.NotApplicable);
            }

            @Override
            public String getToolTipText(Object element)
            {
                return Messages.CursorTimestampTT;
            }
        });
        view_col = TableHelper.createColumn(table_layout, table_viewer, Messages.CursorValue, 40, 30);
        view_col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ModelItem item = (ModelItem) cell.getElement();
                final Optional<PlotDataItem<Instant>> sample = item.getSelectedSample();
                if (sample.isPresent())
                {
                    final PlotSample plot_sample = (PlotSample) sample.get();
                    String text = DefaultVTypeFormat.get().format(plot_sample.getVType());
                    final String units = item.getUnits();
                    if (units != null)
                        text += " " + units; //$NON-NLS-1$
                    cell.setText(text);
                }
                else
                    cell.setText(Messages.NotApplicable);
            }

            @Override
            public String getToolTipText(Object element)
            {
                return Messages.CursorValueTT;
            }
        });

        // Scan Period Column (only applies to PVItems) ----------
        view_col = TableHelper.createColumn(table_layout, table_viewer, Messages.ScanPeriod, 70, 10);
        view_col.setLabelProvider(new CellLabelProvider()
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
        view_col.setEditingSupport(new EditSupportBase(table_viewer)
        {
            @Override
            protected boolean canEdit(Object element)
            {
                return element instanceof PVItem;
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                editing = true;
                return super.getCellEditor(element);
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
                    editing = false;
                }
                catch (NumberFormatException ex)
                {
                    // Display will revert to original value
                    return;
                }
            }
        });

        // Buffer size Column (only applies to PVItems) ----------
        view_col = TableHelper.createColumn(table_layout, table_viewer, Messages.LiveSampleBufferSize, 70, 10);
        view_col.setLabelProvider(new CellLabelProvider()
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
        view_col.setEditingSupport(new EditSupportBase(table_viewer)
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
            protected CellEditor getCellEditor(Object element) {
                editing = true;
                return super.getCellEditor(element);
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
                    editing = false;
                }
                catch (NumberFormatException ex)
                {
                    // Display will revert to original value
                    return;
                }
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
                cell.setText(item.getAxis().getName());
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
                final int cmp = item1.getAxis().getName().compareTo(item2.getAxis().getName());
                if (cmp != 0)
                    return cmp;
                return item1.getDisplayName().compareTo(item2.getDisplayName());
            }
        };
        view_col.setEditingSupport(new EditSupportBase(table_viewer)
        {   // Thread-safe copy of model axes as this editor is invoked
            final List<AxisConfig> axes = new ArrayList<>();

            @Override
            protected CellEditor getCellEditor(final Object element)
            {
                editing = true;
                final List<String> axis_names = new ArrayList<>();
                axes.clear();
                for (AxisConfig axis : model.getAxes())
                {
                    axes.add(axis);
                    axis_names.add(axis.getName());
                }
                final ComboBoxCellEditor combo = new ComboBoxCellEditor(table_viewer.getTable(),
                        axis_names.toArray(new String[axis_names.size()]), SWT.READ_ONLY);
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
                final AxisConfig axis = axes.get(axis_index);
                final ModelItem item = (ModelItem)element;
                if (axis != item.getAxis())
                    new ChangeAxisCommand(operations_manager, item, axis);
                editing = false;
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
                editing = true;
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
                editing = false;
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
            protected CellEditor getCellEditor(Object element) {
                editing = true;
                return super.getCellEditor(element);
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
                editing = false;
            }
        });

        // Point Type Column ----------
        view_col = TableHelper.createColumn(table_layout, table_viewer, Messages.PointType, 75, 10);
        view_col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ModelItem item = (ModelItem) cell.getElement();
                cell.setText(item.getPointType().toString());
            }

            @Override
            public String getToolTipText(Object element)
            {
                return Messages.PointTypeTT;
            }
        });
        view_col.setEditingSupport(new EditSupportBase(table_viewer)
        {
            @Override
            protected CellEditor getCellEditor(final Object element)
            {
                editing = true;
                final ComboBoxCellEditor combo = new ComboBoxCellEditor(table_viewer.getTable(),
                        PointType.getDisplayNames(), SWT.READ_ONLY);
                combo.setValue(getValue(element));
                return combo;
            }
            @Override
            protected Object getValue(final Object element)
            {
                return ((ModelItem) element).getPointType().ordinal();
            }
            @Override
            protected void setValue(final Object element, final Object value)
            {
                final PointType point_type =
                        PointType.fromOrdinal(((Integer)value).intValue());
                final ModelItem item = (ModelItem)element;
                if (point_type != item.getPointType())
                    new ChangePointTypeCommand(operations_manager, item, point_type);
                editing = false;
            }
        });

        // Point Size Column ----------
        view_col = TableHelper.createColumn(table_layout, table_viewer, Messages.PointSize, 40, 10);
        view_col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ModelItem item = (ModelItem) cell.getElement();
                cell.setText(Integer.toString(item.getPointSize()));
            }

            @Override
            public String getToolTipText(Object element)
            {
                return Messages.PointSizeTT;
            }

        });
        view_col.setEditingSupport(new EditSupportBase(table_viewer)
        {
            @Override
            protected Object getValue(final Object element)
            {
                return Integer.toString(((ModelItem) element).getPointSize());
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                editing = true;
                return super.getCellEditor(element);
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                int size;
                try
                {
                    size = Integer.parseInt(value.toString().trim());
                }
                catch (NumberFormatException ex)
                {
                    size = 0;
                }
                final ModelItem item = (ModelItem)element;
                if (size != item.getPointSize())
                    new ChangePointSizeCommand(operations_manager, item, size);
                editing = false;
            }
        });

        // Request Type Column ----------
        view_col = TableHelper.createColumn(table_layout, table_viewer, Messages.RequestType, 75, 10);
        view_col.setLabelProvider(new CellLabelProvider()
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

            @Override
            public String getToolTipText(Object element)
            {
                return Messages.RequestTypeTT;
            }
        });
        // Edit as boolean: Raw == false,  Optimized == true
        view_col.setEditingSupport(new EditSupportBase(table_viewer)
        {
            @Override
            protected boolean canEdit(final Object element)
            {
                return element instanceof PVItem;
            }

            @Override
            protected CellEditor getCellEditor(final Object element)
            {
                editing = true;
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
                if (request_type == RequestType.RAW && ! store.getBoolean(Preferences.ALLOW_REQUEST_RAW))
                {
                    MessageDialogWithToggle md = MessageDialogWithToggle.openOkCancelConfirm(
                            shell,
                            Messages.RequestTypeWarning,
                            Messages.RequestTypeWarningDetail,
                            Messages.DoNotShowAgain,
                            false,
                            null,
                            null);
                    if (md.getToggleState())
                        store.setValue(Preferences.ALLOW_REQUEST_RAW, true);
                    if (md.getReturnCode() == MessageDialog.OK)
                        return;
                }
                new ChangeRequestTypeCommand(operations_manager, item, request_type);
                editing = false;
            }
        });

        // Waveform Index Column ----------
        view_col = TableHelper.createColumn(table_layout, table_viewer, Messages.WaveformIndexCol, 40, 10);
        view_col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ModelItem item = (ModelItem) cell.getElement();
                cell.setText(Integer.toString(item.getWaveformIndex()));
            }

            @Override
            public String getToolTipText(Object element)
            {
                return Messages.WaveformIndexColTT;
            }

        });
        view_col.setEditingSupport(new EditSupportBase(table_viewer)
        {
            @Override
            protected Object getValue(final Object element)
            {
                return Integer.toString(((ModelItem) element).getWaveformIndex());
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                editing = true;
                return super.getCellEditor(element);
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                int index;
                try
                {
                    index = Integer.parseInt(value.toString().trim());
                    if (index < 0)
                        return;
                    final ModelItem item = (ModelItem)element;
                    if (index != item.getWaveformIndex())
                        new ChangeWaveformIndexCommand(operations_manager, item, index);
                }
                catch (NumberFormatException ex)
                {
                    return;
                } finally {
                    editing = false;
                }

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
        final List<ModelItem> items = new ArrayList<>();
        for (ModelItem item : model.getItems())
            items.add(item);
        return items.toArray(new ModelItem[items.size()]);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        // NOP
    }
}
