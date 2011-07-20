/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.commands;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.gef.commands.Command;

/**Orphan a child from its parent.
 * @author Alexander Will (class of same name in SDS)
 * @author Xihui Chen
 *
 */
public class OrphanChildCommand extends Command {
	
	private AbstractContainerModel parent;
	private AbstractWidgetModel child;
	
	private int index;


	public OrphanChildCommand(AbstractContainerModel parent,
			AbstractWidgetModel child) {
		super("Orphan Widget");		
		this.parent = parent;
		this.child = child;
	}
	
	@Override
	public void execute() {
		index = parent.getIndexOf(child);
		parent.removeChild(child);
	}
	
	@Override
	public void undo() {
		parent.addChild(index, child);
	}
	
	

}
