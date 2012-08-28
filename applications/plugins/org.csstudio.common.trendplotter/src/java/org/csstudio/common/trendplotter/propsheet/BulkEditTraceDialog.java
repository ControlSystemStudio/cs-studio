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
public class BulkEditTraceDialog extends Dialog {
    
    private final OperationsManager _operations_manager;
    private final BulkEditTraceDataModel _bulkModel;
    private TableViewer _bulkEditTable;
    private final Model _model;
    final private XYGraphMediaFactory color_registry = XYGraphMediaFactory.getInstance();
    final private String VISIBLE = "visible";
    final private String NOT_VISIBLE = "not visible";
    
    /**
     * Constructor.
     * @param shell
     * @param pvs
     * @param operations_manager 
     */
    public BulkEditTraceDialog(Shell shell,
                               OperationsManager operations_manager,
                               BulkEditTraceDataModel bulkModel,
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
                BulkEditTraceDataModel[] model = new BulkEditTraceDataModel[1];
                model[0] = (BulkEditTraceDataModel) inputElement;
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
        
        // Visible Column ----------
        TableViewerColumn view_col = TableHelper.createColumn(table_layout,
                                                              _bulkEditTable,
                                                              Messages.TraceVisibility,
                                                              60,
                                                              1);
        view_col.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(final ViewerCell cell) {
                final BulkEditTraceDataModel model = (BulkEditTraceDataModel) cell.getElement();
                if (model.getVisible() == null) {
                    cell.setText("");
                } else if (model.getVisible() == true) {
                    cell.setText(VISIBLE);
                } else {
                    cell.setText(NOT_VISIBLE);
                }
            }
            
            @Override
            public String getToolTipText(Object element) {
                return Messages.TraceVisibilityTT;
            }
        });
        view_col.setEditingSupport(new EditSupportBase(_bulkEditTable) {
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
                BulkEditTraceDataModel model = (BulkEditTraceDataModel) element;
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
        
        // Display Name Column ----------
        view_col = TableHelper.createColumn(table_layout,
                                                              bulkEditTable,
                                                              Messages.TraceDisplayName,
                                                              100,
                                                              100);
        view_col.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(final ViewerCell cell) {
                BulkEditTraceDataModel model = (BulkEditTraceDataModel) cell.getElement();
                cell.setText(model.getTraceDisplayName());
            }
            
            @Override
            public String getToolTipText(Object element) {
                return Messages.TraceDisplayNameTT;
            }
        });
        view_col.setEditingSupport(new EditSupportBase(bulkEditTable) {
            @Override
            protected Object getValue(final Object element) {
                return ((BulkEditTraceDataModel) element).getTraceDisplayName();
            }
            
            @Override
            protected void setValue(final Object element, final Object value) {
                _bulkModel.setTraceDisplayNameChanged(true);
                ((BulkEditTraceDataModel) element).setTraceDisplayName((String) value);
                _bulkEditTable.refresh(element);
            }
        });
        
        //        // Color Column ----------
        //        view_col = TableHelper.createColumn(table_layout, _bulkEditTable, Messages.Color, 40, 10);
        //        view_col.setLabelProvider(new CellLabelProvider() {
        //            @Override
        //            public void update(final ViewerCell cell) {
        //                final BulkEditDataModel model = (BulkEditDataModel) cell.getElement();
        //                cell.setBackground(color_registry.getColor(model.getRgb()));
        //            }
        //
        //            @Override
        //            public String getToolTipText(Object element) {
        //                return Messages.ColorTT;
        //            }
        //        });
        //        view_col.setEditingSupport(new EditSupportBase(_bulkEditTable) {
        //            @Override
        //            protected CellEditor getCellEditor(final Object element) {
        //                return new RGBCellEditor(_bulkEditTable.getTable());
        //            }
        //
        //            @Override
        //            protected Object getValue(final Object element) {
        //                return ((BulkEditDataModel) element).getRgb();
        //            }
        //
        //            @Override
        //            protected void setValue(final Object element, final Object value) {
        //                new ChangeColorCommand(operations_manager,
        //                        (ModelItem) element, (RGB)value);
        //            }
        //        });
        
        // Scan Period Column (only applies to PVItems) ----------
        view_col = TableHelper.createColumn(table_layout,
                                            bulkEditTable,
                                            Messages.ScanPeriod,
                                            70,
                                            10);
        view_col.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(final ViewerCell cell) {
                final BulkEditTraceDataModel model = (BulkEditTraceDataModel) cell.getElement();
                if (model.getScanPeriod() == -1) {
                    cell.setText("");
                } else {
                    cell.setText(Double.toString(model.getScanPeriod()));
                }
            }
            
            @Override
            public String getToolTipText(final Object element) {
                return Messages.ScanPeriodTT;
            }
            
        });
        view_col.setEditingSupport(new EditSupportBase(bulkEditTable) {
            
            @Override
            protected Object getValue(final Object element) {
                final BulkEditTraceDataModel model = (BulkEditTraceDataModel) element;
                if (model.getScanPeriod() == -1) {
                    return "";
                } else {
                    return Double.toString(model.getScanPeriod());
                }
            }
            
            @Override
            protected void setValue(final Object element, final Object value) {
                try {
                    Double doubleValue = Double.valueOf((String) value);
                    _bulkModel.setScanPeriodChanged(true);
                    BulkEditTraceDataModel model = (BulkEditTraceDataModel) element;
                    model.setScanPeriod(doubleValue);
                    _bulkEditTable.refresh(element);
                } catch (NumberFormatException ex) {
                    // Display will revert to original value
                    return;
                }
            }
        });
        
        // Buffer size Column (only applies to PVItems) ----------
        view_col = TableHelper.createColumn(table_layout,
                                            bulkEditTable,
                                            Messages.LiveSampleBufferSize,
                                            70,
                                            10);
        view_col.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(final ViewerCell cell) {
                final BulkEditTraceDataModel model = (BulkEditTraceDataModel) cell.getElement();
                if (model.getLiveCapacity() == -1) {
                    cell.setText("");
                } else {
                    cell.setText(Integer.toString(model.getLiveCapacity()));
                }
            }
            
            @Override
            public String getToolTipText(final Object element) {
                return Messages.LiveSampleBufferSize;
            }
            
        });
        view_col.setEditingSupport(new EditSupportBase(bulkEditTable) {
            
            @Override
            protected Object getValue(final Object element) {
                final BulkEditTraceDataModel model = (BulkEditTraceDataModel) element;
                if (model.getScanPeriod() == -1) {
                    return "";
                } else {
                    return Double.toString(model.getScanPeriod());
                }
            }
            
            @Override
            protected void setValue(final Object element, final Object value) {
                try {
                    Integer liveCapacity = Integer.valueOf((String) value);
                    _bulkModel.setLiveCapacityChanged(true);
                    BulkEditTraceDataModel model = (BulkEditTraceDataModel) element;
                    model.setLiveCapacity(liveCapacity);
                    _bulkEditTable.refresh(element);
                } catch (NumberFormatException ex) {
                    // Display will revert to original value
                    return;
                }
            }
        });
        
        // Line Width Column ----------
        view_col = TableHelper.createColumn(table_layout,
                                            _bulkEditTable,
                                            Messages.TraceLineWidth,
                                            40,
                                            10);
        view_col.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(final ViewerCell cell) {
                final BulkEditTraceDataModel model = (BulkEditTraceDataModel) cell.getElement();
                if (model.getLineWidth() == -1) {
                    cell.setText("");
                } else {
                    cell.setText(Integer.toString(model.getLineWidth()));
                }
            }
            
            @Override
            public String getToolTipText(Object element) {
                return Messages.TraceLineWidthTT;
            }
            
        });
        view_col.setEditingSupport(new EditSupportBase(_bulkEditTable) {
            @Override
            protected Object getValue(final Object element) {
                final BulkEditTraceDataModel model = (BulkEditTraceDataModel) element;
                if (model.getLineWidth() == -1) {
                    return "";
                } else {
                    return Integer.toString(model.getLineWidth());
                }
            }
            
            @Override
            protected void setValue(final Object element, final Object value) {
                try {
                    Integer lineWidth = Integer.valueOf((String) value);
                    _bulkModel.setLineWidthChanged(true);
                    BulkEditTraceDataModel model = (BulkEditTraceDataModel) element;
                    model.setLineWidth(lineWidth);
                    _bulkEditTable.refresh(element);
                } catch (NumberFormatException ex) {
                    // Display will revert to original value
                    return;
                }
            }
        });
        
        // Axis Column ----------
        view_col = TableHelper.createColumn(table_layout, _bulkEditTable, Messages.Axis, 60, 30);
        view_col.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(final ViewerCell cell) {
                final BulkEditTraceDataModel model = (BulkEditTraceDataModel) cell.getElement();
                if (model.getAxis() != null) {
                    cell.setText(model.getAxis().getName());
                } else {
                    cell.setText("");
                }
            }
            
            @Override
            public String getToolTipText(Object element) {
                return Messages.AxisTT;
            }
        });
        view_col.setEditingSupport(new EditSupportBase(_bulkEditTable) {
            @Override
            protected CellEditor getCellEditor(final Object element) {
                final String axis_names[] = new String[_model.getAxisCount() + 1];
                axis_names[0] = "";
                for (int i = 1; i < axis_names.length; ++i)
                    axis_names[i] = _model.getAxis(i - 1).getName();
                final ComboBoxCellEditor combo = new ComboBoxCellEditor(_bulkEditTable.getTable(),
                                                                        axis_names,
                                                                        SWT.READ_ONLY);
                combo.setValue(getValue(element));
                return combo;
            }
            
            @Override
            protected Object getValue(final Object element) {
                AxisConfig axis = ((BulkEditTraceDataModel) element).getAxis();
                if (axis == null) {
                    return (int) 0;
                }
                int axisIndex = _model.getAxisIndex(axis);
                return axisIndex + 1;
            }
            
            @Override
            protected void setValue(final Object element, final Object value) {
                _bulkModel.setAxisChanged(true);
                final int axis_index = ((Integer) value).intValue();
                if (axis_index == 0) {
                    _bulkModel.setAxis(null);
                    return;
                }
                final AxisConfig axis = _model.getAxis(axis_index - 1);
                _bulkModel.setAxis(axis);
            }
        });
        
        // Trace Type Column ----------
        view_col = TableHelper.createColumn(table_layout,
                                            _bulkEditTable,
                                            Messages.TraceType,
                                            75,
                                            10);
        view_col.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(final ViewerCell cell) {
                final BulkEditTraceDataModel model = (BulkEditTraceDataModel) cell.getElement();
                if (model.getTraceType() != null) {
                    cell.setText(model.getTraceType().toString());
                } else {
                    cell.setText("");
                }
            }
            
            @Override
            public String getToolTipText(Object element) {
                return Messages.TraceTypeTT;
            }
        });
        view_col.setEditingSupport(new EditSupportBase(_bulkEditTable) {
            @Override
            protected CellEditor getCellEditor(final Object element) {
                List<String> dispNames = new ArrayList<String>();
                Collections.addAll(dispNames, "");
                Collections.addAll(dispNames, TraceType.getDisplayNames());
                final ComboBoxCellEditor combo = new ComboBoxCellEditor(_bulkEditTable.getTable(),
                                                                        dispNames
                                                                                .toArray(new String[dispNames
                                                                                        .size()]),
                                                                        SWT.READ_ONLY);
                combo.setValue(getValue(element));
                return combo;
            }
            
            @Override
            protected Object getValue(final Object element) {
                TraceType traceType = ((BulkEditTraceDataModel) element).getTraceType();
                if (traceType == null) {
                    return (int) 0;
                }
                return traceType.ordinal() + 1;
            }
            
            @Override
            protected void setValue(final Object element, final Object value) {
                _bulkModel.setTraceTypeChanged(true);
                int traceIndex = ((Integer) value).intValue();
                if (traceIndex == 0) {
                    _bulkModel.setTraceType(null);
                } else {
                    _bulkModel.setTraceType(TraceType.fromOrdinal(traceIndex - 1));
                }
            }
        });
        
        // Request Type Column ----------
        view_col = TableHelper.createColumn(table_layout,
                                            _bulkEditTable,
                                            Messages.RequestType,
                                            75,
                                            10);
        view_col.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(final ViewerCell cell) {
                final BulkEditTraceDataModel model = (BulkEditTraceDataModel) cell.getElement();
                if (model.getRequestType() == null) {
                    cell.setText("");
                } else {
                    cell.setText(model.getRequestType().name());
                }
            }
            
            @Override
            public String getToolTipText(Object element) {
                return Messages.RequestTypeTT;
            }
        });
        
        view_col.setEditingSupport(new EditSupportBase(_bulkEditTable) {
            @Override
            protected CellEditor getCellEditor(final Object element) {
                List<String> reqTypes = new ArrayList<String>();
                reqTypes.add("");
                RequestType[] requestTypes = RequestType.values();
                for (RequestType reqType : requestTypes) {
                    reqTypes.add(reqType.name());
                }
                final ComboBoxCellEditor combo = new ComboBoxCellEditor(_bulkEditTable.getTable(),
                                                                        reqTypes.toArray(new String[reqTypes
                                                                                .size()]),
                                                                        SWT.READ_ONLY);
                combo.setValue(getValue(element));
                return combo;
            }
            
            @Override
            protected Object getValue(final Object element) {
                RequestType requestType = ((BulkEditTraceDataModel) element).getRequestType();
                if (requestType == null) {
                    return (int) 0;
                }
                return requestType.ordinal() + 1;
            }
            
            @Override
            protected void setValue(final Object element, final Object value) {
                _bulkModel.setRequestTypeChanged(true);
                int requestIndex = ((Integer) value).intValue();
                _bulkModel.setRequestType(null);
                RequestType[] requestTypes = RequestType.values();
                for (RequestType requestType : requestTypes) {
                    if (requestType.ordinal() == (requestIndex - 1)) {
                        _bulkModel.setRequestType(requestType);
                    }
                }
            }
        });
    }
}
