/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.graphene.opiwidgets.properties;

import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.visualparts.AbstractDialogCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.epics.graphene.AxisRange;
import org.epics.graphene.AxisRanges;

/**The cell editor for OPIColor.
 * @author Xihui Chen
 *
 */
public class AxisRangeCellEditor extends AbstractDialogCellEditor {

	private AxisRange axisRange;
	
	
	public AxisRangeCellEditor(Composite parent, String title) {
		super(parent, title);
	}

	@Override
	protected void openDialog(Shell parentShell, String dialogTitle) {
//		OPIColorDialog dialog = 
//			new OPIColorDialog(parentShell, opiColor, dialogTitle);
//		if(dialog.open() == Window.OK)
//			opiColor = dialog.getOutput();
	}

	@Override
	protected boolean shouldFireChanges() {
		return axisRange != null;
	}

	@Override
	protected Object doGetValue() {
		return axisRange;
	}

	@Override
	protected void doSetValue(Object value) {
		if (value instanceof AxisRange) {
			axisRange = (AxisRange) value;
		} else {
			axisRange = AxisRanges.display();
		}
	}

}
