package org.csstudio.utility.pvmanager.widgets;

import org.epics.vtype.VType;

public class VTableDisplayCell implements VTypeAdaptable {
	private final VTableDisplay vTable;
	private final int row;
	private final int column;
	
	public VTableDisplayCell(VTableDisplay vTable, int row, int column) {
		this.vTable = vTable;
		this.row = row;
		this.column = column;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getColumn() {
		return column;
	}

	@Override
	public VType toVType() {
		return vTable.getVTable();
	}
}
