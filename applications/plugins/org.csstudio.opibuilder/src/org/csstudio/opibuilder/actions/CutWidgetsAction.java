/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.editor.OPIEditor;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.actions.ActionFactory;

/**Cut widgets to clipboard.
 *@author Joerg Rathlev (class of same name in SDS)
 * @author Xihui Chen
 *
 */
public class CutWidgetsAction extends CopyWidgetsAction {

	private DeleteAction deleteAction;
	
	public CutWidgetsAction(OPIEditor part, DeleteAction deleteAction) {
		super(part);
		this.deleteAction = deleteAction;
		setText("Cut");
		setActionDefinitionId("org.eclipse.ui.edit.cut"); //$NON-NLS-1$
		setId(ActionFactory.CUT.getId());
		ISharedImages sharedImages = 
			part.getSite().getWorkbenchWindow().getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages
        .getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
	}
	
	@Override
	public void run() {
		super.run();
		deleteAction.run();
	}

}
