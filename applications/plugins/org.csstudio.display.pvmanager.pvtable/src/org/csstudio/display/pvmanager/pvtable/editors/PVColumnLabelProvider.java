package org.csstudio.display.pvmanager.pvtable.editors;

import org.csstudio.display.pvmanager.pvtable.PVTableModel.Item;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.epics.pvmanager.data.Util;

public class PVColumnLabelProvider extends ColumnLabelProvider {

	private Color red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	private Color yellow = Display.getCurrent()
			.getSystemColor(SWT.COLOR_YELLOW);

	@Override
	public Color getForeground(Object element) {
		Item item = (Item) element;
		if (item == null || item.getProcessVariableName() == null) {
			return null;
		} else if (item.getValue() != null){
			String alarm = Util.alarmOf(item.getValue()).getAlarmSeverity()
					.toString();
			if (alarm.equals("MAJOR")) {
				return red;
			} else if (alarm.equals("MINOR")) {
				return yellow;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

}
