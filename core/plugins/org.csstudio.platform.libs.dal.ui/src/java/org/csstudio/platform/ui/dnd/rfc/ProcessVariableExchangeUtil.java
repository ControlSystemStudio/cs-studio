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
