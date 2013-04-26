/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.propsheet;

import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.swt.xygraph.undo.XYGraphMemento;
import org.csstudio.swt.xygraph.util.XYGraphMediaFactory;
import org.csstudio.trends.sscan.Activator;
import org.csstudio.trends.sscan.Messages;
import org.csstudio.trends.sscan.model.AxesConfig;
import org.csstudio.trends.sscan.model.AxisConfig;
import org.csstudio.trends.sscan.model.Model;
import org.csstudio.trends.sscan.model.ModelItem;
import org.csstudio.trends.sscan.model.ModelListener;
import org.csstudio.trends.sscan.ui.TableHelper;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

/** Helper for a 'Axes' TableViewer that handles the Model's axes.
 *  Each 'row' in the table is an AxisConfig.
 *  @author Kay Kasemir
 */
public class AxesTableHandler implements ILazyContentProvider
{
	final private XYGraphMediaFactory color_registry = XYGraphMediaFactory.getInstance();
    final private OperationsManager operations_manager;
    final private TableViewer axes_table;
    private Model model;

    /** Listen to model changes regarding axes.
     *  Ignore configuration of individual items.
     */
    final private ModelListener model_listener = new ModelListener()
    {
        @Override
        public void changedColors()                      { /* NOP */ }

        @Override
        public void changedAxis(AxisConfig axis)
        {
            if (axis != null)
            {
                axes_table.refresh(axis);
                return;
            }
            // Force total refresh
            axes_table.setItemCount(model.getAxesCount());
            axes_table.refresh();
        }
        
 //       @Override
 //       public void changedAxes(AxesConfig axes)
 //       { 
 //       	if (axes != null)
 //           {
 //               axes_table.refresh(axes);
 //               return;
 //           }
            // Force total refresh
 //           axes_table.setItemCount(model.getAxesCount());
 //           axes_table.refresh();
 //       }

        @Override
        public void itemAdded(ModelItem item)            { /* NOP */ }
        @Override
        public void itemRemoved(ModelItem item)          { /* NOP */ }
        @Override
        public void changedItemVisibility(ModelItem item){ /* NOP */ }
        @Override
        public void changedItemLook(ModelItem item)      { /* NOP */ }

        @Override
        public void changedItemDataConfig(ModelItem item)   { /* NOP */ }
	
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
			// TODO Auto-generated method stub
			
		}
    };

    /** Initialize
     *  @param parent
     *  @param table_layout
     *  @param operations_manager
     * @wbp.parser.entryPoint
     */
    public AxesTableHandler(final Composite parent,
            final TableColumnLayout table_layout, final OperationsManager operations_manager)
    {
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
     * @wbp.parser.entryPoint
     */
    private void createColumns(TableColumnLayout table_layout)
    {
        TableViewerColumn col;

        // Visible? Column ----------
        col = TableHelper.createColumn(table_layout, axes_table, Messages.AxisVisibility, 40, 10);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final AxesConfig axes = (AxesConfig) cell.getElement();
                if (axes.getXAxis().isVisible()||axes.getYAxis().isVisible())
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
                return ((AxesConfig) element).getXAxis().isVisible() && ((AxesConfig) element).getYAxis().isVisible();
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                try
                {
                    final AxesConfig axes = (AxesConfig)element;
                    final AxisConfig xaxis = axes.getXAxis();
                    final ChangeAxisConfigCommand xcommand =
                        new ChangeAxisConfigCommand(operations_manager, xaxis);
                    xaxis.setVisible(((Boolean)value).booleanValue());
                    
                    final AxisConfig yaxis = axes.getYAxis();
                    final ChangeAxisConfigCommand ycommand =
                        new ChangeAxisConfigCommand(operations_manager, yaxis);
                    yaxis.setVisible(((Boolean)value).booleanValue());
                    
                    xcommand.rememberNewConfig();
                    ycommand.rememberNewConfig();
                }
                catch (NumberFormatException ex)
                {
                    // NOP, leave as is
                }
            }
        });

        // X Axis Name Column ----------
        col = TableHelper.createColumn(table_layout, axes_table, Messages.ValueXAxisName, 100, 100);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final AxesConfig axes = (AxesConfig) cell.getElement();
                cell.setText(axes.getXAxis().getName());
            }
        });
        col.setEditingSupport(new EditSupportBase(axes_table)
        {
            @Override
            protected Object getValue(final Object element)
            {
                return ((AxesConfig) element).getXAxis().getName();
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                final AxesConfig axes = (AxesConfig)element;
                final String name = value.toString().trim();
                if (name.equals(axes.getXAxis().getName()))
                    return;
                final ChangeAxesConfigCommand config =
                    new ChangeAxesConfigCommand(operations_manager, axes);
                axes.getXAxis().setName(name);
                config.rememberNewConfig();
            }
        });
        
        // Y Axis Name Column ----------
        col = TableHelper.createColumn(table_layout, axes_table, Messages.ValueYAxisName, 100, 100);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final AxesConfig axes = (AxesConfig) cell.getElement();
                cell.setText(axes.getYAxis().getName());
            }
        });
        col.setEditingSupport(new EditSupportBase(axes_table)
        {
            @Override
            protected Object getValue(final Object element)
            {
                return ((AxesConfig) element).getYAxis().getName();
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                final AxesConfig axes = (AxesConfig)element;
                final String name = value.toString().trim();
                if (name.equals(axes.getYAxis().getName()))
                    return;
                final ChangeAxesConfigCommand config =
                    new ChangeAxesConfigCommand(operations_manager, axes);
                axes.getYAxis().setName(name);
                config.rememberNewConfig();
            }
        });
        // Color Column ----------
        col = TableHelper.createColumn(table_layout, axes_table, Messages.Color, 40, 5);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final AxesConfig axes = (AxesConfig) cell.getElement();
                cell.setBackground(color_registry.getColor(axes.getXAxis().getColor()));
                cell.setBackground(color_registry.getColor(axes.getYAxis().getColor()));
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
                return ((AxesConfig) element).getXAxis().getColor();
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                final AxesConfig axes = (AxesConfig)element;
                final ChangeAxesConfigCommand command =
                    new ChangeAxesConfigCommand(operations_manager, axes);
                axes.getXAxis().setColor((RGB)value);
                axes.getYAxis().setColor((RGB)value);
                command.rememberNewConfig();
            }
        });

        // Minimum x Column ----------
        col = TableHelper.createColumn(table_layout, axes_table, Messages.XAxisMin, 80, 100);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final AxesConfig axes = (AxesConfig) cell.getElement();
                cell.setText(Double.toString(axes.getXAxis().getMin()));
            }
        });
        col.setEditingSupport(new EditSupportBase(axes_table)
        {
            @Override
            protected Object getValue(final Object element)
            {
                return Double.toString(((AxesConfig) element).getXAxis().getMin());
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                try
                {
                    final AxesConfig axes = (AxesConfig)element;
                    final double limit = Double.parseDouble(value.toString().trim());
                    if (limit == axes.getXAxis().getMin())
                        return;
                    final ChangeAxesConfigCommand command =
                        new ChangeAxesConfigCommand(operations_manager, axes);
                    AxisConfig axis = axes.getXAxis();
                    axis.setRange(limit, axes.getXAxis().getMax());
                    command.rememberNewConfig();
                }
                catch (NumberFormatException ex)
                {
                    // NOP, leave as is
                }
            }
        });

        // Maximum x Column ----------
        col = TableHelper.createColumn(table_layout, axes_table, Messages.XAxisMax, 80, 100);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final AxesConfig axes = (AxesConfig) cell.getElement();
                cell.setText(Double.toString(axes.getXAxis().getMax()));
            }
        });
        col.setEditingSupport(new EditSupportBase(axes_table)
        {
            @Override
            protected Object getValue(final Object element)
            {
                return Double.toString(((AxesConfig) element).getXAxis().getMax());
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                try
                {
                    final AxesConfig axes = (AxesConfig)element;
                    final double limit = Double.parseDouble(value.toString().trim());
                    if (limit == axes.getXAxis().getMax())
                        return;
                    final ChangeAxesConfigCommand command =
                        new ChangeAxesConfigCommand(operations_manager, axes);
                    axes.getXAxis().setRange(axes.getXAxis().getMin(), limit);
                    command.rememberNewConfig();
                }
                catch (NumberFormatException ex)
                {
                    // NOP, leave as is
                }
            }
        });
        
        // Minimum value Column ----------
        col = TableHelper.createColumn(table_layout, axes_table, Messages.YAxisMin, 80, 100);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final AxesConfig axes = (AxesConfig) cell.getElement();
                cell.setText(Double.toString(axes.getYAxis().getMin()));
            }
        });
        col.setEditingSupport(new EditSupportBase(axes_table)
        {
            @Override
            protected Object getValue(final Object element)
            {
                return Double.toString(((AxesConfig) element).getYAxis().getMin());
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                try
                {
                    final AxesConfig axes = (AxesConfig)element;
                    final double limit = Double.parseDouble(value.toString().trim());
                    if (limit == axes.getYAxis().getMin())
                        return;
                    final ChangeAxesConfigCommand command =
                        new ChangeAxesConfigCommand(operations_manager, axes);
                    axes.getYAxis().setRange(limit, axes.getYAxis().getMax());
                    command.rememberNewConfig();
                }
                catch (NumberFormatException ex)
                {
                    // NOP, leave as is
                }
            }
        });

        // Maximum value Column ----------
        col = TableHelper.createColumn(table_layout, axes_table, Messages.YAxisMax, 80, 100);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final AxesConfig axis = (AxesConfig) cell.getElement();
                cell.setText(Double.toString(axis.getYAxis().getMax()));
            }
        });
        col.setEditingSupport(new EditSupportBase(axes_table)
        {
            @Override
            protected Object getValue(final Object element)
            {
                return Double.toString(((AxesConfig) element).getYAxis().getMax());
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                try
                {
                    final AxesConfig axes = (AxesConfig)element;
                    final double limit = Double.parseDouble(value.toString().trim());
                    if (limit == axes.getYAxis().getMax())
                        return;
                    final ChangeAxesConfigCommand command =
                        new ChangeAxesConfigCommand(operations_manager, axes);
                    axes.getYAxis().setRange(axes.getYAxis().getMin(), limit);
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
                final AxesConfig axes = (AxesConfig) cell.getElement();
                if (axes.getXAxis().isAutoScale()&&axes.getYAxis().isAutoScale())
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
                return ((AxesConfig) element).getXAxis().isAutoScale() && ((AxesConfig) element).getYAxis().isAutoScale();
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                try
                {
                    final AxesConfig axes = (AxesConfig)element;
                    final ChangeAxesConfigCommand command =
                        new ChangeAxesConfigCommand(operations_manager, axes);
                    axes.getXAxis().setAutoScale(((Boolean)value).booleanValue());
                    axes.getYAxis().setAutoScale(((Boolean)value).booleanValue());
                    command.rememberNewConfig();
                }
                catch (NumberFormatException ex)
                {
                    // NOP, leave as is
                }
            }
        });

        // Log scale Column ----------
        col = TableHelper.createColumn(table_layout, axes_table, Messages.LinLogScaleType, 80, 10);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final AxesConfig axes = (AxesConfig) cell.getElement();
                if (axes.getXAxis().isLogScale()&&axes.getYAxis().isLogScale())
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
                return ((AxesConfig) element).getXAxis().isLogScale()&&((AxesConfig) element).getXAxis().isLogScale();
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                try
                {
                    final AxesConfig axes = (AxesConfig)element;
                    final ChangeAxesConfigCommand command =
                        new ChangeAxesConfigCommand(operations_manager, axes);
                    axes.getXAxis().setLogScale(((Boolean)value).booleanValue());
                    axes.getYAxis().setLogScale(((Boolean)value).booleanValue());
                    command.rememberNewConfig();
                }
                catch (NumberFormatException ex)
                {
                    // NOP, leave as is
                }
            }
        });
    }

    /** Add context menu to axes_table 
     * @wbp.parser.entryPoint*/
    private void createContextMenu()
    {
        final MenuManager menu = new MenuManager();
        menu.setRemoveAllWhenShown(true);
        menu.addMenuListener(new IMenuListener()
        {
            @Override
            public void menuAboutToShow(IMenuManager manager)
            {
                menu.add(new AddAxesAction(operations_manager, model));
                if (!axes_table.getSelection().isEmpty())
                    menu.add(new DeleteAxesAction(operations_manager, axes_table, model));
                if (model.getEmptyAxes() != null)
                    menu.add(new RemoveUnusedAxesAction(operations_manager, model));
            }
        });
        final Table table = axes_table.getTable();
        table.setMenu(menu.createContextMenu(table));
    }

    /** Set input to a Model
     *  @see ILazyContentProvider#inputChanged(Viewer, Object, Object)
     */
    @Override
    public void inputChanged(final Viewer viewer, final Object old_model, final Object new_model)
    {
        if (old_model != null)
            ((Model)old_model).removeListener(model_listener);

        model = (Model) new_model;
        if (model == null)
            return;

        axes_table.setItemCount(model.getAxesCount());
        model.addListener(model_listener);
    }

    /** Called by ILazyContentProvider to get the ModelItem for a table row
     *  {@inheritDoc}
     */
    @Override
    public void updateElement(int index)
    {
        axes_table.replace(model.getAxes(index), index);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        // NOP
    }}
