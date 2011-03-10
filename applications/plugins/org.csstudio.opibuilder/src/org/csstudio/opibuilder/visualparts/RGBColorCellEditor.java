/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.visualparts;

import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**The cell editor for RGB Color.
 * @author Xihui Chen
 *
 */
public class RGBColorCellEditor extends AbstractDialogCellEditor {

	private RGB rgb;
	
	
	public RGBColorCellEditor(Composite parent) {
		super(parent, null);
	}

	@Override
	protected void openDialog(Shell parentShell, String dialogTitle) {
		ColorDialog dialog = 
			new ColorDialog(parentShell);
		dialog.setRGB(rgb);
		RGB result = dialog.open();
		if(result != null)
			rgb = result;
	}

	@Override
	protected boolean shouldFireChanges() {
		return rgb != null;
	}

	@Override
	protected Object doGetValue() {
		return rgb;
	}

	@Override
	protected void doSetValue(Object value) {
		if(value == null || !(value instanceof RGB))
			rgb = new RGB(0,0,0);
		else
			rgb = (RGB)value;
	}

}
