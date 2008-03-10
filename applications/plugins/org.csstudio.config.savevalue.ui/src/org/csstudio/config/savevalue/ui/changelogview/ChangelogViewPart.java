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

import org.csstudio.config.savevalue.service.ChangelogEntry;
import org.csstudio.config.savevalue.service.ChangelogService;
import org.csstudio.config.savevalue.service.SaveValueServiceException;
import org.csstudio.config.savevalue.ui.Activator;
import org.csstudio.config.savevalue.ui.PreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Joerg Rathlev
 *
 */
public class ChangelogViewPart extends ViewPart {
	
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
	 * {@inheritDoc}
	 */
	@Override
	public final void createPartControl(final Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		
		Composite iocBar = new Composite(parent, SWT.NONE);
		iocBar.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		iocBar.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Label iocLabel = new Label(iocBar, SWT.NONE);
		iocLabel.setText("IOC:");
		
		final Text iocText = new Text(iocBar, SWT.BORDER);
		iocText.setLayoutData(new RowData(100, SWT.DEFAULT));
		
		Button readChangelog = new Button(iocBar, SWT.PUSH);
		readChangelog.setText("Get Changelog");
		readChangelog.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(final SelectionEvent e) {
				// ignore (not called by buttons)
			}

			public void widgetSelected(final SelectionEvent e) {
				final String ioc = iocText.getText();
				if (ioc == null) {
					return;
				}
				// The RMI request is run in its own thread, not in the UI
				// thread.
				Runnable r = new Runnable() {
					private Registry _reg;

					public void run() {
						try {
							locateRmiRegistry();
							ChangelogService cs = (ChangelogService) _reg
									.lookup("SaveValue.changelog");
							final ChangelogEntry[] entries = cs.readChangelog(ioc);
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									_table.setInput(entries);
								}
							});
						} catch (RemoteException e) {
							_log.error(this, "Could not connect to RMI registry", e);
							final String message = e.getMessage();
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									MessageDialog.openError(null, "Changelog",
											"Could not connect to RMI registry: "
													+ message);
								}
							});
						} catch (NotBoundException e) {
							_log.error(this, "Changelog Service not bound in RMI registry", e);
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									MessageDialog.openError(null, "Changelog",
											"The changelog service is not available.");
								}
							});
						} catch (SaveValueServiceException e) {
							_log.error(this, "Server reported an rrror reading the changelog", e);
							final String message = e.getMessage();
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									MessageDialog.openError(null, "Changelog",
											"Error reading the changelog: "
													+ message);
								}
							});
						}
					}

					/**
					 * @throws RemoteException
					 */
					private void locateRmiRegistry() throws RemoteException {
						IPreferencesService prefs = Platform.getPreferencesService();
						String registryHost = prefs.getString(
								Activator.PLUGIN_ID,
								PreferenceConstants.RMI_REGISTRY_SERVER,
								null, null);
						_log.debug(this, "Connecting to RMI registry.");
						_reg = LocateRegistry.getRegistry(registryHost);
					}
				};
				new Thread(r).start();
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
	 * Creates the table viewer.
	 * 
	 * @param parent the parent composite.
	 */
	private void createTableViewer(final Composite parent) {
		_table = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		
		Table table = _table.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		addColumn(table, COL_PV, "PV", 180);
		addColumn(table, COL_VALUE, "Value", 100);
		addColumn(table, COL_USER, "User", 150);
		addColumn(table, COL_HOST, "Host", 150);
		addColumn(table, COL_MODIFIED, "Date Modified", 150);
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
