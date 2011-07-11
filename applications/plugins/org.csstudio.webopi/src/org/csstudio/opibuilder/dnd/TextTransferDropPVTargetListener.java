/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.dnd;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.swt.dnd.TextTransfer;

/**The Drop PV target listener for text transfer.
 * @author Xihui Chen
 *
 */
public class TextTransferDropPVTargetListener extends AbstractDropPVTargetListener {

	public TextTransferDropPVTargetListener(EditPartViewer viewer) {
		super(viewer, TextTransfer.getInstance());
	}

	@Override
	protected String[] getPVNamesFromTransfer() {
		if(getCurrentEvent().data == null)
			return null;
		String text = (String)getCurrentEvent().data;
		String[] pvNames = text.trim().split("\\s+"); //$NON-NLS-1$
		return pvNames;
	}

}
