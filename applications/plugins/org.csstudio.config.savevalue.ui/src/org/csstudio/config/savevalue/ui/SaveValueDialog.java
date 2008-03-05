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

import java.net.SocketTimeoutException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.csstudio.config.savevalue.service.SaveValueRequest;
import org.csstudio.config.savevalue.service.SaveValueResult;
import org.csstudio.config.savevalue.service.SaveValueService;
import org.csstudio.config.savevalue.service.SaveValueServiceException;
import org.csstudio.platform.CSSPlatformInfo;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.security.SecurityFacade;
import org.csstudio.utility.ldap.reader.IocFinder;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
	private final CentralLogger _log = CentralLogger.getInstance();

	/**
	 * Text box that displays the process variable. 
	 */
	private Text _processVariable;
	
	/**
	 * Text box that displays the value that will be saved.
	 */
	private Text _value;
	
	/**
	 * Table that displays the save value service results.
	 */
	private Table _resultsTable;
	
	/**
	 * The name of the process variable.
	 */
	private IProcessVariable _pv;

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
	 * Creates a new Save Value Dialog.
	 * 
	 * @param parentShell
	 *            the parent shell, or <code>null</code> to create a top-level
	 *            shell.
	 * @param pv
	 *            the process variable.
	 */
	public SaveValueDialog(final Shell parentShell, final IProcessVariable pv) {
		super(parentShell);
		_pv = pv;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void configureShell(final Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Save Value");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final Control createDialogArea(final Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		applyDialogFont(composite);
		
		// PV Name
		Label label = new Label(composite, SWT.NONE);
		label.setText("Process Variable:");
		_processVariable = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		_processVariable.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		_processVariable.setText(_pv.getName());
		
		// IOC Name
		label = new Label(composite, SWT.NONE);
		label.setText("IOC:");
		_ioc = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		_ioc.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		_ioc.setText(_iocName);
		
		// value
		label = new Label(composite, SWT.NONE);
		label.setText("Value:");
		_value = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		_value.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		_value.setText("42");
		
		Label separator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
		// results table
		_resultsTable = new Table(composite, SWT.BORDER);
		GridData tableLayout = new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1);
		tableLayout.heightHint = 70;
		_resultsTable.setLayoutData(tableLayout);
		_resultsTable.setHeaderVisible(true);
		_resultsTable.setEnabled(false);
		TableColumn stepColumn = new TableColumn(_resultsTable, SWT.LEFT);
		stepColumn.setText("Step");
		stepColumn.setWidth(80);
		TableColumn resultColumn = new TableColumn(_resultsTable, SWT.LEFT);
		resultColumn.setText("Result");
		resultColumn.setWidth(350);
		
		TableItem item1 = new TableItem(_resultsTable, SWT.NONE);
		item1.setText(new String[] { "EPICS Ora", "" });
		TableItem item2 = new TableItem(_resultsTable, SWT.NONE);
		item2.setText(new String[] { "Database", "" });
		TableItem item3 = new TableItem(_resultsTable, SWT.NONE);
		item3.setText(new String[] { "ca_put", "" });
		
		// overall result
		_resultImage = new Label(composite, SWT.NONE);
		_resultImage.setLayoutData(new GridData(SWT.RIGHT, SWT.BEGINNING, false, false));
		// load an image here to get the correct size
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
		createButton(parent, IDialogConstants.OK_ID, "Save", true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int open() {
		_log.debug(this, "Trying to find IOC for process variable: " + _pv);
		_iocName = IocFinder.getIoc(_pv);
		if (_iocName == null) {
			_log.error(this, "Could not execute save value because no IOC was found for PV: " + _pv);
			MessageDialog.openError(null, "Save Value", "The IOC of the process variable was not found.");
			return CANCEL;
		}
		_log.debug(this, "IOC found: " + _iocName);
		return super.open();
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
		
		final String pvValue = _value.getText();
		Runnable r = new Runnable() {
			public void run() {
				String[] services = new String[] {
					"SaveValue.EpicsOra", "SaveValue.Database", "SaveValue.caput"	
				};
				final boolean[] success = new boolean[3];
				try {
					IPreferencesService prefs = Platform.getPreferencesService();
					String registryHost = prefs.getString(
							Activator.PLUGIN_ID,
							PreferenceConstants.RMI_REGISTRY_SERVER,
							null, null);
					_log.debug(this, "Connecting to RMI registry.");
					Registry reg = LocateRegistry.getRegistry(registryHost);
					for (int i = 0; i < 3; i++) {
						String result;
						try {
							_log.debug(this, "Calling save value service: " + services[i]);
							SaveValueService service = (SaveValueService) reg.lookup(services[i]);
							SaveValueRequest req = new SaveValueRequest();
							req.setPvName(_pv.getName());
							req.setIocName(_iocName);
							req.setValue(pvValue);
							req.setUsername(SecurityFacade.getInstance().getCurrentUser().getUsername());
							req.setHostname(CSSPlatformInfo.getInstance().getQualifiedHostname());
							SaveValueResult srr = service.saveValue(req);
							String replacedValue = srr.getReplacedValue();
							if (replacedValue != null) {
								result = "Success: old value " + replacedValue + " replaced with new value";
							} else {
								result = "Success: new entry added to save button file";
							}
						} catch (RemoteException e) {
							Throwable cause = e.getCause();
							if (cause instanceof SocketTimeoutException) {
								_log.warn(this, "Remote call to " + services[i] + " timed out");
								result = "Timeout";
							} else {
								_log.error(this, "Remote call to " + services[i] + " failed with RemoteException", e);
								result = "Connection error: " + e.getMessage();
							}
						} catch (SaveValueServiceException e) {
							_log.warn(this, "Save Value service " + services[i] + " reported an error", e);
							result = "Service error: " + e.getMessage();
						} catch (NotBoundException e) {
							_log.warn(this, "Save value service " + services[i] + " is not bound in RMI registry");
							result = "Service not available";
						}
						final int index = i;
						final String resultCopy = result;
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								TableItem item = _resultsTable.getItem(index);
								item.setText(1, resultCopy);
								success[index] = resultCopy.startsWith("Success");
								int color = success[index] ? SWT.COLOR_DARK_GREEN
										: (index < 2) ? SWT.COLOR_DARK_YELLOW : SWT.COLOR_RED;
								item.setForeground(Display.getCurrent().getSystemColor(color));
								_resultsTable.showItem(item);
							}
						});
					}
					_log.debug(this, "Finished calling remote Save Value services");
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							if (success[2]) {
								_resultLabel.setText("The value was saved successfully.");
								_resultImage.setImage(getInfoImage());
							} else {
								_resultLabel.setText("An error occurred. The value was NOT saved!");
								_resultImage.setImage(getErrorImage());
							}
							_resultLabel.setVisible(true);
							_resultImage.setVisible(true);
							getButton(IDialogConstants.CANCEL_ID)
									.setEnabled(true);
						}
					});
				} catch (RemoteException e) {
					_log.error(this, "Could not connect to RMI registry", e);
					MessageDialog.openError(null, "Save Value", "Could not connect to RMI registry: " + e.getMessage());
					SaveValueDialog.this.close();
				}
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
