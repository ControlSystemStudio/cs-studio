package org.csstudio.utility.toolbox.view.forms.listener;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

public class BuildingModifyListener implements ModifyListener {

	private Text building;
	private Text room;

	public void init(Text building, Text room) {
		this.building = building;
		this.room = room;
	}

	@Override
	public void modifyText(ModifyEvent e) {
		if (StringUtils.isNotEmpty(building.getText())) {
			room.setMessage("");
			room.setEditable(true);
		} else {
			room.setText("");
			room.setMessage("Please provide value for building first...");
			room.setEditable(false);
		}
	}

}