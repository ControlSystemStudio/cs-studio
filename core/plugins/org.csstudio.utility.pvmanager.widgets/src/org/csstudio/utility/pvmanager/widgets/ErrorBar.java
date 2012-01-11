package org.csstudio.utility.pvmanager.widgets;

import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.swtdesigner.ResourceManager;

public class ErrorBar extends Composite {

	private Label errorImage;
	private CLabel errorLabel;
	private Exception exception;
	Button imageButton;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ErrorBar(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginLeft = 1;
		setLayout(gridLayout);
		
		//errorImage = new Label(this, SWT.NONE);
		//GridData gd_errorImage = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		//errorImage.setLayoutData(gd_errorImage);
		//errorImage.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/obj16/warn_tsk.gif"));
		
		imageButton = new Button(this, SWT.FLAT);
		GridData gd_imageButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		imageButton.setLayoutData(gd_imageButton);
		imageButton.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/obj16/warn_tsk.gif"));
		imageButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ExceptionDetailsErrorDialog.openError(getShell(), "Query failed...", exception);
			}
		});
		
		errorLabel = new CLabel(this, SWT.NONE);
		GridData gd_errorLabel = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_errorLabel.widthHint = 221;
		errorLabel.setLayoutData(gd_errorLabel);
		setException(null);
	}
	
	public void setException(Exception ex) {
		if (!isDisposed()) {
			this.exception = ex;
			if (ex == null) {
				errorLabel.setToolTipText("");
				errorLabel.setText("");
				GridData gd = (GridData) errorLabel.getLayoutData();
				gd.exclude = true;
				errorLabel.setLayoutData(gd);
				gd = (GridData) imageButton.getLayoutData();
				gd.exclude = true;
				imageButton.setLayoutData(gd);
			} else {
				ex.printStackTrace();
				errorLabel.setToolTipText(ex.getMessage());
				errorLabel.setText(ex.getMessage());
				GridData gd = (GridData) errorLabel.getLayoutData();
				gd.exclude = false;
				errorLabel.setLayoutData(gd);
				gd = (GridData) imageButton.getLayoutData();
				gd.exclude = false;
				imageButton.setLayoutData(gd);
			}
			getParent().layout();
		}
	}
	

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
