package org.remotercp.contacts.dialogs;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.remotercp.contacts.ContactEntryJFaceModel;

public class AddContactDialog extends Wizard {

	private ContactEntryJFaceModel contactEntry;

	private ContactPage contactPage;

	public AddContactDialog() {
		setWindowTitle("New Contact Wizard");
		this.contactPage = new ContactPage("New contact entry");

		this.contactEntry = new ContactEntryJFaceModel();

		addPage(this.contactPage);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	/**
	 * Returns the created contacts entry
	 * 
	 * @return
	 */
	public ContactEntryJFaceModel getContactsEntry() {
		return this.contactEntry;
	}

	private class ContactPage extends WizardPage {

		private Text name;

		private Text nickname;

		private Text server;

		protected ContactPage(String pageName) {
			super(pageName);
			setTitle("Contact");
			setDescription("Please add a new Contact...");
		}

		public void createControl(Composite parent) {
			Composite comp = new Composite(parent, SWT.None);
			comp.setLayout(new GridLayout(2, false));

			{
				new Label(comp, SWT.None).setText("Name");
				name = new Text(comp, SWT.BORDER);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(name);

				new Label(comp, SWT.None).setText("Nickname");
				nickname = new Text(comp, SWT.BORDER);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(
						nickname);

				new Label(comp, SWT.None).setText("Server");
				server = new Text(comp, SWT.BORDER);
				GridDataFactory.fillDefaults().grab(true, false)
						.applyTo(server);

			}
			setControl(comp);
			initDataBinding();
		}

		/**
		 * JFace Databinding
		 */
		private void initDataBinding() {
			DataBindingContext bindingContext = new DataBindingContext();

			IObservableValue nameObservable = BeansObservables.observeValue(
					contactEntry, "name");
			IObservableValue nicknameObservable = BeansObservables
					.observeValue(contactEntry, "nickname");
			IObservableValue serverObservable = BeansObservables.observeValue(
					contactEntry, "server");

			/*
			 * SWT.Modify has to be set (instead of Focus_out), because in case
			 * of pushing the Finish-button the focus out event won't occur on
			 * the last selected text field
			 */
			bindingContext.bindValue(SWTObservables.observeText(name,
					SWT.Modify), nameObservable, null, null);
			bindingContext.bindValue(SWTObservables.observeText(nickname,
					SWT.Modify), nicknameObservable, null, null);
			bindingContext.bindValue(SWTObservables.observeText(server,
					SWT.Modify), serverObservable, null, null);
		}
	}

}
