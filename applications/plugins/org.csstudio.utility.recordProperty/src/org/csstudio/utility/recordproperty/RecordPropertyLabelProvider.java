package org.csstudio.utility.recordproperty;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class RecordPropertyLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider {

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

	@Override
	public Color getBackground(Object element, int columnIndex) {

		if (element instanceof RecordPropertyEntry) {
			RecordPropertyEntry entry = (RecordPropertyEntry) element;
			switch (columnIndex) {
			case RecordPropertyView.COL_PV:
				return null;
			case RecordPropertyView.COL_RDB:
				if(entry.getRdb() == "N/A") {
					return Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
				} else {
					return null;
				}
			case RecordPropertyView.COL_VAL:
				
				if(entry.getVal() == "N/A") {
					return Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
				} else {
					return null;
				}
			case RecordPropertyView.COL_RMI:
				
				if(entry.getRmi() == "N/A") {
					return Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
				} else {
					return null;
				}
			default:
				return null;
			}
		}
		return null;
		//return Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
	}

	@Override
	public Color getForeground(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}
}
