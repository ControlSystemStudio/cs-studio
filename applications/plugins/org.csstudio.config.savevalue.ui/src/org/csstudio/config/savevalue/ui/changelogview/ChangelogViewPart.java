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
import static org.csstudio.utility.ldap.service.util.LdapUtils.any;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.IOC;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.UNIT;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.apache.log4j.Logger;
import org.csstudio.config.savevalue.service.ChangelogDeletionService;
import org.csstudio.config.savevalue.service.ChangelogEntry;
import org.csstudio.config.savevalue.service.ChangelogService;
import org.csstudio.config.savevalue.service.SaveValueServiceException;
import org.csstudio.config.savevalue.ui.Activator;
import org.csstudio.config.savevalue.ui.Messages;
import org.csstudio.config.savevalue.ui.RemoteMethodCallJob;
import org.csstudio.config.savevalue.ui.SaveValueDialog;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.service.ILdapContentModelBuilder;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.service.LdapServiceException;
import org.csstudio.utility.ldap.service.util.LdapUtils;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;


/**
 * @author Joerg Rathlev
 *
 */
public class ChangelogViewPart extends ViewPart {

	static final String ID = "org.csstudio.config.savevalue.ui.ChangelogView"; // $NON-NLS-1$

	private static final String MODIFIED_PROPERTY = "modified"; //$NON-NLS-1$
	private static final String HOST_PROPERTY = "host"; //$NON-NLS-1$
	private static final String USER_PROPERTY = "user"; //$NON-NLS-1$
	private static final String PV_PROPERTY = "pv"; //$NON-NLS-1$
	private static final String VALUE_PROPERTY = "value"; //$NON-NLS-1$

	/**
	 * The cell modifier for the view's table viewer.
	 */
	private class ChangelogViewCellModifier implements ICellModifier {

		/**
		 * {@inheritDoc}
		 */
		public boolean canModify(final Object element, final String property) {
			// only the "value" column is modifiable
			return VALUE_PROPERTY.equals(property);
		}

		/**
		 * {@inheritDoc}
		 */
		public Object getValue(final Object element, final String property) {
			if (VALUE_PROPERTY.equals(property)) {
				return ((ChangelogEntry) element).getValue();
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		public void modify(Object element, final String property, final Object value) {
			// element may be Item, see ICellModifier#modify method comment
			if (element instanceof Item) {
				element = ((Item) element).getData();
			}

			if (VALUE_PROPERTY.equals(property)) {
				final ChangelogEntry entry = (ChangelogEntry) element;
				final String val = (String) value;
				if (!val.equals(entry.getValue())) {
					final String pv = entry.getPvName();
					final SaveValueDialog dialog = new SaveValueDialog(null, pv, val);
					if (Window.OK == dialog.open()) {
						startChangelogRequest();
					}
				}
			}
		}
	}

	/**
	 * Actions which deletes the selected changelog entry.
	 *
	 * @author Joerg Rathlev
	 */
	private class DeleteChangelogEntryAction extends BaseSelectionListenerAction {

		/**
		 * Creates the action.
		 */
		protected DeleteChangelogEntryAction() {
			super(Messages.ChangelogViewPart_ActionText);
			setEnabled(updateSelection(getStructuredSelection()));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			final String ioc = _iocText.getText();
			if (ioc == null) {
				return;
			}
			final String pvName = getSelectedPvName();

			final Job job = new RemoteMethodCallJob(Messages.ChangelogViewPart_DeletionJob) {
				@Override
				protected IStatus runWithRmiRegistry(final Registry reg) {
					try {
						final Remote service = reg.lookup("SaveValue.changelog"); //$NON-NLS-1$
						if (service instanceof ChangelogDeletionService) {
							final ChangelogDeletionService deletionService =
								(ChangelogDeletionService) service;
							try {
								deletionService.deleteEntries(ioc, pvName);

								Display.getDefault().asyncExec(new Runnable() {
									public void run() {
										startChangelogRequest();
									}
								});
							} catch (final SaveValueServiceException e) {
								LOG.error("Server reported an error.", e); //$NON-NLS-1$
								showErrorDialog(Messages.ChangelogViewPart_DeletionServiceError);
							} catch (final RemoteException e) {
								LOG.error("Communication error.", e); //$NON-NLS-1$
								showErrorDialog(Messages.ChangelogViewPart_RemoteCallFailed);
							}
						} else {
							showErrorDialog(Messages.ChangelogViewPart_DeletionNotSupported);
						}
					} catch (final RemoteException e) {
						LOG.error("Could not connect to RMI registry", e); //$NON-NLS-1$
						showErrorDialog(
								Messages.ChangelogViewPart_ERRMSG_RMI_REGISTRY +
								e.getMessage());
					} catch (final NotBoundException e) {
						LOG.error("Changelog Service not bound in RMI registry", e); //$NON-NLS-1$
						showErrorDialog(
								Messages.ChangelogViewPart_ERRMSG_SERVICE_NOT_AVAILABLE);
					}
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}

		private String getSelectedPvName() {
			return ((ChangelogEntry) getStructuredSelection().getFirstElement()).getPvName();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean updateSelection(final IStructuredSelection selection) {
			return selection.size() == 1;
		}

	}

	/**
	 * The logger.
	 */
	private static final Logger LOG = CentralLogger.getInstance().getLogger(ChangelogViewPart.class);

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
		new String[] {
			PV_PROPERTY,
			VALUE_PROPERTY,
			USER_PROPERTY,
			HOST_PROPERTY,
			MODIFIED_PROPERTY
		};

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

		final Composite iocBar = new Composite(parent, SWT.NONE);
		iocBar.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		iocBar.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Label iocLabel = new Label(iocBar, SWT.NONE);
		iocLabel.setText(Messages.ChangelogViewPart_IOC_FIELD_LABEL);

		_iocText = new Combo(iocBar, SWT.BORDER);
		_iocText.setLayoutData(new RowData(100, SWT.DEFAULT));
		getAvailableIocsInBackground();

		final Button readChangelog = new Button(iocBar, SWT.PUSH);
		readChangelog.setText(Messages.ChangelogViewPart_GET_CHANGELOG_BUTTON);
		readChangelog.addSelectionListener(new SelectionAdapter() {
			@Override
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

		createContextMenu();
	}

	/**
	 * Runs a background job to get a list of available IOCs and populates the
	 * IOC entry field with them.
	 */
	private void getAvailableIocsInBackground() {
		final IWorkbenchSiteProgressService progressService =
			(IWorkbenchSiteProgressService) getSite().getAdapter(
					IWorkbenchSiteProgressService.class);
		final Job job = new Job(Messages.ChangelogViewPart_GET_IOC_JOB) {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				monitor.beginTask(Messages.ChangelogViewPart_GET_IOC_JOB,
						IProgressMonitor.UNKNOWN);
				final ILdapService service = Activator.getDefault().getLdapService();
				if (service == null) {
				    return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "LDAP Service currently unavailable."); //$NON-NLS-1$
				}
				final ILdapSearchResult result =
				    service.retrieveSearchResultSynchronously(LdapUtils.createLdapName(UNIT.getNodeTypeName(), UNIT.getUnitTypeValue()),
				                                              any(IOC.getNodeTypeName()),
				                                              SearchControls.SUBTREE_SCOPE);

                try {
                    final ILdapContentModelBuilder<LdapEpicsControlsConfiguration> builder =
                        service.getLdapContentModelBuilder(LdapEpicsControlsConfiguration.VIRTUAL_ROOT, result);
                    builder.build();
                    final ContentModel<LdapEpicsControlsConfiguration> model = builder.getModel();

                    final List<String> iocNames = new ArrayList<String>(model.getSimpleNames(LdapEpicsControlsConfiguration.IOC));

                    Collections.sort(iocNames);
                    Display.getDefault().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            _iocText.setItems(iocNames.toArray(new String[iocNames.size()]));
                        }
                    });

                } catch (final CreateContentModelException e) {
                    LOG.error("Content model could not be constructed due to invalid LDAP name for root component.", e); //$NON-NLS-1$
                    monitor.done();
                    return new Status(IStatus.ERROR, Activator.PLUGIN_ID, ""); //$NON-NLS-1$
                } catch (LdapServiceException e) {
                    LOG.error("Content model could not be constructed due LDAP service exception.", e); //$NON-NLS-1$
                    monitor.done();
                    return new Status(IStatus.ERROR, Activator.PLUGIN_ID, ""); //$NON-NLS-1$
                }

				monitor.done();
				return new Status(IStatus.OK, Activator.PLUGIN_ID, ""); //$NON-NLS-1$
			}
		};
		progressService.schedule(job, 0, true);
	}


	/**
	 * Creates the context menu for the table.
	 */
	private void createContextMenu() {
		final MenuManager menuManager = new MenuManager("#PopupMenu"); //$NON-NLS-1$

		final BaseSelectionListenerAction delete = new DeleteChangelogEntryAction();
		_table.addSelectionChangedListener(delete);
		menuManager.add(delete);

		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		final Menu contextMenu = menuManager.createContextMenu(_table.getControl());
		_table.getControl().setMenu(contextMenu);
		getSite().registerContextMenu(menuManager, _table);
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
		// The RMI request is run in its own thread, not in the UI thread.
		final Job job = new RemoteMethodCallJob(Messages.ChangelogViewPart_ChangelogRequestJob) {
			@Override
			protected IStatus runWithRmiRegistry(final Registry registry) {
				try {
					final ChangelogService cs = (ChangelogService) registry
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
				} catch (final RemoteException e) {
					LOG.error("Could not connect to RMI registry", e); //$NON-NLS-1$
					showErrorDialog(
							Messages.ChangelogViewPart_ERRMSG_RMI_REGISTRY +
							e.getMessage());
				} catch (final NotBoundException e) {
					LOG.error("Changelog Service not bound in RMI registry", e); //$NON-NLS-1$
					showErrorDialog(
							Messages.ChangelogViewPart_ERRMSG_SERVICE_NOT_AVAILABLE);
				} catch (final SaveValueServiceException e) {
					LOG.error("Server reported an rrror reading the changelog", e); //$NON-NLS-1$
					showErrorDialog(
							Messages.ChangelogViewPart_ERRMSG_SERVICE_NOT_AVAILABLE +
							e.getMessage());
				}
				return new Status(IStatus.OK, Activator.PLUGIN_ID, ""); //$NON-NLS-1$
			}
		};

		final IWorkbenchSiteProgressService progressService =
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

		final Table table = _table.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		addColumn(table, COL_PV, Messages.ChangelogViewPart_PV_COLUMN, 180);
		addColumn(table, COL_VALUE, Messages.ChangelogViewPart_VALUE_COLUMN, 100);
		addColumn(table, COL_USER, Messages.ChangelogViewPart_USER_COLUMN, 150);
		addColumn(table, COL_HOST, Messages.ChangelogViewPart_HOST_COLUMN, 150);
		addColumn(table, COL_MODIFIED, Messages.ChangelogViewPart_DATE_MODIFIED_COLUMN, 150);

		final CellEditor[] editors = new CellEditor[5];
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
		final TableColumn column = new TableColumn(table, SWT.LEFT, index);
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
