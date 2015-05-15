package com.cosylab.vdct.inspector;

/**
 * Copyright (c) 2002, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the Cosylab, Ltd., Control System Laboratory nor the names
 * of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * Insert the type's description here.
 * Creation date: (6.1.2001 23:29:20)
 * @author Matej Sekoranja
 */
public class InspectorTableModel extends javax.swing.table.AbstractTableModel {
    private InspectableProperty[] data = null;
    private Inspectable dataObject = null;
    private InspectorInterface inspector = null;
/**
 * InspactorTableModel constructor comment.
 */
public InspectorTableModel(InspectorInterface inspector) {
    super();
    this.inspector=inspector;
}
/**
 * Insert the method's description here.
 * Creation date: (14.11.1999 15:22:35)
 * @return java.lang.Class
 * @param column int
 */
public Class getColumnClass(int column) {
    return String.class;
}
/**
 * getColumnCount method comment.
 */
public int getColumnCount() {
    return 3;            // visibility & name & value
}
/**
 * Insert the method's description here.
 * Creation date: (10.1.2001 17:12:58)
 * @return com.cosylab.vdct.inspector.Inspectable
 */
public Inspectable getDataObject() {
    return dataObject;
}
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 22:17:39)
 * @return com.cosylab.vdct.inspector.InspectableProperty
 * @param row int
 */
public InspectableProperty getPropertyAt(int row) {
    return data[row];
}
/**
 * getRowCount method comment.
 */
public int getRowCount() {
    if (data!=null)
        return data.length;
    else
        return 0;
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int column) {
    switch (column)
    {
        case 0: return null;
        case 1: return data[row].getName();
        case 2: return data[row].getValue();
    }
    return null;
}
/**
 * Insert the method's description here.
 * Creation date: (6.1.2001 23:41:13)
 * @return boolean
 * @param rowIndex int
 * @param columnIndex int
 */
public boolean isCellEditable(int rowIndex, int columnIndex) {

    // disable editing in debug mode (!!!)
    if (com.cosylab.vdct.plugin.debug.PluginDebugManager.isDebugState())
        return false;

    // value
    if (columnIndex==2) return data[rowIndex].isEditable();
    else return false;
}
/**
 * Insert the method's description here.
 * Creation date: (10.1.2001 17:12:58)
 * @param object com.cosylab.vdct.inspector.Inspectable
 */
public void setDataObject(Inspectable object) {
    dataObject = object;
    if (object!=null)
        data = object.getProperties(inspector.getMode());
    else
        data = null;
    fireTableDataChanged();
}
/**
 * Sets the object value for the cell at <I>column</I> and
 * <I>row</I>.  <I>aValue</I> is the new value.  This method
 * will generate a tableChanged() notification.
 *
 * @param   aValue          the new value.  This can be null.
 * @param   row             the row whose value is to be looked up
 * @param   column          the column whose value is to be looked up
 * @return                  the value Object at the specified cell
 */

public void setValueAt(Object aValue, int row, int column) {
    data[row].setValue(aValue.toString());
    // generate notification
    fireTableCellUpdated(row, column);
}
/**
 * Insert the method's description here.
 * Creation date: (5.5.2001 15:12:05)
 * @param property com.cosylab.vdct.inspector.InspectableProperty
 */
public void updateProperty(InspectableProperty property) {
    for (int row=0; row < data.length; row++)
        if (data[row]==property)
                fireTableRowsUpdated(row, row);
//                fireTableCellUpdated(row, 2);
}
/**
 * Insert the method's description here.
 * Creation date: (5.5.2001 15:06:07)
 * @param propertyName java.lang.String
 */
public void updateProperty(String propertyName) {
    for (int row=0; row < data.length; row++)
        if (data[row].getName().equals(propertyName))
                fireTableRowsUpdated(row, row);
//            fireTableCellUpdated(row, 2);
}
}
