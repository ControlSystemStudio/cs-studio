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

/**The command which add a tab to the tab widget.
 * @author Xihui Chen
 *
 */
public class AddTabCommand extends Command {
	private int tabIndex;
	private TabModel tabModel;
	private TabItem tabItem = null;
	
	public AddTabCommand(TabEditPart tabEditPart, boolean before) {
		this.tabModel = tabEditPart.getWidgetModel();
		if(before)
			this.tabIndex = tabEditPart.getActiveTabIndex();
		else
			this.tabIndex = tabEditPart.getActiveTabIndex()+1;
		setLabel("Add Tab");
	}
	
	@Override
	public void execute() {
		if(tabItem == null)
			tabItem = new TabItem(tabModel, tabIndex);
		tabModel.addTab(tabIndex, tabItem);
	}
	
	@Override
	public void undo() {
		tabModel.removeTab(tabIndex);
	}
	
	
	
	
	
}
