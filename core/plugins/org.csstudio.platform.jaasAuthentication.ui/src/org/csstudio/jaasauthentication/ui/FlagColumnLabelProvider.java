/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
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
