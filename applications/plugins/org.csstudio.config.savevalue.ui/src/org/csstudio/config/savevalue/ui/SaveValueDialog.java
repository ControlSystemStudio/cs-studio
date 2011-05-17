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
package org.csstudio.config.savevalue.ui;

import static org.csstudio.utility.ldap.service.util.LdapFieldsAndAttributes.FIELD_ASSIGNMENT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.IOC;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.RECORD;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.UNIT;

import java.net.SocketTimeoutException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;

import org.apache.log4j.Logger;
import org.csstudio.auth.security.SecurityFacade;
import org.csstudio.auth.security.User;
import org.csstudio.config.savevalue.service.SaveValueRequest;
import org.csstudio.config.savevalue.service.SaveValueResult;
import org.csstudio.config.savevalue.service.SaveValueService;
import org.csstudio.config.savevalue.service.SaveValueServiceException;
import org.csstudio.platform.CSSPlatformInfo;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.service.util.LdapUtils;
import org.csstudio.utility.ldap.utils.LdapNameUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;


/**
 * Dialog for the Save Value function.
 *
 * @author Joerg Rathlev
 */
public class SaveValueDialog extends Dialog {

	/**
	 * The logger.
	 */
    private static final Logger LOG = CentralLogger.getInstance().getLogger(SaveValueDialog.class);
	/**
	 * Text box that displays the process variable.
	 */
	private Text _processVariable;

	/**
	 * Text box that displays the value that will be saved.
	 */
	private Text _valueTextField;

	/**
	 * Table that displays the save value service results.
	 */
	private Table _resultsTable;

	/**
	 * The name of the process variable.
	 */
	private final String _pv;

	/**
	 * The value to be saved.
	 */
	private final String _value;

	/**
	 * The text field which displays the IOC name.
	 */
	private Text _ioc;

	/**
	 * The name of the IOC.
	 */
	private String _iocName;

	/**
	 * The label displaying the overall result.
	 */
	private Label _resultLabel;

	/**
	 * The image indicating the overall result.
	 */
	private Label _resultImage;

	/**
	 * The services.
	 */
	private SaveValueServiceDescription[] _services;

	/**
	 * Creates a new Save Value Dialog.
	 *
	 * @param parentShell
	 *            the parent shell, or <code>null</code> to create a top-level
	 *            shell.
	 * @param pv
	 *            the process variable.
	 * @param value
	 *            the value.
	 */
	public SaveValueDialog(final Shell parentShell, final String pv, final String value) {
		super(parentShell);
		_pv = pv;
		_value = value;
		initializeServiceDescriptions();
	}

	/**
	 * Initializes the descriptions of the services that will be used.
	 */
	private void initializeServiceDescriptions() {
		final IPreferencesService prefs = Platform.getPreferencesService();
		_services = new SaveValueServiceDescription[] {
			new SaveValueServiceDescription("SaveValue.EpicsOra", //$NON-NLS-1$
					prefs.getBoolean(Activator.PLUGIN_ID,
							PreferenceConstants.EPIS_ORA_REQUIRED, false,
							null),
					Messages.EPICS_ORA_SERVICE_NAME),
			new SaveValueServiceDescription("SaveValue.Database", //$NON-NLS-1$
					prefs.getBoolean(Activator.PLUGIN_ID,
							PreferenceConstants.DATABASE_REQUIRED, false,
							null),
					Messages.DATABASE_SERVICE_NAME),
			new SaveValueServiceDescription("SaveValue.caput", //$NON-NLS-1$
					prefs.getBoolean(Activator.PLUGIN_ID,
							PreferenceConstants.CA_FILE_REQUIRED, false,
							null),
					Messages.CA_FILE_SERVICE_NAME),
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void configureShell(final Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.SaveValueDialog_DIALOG_TITLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final Control createDialogArea(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		applyDialogFont(composite);

		// PV Name
		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.SaveValueDialog_PV_FIELD_LABEL);
		_processVariable = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		_processVariable.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		_processVariable.setText(_pv);

		// IOC Name
		label = new Label(composite, SWT.NONE);
		label.setText(Messages.SaveValueDialog_IOC_FIELD_LABEL);
		_ioc = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		_ioc.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		_ioc.setText(_iocName);

		// value
		label = new Label(composite, SWT.NONE);
		label.setText(Messages.SaveValueDialog_VALUE_FIELD_LABEL);
		_valueTextField = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		_valueTextField.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		_valueTextField.setText(_value);

		final Label separator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));

		// results table
		_resultsTable = new Table(composite, SWT.BORDER);
		final GridData tableLayout = new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1);
		tableLayout.heightHint = 70;
		_resultsTable.setLayoutData(tableLayout);
		_resultsTable.setHeaderVisible(true);
		_resultsTable.setEnabled(false);
		final TableColumn stepColumn = new TableColumn(_resultsTable, SWT.LEFT);
		stepColumn.setText(Messages.SaveValueDialog_STEP_COLUMN);
		stepColumn.setWidth(80);
		final TableColumn resultColumn = new TableColumn(_resultsTable, SWT.LEFT);
		resultColumn.setText(Messages.SaveValueDialog_RESULT_COLUMN);
		resultColumn.setWidth(420);

		for (final SaveValueServiceDescription service : _services) {
			final TableItem item = new TableItem(_resultsTable, SWT.NONE);
			item.setText(service.getDisplayName());
		}

		// overall result
		_resultImage = new Label(composite, SWT.NONE);
		_resultImage.setLayoutData(new GridData(SWT.RIGHT, SWT.BEGINNING, false, false));
		// Assign an image here so that the correct size for the label is
		// computed. The actual image to be displayed is assigned when the
		// result is displayed.
		_resultImage.setImage(getErrorImage());
		_resultImage.setVisible(false);
		_resultLabel = new Label(composite, SWT.NONE);
		_resultLabel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		_resultLabel.setVisible(false);

		return composite;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void createButtonsForButtonBar(final Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Messages.SaveValueDialog_SAVE_BUTTON, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int open() {
		if (!hasRequiredService()) {
			MessageDialog.openError(null, Messages.SaveValueDialog_DIALOG_TITLE, Messages.SaveValueDialog_ERRMSG_NO_REQUIRED_SERVICES);
			return CANCEL;
		}

		final String detailErrorMessage = findIoc(_pv);
		if (detailErrorMessage != null) {
		    MessageDialog.openError(null,
		                            Messages.SaveValueDialog_DIALOG_TITLE,
		                            NLS.bind(Messages.SaveValueDialog_ERRMSG_IOC_NOT_FOUND, _pv) +
		                            Messages.SaveValueDialog_ERRMSG_DETAILS + detailErrorMessage);
		    return CANCEL;

		}

		return super.open();
	}



	/**
	 * Finds the IOC for the PV.
	 * @param pv
	 *
	 * @return <code>true</code> if the IOC was found, <code>false</code>
	 *         otherwise.
	 */
	private String findIoc(String pv) {
		LOG.debug("Trying to find IOC for process variable: " + pv); //$NON-NLS-1$
		if (pv.contains(".") && ProcessVariableAdressFactory.getInstance().getDefaultControlSystem() == ControlSystemEnum.EPICS) {
		    pv = pv.substring(0, pv.indexOf("."));
		}

		final ILdapService service = Activator.getDefault().getLdapService();
		if (service == null) {
		    return "LDAP service unavailable. Try again later!";
		}

	    final ILdapSearchResult result =
	        service.retrieveSearchResultSynchronously(LdapUtils.createLdapName(UNIT.getNodeTypeName(), UNIT.getUnitTypeValue()),
	                                                  RECORD.getNodeTypeName() + FIELD_ASSIGNMENT + pv,
	                                                  SearchControls.SUBTREE_SCOPE);

	    if (result == null || result.getAnswerSet().isEmpty()) {
	        return Messages.SaveValueDialog_ERRMSG_DETAIL_NO_ENTRY;
	    }
	    final Set<SearchResult> answerSet = result.getAnswerSet();
	    try {
	        if (answerSet.size() > 1) {
	            LOG.error("IOC could not be uniquely identified for PV: " + pv ); //$NON-NLS-1$
	            String iocs = "";
	            for (final SearchResult row : answerSet) {
                    final String nameInNamespace = row.getNameInNamespace();
                    iocs += "\n" + nameInNamespace;
	            }
	            return Messages.SaveValueDialog_ERRMSG_DETAIL_NOT_UNIQUE+ "\n" + iocs;
	        }

	        final SearchResult row = result.getAnswerSet().iterator().next();
	        final LdapName ldapName = LdapNameUtils.parseSearchResult(row);
	        if (ldapName != null) {
	            final String iocName = LdapNameUtils.getValueOfRdnType(ldapName, IOC.getNodeTypeName());
	            if (iocName != null) {
	                _iocName = iocName;
	                return null;
	            }
	        }
	        LOG.error("IOC name could not be parsed out of search result: " + row.getNameInNamespace()); //$NON-NLS-1$

	    } catch (final NamingException e) {
	        return Messages.SaveValueDialog_ERRMSG_DETAIL_IOC_NAME_UNPARSEABLE + "\n" + e.getLocalizedMessage();
	    }
	    return Messages.SaveValueDialog_ERRMSG_DETAIL_IOC_NAME_UNPARSEABLE;
	}

	/**
	 * Checks that at least one service is selected as required in the
	 * preferences.
	 *
	 * @return <code>true</code> if there is at least one required service,
	 *         <code>false</code> otherwise.
	 */
	private boolean hasRequiredService() {
		for (final SaveValueServiceDescription service : _services) {
			if (service.isRequired()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void buttonPressed(final int buttonId) {
		if (IDialogConstants.OK_ID == buttonId) {
			savePressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			// this is the close button after save was pressed
			if (_resultsTable.isEnabled()) {
				closePressed();
			} else {
				cancelPressed();
			}
		}
	}

	/**
	 * Called when the close button of this dialog has been pressed.
	 */
	private void closePressed() {
		setReturnCode(IDialogConstants.OK_ID);
		close();
	}

	/**
	 * Called when the save button of this dialog has been pressed.
	 */
	private void savePressed() {
		getButton(IDialogConstants.OK_ID).setVisible(false);
		getButton(IDialogConstants.CANCEL_ID).setText(IDialogConstants.CLOSE_LABEL);
		getButton(IDialogConstants.CANCEL_ID).setEnabled(false);
		_resultsTable.setEnabled(true);

		final Runnable r = new Runnable() {
			private Registry _reg;

			public void run() {
				final boolean[] success = new boolean[_services.length];
				try {
					locateRmiRegistry();
					for (int i = 0; i < _services.length; i++) {
						String result;
						success[i] = false;
						try {
							final SaveValueResult srr = callService(_services[i], _value);
							success[i] = true;
							final String replacedValue = srr.getReplacedValue();
							if (replacedValue != null) {
								result = NLS.bind(Messages.SaveValueDialog_SUCCESS_REPLACED, replacedValue);
							} else {
								result = Messages.SaveValueDialog_SUCCESS_NEW_ENTRY;
							}
						} catch (final RemoteException e) {
							final Throwable cause = e.getCause();
							if (cause instanceof SocketTimeoutException) {
								LOG.warn("Remote call to " + _services[i] + " timed out"); //$NON-NLS-1$ //$NON-NLS-2$
								result = Messages.SaveValueDialog_FAILED_TIMEOUT;
							} else {
								LOG.error("Remote call to " + _services[i] + " failed with RemoteException", e); //$NON-NLS-1$ //$NON-NLS-2$
								result = Messages.SaveValueDialog_FAILED_WITH_REMOTE_EXCEPTION + e.getMessage();
							}
						} catch (final SaveValueServiceException e) {
							LOG.warn("Save Value service " + _services[i] + " reported an error", e); //$NON-NLS-1$ //$NON-NLS-2$
							result = Messages.SaveValueDialog_FAILED_WITH_SERVICE_ERROR + e.getMessage();
						} catch (final NotBoundException e) {
							LOG.warn("Save value service " + _services[i] + " is not bound in RMI registry"); //$NON-NLS-1$ //$NON-NLS-2$
							result = Messages.SaveValueDialog_NOT_BOUND;
						}
						final int index = i;
						final String resultCopy = result;
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								final TableItem item = _resultsTable.getItem(index);
								item.setText(1, resultCopy);
								final int color = success[index] ? SWT.COLOR_DARK_GREEN
										: _services[index].isRequired() ? SWT.COLOR_RED : SWT.COLOR_DARK_GRAY;
								item.setForeground(Display.getCurrent().getSystemColor(color));
								_resultsTable.showItem(item);
							}
						});
					}
					LOG.debug("Finished calling remote Save Value services"); //$NON-NLS-1$
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							boolean overallSuccess = true;
							for (int i = 0; i < success.length; i++) {
								if (_services[i].isRequired()) {
									overallSuccess &= success[i];
								}
							}
							if (overallSuccess) {
								_resultLabel.setText(Messages.SaveValueDialog_RESULT_SUCCESS);
								_resultImage.setImage(getInfoImage());
							} else {
								_resultLabel.setText(Messages.SaveValueDialog_RESULT_ERROR_VALUE_NOT_SAVED);
								_resultImage.setImage(getErrorImage());
							}
							_resultLabel.setVisible(true);
							_resultImage.setVisible(true);
							final Button close = getButton(IDialogConstants.CANCEL_ID);
							close.setEnabled(true);
							close.setFocus();
						}
					});
				} catch (final RemoteException e) {
					LOG.error("Could not connect to RMI registry", e); //$NON-NLS-1$
					final String message = e.getMessage();
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(null, Messages.SaveValueDialog_DIALOG_TITLE,
									Messages.SaveValueDialog_ERRMSG_NO_RMI_REGISTRY
											+ message);
							SaveValueDialog.this.close();
						}
					});
				}
			}

			/**
			 * @throws RemoteException
			 */
			private void locateRmiRegistry() throws RemoteException {
				final IPreferencesService prefs = Platform.getPreferencesService();
				final String registryHost = prefs.getString(
						Activator.PLUGIN_ID,
						PreferenceConstants.RMI_REGISTRY_SERVER,
						null, null);
				LOG.debug("Connecting to RMI registry."); //$NON-NLS-1$
				_reg = LocateRegistry.getRegistry(registryHost);
			}

			/**
			 * @param service
			 * @param pvValue
			 * @return
			 * @throws SaveValueServiceException
			 * @throws RemoteException
			 * @throws NotBoundException
			 */
			private SaveValueResult callService(final SaveValueServiceDescription serviceDescr,
					final String pvValue)
					throws SaveValueServiceException, RemoteException, NotBoundException {
				LOG.debug("Calling save value service: " + serviceDescr); //$NON-NLS-1$
				final SaveValueService service = (SaveValueService) _reg.lookup(serviceDescr.getRmiName());
				final SaveValueRequest req = new SaveValueRequest();
				req.setPvName(_pv);
				req.setIocName(_iocName);
				req.setValue(pvValue);
				final User user = SecurityFacade.getInstance().getCurrentUser();
				if (user != null) {
					req.setUsername(user.getUsername());
				} else {
					req.setUsername("");
				}
				req.setHostname(CSSPlatformInfo.getInstance().getQualifiedHostname());
				final SaveValueResult srr = service.saveValue(req);
				return srr;
			}
		};
		new Thread(r).start();
	}


	/**
	 * Return the <code>Image</code> to be used when displaying information.
	 *
	 * @return image the information image
	 */
	// copied from org.eclipse.jface.dialogs.IconAndMessageDialog
	public Image getInfoImage() {
		return getSWTImage(SWT.ICON_INFORMATION);
	}

	/**
	 * Return the <code>Image</code> to be used when displaying an error.
	 *
	 * @return image the error image
	 */
	// copied from org.eclipse.jface.dialogs.IconAndMessageDialog
	public Image getErrorImage() {
		return getSWTImage(SWT.ICON_ERROR);
	}

	/**
	 * Get an <code>Image</code> from the provide SWT image constant.
	 *
	 * @param imageID
	 *            the SWT image constant
	 * @return image the image
	 */
	// copied from org.eclipse.jface.dialogs.IconAndMessageDialog
	private Image getSWTImage(final int imageID) {
		Shell shell = getShell();
		final Display display;
		if (shell == null) {
			shell = getParentShell();
		}
		if (shell == null) {
			display = Display.getCurrent();
		} else {
			display = shell.getDisplay();
		}

		final Image[] image = new Image[1];
		display.syncExec(new Runnable() {
			public void run() {
				image[0] = display.getSystemImage(imageID);
			}
		});

		return image[0];

	}

}
