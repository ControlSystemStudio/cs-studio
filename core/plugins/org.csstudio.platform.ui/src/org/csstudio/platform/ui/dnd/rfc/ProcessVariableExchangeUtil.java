package org.csstudio.platform.ui.dnd.rfc;

import org.csstudio.platform.CSSPlatformPlugin;
import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.IProcessVariableAdressProvider;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.ui.CSSPlatformUiPlugin;
import org.csstudio.platform.ui.internal.dnd.ChooseControlSystemPrefixDialog;
import org.csstudio.platform.ui.internal.dnd.ProcessVariableAdressDragSourceAdapter;
import org.csstudio.platform.ui.internal.dnd.ProcessVariableAdressDropTargetAdapter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

public class ProcessVariableExchangeUtil {

	public static void addProcessVariableAdressDragSupport(Control control,
			final int style, IProcessVariableAdressProvider provider) {
		DragSource dragSource = new DragSource(control, style);

		Transfer[] types = new Transfer[] {
				ProcessVariableAddressTransfer.getInstance(),
				TextTransfer.getInstance() };

		dragSource.setTransfer(types);

		dragSource.addDragListener(new ProcessVariableAdressDragSourceAdapter(
				provider));
	}

	public static void addProcessVariableAddressDropSupport(Control control,
			final int style, IProcessVariableAdressReceiver receiver) {
		addProcessVariableAddressDropSupport(control, style, receiver,
				new IShowControlSystemDialogStrategy() {
					public boolean showControlSystem(String rawName) {
						return true;
					}
				});
	}

	public static void addProcessVariableAddressDropSupport(Control control,
			final int style, IProcessVariableAdressReceiver receiver,
			IShowControlSystemDialogStrategy showControlSystemDialogStrategy) {
		assert control != null;
		assert receiver != null;
		assert showControlSystemDialogStrategy != null;

		DropTarget dropTarget = new DropTarget(control, style);
		Transfer[] transferTypes = new Transfer[] { TextTransfer.getInstance(),
				ProcessVariableAddressTransfer.getInstance() };
		dropTarget.setTransfer(transferTypes);
		dropTarget.addDropListener(new ProcessVariableAdressDropTargetAdapter(
				receiver, showControlSystemDialogStrategy));
	}

	public static IProcessVariableAddress parseProcessVariableAdress(
			String rawName, boolean showControlSystemDialog) {
		IProcessVariableAddress pv = null;
		if (ProcessVariableAdressFactory.getInstance()
				.hasValidControlSystemPrefix(rawName)) {
			pv = ProcessVariableAdressFactory.getInstance()
					.createProcessVariableAdress(rawName);
		} else {
			if (showControlSystemDialog
					&& ProcessVariableAdressFactory.getInstance()
							.askForControlSystem()) {
				ChooseControlSystemPrefixDialog dialog = new ChooseControlSystemPrefixDialog(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow()
								.getShell());

				if (dialog.open() == Window.OK) {
					// set preferences

					boolean askAgain = !dialog.dontAskAgain();

					CSSPlatformUiPlugin
							.getCorePreferenceStore()
							.setValue(
									ProcessVariableAdressFactory.PROP_ASK_FOR_CONTROL_SYSTEM,
									askAgain);
					CSSPlatformPlugin.getDefault().savePluginPreferences();

					ControlSystemEnum controlSystem = dialog
							.getSelectedControlSystem();

					pv = ProcessVariableAdressFactory
							.getInstance()
							.createProcessVariableAdress(rawName, controlSystem);
				}
			} else {
				pv = ProcessVariableAdressFactory.getInstance()
						.createProcessVariableAdress(rawName);
			}
		}

		return pv;
	}

}
