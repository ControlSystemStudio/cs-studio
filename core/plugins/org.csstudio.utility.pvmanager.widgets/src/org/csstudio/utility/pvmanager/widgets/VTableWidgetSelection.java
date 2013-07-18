package org.csstudio.utility.pvmanager.widgets;

import org.epics.vtype.VType;

public class VTableWidgetSelection implements VTypeAdaptable {
	private final VTableWidget vTableWidget;
	
	public VTableWidgetSelection(VTableWidget vTableWidget) {
		this.vTableWidget = vTableWidget;
	}

	@Override
	public VType toVType() {
		return vTableWidget.getValue();
	}
}
