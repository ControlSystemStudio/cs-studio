/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.util.swt.stringtable;

import java.util.Arrays;
import java.util.List;

import org.csstudio.ui.util.swt.stringtable.StringTableEditor.CellEditorType;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Table;

/** Editor for table with multiple columns (List<String[]>)
 *  @author Xihui Chen
 */
class StringMultiColumnsEditor extends EditingSupport {

    private static final long serialVersionUID = 5729798308622621422L;
    final private TableViewer table_viewer;
    final private int columnNo;
    final private int numOfColumns;
    private CellEditorType cellEditorType;
    private Object cellEditorData;

    public StringMultiColumnsEditor(final TableViewer viewer,
            final int numOfColumns, final int columnNo, final CellEditorType cellEditorType, final Object cellData) {
        super(viewer);
        this.table_viewer = viewer;
        this.columnNo = columnNo;
        this.numOfColumns = numOfColumns;
        this.cellEditorType = cellEditorType;
        this.cellEditorData = cellData;
        if(cellEditorType == CellEditorType.CHECKBOX){
            if(cellEditorData==null || !(cellEditorData instanceof String[]) || ((String[])cellEditorData).length<2)
                cellEditorData = new String[]{"Yes", "No"};
        }
    }

    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        final Table parent = (Table) getViewer().getControl();
        switch (cellEditorType) {
        case CHECKBOX:
            return new CheckboxCellEditor(parent){
                /**
                 *
                 */
                private static final long serialVersionUID = 3284307131124279645L;
                protected Object doGetValue() {
                    return (Boolean) super.doGetValue()?((String[])cellEditorData)[1]:((String[])cellEditorData)[0];
                };
                @Override
                protected void doSetValue(Object value) {
                    if(value.toString().toLowerCase().equals(((String[])cellEditorData)[1].toLowerCase()))
                        super.doSetValue(true);
                    else
                        super.doSetValue(false);
                }
            };
        case DROPDOWN:
            return new ComboBoxCellEditor(parent,
                    (String[])cellEditorData,SWT.NONE){
                /**
                         *
                         */
                        private static final long serialVersionUID = 4196165158838137091L;

                @Override
                protected Object doGetValue() {
                    return ((CCombo)getControl()).getText();
                }

                @Override
                protected void doSetValue(Object value) {
                    ((CCombo)getControl()).setText(value.toString());
                }
            };

        default:
            return new TextCellEditor(parent);
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object getValue(Object element) {

        if (element == StringTableContentProvider.ADD_ELEMENT)
            return ""; //$NON-NLS-1$
        final int index = ((Integer)element).intValue();
        final List<String[]> items = (List<String[]>) table_viewer.getInput();
        if (columnNo < items.get(index).length)
            return items.get(index)[columnNo];
        else
            return "";
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void setValue(Object element, Object value) {
        final List<String[]> items = (List<String[]>) table_viewer.getInput();
        String[] rowData;
        if (element == StringTableContentProvider.ADD_ELEMENT)
        {
            rowData = new String[numOfColumns];
            Arrays.fill(rowData, ""); //$NON-NLS-1$
            rowData[columnNo] = value.toString();
            items.add(rowData);
            getViewer().refresh();
            return;
        }
        // else
        final int index = ((Integer)element).intValue();
        rowData = items.get(index);
        if (columnNo >= rowData.length) {
            String [] newRowData = new String[columnNo + 1];
            int i = 0;
            for (; i<rowData.length; i++) {
                newRowData[i] = rowData[i];
            }
            for (; i<newRowData.length; i++) {
                newRowData[i] = "";
            }
            rowData = newRowData;
        }
        rowData[columnNo] = value.toString();
        items.set(index, rowData);
        getViewer().refresh(element);
    }
}
