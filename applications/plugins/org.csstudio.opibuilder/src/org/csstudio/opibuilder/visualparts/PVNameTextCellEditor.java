/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.visualparts;

import org.csstudio.autocomplete.ui.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

/**A text cell editor that allows pv name auto complete.
 * @author Xihui Chen
 *
 */
public class PVNameTextCellEditor extends TextCellEditor {
	
	private ContentProposalAdapter contentProposalAdapter;
	
	public PVNameTextCellEditor(Composite parent) {
		super(parent);
	}
	
	public void setContentProposalAdapter(ContentProposalAdapter contentProposalAdapter) {
		this.contentProposalAdapter = contentProposalAdapter;
	}
	

	public void applyValue() {			
		fireApplyEditorValue();
	}
	
	/**Add a listener that will be executed when pv name is seleteced 
	 * by double click on proposal dialog.
	 * @param listener
	 */
	public void addContentProposalListener(IContentProposalListener listener){			
		if(contentProposalAdapter != null)
			contentProposalAdapter.addContentProposalListener(listener);
		
	}		

}
