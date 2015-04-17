/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.actions;

import org.csstudio.opibuilder.widgets.editparts.TabEditPart;
import org.csstudio.opibuilder.widgets.editparts.TabItem;
import org.csstudio.opibuilder.widgets.model.TabModel;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;

/**The command which add a tab to the tab widget.
 * @author Xihui Chen
 *
 */
public class RemoveTabCommand extends Command {
	private int tabIndex;
	private TabModel tabModel;
	
	private TabItem tabItem;
	
	private boolean executed = false;
	
	public RemoveTabCommand(TabEditPart tabEditPart) {
		this.tabModel = tabEditPart.getWidgetModel();
		this.tabIndex = tabEditPart.getActiveTabIndex();
		this.tabItem = tabEditPart.getTabItem(tabIndex);
		setLabel("Remove Tab");
	}

	@Override
	public void execute() {
		if(tabModel.getChildren().size()>1){
			tabModel.removeTab(tabIndex);
			executed = true;
		}			
		else
			MessageDialog.openInformation(null, "Failed to Remove Tab", 
					"There must be at least one tab in the tab folder.");
			
	}
	
	@Override
	public void undo() {
		if(executed)
			tabModel.addTab(tabIndex, tabItem);
		executed = false;
	}
	
	
	
	
	
}
