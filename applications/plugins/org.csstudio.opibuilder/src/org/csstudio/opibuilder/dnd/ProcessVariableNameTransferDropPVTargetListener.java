package org.csstudio.opibuilder.dnd;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableNameTransfer;
import org.eclipse.gef.EditPartViewer;

/**The Drop PV target listener for process variable name transfer.
 * @author Xihui Chen
 *
 */
public class ProcessVariableNameTransferDropPVTargetListener extends AbstractDropPVTargetListener {

	public ProcessVariableNameTransferDropPVTargetListener(EditPartViewer viewer) {
		super(viewer, ProcessVariableNameTransfer.getInstance());
	}

	@Override
	protected String[] getPVNamesFromTransfer() {
		if(getCurrentEvent().data == null)
			return null;
		IProcessVariable[] pvArray = (IProcessVariable[])getCurrentEvent().data;
		List<String> pvList = new ArrayList<String>();
		for(IProcessVariable pv : pvArray){
			pvList.add(pv.getName());
		}
		return pvList.toArray(new String[pvList.size()]);
	}
}
