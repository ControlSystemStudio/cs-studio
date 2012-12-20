package org.csstudio.display.pvmanager.pvtable;

import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.csstudio.csdata.ProcessVariable;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class PVTableModel {
	
	private List<ProcessVariable> pvNames = new ArrayList<ProcessVariable>();
	private List<Object> values;
	private List<Exception> lastExceptions;
	private Set<PVTableModelListener> listeners = new HashSet<PVTableModelListener>();
	
	public void addPVTableModelListener(PVTableModelListener listener) {
		listeners.add(listener);
	}
	
	public void removePVTableModelListener(PVTableModelListener listener) {
		listeners.remove(listener);
	}
	
	public void updateValues(List<Object> values, List<Exception> lastExceptions) {
		this.values = values;
		this.lastExceptions = lastExceptions;
		fireDataChanged();
	}
	
	public void updateValues(List<Exception> lastExceptions) {
		this.lastExceptions = lastExceptions;
		fireDataChanged();
	}
	
	public void addPVName(ProcessVariable pvName) {
		pvNames.add(pvName);
		fireDataChanged();
	}
	
	public void updatePVName(Item item, ProcessVariable pvName) {
		if (item.row < pvNames.size()) {
			pvNames.set(item.row, pvName);
		} else {
			pvNames.add(pvName);
		}
		fireDataChanged();
	}
	
	public void removeItem(Item item) {
		if (item.row < pvNames.size()) {
			pvNames.remove(item.row);
			values.remove(item.row);
			lastExceptions.remove(item.row);
			fireDataChanged();
		}
	}
	
	public void removeItems(Item[] items) {
		int[] indexes = new int[items.length];
		for (int i = 0; i < items.length; i++) {
			indexes[i] = items[i].row;
		}
		Arrays.sort(indexes);
		for (int i = indexes.length - 1; i >=0; i--) {
			int row = indexes[i];
			pvNames.remove(row);
			values.remove(row);
			lastExceptions.remove(row);
		}
		fireDataChanged();
	}
	
	private void fireDataChanged() {
		for (PVTableModelListener listener : listeners) {
			listener.dataChanged();
		}
	}
	
	public class Item {
		
		private final int row;
		
		private Item(int row) {
			this.row = row;
		}
		
		public int getRow() {
			return row;
		}
		
		public ProcessVariable getProcessVariableName() {
			if (row < pvNames.size())
				return pvNames.get(row);
			else
				return null;
		}
		
		public Object getValue() {
			if (values != null && row < values.size())
				return values.get(row);
			else
				return null;
		}
		
		public Exception getException() {
			if (lastExceptions != null && row < lastExceptions.size())
				return lastExceptions.get(row);
			else
				return null;
		}
		
		@Override
		public int hashCode() {
			return new Integer(row).hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Item) {
				return row == ((Item) obj).row;
			}
			return false;
		}
	}
	
	public Item[] getItems() {
		Item[] result = new Item[pvNames.size() + 1];
		for (int i = 0; i < pvNames.size() + 1; i++) {
			result[i] = new Item(i);
		}
		return result;
	}
	
}
