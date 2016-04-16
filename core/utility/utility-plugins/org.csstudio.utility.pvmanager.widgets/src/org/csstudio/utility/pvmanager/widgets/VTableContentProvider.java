package org.csstudio.utility.pvmanager.widgets;

import java.time.Instant;
import java.util.List;

import org.diirt.util.array.ListNumber;
import org.diirt.vtype.VTable;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class VTableContentProvider implements IStructuredContentProvider {

    public static class VTableRow {
        private final int row;
        private final VTable vTable;

        public VTableRow(VTable vTable, int row) {
            this.row = row;
            this.vTable = vTable;
        }

        public int getRow() {
            return row;
        }

        public VTable getVTable() {
            return vTable;
        }

        public Object getValue(int column) {
            if (vTable.getColumnType(column).equals(Integer.TYPE)) {
                return ((ListNumber) vTable.getColumnData(column)).getInt(row);
            } else if (vTable.getColumnType(column).equals(Double.TYPE)) {
                return ((ListNumber) vTable.getColumnData(column)).getDouble(row);
            } else if (vTable.getColumnType(column).equals(String.class)) {
                    Object o = ((List<?>) vTable.getColumnData(column)).get(row);
                    if(o == null){
                        return null;
                    }else{
                    return o.toString();
                    }
            } else if (vTable.getColumnType(column).equals(Instant.class)){
                 return ((List<?>) vTable.getColumnData(column)).get(row);
             } else {
                throw new RuntimeException("Table contain unsupported type " + vTable.getColumnType(column).getName());
            }
        }

    }

    @Override
    public void dispose() {
        // Nothing to do
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // Nothing to do
    }

    @Override
    public Object[] getElements(Object inputElement) {
        VTable vTable = (VTable) inputElement;

        Object[] result = new Object[vTable.getRowCount()];

        for (int i = 0; i < result.length; i++) {
            result[i] = new VTableRow(vTable, i);
        }

        return result;
    }


}
