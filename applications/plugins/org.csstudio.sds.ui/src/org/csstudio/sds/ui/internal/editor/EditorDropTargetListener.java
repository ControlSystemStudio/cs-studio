/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.sds.ui.internal.editor;

import org.csstudio.dal.ui.dnd.rfc.ProcessVariableAddressTransfer;
import org.csstudio.dal.ui.dnd.rfc.ProcessVariableExchangeUtil;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;

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
		final DropPvRequest request = new DropPvRequest();
		return request;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void handleDragOver() {
		for (final TransferData transfer : getCurrentEvent().dataTypes) {
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
		final TransferData transferData = this.getCurrentEvent().currentDataType;

		if (ProcessVariableAddressTransfer.getInstance().isSupportedType(
				transferData)) {
			final IProcessVariableAddress[] pvs = (IProcessVariableAddress[]) ProcessVariableAddressTransfer.getInstance()
			.nativeToJava(transferData);

			((DropPvRequest) getTargetRequest()).setProcessVariableAddress(pvs[0]);
		} else if (this.getCurrentEvent().data instanceof String) {
			final String pvName = (String) getCurrentEvent().data;
			final IProcessVariableAddress pv = ProcessVariableExchangeUtil
					.parseProcessVariableAdress(pvName, true);
			((DropPvRequest) getTargetRequest()).setProcessVariableAddress(pv);
		}
		super.handleDrop();
	}

}
