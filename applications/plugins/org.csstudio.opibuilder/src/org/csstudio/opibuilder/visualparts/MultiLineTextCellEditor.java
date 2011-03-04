/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.visualparts;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**The dialog cell editor for multiline text editing.
 * @author Xihui Chen
 *
 */
public class MultiLineTextCellEditor extends AbstractDialogCellEditor {
	
	private String stringValue;

	public MultiLineTextCellEditor(Composite parent, String title) {
		super(parent, title);
	}

	@Override
	protected void openDialog(Shell parentShell, String dialogTitle) {
		MultilineTextEditDialog dialog = 
			new MultilineTextEditDialog(parentShell, stringValue,dialogTitle);
		if(dialog.open() == Window.OK){
			stringValue = dialog.getResult();
		}

	}

	@Override
	protected boolean shouldFireChanges() {
		return stringValue != null;
	}

	@Override
	protected Object doGetValue() {
		return stringValue;
	}

	@Override
	protected void doSetValue(Object value) {
		if(value == null)
			stringValue =  "";  //$NON-NLS-1$
		else
			stringValue = value.toString();
			
	}

}
