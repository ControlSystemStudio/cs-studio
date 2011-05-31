/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.runmode.OPIRunner;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPart;

/**Automatically navigate between running OPIs.
 * @author Xihui Chen
 *
 */
public class AutoNavigateAction extends Action {

	private IWorkbenchPart part;
	private int pointer;
	private List<OPIRunner> opiRunners; 
	
	public AutoNavigateAction() {
		super("Auto Navigate", AS_CHECK_BOX);
		opiRunners = new ArrayList<OPIRunner>();
		setToolTipText("Automatically Navigate between running OPIs");
		setImageDescriptor(CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
				OPIBuilderPlugin.PLUGIN_ID, "icons/auto_navigate.png"));
	}
	
	public void setPart(IWorkbenchPart part) {
		this.part = part;
	}
	
	@Override
	public void run() {
		if(!isChecked()){
			return;
		}		
		
		Runnable activateTask = new Runnable() {			
			public void run() {
				
				IEditorReference[] refs = 
					part.getSite().getWorkbenchWindow().getActivePage().getEditorReferences();
				opiRunners.clear();
				for(IEditorReference e : refs){
					if(e.getEditor(false) instanceof OPIRunner){
						opiRunners.add((OPIRunner)e.getEditor(false));
					}
				}
				if(opiRunners.size() <=0)
					return;
				if(opiRunners.size() == 1)
					setChecked(false);
				if(pointer >= opiRunners.size())
					pointer = 0;
				OPIRunner opi = opiRunners.get(pointer++);
				System.out.println(opi);
				if(opi != null){
					part.getSite().getWorkbenchWindow().getActivePage().activate(opi);
				}				
				if(isChecked())
					Display.getDefault().timerExec(1000, this);
			}
		};		
		Display.getDefault().asyncExec(activateTask);		
		
	}
	
	
}
