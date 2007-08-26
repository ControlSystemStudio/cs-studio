/**
 * 
 */
package org.csstudio.platform.ui.internal.dnd;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.ui.dnd.rfc.IProcessVariableAdressReceiver;
import org.csstudio.platform.ui.dnd.rfc.IShowControlSystemDialogStrategy;
import org.csstudio.platform.ui.dnd.rfc.ProcessVariableAddressTransfer;
import org.csstudio.platform.ui.dnd.rfc.ProcessVariableExchangeUtil;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;

public class ProcessVariableAdressDropTargetAdapter extends DropTargetAdapter {
	private IProcessVariableAdressReceiver _receiver;

	private IShowControlSystemDialogStrategy _showControlSystemDialogStrategy;

	public ProcessVariableAdressDropTargetAdapter(
			IProcessVariableAdressReceiver pvCallback,
			IShowControlSystemDialogStrategy showControlSystemDialogStrategy) {
		assert pvCallback != null;
		assert showControlSystemDialogStrategy != null;
		_receiver = pvCallback;
		_showControlSystemDialogStrategy = showControlSystemDialogStrategy;
	}

	@Override
	public void drop(final DropTargetEvent event) {
		IProcessVariableAddress[] pvs = new IProcessVariableAddress[0];

		if (ProcessVariableAddressTransfer.getInstance().isSupportedType(event.currentDataType)) {
			pvs = (IProcessVariableAddress[]) ProcessVariableAddressTransfer.getInstance()
					.nativeToJava(event.currentDataType);
		} else if (TextTransfer.getInstance().isSupportedType(
				event.currentDataType)) {
			String rawName = (String) TextTransfer.getInstance().nativeToJava(
					event.currentDataType);

			// TODO: Strategy einbauen, an welcher sich entscheidet, ob nach dem
			// Control-Systeme gefragt wird (z.B. sollte beim Drop von Strings
			// mit Aliasen nicht
			// auf Krampf versucht werden, eine PV aufzulösen)
			IProcessVariableAddress pv = ProcessVariableExchangeUtil
					.parseProcessVariableAdress(rawName,
							_showControlSystemDialogStrategy
									.showControlSystem(rawName));

			pvs = new IProcessVariableAddress[] { pv };
		}

		if (pvs.length > 0) {
			_receiver.receive(pvs, event);
		}
	}

	@Override
	public void dropAccept(final DropTargetEvent event) {
		if (!isSupportedType(event)) {
			event.detail = DND.DROP_NONE;
		} else {
			event.detail = DND.DROP_COPY;
		}
	}

	@Override
	public void dragEnter(final DropTargetEvent event) {
		if (!isSupportedType(event)) {
			event.detail = DND.DROP_NONE;
		} else {
			event.detail = DND.DROP_COPY;
		}
	}

	@Override
	public void dragOver(final DropTargetEvent event) {
		if (!isSupportedType(event)) {
			event.detail = DND.DROP_NONE;
		} else {
			event.detail = DND.DROP_COPY;
		}
	}

	private boolean isSupportedType(DropTargetEvent event) {
		boolean supported = (ProcessVariableAddressTransfer.getInstance().isSupportedType(
				event.currentDataType) || TextTransfer.getInstance()
				.isSupportedType(event.currentDataType));

		return supported;
	}
}