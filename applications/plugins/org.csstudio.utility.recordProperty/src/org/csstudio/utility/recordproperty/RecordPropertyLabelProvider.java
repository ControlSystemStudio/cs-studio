package org.csstudio.utility.recordproperty;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class RecordPropertyLabelProvider extends LabelProvider implements ITableLabelProvider {

	/**
	 * {@inheritDoc}
	 */
	public Image getColumnImage(final Object element, final int columnIndex) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getColumnText(final Object element, final int columnIndex) {
		if (element instanceof RecordPropertyEntry) {
			RecordPropertyEntry entry = (RecordPropertyEntry) element;
			switch (columnIndex) {
			case RecordPropertyView.COL_PV:
				return entry.getPvName();
			case RecordPropertyView.COL_RDB:
				return entry.getRdb();
			case RecordPropertyView.COL_VAL:
				return entry.getVal();
			case RecordPropertyView.COL_RMI:
				return entry.getRmi();
			default:
				return null;
			}
		}
		return null;
	}
}
