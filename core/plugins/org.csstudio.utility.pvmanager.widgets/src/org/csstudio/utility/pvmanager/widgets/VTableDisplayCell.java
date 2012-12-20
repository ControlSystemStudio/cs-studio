package org.csstudio.utility.pvmanager.widgets;

public class VTableDisplayCell {
	private final int row;
	private final int column;
	
	public VTableDisplayCell(int row, int column) {
		super();
		this.row = row;
		this.column = column;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getColumn() {
		return column;
	}
}
