/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.common.trendplotter.propsheet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.csstudio.common.trendplotter.Activator;
import org.csstudio.common.trendplotter.Messages;
import org.csstudio.common.trendplotter.model.AxisConfig;
import org.csstudio.common.trendplotter.model.Model;
import org.csstudio.common.trendplotter.model.ModelItem;
import org.csstudio.common.trendplotter.model.PVItem;
import org.csstudio.common.trendplotter.model.RequestType;
import org.csstudio.common.trendplotter.model.TraceType;
import org.csstudio.common.trendplotter.ui.TableHelper;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.swt.xygraph.util.XYGraphMediaFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;

/**
 * TODO (jhatje) : 
 * 
 * @author jhatje
 * @since 09.05.2012
 */
public class BulkEditValueDialog extends Dialog {
    
    private final OperationsManager _operations_manager;
    private final BulkEditValueDataModel _bulkModel;
    private TableViewer _bulkEditTable;
    private final Model _model;
    final private XYGraphMediaFactory color_registry = XYGraphMediaFactory.getInstance();
    final private String VISIBLE = "visible";
    final private String NOT_VISIBLE = "not visible";
    final private String AUTOSCALE = "autoscale";
    final private String NOT_AUTOSCALE = "not autoscale";
    final private String LOGSCALE = "log scale";
    final private String NOT_LOGSCALE = "not log scale";
    
    /**
     * Constructor.
     * @param shell
     * @param pvs
     * @param operations_manager 
     */
    public BulkEditValueDialog(Shell shell,
                               OperationsManager operations_manager,
                               BulkEditValueDataModel bulkModel,
                               Model model) {
        super(shell);
        _operations_manager = operations_manager;
        _bulkModel = bulkModel;
        _model = model;
    }
    
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Bulk Editing Dialog");
    }
    
    @Override
    protected Control createDialogArea(Composite parent) {
        final Composite area = (Composite) super.createDialogArea(parent);
        
        final TableColumnLayout table_layout = new TableColumnLayout();
        area.setLayout(table_layout);
        _bulkEditTable = new TableViewer(area, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI
                | SWT.FULL_SELECTION | SWT.VIRTUAL);
        final Table table = _bulkEditTable.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        
        createColumns(area, _bulkEditTable, table_layout);
        
        _bulkEditTable.setContentProvider(new IStructuredContentProvider() {
            
            @Override
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
                // TODO Auto-generated method stub
            }
            
            @Override
            public void dispose() {
                // TODO Auto-generated method stub
            }
            
            @Override
            public Object[] getElements(Object inputElement) {
                BulkEditValueDataModel[] model = new BulkEditValueDataModel[1];
                model[0] = (BulkEditValueDataModel) inputElement;
                return model;
            }
        });
        _bulkEditTable.setInput(_bulkModel);
        return area;
    }
    
    /**
     * @param area
     * @param bulkEditTable
     * @param table_layout 
     */
    private void createColumns(Composite area,
                               TableViewer bulkEditTable,
                               TableColumnLayout table_layout) {
        
        TableViewerColumn col;

        // Visible? Column ----------
        col = TableHelper.createColumn(table_layout, bulkEditTable, Messages.AxisVisibility, 80, 10);
        col.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(final ViewerCell cell) {
                final BulkEditValueDataModel model = (BulkEditValueDataModel) cell.getElement();
                if (model.getVisible() == null) {
                    cell.setText("");
                } else if (model.getVisible() == true) {
                    cell.setText(VISIBLE);
                } else {
                    cell.setText(NOT_VISIBLE);
                }
            }
        });
        col.setEditingSupport(new EditSupportBase(_bulkEditTable) {
            @Override
            protected CellEditor getCellEditor(final Object element) {
                final String visibleOptions[] = new String[3];
                visibleOptions[0] = "";
                visibleOptions[1] = VISIBLE;
                visibleOptions[2] = NOT_VISIBLE;
                final ComboBoxCellEditor combo = new ComboBoxCellEditor(_bulkEditTable.getTable(),
                                                                        visibleOptions,
                                                                        SWT.READ_ONLY);
                combo.setValue(getValue(element));
                return combo;
            }

            @Override
            protected Object getValue(final Object element) {
                BulkEditValueDataModel model = (BulkEditValueDataModel) element;
                if (model.getVisible() == null) {
                    return 0;
                } else if (model.getVisible() == true) {
                    return 1;
                } else {
                    return 2;
                }
            }

            @Override
            protected void setValue(final Object element, final Object value) {
                final int comboIndex = ((Integer) value).intValue();
                _bulkModel.setVisibleChanged(true);
                if (comboIndex == 0) {
                    _bulkModel.setVisible(null);
                    return;
                }
                if (comboIndex == 1) {
                    _bulkModel.setVisible(true);
                    return;
                }
                if (comboIndex == 2) {
                    _bulkModel.setVisible(false);
                    return;
                }
            }
        });
        
        // Axis Name Column ----------
        col = TableHelper.createColumn(table_layout, _bulkEditTable, Messages.ValueAxisName, 100, 100);
        col.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(final ViewerCell cell) {
                final BulkEditValueDataModel model = (BulkEditValueDataModel) cell.getElement();
                cell.setText(model.getAxisName());
            }
        });
        col.setEditingSupport(new EditSupportBase(_bulkEditTable) {
            @Override
            protected Object getValue(final Object element) {
                return ((BulkEditValueDataModel) element).getAxisName();
            }

            @Override
            protected void setValue(final Object element, final Object value) {
                _bulkModel.setAxisNameChanged(true);
                ((BulkEditValueDataModel) element).setAxisName((String) value);
                _bulkEditTable.refresh(element);
            }
        });

        // Minimum value Column ----------
        col = TableHelper.createColumn(table_layout, _bulkEditTable, Messages.AxisMin, 80, 100);
        col.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(final ViewerCell cell) {
                final BulkEditValueDataModel model = (BulkEditValueDataModel) cell.getElement();
                cell.setText(model.getMin());
            }
        });
        col.setEditingSupport(new EditSupportBase(bulkEditTable) {
            @Override
            protected Object getValue(final Object element) {
                return ((BulkEditValueDataModel) element).getMin();
            }

            @Override
            protected void setValue(final Object element, final Object value) {
                _bulkModel.setMinChanged(true);
                final BulkEditValueDataModel model = (BulkEditValueDataModel) element;
                model.setMin((String) value);
                _bulkEditTable.refresh();
            }
        });

        // Maximum value Column ----------
        col = TableHelper.createColumn(table_layout, _bulkEditTable, Messages.AxisMax, 80, 100);
        col.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(final ViewerCell cell) {
                final BulkEditValueDataModel model = (BulkEditValueDataModel) cell.getElement();
                cell.setText(model.getMax());
            }
        });
        col.setEditingSupport(new EditSupportBase(_bulkEditTable) {
            @Override
            protected Object getValue(final Object element) {
                return ((BulkEditValueDataModel) element).getMax();
            }

            @Override
            protected void setValue(final Object element, final Object value) {
                _bulkModel.setMaxChanged(true);
                final BulkEditValueDataModel model = (BulkEditValueDataModel) element;
                model.setMax((String) value);
                _bulkEditTable.refresh();
            }
        });
        
        // Auto scale Column ----------
        col = TableHelper.createColumn(table_layout, bulkEditTable, Messages.AxisVisibility, 80, 10);
        col.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(final ViewerCell cell) {
                final BulkEditValueDataModel model = (BulkEditValueDataModel) cell.getElement();
                if (model.getAutoScale() == null) {
                    cell.setText("");
                } else if (model.getAutoScale() == true) {
                    cell.setText(AUTOSCALE);
                } else {
                    cell.setText(NOT_AUTOSCALE);
                }
            }
        });
        col.setEditingSupport(new EditSupportBase(_bulkEditTable) {
            @Override
            protected CellEditor getCellEditor(final Object element) {
                final String visibleOptions[] = new String[3];
                visibleOptions[0] = "";
                visibleOptions[1] = AUTOSCALE;
                visibleOptions[2] = NOT_AUTOSCALE;
                final ComboBoxCellEditor combo = new ComboBoxCellEditor(_bulkEditTable.getTable(),
                                                                        visibleOptions,
                                                                        SWT.READ_ONLY);
                combo.setValue(getValue(element));
                return combo;
            }

            @Override
            protected Object getValue(final Object element) {
                BulkEditValueDataModel model = (BulkEditValueDataModel) element;
                if (model.getAutoScale() == null) {
                    return 0;
                } else if (model.getAutoScale() == true) {
                    return 1;
                } else {
                    return 2;
                }
            }

            @Override
            protected void setValue(final Object element, final Object value) {
                final int comboIndex = ((Integer) value).intValue();
                _bulkModel.setAutoScaleChanged(true);
                if (comboIndex == 0) {
                    _bulkModel.setAutoScale(null);
                    return;
                }
                if (comboIndex == 1) {
                    _bulkModel.setAutoScale(true);
                    return;
                }
                if (comboIndex == 2) {
                    _bulkModel.setAutoScale(false);
                    return;
                }
            }
        });

        // Log scale Column ----------
        col = TableHelper.createColumn(table_layout, bulkEditTable, Messages.AxisVisibility, 80, 10);
        col.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(final ViewerCell cell) {
                final BulkEditValueDataModel model = (BulkEditValueDataModel) cell.getElement();
                if (model.getLogScale() == null) {
                    cell.setText("");
                } else if (model.getLogScale() == true) {
                    cell.setText(LOGSCALE);
                } else {
                    cell.setText(NOT_LOGSCALE);
                }
            }
        });
        col.setEditingSupport(new EditSupportBase(_bulkEditTable) {
            @Override
            protected CellEditor getCellEditor(final Object element) {
                final String visibleOptions[] = new String[3];
                visibleOptions[0] = "";
                visibleOptions[1] = LOGSCALE;
                visibleOptions[2] = NOT_LOGSCALE;
                final ComboBoxCellEditor combo = new ComboBoxCellEditor(_bulkEditTable.getTable(),
                                                                        visibleOptions,
                                                                        SWT.READ_ONLY);
                combo.setValue(getValue(element));
                return combo;
            }
            
            @Override
            protected Object getValue(final Object element) {
                BulkEditValueDataModel model = (BulkEditValueDataModel) element;
                if (model.getLogScale() == null) {
                    return 0;
                } else if (model.getLogScale() == true) {
                    return 1;
                } else {
                    return 2;
                }
            }
            
            @Override
            protected void setValue(final Object element, final Object value) {
                final int comboIndex = ((Integer) value).intValue();
                _bulkModel.setLogScaleChanged(true);
                if (comboIndex == 0) {
                    _bulkModel.setLogScale(null);
                    return;
                }
                if (comboIndex == 1) {
                    _bulkModel.setLogScale(true);
                    return;
                }
                if (comboIndex == 2) {
                    _bulkModel.setLogScale(false);
                    return;
                }
            }
        });
    }
}
