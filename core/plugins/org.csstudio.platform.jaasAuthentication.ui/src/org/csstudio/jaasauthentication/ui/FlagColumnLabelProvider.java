package org.csstudio.jaasauthentication.ui;

import org.csstudio.platform.internal.jassauthentication.preference.JAASPreferenceModel;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

public class FlagColumnLabelProvider extends CellLabelProvider {
	

	public FlagColumnLabelProvider() {
		super();
	}

	@Override
	public void update(ViewerCell cell) {
		final int index = ((Integer) cell.getElement()).intValue();
		//if this is the extra row
		if (index < 0)
				cell.setText(""); //$NON-NLS-1$
		else
		{
			String text = JAASPreferenceModel.configurationEntryList.get(index).getModuleControlFlag();
            cell.setText(text);
		}
	}

}
