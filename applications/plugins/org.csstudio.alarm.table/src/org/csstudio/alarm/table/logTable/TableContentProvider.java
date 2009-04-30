/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.alarm.table.logTable;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.dataModel.IJMSMessageViewer;
import org.csstudio.alarm.table.dataModel.JMSMessage;
import org.csstudio.alarm.table.dataModel.JMSMessageList;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

/**
 * @author jhatje
 * 
 */
public class TableContentProvider implements IJMSMessageViewer,
		IStructuredContentProvider {

	private JMSLogTableViewer tableViewer;

	private JMSMessageList messageList;

	public TableContentProvider(JMSLogTableViewer tv, JMSMessageList jmsml) {
		tableViewer = tv;
		messageList = jmsml;
	}

	public void addJMSMessage(final JMSMessage jmsm) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					tableViewer.add(jmsm);
				} catch (Exception e) {
					e.printStackTrace();
					JmsLogsPlugin.logException("", e); //$NON-NLS-1$
				}
			}
		});

	}

	public void addJMSMessages(final JMSMessage[] jmsm) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					tableViewer.add(jmsm);
				} catch (Exception e) {
					e.printStackTrace();
					JmsLogsPlugin.logException("", e); //$NON-NLS-1$
				}
			}
		});
	}

	public void removeJMSMessage(final JMSMessage jmsm) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					tableViewer.remove(jmsm);
				} catch (Exception e) {
					e.printStackTrace();
					JmsLogsPlugin.logException("", e); //$NON-NLS-1$
				}
			}
		});
	}

	public void removeJMSMessage(final JMSMessage[] jmsm) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					tableViewer.remove(jmsm);
				} catch (Exception e) {
					e.printStackTrace();
					JmsLogsPlugin.logException("", e); //$NON-NLS-1$
				}
			}
		});
	}

	public void updateJMSMessage(final JMSMessage jmsm) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					tableViewer.update(jmsm, null);
				} catch (Exception e) {
					e.printStackTrace();
					JmsLogsPlugin.logException("", e); //$NON-NLS-1$
				}
			}
		});
	}

	public void dispose() {
		messageList.removeChangeListener(this);
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput != null)
			((JMSMessageList) newInput).addChangeListener(this);
		if (oldInput != null)
			((JMSMessageList) oldInput).removeChangeListener(this);
	}

	public Object[] getElements(Object inputElement) {
		return messageList.getJMSMessageList().toArray();
	}
}
