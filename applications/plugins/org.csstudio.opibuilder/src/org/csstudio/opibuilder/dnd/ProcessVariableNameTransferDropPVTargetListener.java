/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.dnd;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.dnd.SerializableItemTransfer;
import org.eclipse.gef.EditPartViewer;

/**The Drop PV target listener for process variable name transfer.
 * @author Xihui Chen
 *
 */
public class ProcessVariableNameTransferDropPVTargetListener extends AbstractDropPVTargetListener {

	public ProcessVariableNameTransferDropPVTargetListener(EditPartViewer viewer) {
		super(viewer, SerializableItemTransfer.getTransfer(ProcessVariable[].class));
	}

	@Override
	protected String[] getPVNamesFromTransfer() {
		if(getCurrentEvent().data == null)
			return null;
		ProcessVariable[] pvArray = (ProcessVariable[])getCurrentEvent().data;
		List<String> pvList = new ArrayList<String>();
		for(ProcessVariable pv : pvArray){
			pvList.add(pv.getName());
		}
		return pvList.toArray(new String[pvList.size()]);
	}
}
