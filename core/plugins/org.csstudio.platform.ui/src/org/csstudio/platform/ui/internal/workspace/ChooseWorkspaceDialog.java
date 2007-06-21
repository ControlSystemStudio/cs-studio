/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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
package org.csstudio.platform.ui.internal.workspace;

import java.io.File;

import org.csstudio.platform.ui.internal.localization.Messages;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.util.Geometry;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A dialog that prompts for a directory to use as a workspace.
 * 
 * <p>
 * <b>Code is based upon
 * <code>org.eclipse.ui.internal.ide.ChooseWorkspaceDialog</code> in plugin
 * <code>org.eclipse.ui.ide</code>.</b>
 * </p>
 * 
 * @author Alexander Will
 * @author Kay Kasemir
 * @version $Revision$
 */
public final class ChooseWorkspaceDialog extends TitleAreaDialog {
	/**
	 * The workspaces data.
	 */
	private ChooseWorkspaceData _launchData;

	/**
	 * The workspace selection combo field.
	 */
	private Combo _text;

	/**
	 * True means the dialog will not have a "don't ask again" button.
	 */
	private boolean _suppressAskAgain = false;

	/**
	 * Indicates whether the dialog should be centered on the monitor.
	 */
	private boolean _centerOnMonitor = false;

	/**
	 * Create a modal dialog on the arugment shell, using and updating the
	 * argument data object.
	 * 
	 * @param parentShell
	 *            the parent shell for this dialog
	 * @param launchData
	 *            the launch data from past launches
	 * 
	 * @param suppressAskAgain
	 *            true means the dialog will not have a "don't ask again" button
	 * @param centerOnMonitor
	 *            indicates whether the dialog should be centered on the monitor
	 *            or according to it's parent if there is one
	 */
	public ChooseWorkspaceDialog(final Shell parentShell,
			final ChooseWorkspaceData launchData,
			final boolean suppressAskAgain, final boolean centerOnMonitor) {
		super(parentShell);
		_launchData = launchData;
		_suppressAskAgain = suppressAskAgain;
		_centerOnMonitor = centerOnMonitor;
	}

	/**
	 * Show the dialog to the user (if needed). When this method finishes,
	 * #getSelection will return the workspace that should be used (whether it
	 * was just selected by the user or some previous default has been used. The
	 * parameter can be used to override the users preference. For example, this
	 * is important in cases where the default selection is already in use and
	 * the user is forced to choose a different one.
	 * 
	 * @param force
	 *            true if the dialog should be opened regardless of the value of
	 *            the show dialog checkbox
	 */
	public void prompt(final boolean force) {
		if (force || _launchData.getShowDialog()) {
			open();

			// 70576: make sure dialog gets dismissed on ESC too
			if (getReturnCode() == CANCEL) {
				_launchData.workspaceSelected(null);
			}

			return;
		}

		String[] recent = _launchData.getRecentWorkspaces();

		// If the selection dialog was not used then the workspace to use is
		// either the
		// most recent selection or the initialDefault (if there is no history).
		String workspace = null;
		if ((recent != null) && (recent.length > 0)) {
			workspace = recent[0];
		}
		if ((workspace == null) || (workspace.length() == 0)) {
			workspace = _launchData.getInitialDefault();
		}
		_launchData.workspaceSelected(workspace);
	}

	/**
	 * Creates and returns the contents of the upper part of this dialog (above
	 * the button bar).
	 * <p>
	 * The <code>Dialog</code> implementation of this framework method creates
	 * and returns a new <code>Composite</code> with no margins and spacing.
	 * </p>
	 * 
	 * @param parent
	 *            the parent composite to contain the dialog area
	 * @return the dialog area control
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		String productName = null;
		IProduct product = Platform.getProduct();
		if (product != null) {
			productName = product.getName();
		}
		if (productName == null) {
			productName = Messages
					.getString("ChooseWorkspaceDialog.PRODUCT_NAME"); //$NON-NLS-1$
		}

		Composite composite = (Composite) super.createDialogArea(parent);
		setTitle(Messages.getString("ChooseWorkspaceDialog.TITLE")); //$NON-NLS-1$
		setMessage(NLS.bind(Messages
				.getString("ChooseWorkspaceDialog.PROBLEM_MULTIPLE_PROJECTS"), //$NON-NLS-1$
				productName));

		// bug 59934: load title image for sizing, but set it non-visible so the
		// white background is displayed
		if (getTitleImageLabel() != null) {
			getTitleImageLabel().setVisible(false);
		}

		createWorkspaceBrowseRow(composite);
		if (!_suppressAskAgain) {
			createShowDialogButton(composite);
		}
		Dialog.applyDialogFont(composite);
		return composite;
	}

	/**
	 * Configures the given shell in preparation for opening this window in it.
	 * <p>
	 * The default implementation of this framework method sets the shell's
	 * image and gives it a grid layout. Subclasses may extend or reimplement.
	 * </p>
	 * 
	 * @param shell
	 *            the shell
	 */
	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText(Messages.getString("ChooseWorkspaceDialog.WINDOW_TITLE")); //$NON-NLS-1$
	}

	/**
	 * Notifies that the ok button of this dialog has been pressed.
	 * <p>
	 * The <code>Dialog</code> implementation of this framework method sets
	 * this dialog's return code to <code>Window.OK</code> and closes the
	 * dialog. Subclasses may override.
	 * </p>
	 */
	@Override
	protected void okPressed() {
		_launchData.workspaceSelected(_text.getText());
		super.okPressed();
	}

	/**
	 * Notifies that the cancel button of this dialog has been pressed.
	 * <p>
	 * The <code>Dialog</code> implementation of this framework method sets
	 * this dialog's return code to <code>Window.CANCEL</code> and closes the
	 * dialog. Subclasses may override if desired.
	 * </p>
	 */
	@Override
	protected void cancelPressed() {
		_launchData.workspaceSelected(null);
		super.cancelPressed();
	}

	/**
	 * The main area of the dialog is just a row with the current selection
	 * information and a drop-down of the most recently used workspaces.
	 * 
	 * @param parent
	 *            the parent composite to contain the dialog area
	 */
	private void createWorkspaceBrowseRow(final Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		panel.setLayout(layout);
		panel.setLayoutData(new GridData(GridData.FILL_BOTH));
		panel.setFont(parent.getFont());

        
        Label label = new Label(panel, SWT.NONE);
        label.setText(Messages
                        .getString("ChooseWorkspaceDialog.CURRENT_WORKSPACE_LABEL")); //$NON-NLS-1$
        Text text = new Text(panel, SWT.READ_ONLY);
        text.setText(Platform.getInstanceLocation().getURL().getPath());
        final GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        text.setLayoutData(gd);
        
        label = new Label(panel, SWT.NONE);
		label.setText(Messages
				.getString("ChooseWorkspaceDialog.WORKSPACE_LABEL")); //$NON-NLS-1$

		_text = new Combo(panel, SWT.BORDER | SWT.LEAD | SWT.DROP_DOWN);
		_text.setFocus();
		_text.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.FILL_HORIZONTAL));
		setInitialTextValues(_text);

		Button browseButton = new Button(panel, SWT.PUSH);
		browseButton.setText(Messages
				.getString("ChooseWorkspaceDialog.BROWSE_LABEL")); //$NON-NLS-1$
		setButtonLayoutData(browseButton);
		GridData data = (GridData) browseButton.getLayoutData();
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		browseButton.setLayoutData(data);
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText(Messages
						.getString("ChooseWorkspaceDialog.BROWSER_TITLE")); //$NON-NLS-1$
				dialog.setMessage(Messages
						.getString("ChooseWorkspaceDialog.BROWSER_MESSAGE")); //$NON-NLS-1$
				dialog.setFilterPath(getInitialBrowsePath());
				String dir = dialog.open();
				if (dir != null) {
					_text.setText(dir);
				}
			}
		});
	}

	/**
	 * Return a string containing the path that is closest to the current
	 * selection in the text widget. This starts with the current value and
	 * works toward the root until there is a directory for which File.exists
	 * returns true. Return the current working dir if the text box does not
	 * contain a valid path.
	 * 
	 * @return closest parent that exists or an empty string
	 */
	private String getInitialBrowsePath() {
		File dir = new File(_text.getText());
		while ((dir != null) && !dir.exists()) {
			dir = dir.getParentFile();
		}

		return dir != null ? dir.getAbsolutePath() : System
				.getProperty("user.dir"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Point getInitialLocation(final Point initialSize) {
		Composite parent = getShell().getParent();

		if (!_centerOnMonitor || (parent == null)) {
			return super.getInitialLocation(initialSize);
		}

		Monitor monitor = parent.getMonitor();
		Rectangle monitorBounds = monitor.getClientArea();
		Point centerPoint = Geometry.centerPoint(monitorBounds);

		return new Point(centerPoint.x - (initialSize.x / 2), Math.max(
				monitorBounds.y, Math.min(centerPoint.y
						- (initialSize.y * 2 / 3), monitorBounds.y
						+ monitorBounds.height - initialSize.y)));
	}

	/**
	 * The show dialog button allows the user to choose to neven be nagged
	 * again.
	 * 
	 * @param parent
	 *            the parent composite to contain the dialog area
	 */
	private void createShowDialogButton(final Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		panel.setFont(parent.getFont());

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		panel.setLayout(layout);

		GridData data = new GridData(GridData.FILL_BOTH);
		data.verticalAlignment = GridData.END;
		panel.setLayoutData(data);

		Button button = new Button(panel, SWT.CHECK);
		button.setText(Messages
				.getString("ChooseWorkspaceDialog.USE_AS_DEFAULT")); //$NON-NLS-1$
		button.setSelection(!_launchData.getShowDialog());
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				_launchData.toggleShowDialog();
			}
		});
	}

	/**
	 * Set the initial values for the workspace selection combo widget.
	 * 
	 * @param text
	 *            The workspace selection combo widget.
	 */
	private void setInitialTextValues(final Combo text) {
		String[] recentWorkspaces = _launchData.getRecentWorkspaces();
		for (int i = 0; i < recentWorkspaces.length; ++i) {
			if (recentWorkspaces[i] != null) {
				text.add(recentWorkspaces[i]);
			}
		}

		text.setText(text.getItemCount() > 0 ? text.getItem(0) : _launchData
				.getInitialDefault());
	}
}
