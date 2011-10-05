/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.visualparts;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.script.ScriptsInput;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**The cell editor for scripts input.
 * @author Xihui Chen
 *
 */
public class ScriptsInputCellEditor extends AbstractDialogCellEditor {
	
	private ScriptsInput scriptsInput;
	
	private AbstractWidgetModel widgetModel;

	public ScriptsInputCellEditor(Composite parent,final AbstractWidgetModel widgetModel, String title) {
		super(parent, title);
		this.widgetModel = widgetModel;
	}

	@Override
	protected void openDialog(Shell parentShell, String dialogTitle) {
		if(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
		"org.eclipse.help.ui.HelpView") !=null) //$NON-NLS-1$
			PlatformUI.getWorkbench().getHelpSystem().displayHelp(
					OPIBuilderPlugin.PLUGIN_ID + ".script"); //$NON-NLS-1$
		ScriptsInputDialog dialog = 
			new ScriptsInputDialog(parentShell, scriptsInput,					 
					dialogTitle, widgetModel);
		if(dialog.open() == Window.OK){
			scriptsInput = new ScriptsInput(dialog.getScriptDataList());
		}

	}

	@Override
	protected boolean shouldFireChanges() {
		return scriptsInput != null;
	}

	@Override
	protected Object doGetValue() {
		return scriptsInput;
	}

	@Override
	protected void doSetValue(Object value) {
		if(value == null || !(value instanceof ScriptsInput))
			scriptsInput = new ScriptsInput();
		else
			scriptsInput = (ScriptsInput)value;
			
	}

}
