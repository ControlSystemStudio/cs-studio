package org.csstudio.utility.pvmanager.widgets;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.epics.pvmanager.data.VTable;

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
				return ((int[]) vTable.getColumnArray(column))[row];
			} else if (vTable.getColumnType(column).equals(Double.TYPE)) {
				return ((double[]) vTable.getColumnArray(column))[row];
			} else if (vTable.getColumnType(column).equals(String.class)) {
				return ((String[]) vTable.getColumnArray(column))[row];
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
		
		Object[] result = new Object[((String[]) vTable.getColumnArray(0)).length];
		
		for (int i = 0; i < result.length; i++) {
			result[i] = new VTableRow(vTable, i);
		}
		
		return result;
	}
	

}
