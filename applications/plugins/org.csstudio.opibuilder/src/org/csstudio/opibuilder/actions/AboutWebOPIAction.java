package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.scriptUtil.FileUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogLabelKeys;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.UIPlugin;

/**The action to pop up an About WebOPI Dialog.
 * @author Xihui Chen
 *
 */
public class AboutWebOPIAction extends Action {

	
	public AboutWebOPIAction() {
		setText("About WebOPI...");
	}
	
	@Override
	public void run() {
		new AboutDialog(null).open();
	}

	
	class AboutDialog extends Dialog {

		/**
		 * Create the dialog.
		 * @param parentShell
		 */
		public AboutDialog(Shell parentShell) {
			super(parentShell);
			
		}

		
		
		/**
		 * Create contents of the dialog.
		 * @param parent
		 */
		@SuppressWarnings({ "restriction", "serial" })
		@Override
		protected Control createDialogArea(Composite parent) {
			getShell().setText("About WebOPI");
			String version = "v" + OPIBuilderPlugin. //$NON-NLS-1$
					getDefault().getBundle().getVersion().toString();
			Composite container = (Composite) super.createDialogArea(parent);
			GridLayout gl_container = new GridLayout(1, false);
			container.setLayout(gl_container);
			GridData gd = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
			Label emptyLabel = new Label(container, SWT.NONE);
			gd.heightHint=10;
			emptyLabel.setLayoutData(gd);
			
			Label lblNewLabel = new Label(container, SWT.NONE);
			gd = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
			gd.heightHint = 20;
			lblNewLabel.setLayoutData(gd);
			lblNewLabel.setFont(CustomMediaFactory.getInstance().getFont("Verdana", 16, SWT.BOLD));
			lblNewLabel.setText("WebOPI " + version); //$NON-NLS-1$
			
			Label lblCompatibleWithBoy = new Label(container, SWT.NONE);
			GridData gd_lblCompatibleWithBoy = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
			gd_lblCompatibleWithBoy.heightHint = 20;
			lblCompatibleWithBoy.setLayoutData(gd_lblCompatibleWithBoy);
			lblCompatibleWithBoy.setText("Compatible with BOY " + version);
			
			Label rapLabel = new Label(container, SWT.NONE);
			gd_lblCompatibleWithBoy = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
			gd_lblCompatibleWithBoy.heightHint = 20;
			rapLabel.setLayoutData(gd_lblCompatibleWithBoy);
			rapLabel.setText("Built on RAP " + "v" + UIPlugin. //$NON-NLS-2$
					getDefault().getBundle().getVersion().toString());
			
			Link link = new Link(container, SWT.NONE);
			GridData gd_link = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
			gd_link.heightHint = 20;
			link.setLayoutData(gd_link);
			link.setText("<a>Visit WebOPI home page</a>");
			link.addSelectionListener(new SelectionAdapter() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					FileUtil.openWebPage("http://sourceforge.net/apps/trac/cs-studio/wiki/webopi"); //$NON-NLS-1$
				}
			});
			Link link_1 = new Link(container, 0);
			link_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
			link_1.setText("WebOPI is a subproject of <a>Control System Studio (CSS)</a>");
			link_1.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					FileUtil.openWebPage("http://cs-studio.sourceforge.net/"); //$NON-NLS-1$
				}
			});
			return container;
		}

		protected void createButtonsForButtonBar(Composite parent) {
			// create OK and Cancel buttons by default
			createButton(parent, IDialogConstants.OK_ID, JFaceResources.getString(IDialogLabelKeys.OK_LABEL_KEY),
					true);			
		}

	}
}


