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

/**Change tab index.
 * @author Xihui Chen
 *
 */
public class ChangeTabIndexCommand extends Command {
	private int newIndex, oldIndex;
	private TabModel tabModel;
	private TabItem tabItem;
	
	public ChangeTabIndexCommand(TabEditPart tabEditPart, int newIndex) {
		this.tabModel = tabEditPart.getWidgetModel();
		this.oldIndex = tabEditPart.getActiveTabIndex();
		this.newIndex = newIndex;
		
		this.tabItem = tabEditPart.getTabItem(oldIndex);
			
		setLabel("Change Tab Index");
	}
	
	@Override
	public void execute() {
		tabModel.removeTab(oldIndex);
		tabModel.addTab(newIndex, tabItem);
	}
	
	@Override
	public void undo() {
		tabModel.removeTab(newIndex);
		tabModel.addTab(oldIndex, tabItem);
	}
	
	
	
	
	
}
