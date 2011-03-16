/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.actions;

import java.util.logging.Level;

import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.widgets.Activator;
import org.csstudio.opibuilder.widgets.editparts.TabEditPart;
import org.csstudio.opibuilder.widgets.editparts.TabItem;
import org.csstudio.opibuilder.widgets.model.TabModel;
import org.eclipse.gef.commands.Command;

/**Duplicate a tab
 * @author Xihui Chen
 *
 */
public class DuplicateTabCommand extends Command {
    final private int tabIndex;
	final private TabModel tabModel;
	private TabItem tabItem;

	public DuplicateTabCommand(final TabEditPart tabEditPart) {
		this.tabModel = tabEditPart.getWidgetModel();
		this.tabIndex = tabEditPart.getActiveTabIndex()+1;
		try {
			this.tabItem = tabEditPart.getTabItem(tabIndex -1).getCopy();
		} catch (Exception e) {
		    final String message = "Failed to duplicate tab";
			Activator.getLogger().log(Level.WARNING, message , e);
			ConsoleService.getInstance().writeError(message);
		}
		setLabel("Duplicate Tab");
	}

	@Override
	public void execute() {
		tabModel.addTab(tabIndex, tabItem);
	}

	@Override
	public void undo() {
		tabModel.removeTab(tabIndex);
	}
}
