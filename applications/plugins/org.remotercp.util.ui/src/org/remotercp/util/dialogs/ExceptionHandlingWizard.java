package org.remotercp.util.dialogs;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * This wizard is used to display exceptions.
 * 
 * @author Eugen Reiswich
 * 
 */
public class ExceptionHandlingWizard extends Wizard {

	public ExceptionHandlingWizard(Exception e, String text) {
		setWindowTitle("Exception info wizard");

		addPage(new ExceptionPage(e, "The following exception occured", text));

	}

	@Override
	public boolean performFinish() {
		return true;
	}

	private class ExceptionPage extends WizardPage {

		private Exception exception;

		protected ExceptionPage(Exception e, String pageName, String description) {
			super(pageName);
			setDescription(description);
			this.exception = e;
			setErrorMessage(description);
		}

		public void createControl(Composite parent) {
			Composite main = new Composite(parent, SWT.None);
			main.setLayout(new GridLayout(1, true));
			GridDataFactory.fillDefaults().grab(true, true).applyTo(main);
			{
				Text exceptionText = new Text(main, SWT.WRAP | SWT.BORDER
						| SWT.V_SCROLL | SWT.H_SCROLL);
				GridDataFactory.fillDefaults().grab(true, true).applyTo(
						exceptionText);
				if (this.exception != null) {
					if (this.exception.getMessage() != null) {
						exceptionText.append("Message:");
						exceptionText.append("\n");
						exceptionText.append(this.exception.getMessage());
						exceptionText.append("\n");
						exceptionText.append("\n");
					}

					if (this.exception.getCause() != null) {
						exceptionText.append("Cause:");
						exceptionText.append("\n");
						exceptionText.append(this.exception.getCause()
								.toString());

						// append cause
						if (this.exception.getCause().getCause() != null) {
							exceptionText.append("\n");
							exceptionText.append("Further details:");
							exceptionText.append(this.exception.getCause()
									.getCause().toString());
							exceptionText.append("\n");
						}
						exceptionText.append("\n");
						exceptionText.append("\n");
					}

					if (this.exception.getStackTrace() != null) {
						StackTraceElement[] stackTrace = this.exception
								.getStackTrace();
						for (int error = 0; error < stackTrace.length; error++) {
							exceptionText.append(stackTrace[error].toString());
							exceptionText.append("\n");
						}
					}
				} else {
					exceptionText.append("Exception date is corrupt");
				}
			}
			setControl(main);
		}
	}

}
