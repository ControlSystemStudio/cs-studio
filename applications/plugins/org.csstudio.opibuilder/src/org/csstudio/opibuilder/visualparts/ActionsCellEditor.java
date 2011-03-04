/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.visualparts;

import org.csstudio.opibuilder.widgetActions.ActionsInput;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**The cell editor for actions.
 * @author Xihui Chen
 *
 */
public class ActionsCellEditor extends AbstractDialogCellEditor {

	private ActionsInput actionsInput;
	private boolean showHookOption;
	
	public ActionsCellEditor(Composite parent, String title, boolean showHookOption) {
		super(parent, title);
		this.showHookOption = showHookOption;
	}

	@Override
	protected void openDialog(Shell parentShell, String dialogTitle) {
		ActionsInputDialog dialog = 
			new ActionsInputDialog(parentShell, actionsInput, dialogTitle, showHookOption);
		if(dialog.open() == Window.OK)
			actionsInput = dialog.getOutput();
	}

	@Override
	protected boolean shouldFireChanges() {
		return actionsInput != null;
	}

	@Override
	protected Object doGetValue() {
		return actionsInput;
	}

	@Override
	protected void doSetValue(Object value) {
		if(value == null || !(value instanceof ActionsInput))
			actionsInput = new ActionsInput();
		else
			actionsInput = (ActionsInput)value;
	}

}
