/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.actions;

import org.csstudio.opibuilder.widgets.editparts.GroupingContainerEditPart;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler to handle lock/unlock grouping container children which has a key binding of Ctrl+L.
 * @author Xihui Chen 
 *
 */
public class LockUnlockChildrenHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		
			Object viewer = HandlerUtil.getActivePart(event).getAdapter(GraphicalViewer.class);			
			if(viewer == null) return null;
			
			ISelection currentSelection =((GraphicalViewer)viewer).getSelection();
			if(currentSelection instanceof IStructuredSelection){
				Object element = ((IStructuredSelection) currentSelection)
						.getFirstElement();
				if(element instanceof GroupingContainerEditPart){
					CommandStack commandStack = 
						(CommandStack) (HandlerUtil.getActivePart(event)).getAdapter(CommandStack.class);
					if(commandStack != null)
						commandStack.execute(LockUnlockChildrenAction.createLockUnlockCommand
								(((GroupingContainerEditPart)element).getWidgetModel()));
				}
			}	
		
		return null;
	}

}
