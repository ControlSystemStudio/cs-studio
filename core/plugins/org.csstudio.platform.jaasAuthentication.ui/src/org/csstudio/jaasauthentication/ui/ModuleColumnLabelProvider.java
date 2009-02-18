package org.csstudio.jaasauthentication.ui;

import org.csstudio.platform.internal.jassauthentication.preference.JAASPreferenceModel;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

public class ModuleColumnLabelProvider extends CellLabelProvider {
	
	public ModuleColumnLabelProvider() {
		super();
	}

	@Override
	public void update(ViewerCell cell) {
		final int index = ((Integer) cell.getElement()).intValue();
		//if this is the extra row
		if (index < 0)
				cell.setText(Messages.ModuleColumnLabelProvider_add);
		else
		{
			String text = JAASPreferenceModel.configurationEntryList.get(index).getLoginModuleName();
            cell.setText(text);
		}
	}


}
