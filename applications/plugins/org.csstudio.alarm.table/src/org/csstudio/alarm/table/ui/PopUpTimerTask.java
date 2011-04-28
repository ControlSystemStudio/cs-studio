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
package org.csstudio.alarm.table.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * The TimerTask checks the period of the last JMSAccess and closes the
 * connection if the period is longer than a threshold.
 * 
 * @author jhatje
 * @author $Author$
 * @version $Revision$
 * @since 15.05.2008
 */
public class PopUpTimerTask extends TimerTask {

	List<IExpirationLisener> _listeners = new ArrayList<IExpirationLisener>();
	private MessageDialog _dialog;
	private int _result = -1;

	@Override
	public synchronized void run() {
		// PopUp with warning
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				try {
					if (_dialog == null && _result == -1) {
						// System.out.println("dialog null, result -1: open dialog");
						_dialog = new MessageDialog(PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow().getShell(),
								"Message Table", null,
								"Die Tabelle wird derzeit nicht aktualisiert!",
								MessageDialog.WARNING, new String[] { "OK" }, 0);
						_result = _dialog.open();
					} else {
						if (_result == -1) {
							// System.out.println("dialog NOT null, result -1: close dialog, call listener");
							_dialog.close();
							for (IExpirationLisener listener : _listeners) {
								listener.expired();
							}
						} else {
							// System.out.println("dialog NOT null, result != -1: open dialog again");
							_result = -1;
							_dialog = new MessageDialog(PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getShell(),
									"Message Table", null,
									"Die Tabelle wird derzeit nicht aktualisiert!",
									MessageDialog.WARNING, new String[] { "OK" }, 0);
							_result = _dialog.open();
						}
					}
				} catch (Exception e) {
					// e.printStackTrace();
					JmsLogsPlugin.logException("Error while processing MessageDialog", e); //$NON-NLS-1$
				}
			}
		});
	}

	public void addExpirationListener(IExpirationLisener expirationLisener) {
		_listeners.add(expirationLisener);
	}
}
