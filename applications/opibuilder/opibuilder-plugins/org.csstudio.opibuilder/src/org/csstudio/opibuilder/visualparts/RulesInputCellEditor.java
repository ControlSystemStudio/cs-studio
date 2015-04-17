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
import org.csstudio.opibuilder.script.RulesInput;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**The cell editor for rules input.
 * @author Xihui Chen
 *
 */
public class RulesInputCellEditor extends AbstractDialogCellEditor {
	
	private RulesInput rulesInput;
	
	private AbstractWidgetModel widgetModel;

	public RulesInputCellEditor(Composite parent,final AbstractWidgetModel widgetModel, String title) {
		super(parent, title);
		this.widgetModel = widgetModel;
	}

	@Override
	protected void openDialog(Shell parentShell, String dialogTitle) {
		RulesInputDialog dialog = 
			new RulesInputDialog(parentShell, rulesInput, widgetModel, dialogTitle);
		
		if(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
				"org.eclipse.help.ui.HelpView") !=null) //$NON-NLS-1$
			PlatformUI.getWorkbench().getHelpSystem().displayHelp(
					OPIBuilderPlugin.PLUGIN_ID + ".rule"); //$NON-NLS-1$
		
		if(dialog.open() == Window.OK){
			rulesInput = new RulesInput(dialog.getRuleDataList());
		}

	}

	@Override
	protected boolean shouldFireChanges() {
		return rulesInput != null;
	}

	@Override
	protected Object doGetValue() {
		return rulesInput;
	}

	@Override
	protected void doSetValue(Object value) {
		if(value == null || !(value instanceof RulesInput))
			rulesInput = new RulesInput();
		else
			rulesInput = (RulesInput)value;
			
	}

}
