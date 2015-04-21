/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.properties;

import org.csstudio.opibuilder.visualparts.AbstractDialogCellEditor;
import org.csstudio.opibuilder.widgets.model.IntensityGraphModel;
import org.csstudio.swt.widgets.datadefinition.ColorMap;
import org.csstudio.swt.widgets.datadefinition.ColorMap.PredefinedColorMap;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**The cell editor for {@link ColorMap}
 * @author Xihui Chen
 *
 */
public class ColorMapCellEditor extends AbstractDialogCellEditor {

	private ColorMap colorMap;
	private IntensityGraphModel widgetModel;
	public ColorMapCellEditor(Composite parent, String title, IntensityGraphModel widgetModel) {
		super(parent, title);
		this.widgetModel = widgetModel;
	}

	@Override
	protected void openDialog(Shell parentShell, String dialogTitle) {
		ColorMapEditDialog dialog = 
			new ColorMapEditDialog(parentShell, colorMap, dialogTitle, widgetModel.getMinimum(), widgetModel.getMaximum());
		if(dialog.open() == Window.OK)
			colorMap = dialog.getOutput();
	}

	@Override
	protected boolean shouldFireChanges() {
		return colorMap != null;
	}

	@Override
	protected Object doGetValue() {
		return colorMap;
	}

	@Override
	protected void doSetValue(Object value) {
		if(value == null || !(value instanceof ColorMap))
			colorMap = new ColorMap(PredefinedColorMap.GrayScale, true, true);
		else
			colorMap = (ColorMap)value;
	}

}
