/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.propsheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.csstudio.swt.rtplot.undo.UndoableActionManager;
import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.ArchiveRescale;
import org.csstudio.trends.databrowser2.model.AxisConfig;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelListener;
import org.csstudio.trends.databrowser2.model.ModelListenerAdapter;
import org.csstudio.trends.databrowser2.ui.TableHelper;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchActionConstants;

/** Helper for a 'Axes' TableViewer that handles the Model's axes.
 *  Each 'row' in the table is an AxisConfig.
 *  @author Kay Kasemir
 */
public class AxesTableHandler implements IStructuredContentProvider
{
    final private LocalResourceManager color_registry;
    final private UndoableActionManager operations_manager;
    final private TableViewer axes_table;
    private volatile Model model;
    private volatile AxisConfig[] axes = new AxisConfig[0];

    /** Listen to model changes regarding axes.
     *  Ignore configuration of individual items.
     */
    final private ModelListener model_listener = new ModelListenerAdapter()
    {
        @Override
        public void changedAxis(final Optional<AxisConfig> axis)
        {
            if (axis.isPresent())
                axes_table.refresh(axis.get());
            else
            {   // Force total refresh
                getModelAxisCopy(model);
                axes_table.refresh();
            }
        }
    };

    /** Initialize
     *  @param parent
     *  @param table_layout
     *  @param operations_manager
     */
    public AxesTableHandler(final Composite parent,
            final TableColumnLayout table_layout, final UndoableActionManager operations_manager)
    {
        color_registry = new LocalResourceManager(JFaceResources.getResources(), parent);
        this.operations_manager = operations_manager;

        axes_table = new TableViewer(parent,
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION
                | SWT.VIRTUAL);
        final Table table = axes_table.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        createColumns(table_layout);

        axes_table.setContentProvider(this);

        createContextMenu();
    }

    /** @return TableViewer for the axes table */
    TableViewer getAxesTable()
    {
        return axes_table;
    }

    /** Create table columns: Auto-sizable, with label provider and editor
     *  @param table_layout
     */
    private void createColumns(TableColumnLayout table_layout)
    {
        TableViewerColumn col;

        // Visible? Column ----------
        col = TableHelper.createColumn(table_layout, axes_table, Messages.AxisVisibility, 45, 10);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final AxisConfig axis = (AxisConfig) cell.getElement();
                if (axis.isVisible())
                    cell.setImage(Activator.getDefault().getImage(Activator.ICON_CHECKED));
                else
                    cell.setImage(Activator.getDefault().getImage(Activator.ICON_UNCHECKED));
            }
        });
        col.setEditingSupport(new EditSupportBase(axes_table)
        {
            @Override
            protected CellEditor getCellEditor(final Object element)
            {
                return new CheckboxCellEditor(((TableViewer)getViewer()).getTable());
            }

            @Override
            protected Object getValue(final Object element)
            {
                return ((AxisConfig) element).isVisible();
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                final AxisConfig axis = (AxisConfig)element;
                final ChangeAxisConfigCommand command =
                    new ChangeAxisConfigCommand(operations_manager, axis);
                axis.setVisible(((Boolean)value).booleanValue());
                if (((Boolean)value).booleanValue()) {
                    if (axis.isAutoScale())
                        model.setArchiveRescale(ArchiveRescale.NONE);
                }
                command.rememberNewConfig();
            }
        });

        // Axis Name Column ----------
        col = TableHelper.createColumn(table_layout, axes_table, Messages.ValueAxisName, 100, 100);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final AxisConfig axis = (AxisConfig) cell.getElement();
                cell.setText(axis.getName());
            }
        });
        col.setEditingSupport(new EditSupportBase(axes_table)
        {
            @Override
            protected Object getValue(final Object element)
            {
                return ((AxisConfig) element).getName();
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                final AxisConfig axis = (AxisConfig)element;
                final String name = value.toString().trim();
                if (name.equals(axis.getName()))
                    return;
                final ChangeAxisConfigCommand config =
                    new ChangeAxisConfigCommand(operations_manager, axis);
                axis.setName(name);
                config.rememberNewConfig();
            }
        });

        // Use Axis Name ----------
        col = TableHelper.createColumn(table_layout, axes_table, Messages.UseAxisName, 95, 10);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final AxisConfig axis = (AxisConfig) cell.getElement();
                if (axis.isUsingAxisName())
                    cell.setImage(Activator.getDefault().getImage(Activator.ICON_CHECKED));
                else
                    cell.setImage(Activator.getDefault().getImage(Activator.ICON_UNCHECKED));
            }
        });
        col.setEditingSupport(new EditSupportBase(axes_table)
        {
            @Override
            protected CellEditor getCellEditor(final Object element)
            {
                return new CheckboxCellEditor(((TableViewer)getViewer()).getTable());
            }

            @Override
            protected Object getValue(final Object element)
            {
                return ((AxisConfig) element).isUsingAxisName();
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                final AxisConfig axis = (AxisConfig)element;
                final ChangeAxisConfigCommand command =
                    new ChangeAxisConfigCommand(operations_manager, axis);
                axis.useAxisName(((Boolean)value).booleanValue());
                command.rememberNewConfig();
            }
        });

        // Use Trace Names ----------
        col = TableHelper.createColumn(table_layout, axes_table, Messages.UseTraceNames, 110, 10);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final AxisConfig axis = (AxisConfig) cell.getElement();
                if (axis.isUsingTraceNames())
                    cell.setImage(Activator.getDefault().getImage(Activator.ICON_CHECKED));
                else
                    cell.setImage(Activator.getDefault().getImage(Activator.ICON_UNCHECKED));
            }
        });
        col.setEditingSupport(new EditSupportBase(axes_table)
        {
            @Override
            protected CellEditor getCellEditor(final Object element)
            {
                return new CheckboxCellEditor(((TableViewer)getViewer()).getTable());
            }

            @Override
            protected Object getValue(final Object element)
            {
                return ((AxisConfig) element).isUsingTraceNames();
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                final AxisConfig axis = (AxisConfig)element;
                final ChangeAxisConfigCommand command =
                    new ChangeAxisConfigCommand(operations_manager, axis);
                axis.useTraceNames(((Boolean)value).booleanValue());
                command.rememberNewConfig();
            }
        });

        // Show Grid? ----------
        col = TableHelper.createColumn(table_layout, axes_table, Messages.Grid, 50, 5);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final AxisConfig axis = (AxisConfig) cell.getElement();
                if (axis.isGridVisible())
                    cell.setImage(Activator.getDefault().getImage(Activator.ICON_CHECKED));
                else
                    cell.setImage(Activator.getDefault().getImage(Activator.ICON_UNCHECKED));
            }
        });
        col.setEditingSupport(new EditSupportBase(axes_table)
        {
            @Override
            protected CellEditor getCellEditor(final Object element)
            {
                return new CheckboxCellEditor(((TableViewer)getViewer()).getTable());
            }

            @Override
            protected Object getValue(final Object element)
            {
                return ((AxisConfig) element).isGridVisible();
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                final AxisConfig axis = (AxisConfig)element;
                final ChangeAxisConfigCommand command =
                    new ChangeAxisConfigCommand(operations_manager, axis);
                axis.setGridVisible(((Boolean)value).booleanValue());
                command.rememberNewConfig();
            }
        });

        // Use Right Side? ----------
        col = TableHelper.createColumn(table_layout, axes_table, Messages.AxisOnRight, 80, 10);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final AxisConfig axis = (AxisConfig) cell.getElement();
                if (axis.isOnRight())
                    cell.setImage(Activator.getDefault().getImage(Activator.ICON_CHECKED));
                else
                    cell.setImage(Activator.getDefault().getImage(Activator.ICON_UNCHECKED));
            }
        });
        col.setEditingSupport(new EditSupportBase(axes_table)
        {
            @Override
            protected CellEditor getCellEditor(final Object element)
            {
                return new CheckboxCellEditor(((TableViewer)getViewer()).getTable());
            }

            @Override
            protected Object getValue(final Object element)
            {
                return ((AxisConfig) element).isOnRight();
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                final AxisConfig axis = (AxisConfig)element;
                final ChangeAxisConfigCommand command =
                    new ChangeAxisConfigCommand(operations_manager, axis);
                axis.setOnRight(((Boolean)value).booleanValue());
                command.rememberNewConfig();
            }
        });

        // Color Column ----------
        col = TableHelper.createColumn(table_layout, axes_table, Messages.Color, 40, 5);
        col.setLabelProvider(new ColorCellLabelProvider<AxisConfig>()
        {
            @Override
            protected Color getColor(final AxisConfig axis)
            {
                return color_registry.createColor(axis.getColor());
            }
        });
        col.setEditingSupport(new EditSupportBase(axes_table)
        {
            @Override
            protected CellEditor getCellEditor(final Object element)
            {
                return new RGBCellEditor(axes_table.getTable());
            }

            @Override
            protected Object getValue(final Object element)
            {
                return ((AxisConfig) element).getColor();
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                final AxisConfig axis = (AxisConfig)element;
                final ChangeAxisConfigCommand command =
                    new ChangeAxisConfigCommand(operations_manager, axis);
                axis.setColor((RGB)value);
                command.rememberNewConfig();
            }
        });

        // Minimum value Column ----------
        col = TableHelper.createColumn(table_layout, axes_table, Messages.AxisMin, 70, 50);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final AxisConfig axis = (AxisConfig) cell.getElement();
                cell.setText(Double.toString(axis.getMin()));
            }
        });
        col.setEditingSupport(new EditSupportBase(axes_table)
        {
            @Override
            protected Object getValue(final Object element)
            {
                return Double.toString(((AxisConfig) element).getMin());
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                try
                {
                    final AxisConfig axis = (AxisConfig)element;
                    final double limit = Double.parseDouble(value.toString().trim());
                    if (limit == axis.getMin())
                        return;
                    final ChangeAxisConfigCommand command =
                        new ChangeAxisConfigCommand(operations_manager, axis);
                    axis.setRange(limit, axis.getMax());
                    command.rememberNewConfig();
                }
                catch (NumberFormatException ex)
                {
                    // NOP, leave as is
                }
            }
        });

        // Maximum value Column ----------
        col = TableHelper.createColumn(table_layout, axes_table, Messages.AxisMax, 70, 50);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final AxisConfig axis = (AxisConfig) cell.getElement();
                cell.setText(Double.toString(axis.getMax()));
            }
        });
        col.setEditingSupport(new EditSupportBase(axes_table)
        {
            @Override
            protected Object getValue(final Object element)
            {
                return Double.toString(((AxisConfig) element).getMax());
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                try
                {
                    final AxisConfig axis = (AxisConfig)element;
                    final double limit = Double.parseDouble(value.toString().trim());
                    if (limit == axis.getMax())
                        return;
                    final ChangeAxisConfigCommand command =
                        new ChangeAxisConfigCommand(operations_manager, axis);
                    axis.setRange(axis.getMin(), limit);
                    command.rememberNewConfig();
                }
                catch (NumberFormatException ex)
                {
                    // NOP, leave as is
                }
            }
        });

        // Auto scale Column ----------
        col = TableHelper.createColumn(table_layout, axes_table, Messages.AutoScale, 80, 10);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final AxisConfig axis = (AxisConfig) cell.getElement();
                if (axis.isAutoScale())
                    cell.setImage(Activator.getDefault().getImage(Activator.ICON_CHECKED));
                else
                    cell.setImage(Activator.getDefault().getImage(Activator.ICON_UNCHECKED));
            }
        });
        col.setEditingSupport(new EditSupportBase(axes_table)
        {
            @Override
            protected CellEditor getCellEditor(final Object element)
            {
                return new CheckboxCellEditor(((TableViewer)getViewer()).getTable());
            }

            @Override
            protected Object getValue(final Object element)
            {
                return ((AxisConfig) element).isAutoScale();
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                final AxisConfig axis = (AxisConfig)element;
                final ChangeAxisConfigCommand command =
                    new ChangeAxisConfigCommand(operations_manager, axis);
                axis.setAutoScale(((Boolean)value).booleanValue());
                if (((Boolean) value).booleanValue() && axis.isVisible())
                    model.setArchiveRescale(ArchiveRescale.NONE);
                command.rememberNewConfig();
            }
        });

        // Log scale Column ----------
        col = TableHelper.createColumn(table_layout, axes_table, Messages.LinLogScaleType, 80, 10);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final AxisConfig axis = (AxisConfig) cell.getElement();
                if (axis.isLogScale())
                    cell.setText(Messages.LogScale);
                else
                    cell.setText(Messages.LinacScale);
            }
        });
        col.setEditingSupport(new EditSupportBase(axes_table)
        {
            @Override
            protected CellEditor getCellEditor(final Object element)
            {
                return new CheckboxCellEditor(((TableViewer)getViewer()).getTable());
            }

            @Override
            protected Object getValue(final Object element)
            {
                return ((AxisConfig) element).isLogScale();
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                final AxisConfig axis = (AxisConfig)element;
                final ChangeAxisConfigCommand command =
                    new ChangeAxisConfigCommand(operations_manager, axis);
                axis.setLogScale(((Boolean)value).booleanValue());
                command.rememberNewConfig();
            }
        });

        // Label Font Column ----------
        col = TableHelper.createColumn(table_layout, axes_table, Messages.AxisLabelFont, 40, 5);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final AxisConfig axis = (AxisConfig) cell.getElement();
                cell.setText(axis.getLabelFont().toString());
            }
        });
        col.setEditingSupport(new EditSupportBase(axes_table)
        {
            @Override
            protected CellEditor getCellEditor(final Object element)
            {
                return new FontCellEditor(axes_table.getTable());
            }

            @Override
            protected Object getValue(final Object element)
            {
                return ((AxisConfig) element).getLabelFont();
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                final AxisConfig axis = (AxisConfig)element;
                final ChangeAxisConfigCommand command = new ChangeAxisConfigCommand(operations_manager, axis);
                axis.setLabelFont((FontData) value);
                command.rememberNewConfig();
            }
        });

        // Scale Font Column ----------
        col = TableHelper.createColumn(table_layout, axes_table, Messages.AxisScaleFont, 40, 5);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final AxisConfig axis = (AxisConfig) cell.getElement();
                cell.setText(axis.getScaleFont().toString());
            }
        });
        col.setEditingSupport(new EditSupportBase(axes_table)
        {
            @Override
            protected CellEditor getCellEditor(final Object element)
            {
                return new FontCellEditor(axes_table.getTable());
            }

            @Override
            protected Object getValue(final Object element)
            {
                return ((AxisConfig) element).getScaleFont();
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                final AxisConfig axis = (AxisConfig)element;
                final ChangeAxisConfigCommand command = new ChangeAxisConfigCommand(operations_manager, axis);
                axis.setScaleFont((FontData) value);
                command.rememberNewConfig();
            }
        });


    }

    /** Add context menu to axes_table */
    private void createContextMenu()
    {
        final MenuManager menu = new MenuManager();
        menu.setRemoveAllWhenShown(true);
        menu.addMenuListener(new IMenuListener()
        {
            @Override
            public void menuAboutToShow(IMenuManager manager)
            {
                menu.add(new AddAxisAction(operations_manager, model));
                if (!axes_table.getSelection().isEmpty())
                    menu.add(new DeleteAxesAction(operations_manager, axes_table, model));
                if (model.getEmptyAxis() != null)
                    menu.add(new RemoveUnusedAxesAction(operations_manager, model));
                menu.add(new Separator());
                menu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
            }
        });
        final Table table = axes_table.getTable();
        table.setMenu(menu.createContextMenu(table));
    }

    /** {@inheritDoc} */
    @Override
    public void inputChanged(final Viewer viewer, final Object old_model, final Object new_model)
    {
        if (old_model != null)
            ((Model)old_model).removeListener(model_listener);

        model = (Model) new_model;
        if (model != null)
            model.addListener(model_listener);
        getModelAxisCopy(model);
    }

    private void getModelAxisCopy(final Model model)
    {
        if (model == null)
        {
            axes = new AxisConfig[0];
            return;
        }
        final List<AxisConfig> new_axes = new ArrayList<>();
        for (AxisConfig axis : model.getAxes())
            new_axes.add(axis);
        axes = new_axes.toArray(new AxisConfig[new_axes.size()]);
    }

    /** {@inheritDoc} */
    @Override
    public Object[] getElements(final Object inputElement)
    {
        return axes;
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        // NOP
    }}
