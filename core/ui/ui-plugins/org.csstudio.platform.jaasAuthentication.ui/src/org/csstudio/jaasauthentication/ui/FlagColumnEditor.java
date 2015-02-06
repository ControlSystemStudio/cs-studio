/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.jaasauthentication.ui;

import org.csstudio.platform.internal.jassauthentication.preference.JAASConfigurationEntry;
import org.csstudio.platform.internal.jassauthentication.preference.JAASPreferenceModel;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;

public class FlagColumnEditor extends EditingSupport {

	public FlagColumnEditor(ColumnViewer viewer) {
		super(viewer);
	}

	@Override
	protected boolean canEdit(Object element) {
		if(element == ModuleTableContentProvider.ADD_ELEMENT)
			return false;
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		final Table parent = (Table) getViewer().getControl();
		return new ComboBoxCellEditor(parent, JAASPreferenceModel.FLAGS, SWT.DROP_DOWN | SWT.READ_ONLY);
	}

	@Override
	protected Object getValue(Object element) {
		if (element == ModuleTableContentProvider.ADD_ELEMENT)
			return ""; //$NON-NLS-1$
		final int index = ((Integer)element).intValue();
		return JAASPreferenceModel.configurationEntryList.get(index).getModuleControlFlagIndex();
	}

	@Override
	protected void setValue(Object element, Object value) {
		JAASConfigurationEntry je;
		final int index = ((Integer)element).intValue();
		je = JAASPreferenceModel.configurationEntryList.get(index);
		je.setModuleControlFlag(JAASPreferenceModel.FLAGS[(Integer)value]);
		getViewer().refresh(element);
	}

}
