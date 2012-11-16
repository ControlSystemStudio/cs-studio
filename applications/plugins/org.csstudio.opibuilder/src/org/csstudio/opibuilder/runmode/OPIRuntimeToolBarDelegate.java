/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.runmode;
import org.csstudio.opibuilder.actions.NavigateOPIsAction;
import org.csstudio.opibuilder.actions.PartZoomInAction;
import org.csstudio.opibuilder.actions.PartZoomOutAction;
import org.csstudio.opibuilder.visualparts.PartZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.actions.ActionFactory;


/**The toolbar contributor for OPI runner
 * @author Xihui Chen
 *
 */
public class OPIRuntimeToolBarDelegate{

	 /**
     * The action bars; <code>null</code> until <code>init</code> is called.
     */
    private IActionBars bars;

    /**
     * The workbench page; <code>null</code> until <code>init</code> is called.
     */
    private IWorkbenchPage page;
	
	private NavigateOPIsAction backwardAction, forwardAction;
	private PartZoomInAction partZoomInAction;
	private PartZoomOutAction partZoomOutAction;
	private PartZoomComboContributionItem partZoomComboContributionItem;

	
	 public void init(IActionBars bars, IWorkbenchPage page) {
	        this.page = page;
	        this.bars = bars;
	        backwardAction = new NavigateOPIsAction(false);
			forwardAction = new NavigateOPIsAction(true);
			partZoomInAction = new PartZoomInAction();
			partZoomOutAction = new PartZoomOutAction();
	        partZoomComboContributionItem = new PartZoomComboContributionItem(page);
	 }
	
	public void contributeToToolBar(IToolBarManager toolBarManager) {

		toolBarManager.add(partZoomInAction);
		toolBarManager.add(partZoomOutAction);
		toolBarManager.add(partZoomComboContributionItem);
		toolBarManager.add(backwardAction);
		toolBarManager.add(forwardAction);
	}
	
	
	/**Hook {@link IOPIRuntime} with this toolbar.
	 * @param opiRuntime
	 */
	public void setActiveOPIRuntime(IOPIRuntime opiRuntime) {
		
		partZoomInAction.setPart(opiRuntime);
		partZoomOutAction.setPart(opiRuntime);
		partZoomComboContributionItem.setPart(opiRuntime);
		DisplayOpenManager manager = 
			(DisplayOpenManager)opiRuntime.getAdapter(DisplayOpenManager.class);
		backwardAction.setDisplayOpenManager(manager);
		forwardAction.setDisplayOpenManager(manager);
		IActionBars bars = getActionBars();		
		bars.setGlobalActionHandler(backwardAction.getId(), backwardAction);	
		bars.setGlobalActionHandler(forwardAction.getId(), forwardAction);
	
		ActionRegistry actionRegistry =
			(ActionRegistry) opiRuntime.getAdapter(ActionRegistry.class);
		bars.setGlobalActionHandler(ActionFactory.PRINT.getId(), 
				actionRegistry.getAction(ActionFactory.PRINT.getId()));
		bars.setGlobalActionHandler(ActionFactory.REFRESH.getId(), 
				actionRegistry.getAction(ActionFactory.REFRESH.getId()));
		bars.setGlobalActionHandler(partZoomInAction.getId(), partZoomInAction);
		bars.setGlobalActionHandler(partZoomOutAction.getId(), partZoomOutAction);
		bars.updateActionBars();
		
	}
	
	 /**
     * Returns this contributor's workbench page.
     *
     * @return the workbench page
     */
    public IWorkbenchPage getPage() {
        return page;
    }
	
    
    /**
     * Returns this contributor's action bars.
     *
     * @return the action bars
     */
    public IActionBars getActionBars() {
        return bars;
    }
}
