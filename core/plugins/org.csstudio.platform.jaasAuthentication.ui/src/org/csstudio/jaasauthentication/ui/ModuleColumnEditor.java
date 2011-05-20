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
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Table;

public class ModuleColumnEditor extends EditingSupport {

	final ModuleTableEditor moduleTableEditor;
	public ModuleColumnEditor(final ColumnViewer viewer, final ModuleTableEditor moduleTableEditor) {
		super(viewer);
		this.moduleTableEditor = moduleTableEditor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		final Table parent = (Table) getViewer().getControl();
		return new TextCellEditor(parent);
	}

	@Override
	protected Object getValue(Object element) {
		if (element == ModuleTableContentProvider.ADD_ELEMENT)
			return ""; //$NON-NLS-1$
		final int index = ((Integer)element).intValue();
		return JAASPreferenceModel.configurationEntryList.get(index).getLoginModuleName();
	}

	@Override
	protected void setValue(Object element, Object value) {
		JAASConfigurationEntry je;
		if (element == ModuleTableContentProvider.ADD_ELEMENT)
		{
			je = new JAASConfigurationEntry(value.toString(), JAASPreferenceModel.FLAG_REQUIRED);
			JAASPreferenceModel.configurationEntryList.add(je);
			getViewer().refresh();
			TableViewer tableViewer = (TableViewer) getViewer();
			tableViewer.getTable().select(JAASPreferenceModel.configurationEntryList.size()-1);
			moduleTableEditor.updateOptionsTable();

			return;
		}
		// else
		final int index = ((Integer)element).intValue();
		je = JAASPreferenceModel.configurationEntryList.get(index);
		je.setLoginModuleName(value.toString());
		getViewer().refresh(element);
	}

}
