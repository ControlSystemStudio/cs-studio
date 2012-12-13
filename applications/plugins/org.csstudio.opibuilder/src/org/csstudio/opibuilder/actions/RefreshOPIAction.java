/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.runmode.IOPIRuntime;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;

/**
 * Refresh the OPI just like the OPI is reopened.
 * 
 * @author Xihui Chen
 * 
 */
public class RefreshOPIAction extends Action {
	
	final private IOPIRuntime opiRuntime;

    public RefreshOPIAction(IOPIRuntime opiRuntime) {
    	this.opiRuntime = opiRuntime;
    	setActionDefinitionId(IWorkbenchCommandConstants.FILE_REFRESH); //$NON-NLS-1$
		setId(ActionFactory.REFRESH.getId());
    	setText("Refresh OPI");
    	setImageDescriptor(
    			CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
    					OPIBuilderPlugin.PLUGIN_ID, "icons/refresh.gif")); //$NON-NLS-1$
    }  
    
    @Override
    public void run() {
    	try {
			opiRuntime.setOPIInput(opiRuntime.getOPIInput());
		} catch (PartInitException e) {
			ErrorHandlerUtil.handleError("Failed to refresh OPI", e);
		}
    }   

	
}
