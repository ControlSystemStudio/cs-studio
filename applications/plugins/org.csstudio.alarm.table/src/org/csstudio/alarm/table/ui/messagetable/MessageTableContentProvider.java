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

package org.csstudio.alarm.table.ui.messagetable;
import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.dataModel.BasicMessage;
import org.csstudio.alarm.table.dataModel.IMessageViewer;
import org.csstudio.alarm.table.dataModel.AbstractMessageList;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author jhatje
 *
 */
public class MessageTableContentProvider implements IMessageViewer,
		IStructuredContentProvider {

	private final TableViewer _tableViewer;

	private final AbstractMessageList _messageList;

	/**
	 * is message update for the table paused.
	 */
	boolean _pause = false;

	public MessageTableContentProvider(final TableViewer tv, final AbstractMessageList jmsml) {
		_tableViewer = tv;
		_messageList = jmsml;
	}

	public void addJMSMessage(final BasicMessage jmsm) {
		if (_pause) {
			return;
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					if (!_tableViewer.getTable().isDisposed()) {
						_tableViewer.add(jmsm);
					}
					// LOG.debug(this,
					// "Add Message, Number of Msg in Model: " +
					// _messageList.getSize() + "; Number of Msg in Table: " +
					// _tableViewer.getTable().getItemCount());
				} catch (final Exception e) {
					e.printStackTrace();
					JmsLogsPlugin.logException("", e); //$NON-NLS-1$
				}
			}
		});

	}

	public void addJMSMessages(final BasicMessage[] jmsm) {
		if (_pause) {
			return;
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					_tableViewer.add(jmsm);
					// LOG.debug(this,
					// "Add Messages[], Number of Msg in Model: " +
					// _messageList.getSize() + "; Number of Msg in Table: " +
					// _tableViewer.getTable().getItemCount());
				} catch (final Exception e) {
					e.printStackTrace();
					JmsLogsPlugin.logException("", e); //$NON-NLS-1$
				}
			}
		});
	}

	public void removeJMSMessage(final BasicMessage jmsm) {
		if (_pause) {
			return;
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					if (!_tableViewer.getTable().isDisposed()) {
						_tableViewer.remove(jmsm);
					}
					// LOG.debug(this,
					// "Remove Message, Number of Msg in Model: " +
					// _messageList.getSize() + "; Number of Msg in Table: " +
					// _tableViewer.getTable().getItemCount());
				} catch (final Exception e) {
					e.printStackTrace();
					JmsLogsPlugin.logException("", e); //$NON-NLS-1$
				}
			}
		});
	}

	public void removeJMSMessage(final BasicMessage[] jmsm) {
		if (_pause) {
			return;
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					_tableViewer.remove(jmsm);
					// LOG.debug(this,
					// "Remove Messages[], Number of Msg in Model: " +
					// _messageList.getSize() + "; Number of Msg in Table: " +
					// _tableViewer.getTable().getItemCount());
				} catch (final Exception e) {
					e.printStackTrace();
					JmsLogsPlugin.logException("", e); //$NON-NLS-1$
				}
			}
		});
	}

	public void updateJMSMessage(final BasicMessage jmsm) {
		if (_pause) {
			return;
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					if (jmsm.getProperty(AlarmMessageKey.ACK.getDefiningName()) == null) {
						_tableViewer.update(jmsm, null);
					}
					for (int i = 0; i < _tableViewer.getTable().getItemCount(); i++) {
						final TableItem directTableItem = _tableViewer.getTable()
								.getItem(i);
						final Object item = directTableItem.getData();
						if (item instanceof BasicMessage) {
							final BasicMessage messageInTable = (BasicMessage) item;
							final String timeProp = jmsm.getProperty(AlarmMessageKey.EVENTTIME.getDefiningName());
                            final String nameProp = jmsm.getProperty(AlarmMessageKey.NAME.getDefiningName());
                            if (((nameProp != null) && nameProp.equals(messageInTable.getProperty(AlarmMessageKey.NAME.getDefiningName()))) &&
                                (timeProp != null) && (timeProp.equals(messageInTable.getProperty(AlarmMessageKey.EVENTTIME.getDefiningName())))) {
								directTableItem.setChecked(true);
								// _tableViewer.refresh();
								_tableViewer.update(item,
										new String[] { AlarmMessageKey.ACK.getDefiningName() });
								break;
							}
						}
					}
				} catch (final Exception e) {
					e.printStackTrace();
					JmsLogsPlugin.logException("", e); //$NON-NLS-1$
				}
				// LOG.debug(this,
				// "Update Message, Number of Msg in Model: " +
				// _messageList.getSize() + "; Number of Msg in Table: " +
				// _tableViewer.getTable().getItemCount());
			}
		});
	}

	public void dispose() {
		_messageList.removeChangeListener(this);
	}

	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		if (newInput != null) {
            ((AbstractMessageList) newInput).addChangeListener(this);
        }
		if (oldInput != null) {
            ((AbstractMessageList) oldInput).removeChangeListener(this);
        }
	}

	@Override
	public Object[] getElements(final Object inputElement) {
		return _messageList.getMessageList().toArray();
	}

	public void setMessageUpdatePause(final boolean pause) {
		_pause = pause;
	}

	public boolean getMessageUpdatePause() {
		return _pause;
	}
}
