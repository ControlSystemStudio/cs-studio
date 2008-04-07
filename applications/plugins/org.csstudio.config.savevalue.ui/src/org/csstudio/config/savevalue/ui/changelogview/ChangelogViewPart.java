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

package org.csstudio.config.savevalue.ui.changelogview;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collections;
import java.util.List;

import org.csstudio.config.savevalue.service.ChangelogEntry;
import org.csstudio.config.savevalue.service.ChangelogService;
import org.csstudio.config.savevalue.service.SaveValueServiceException;
import org.csstudio.config.savevalue.ui.Activator;
import org.csstudio.config.savevalue.ui.Messages;
import org.csstudio.config.savevalue.ui.PreferenceConstants;
import org.csstudio.config.savevalue.ui.SaveValueDialog;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.reader.IocFinder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

/**
 * @author Joerg Rathlev
 *
 */
public class ChangelogViewPart extends ViewPart {
	
	/**
	 * The cell modifier for the view's table viewer.
	 */
	private class ChangelogViewCellModifier implements ICellModifier {

		/**
		 * {@inheritDoc}
		 */
		public boolean canModify(Object element, String property) {
			// only the "value" column is modifiable
			return "value".equals(property);
		}

		/**
		 * {@inheritDoc}
		 */
		public Object getValue(Object element, String property) {
			if ("value".equals(property)) {
				return ((ChangelogEntry) element).getValue();
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		public void modify(Object element, String property, Object value) {
			// element may be Item, see ICellModifier#modify method comment
			if (element instanceof Item) {
				element = ((Item) element).getData();
			}
			
			if ("value".equals(property)) {
				ChangelogEntry entry = (ChangelogEntry) element;
				String val = (String) value;
				if (!val.equals(entry.getValue())) {
					String pv = entry.getPvName();
					SaveValueDialog dialog = new SaveValueDialog(null, pv, val);
					if (Window.OK == dialog.open()) {
						startChangelogRequest();
					}
				}
			}
		}
	}

	/**
	 * The logger.
	 */
	private final CentralLogger _log = CentralLogger.getInstance();

	/**
	 * The table viewer for the changelog entries.
	 */
	private TableViewer _table;
	
	/**
	 * Column index for the PV column.
	 */
	static final int COL_PV = 0;
	
	/**
	 * Column index for the value column.
	 */
	static final int COL_VALUE = 1;
	
	/**
	 * Column index for the user column.
	 */
	static final int COL_USER = 2;

	/**
	 * Column index for the host column.
	 */
	static final int COL_HOST = 3;
	
	/**
	 * Column index for the last modified column.
	 */
	static final int COL_MODIFIED = 4;
	
	/**
	 * The column properties which are used to identify the columns in cell
	 * modifiers.
	 */
	static final String[] COLUMN_PROPERTIES =
		new String[] { "pv", "value", "user", "host", "modified" };

	/**
	 * The combo with the IOC names.
	 */
	private Combo _iocText;
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void createPartControl(final Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		
		Composite iocBar = new Composite(parent, SWT.NONE);
		iocBar.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		iocBar.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Label iocLabel = new Label(iocBar, SWT.NONE);
		iocLabel.setText(Messages.ChangelogViewPart_IOC_FIELD_LABEL);
		
		_iocText = new Combo(iocBar, SWT.BORDER);
		_iocText.setLayoutData(new RowData(100, SWT.DEFAULT));
		
		IWorkbenchSiteProgressService progressService =
			(IWorkbenchSiteProgressService) getSite().getAdapter(
					IWorkbenchSiteProgressService.class);
		Job job = new Job(Messages.ChangelogViewPart_GET_IOC_JOB) {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				monitor.beginTask(Messages.ChangelogViewPart_GET_IOC_JOB,
						IProgressMonitor.UNKNOWN);
				final List<String> iocs = IocFinder.getIocList();
				Collections.sort(iocs);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						_iocText.setItems(iocs.toArray(new String[iocs.size()]));
					}
				});
				monitor.done();
				return new Status(IStatus.OK, Activator.PLUGIN_ID, "");
			}
		};
		progressService.schedule(job, 0, true);
		
		Button readChangelog = new Button(iocBar, SWT.PUSH);
		readChangelog.setText(Messages.ChangelogViewPart_GET_CHANGELOG_BUTTON);
		readChangelog.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(final SelectionEvent e) {
				// ignore (not called by buttons)
			}

			public void widgetSelected(final SelectionEvent e) {
				startChangelogRequest();
			}
		});
		
		// create the table viewer
		createTableViewer(parent);
		_table.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		_table.setContentProvider(new ChangelogContentProvider());
		_table.setLabelProvider(new ChangelogLabelProvider());
		
		// the table viewer is the selection provider for the site
		getSite().setSelectionProvider(_table);
	}


	/**
	 * Starts a new changelog request. The request will run asynchronously and
	 * will refresh the contents of the viewer when complete.
	 */
	private void startChangelogRequest() {
		final String ioc = _iocText.getText();
		if (ioc == null) {
			return;
		}
		// The RMI request is run in its own thread, not in the UI
		// thread.
		Job job = new Job("") {
			private Registry _reg;

			/**
			 * @throws RemoteException
			 */
			private void locateRmiRegistry() throws RemoteException {
				IPreferencesService prefs = Platform.getPreferencesService();
				String registryHost = prefs.getString(
						Activator.PLUGIN_ID,
						PreferenceConstants.RMI_REGISTRY_SERVER,
						null, null);
				_log.debug(this, "Connecting to RMI registry."); //$NON-NLS-1$
				_reg = LocateRegistry.getRegistry(registryHost);
			}

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("",
						IProgressMonitor.UNKNOWN);

				try {
					locateRmiRegistry();
					ChangelogService cs = (ChangelogService) _reg
							.lookup("SaveValue.changelog"); //$NON-NLS-1$
					final ChangelogEntry[] entries = cs.readChangelog(ioc);
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							_table.setInput(entries);
							
							// if the list of entries is empty, we also
							// display a message so the user knows
							// what is going on
							if (entries.length == 0) {
								MessageDialog.openInformation(null,
										Messages.ChangelogViewPart_TITLE,
										Messages.ChangeLogViewPart_NO_ENTRIES);
							}
						}
					});
				} catch (RemoteException e) {
					_log.error(this, "Could not connect to RMI registry", e); //$NON-NLS-1$
					final String message = e.getMessage();
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(null, Messages.ChangelogViewPart_TITLE,
									Messages.ChangelogViewPart_ERRMSG_RMI_REGISTRY
											+ message);
						}
					});
				} catch (NotBoundException e) {
					_log.error(this, "Changelog Service not bound in RMI registry", e); //$NON-NLS-1$
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(null, Messages.ChangelogViewPart_TITLE,
									Messages.ChangelogViewPart_ERRMSG_SERVICE_NOT_AVAILABLE);
						}
					});
				} catch (SaveValueServiceException e) {
					_log.error(this, "Server reported an rrror reading the changelog", e); //$NON-NLS-1$
					final String message = e.getMessage();
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(null, Messages.ChangelogViewPart_TITLE,
									Messages.ChangelogViewPart_ERRMSG_READ_ERROR
											+ message);
						}
					});
				}
				
				monitor.done();
				return new Status(IStatus.OK, Activator.PLUGIN_ID, "");
			}
		};
		
		IWorkbenchSiteProgressService progressService =
			(IWorkbenchSiteProgressService) getSite().getAdapter(
					IWorkbenchSiteProgressService.class);
		progressService.schedule(job, 0, true);
	}


	/**
	 * Creates the table viewer.
	 * 
	 * @param parent the parent composite.
	 */
	private void createTableViewer(final Composite parent) {
		_table = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		_table.setColumnProperties(COLUMN_PROPERTIES);
		
		Table table = _table.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		addColumn(table, COL_PV, Messages.ChangelogViewPart_PV_COLUMN, 180);
		addColumn(table, COL_VALUE, Messages.ChangelogViewPart_VALUE_COLUMN, 100);
		addColumn(table, COL_USER, Messages.ChangelogViewPart_USER_COLUMN, 150);
		addColumn(table, COL_HOST, Messages.ChangelogViewPart_HOST_COLUMN, 150);
		addColumn(table, COL_MODIFIED, Messages.ChangelogViewPart_DATE_MODIFIED_COLUMN, 150);
		
		CellEditor[] editors = new CellEditor[5];
		editors[1] = new TextCellEditor(table);
		_table.setCellEditors(editors);
		_table.setCellModifier(new ChangelogViewCellModifier());
	}
	
	/**
	 * Adds a column to the given table.
	 * 
	 * @param table the table.
	 * @param index the column index.
	 * @param text the column text.
	 * @param width the width of the column.
	 */
	private void addColumn(final Table table, final int index, final String text, final int width) {
		TableColumn column = new TableColumn(table, SWT.LEFT, index);
		column.setText(text);
		column.setWidth(width);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setFocus() {
		_table.getControl().setFocus();
	}

}
