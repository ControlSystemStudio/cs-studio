package org.csstudio.platform.ui.dnd.rfc;

import org.csstudio.platform.CSSPlatformPlugin;
import org.csstudio.platform.model.rfc.ControlSystemEnum;
import org.csstudio.platform.model.rfc.IPVAdressListProvider;
import org.csstudio.platform.model.rfc.IPVAdressProvider;
import org.csstudio.platform.model.rfc.IProcessVariableAdress;
import org.csstudio.platform.model.rfc.PvAdressFactory;
import org.csstudio.platform.ui.CSSPlatformUiPlugin;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

public class ProcessVariableExchangeUtil {

	public static void addProcessVariableAdressDragSupport(Control control,
			final int style, IPVAdressProvider provider) {
		DragSource dragSource = new DragSource(control, style);

		Transfer[] types = new Transfer[] { PVTransfer.getInstance(),
				TextTransfer.getInstance() };

		dragSource.setTransfer(types);

		dragSource.addDragListener(new ProcessVariableAdressDragSourceAdapter(
				provider));
	}

	public static void addProcessVariableAdressDragSupport(Control control,
			final int style, IPVAdressListProvider provider) {
		DragSource dragSource = new DragSource(control, style);

		Transfer[] types = new Transfer[] { PVTransfer.getInstance(),
				TextTransfer.getInstance() };

		dragSource.setTransfer(types);

		dragSource.addDragListener(new ProcessVariableAdressDragSourceAdapter(
				provider));
	}

	public static void addProcessVariableDropSupport(Control control,
			final int style, IProcessVariableAdressReceiver receiver) {
		addProcessVariableDropSupport(control, style, receiver,
				new IShowControlSystemDialogStrategy() {
					public boolean showControlSystem(String rawName) {
						return true;
					}
				});
	}

	public static void addProcessVariableDropSupport(Control control,
			final int style, IProcessVariableAdressReceiver receiver,
			IShowControlSystemDialogStrategy showControlSystemDialogStrategy) {
		assert control != null;
		assert receiver != null;
		assert showControlSystemDialogStrategy != null;

		DropTarget dropTarget = new DropTarget(control, style);
		Transfer[] transferTypes = new Transfer[] { TextTransfer.getInstance(),
				PVTransfer.getInstance() };
		dropTarget.setTransfer(transferTypes);
		dropTarget.addDropListener(new ProcessVariableAdressDropTargetAdapter(
				receiver, showControlSystemDialogStrategy));
	}

	public static IProcessVariableAdress parseProcessVariableAdress(
			String rawName, boolean showControlSystemDialog) {
		IProcessVariableAdress pv = null;
		if (PvAdressFactory.getInstance().hasValidControlSystemPrefix(rawName)) {
			pv = PvAdressFactory.getInstance().createProcessVariableAdress(
					rawName);
		} else {
			if (showControlSystemDialog
					&& PvAdressFactory.getInstance().askForControlSystem()) {
				ChooseControlSystemPrefixDialog dialog = new ChooseControlSystemPrefixDialog(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow()
								.getShell());

				if (dialog.open() == Window.OK) {
					// set preferences

					boolean askAgain = !dialog.dontAskAgain();

					CSSPlatformUiPlugin.getCorePreferenceStore().setValue(
							PvAdressFactory.PROP_ASK_FOR_CONTROL_SYSTEM,
							askAgain);
					CSSPlatformPlugin.getDefault().savePluginPreferences();

					ControlSystemEnum controlSystem = dialog
							.getSelectedControlSystem();

					pv = PvAdressFactory
							.getInstance()
							.createProcessVariableAdress(rawName, controlSystem);
				}
			} else {
				pv = PvAdressFactory.getInstance().createProcessVariableAdress(
						rawName);
			}
		}

		return pv;
	}

}
