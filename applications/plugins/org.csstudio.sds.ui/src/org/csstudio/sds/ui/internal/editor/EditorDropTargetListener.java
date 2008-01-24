package org.csstudio.sds.ui.internal.editor;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.IProcessVariableAdressProvider;
import org.csstudio.platform.ui.dnd.rfc.ProcessVariableAddressTransfer;
import org.csstudio.platform.ui.dnd.rfc.ProcessVariableExchangeUtil;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.PlatformUI;

/**
 * This class represents a DropTargetListener, which allows to drag a PV onto a
 * DisplayEditor.
 * 
 * @author Kai Meyer
 */
public final class EditorDropTargetListener extends
		AbstractTransferDropTargetListener {

	/**
	 * Constructor. Creates a TargetListener for TextTransfer
	 * 
	 * @param viewer
	 *            The EditPartViewer for this TargetListener
	 */
	public EditorDropTargetListener(final EditPartViewer viewer) {
		super(viewer, TextTransfer.getInstance());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void updateTargetRequest() {
		((DropPvRequest) getTargetRequest()).setLocation(getDropLocation());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Request createTargetRequest() {
		DropPvRequest request = new DropPvRequest();
		return request;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void handleDragOver() {
		for (TransferData transfer : getCurrentEvent().dataTypes) {
			if (ProcessVariableAddressTransfer.getInstance().isSupportedType(
					transfer)) {
				getCurrentEvent().detail = DND.DROP_COPY;
				break;
			} else if (TextTransfer.getInstance().isSupportedType(transfer)) {
				getCurrentEvent().detail = DND.DROP_COPY;
				break;
			}
		}
		super.handleDragOver();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void handleDrop() {
		TransferData transferData = this.getCurrentEvent().currentDataType;

		if (ProcessVariableAddressTransfer.getInstance().isSupportedType(
				transferData)) {
			IProcessVariableAddress[] pvs = (IProcessVariableAddress[]) ProcessVariableAddressTransfer.getInstance()
			.nativeToJava(transferData);
			
			((DropPvRequest) getTargetRequest()).setProcessVariableAddress(pvs[0]);
		} else if (this.getCurrentEvent().data instanceof String) {
			final String pvName = ((String) getCurrentEvent().data);
			IProcessVariableAddress pv = ProcessVariableExchangeUtil
					.parseProcessVariableAdress(pvName, true);
			((DropPvRequest) getTargetRequest()).setProcessVariableAddress(pv);
		}
		super.handleDrop();
	}

}
