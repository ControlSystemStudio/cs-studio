package org.csstudio.opibuilder.dnd;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;

/**The listener when a pv name related transfer data is dropped to the viewer.
 * @author Xihui Chen
 *
 */
public abstract class AbstractDropPVTargetListener extends AbstractTransferDropTargetListener {
	

	public AbstractDropPVTargetListener(EditPartViewer viewer, Transfer xfer) {
		super(viewer, xfer);
	}

	@Override
	protected void updateTargetRequest() {
		((DropPVRequest)getTargetRequest()).setLocation(getDropLocation());
	}
	
	@Override
	protected void updateTargetEditPart() {
		super.updateTargetEditPart();
		if(getTargetEditPart() instanceof AbstractBaseEditPart)
			((DropPVRequest)getTargetRequest()).setTargetWidget(
					(AbstractBaseEditPart) getTargetEditPart());

	}
	
	@Override
	protected Request createTargetRequest() {		
		return new DropPVRequest();
	}
	
	@Override
	protected void handleDragOver() {
		getCurrentEvent().detail = DND.DROP_COPY;
		super.handleDragOver();
	}
	
	@Override
	protected void handleDrop() {
		String[] pvNames = getPVNamesFromTransfer();
		((DropPVRequest)getTargetRequest()).setPvNames(pvNames);
		super.handleDrop();
	}
	
	/**
	 * @return the PV Name array from transfer.
	 */
	protected abstract String[] getPVNamesFromTransfer();

}
