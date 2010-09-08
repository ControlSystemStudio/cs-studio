package org.remotercp.filetransfer.receiver.dialogs;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class AcceptFiletransferDialog extends Wizard {

	private AcceptFilePage page;

	public AcceptFiletransferDialog(String fileName, String sender) {
		setWindowTitle("Filetransfer");

		this.page = new AcceptFilePage("Accept Filetransfer", fileName, sender);
		addPage(this.page);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	private class AcceptFilePage extends WizardPage {

		private final String fileName;
		private final String user;

		protected AcceptFilePage(String pageName, String fileName, String user) {
			super(pageName);
			this.fileName = fileName;
			this.user = user;
			setDescription("Please accept or decline incoming file transfer");
		}

		public void createControl(Composite parent) {
			Composite main = new Composite(parent, SWT.None);
			main.setLayout(new GridLayout());
			GridDataFactory.fillDefaults().grab(true, true).applyTo(main);

			{
				new Label(main, SWT.READ_ONLY).setText("File:");

				Text fileinfo = new Text(main, SWT.BORDER | SWT.WRAP
						| SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
				GridDataFactory.fillDefaults().grab(true, true).applyTo(
						fileinfo);

				fileinfo.append("Sender: " + user);
				fileinfo.append("\n");
				fileinfo.append("Filename: " + fileName);

			}

			setControl(main);
		}

	}

}
