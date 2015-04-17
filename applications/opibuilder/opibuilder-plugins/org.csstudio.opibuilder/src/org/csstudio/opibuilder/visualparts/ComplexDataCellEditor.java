/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.visualparts;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.datadefinition.AbstractComplexData;
import org.csstudio.opibuilder.datadefinition.PropertyData;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**The cell editor for complex data.
 * @author Xihui Chen
 *
 */
public class ComplexDataCellEditor extends AbstractDialogCellEditor {

	private AbstractComplexData complexData;
	
	public ComplexDataCellEditor(Composite parent, String title) {
		super(parent, title);
	}

	@Override
	protected void openDialog(Shell parentShell, String dialogTitle) {
		if(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
		"org.eclipse.help.ui.HelpView") !=null) //$NON-NLS-1$
			PlatformUI.getWorkbench().getHelpSystem().displayHelp(
			OPIBuilderPlugin.PLUGIN_ID + ".action"); //$NON-NLS-1$
		PropertiesEditDialog dialog = 
			new PropertiesEditDialog(parentShell, complexData.getAllProperties(), dialogTitle);
		
		if(dialog.open() == Window.OK){
			complexData = complexData.getCopy();
			for (PropertyData propertyData : dialog.getOutput()){
				complexData.setPropertyValue(
						propertyData.property.getPropertyID(), propertyData.tmpValue);
			}			
		}
	}

	@Override
	protected boolean shouldFireChanges() {
		return complexData != null;
	}

	@Override
	protected Object doGetValue() {
		return complexData;
	}

	@Override
	protected void doSetValue(Object value) {
		if(value == null || !(value instanceof AbstractComplexData))
			throw new RuntimeException(value + " is not instance of AbstractComplexData");
		else
			complexData = (AbstractComplexData)value;
	}

}
