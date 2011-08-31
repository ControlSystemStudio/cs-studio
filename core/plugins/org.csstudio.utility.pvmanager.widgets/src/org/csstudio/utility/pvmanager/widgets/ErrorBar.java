package org.csstudio.utility.pvmanager.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.custom.CLabel;

import com.swtdesigner.ResourceManager;

public class ErrorBar extends Composite {

	private Label errorImage;
	private CLabel errorLabel;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ErrorBar(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginLeft = 1;
		setLayout(gridLayout);
		
		errorImage = new Label(this, SWT.NONE);
		GridData gd_errorImage = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		errorImage.setLayoutData(gd_errorImage);
		errorImage.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/obj16/warn_tsk.gif"));
		
		errorLabel = new CLabel(this, SWT.NONE);
		GridData gd_errorLabel = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_errorLabel.widthHint = 221;
		errorLabel.setLayoutData(gd_errorLabel);
		setException(null);
	}
	
	public void setException(Exception ex) {
		if (!isDisposed()) {
			if (ex == null) {
				errorLabel.setToolTipText("");
				errorLabel.setText("");
				GridData gd = (GridData) errorLabel.getLayoutData();
				gd.exclude = true;
				errorLabel.setLayoutData(gd);
				gd = (GridData) errorImage.getLayoutData();
				gd.exclude = true;
				errorImage.setLayoutData(gd);
			} else {
				ex.printStackTrace();
				errorLabel.setToolTipText(ex.getMessage());
				errorLabel.setText(ex.getMessage());
				GridData gd = (GridData) errorLabel.getLayoutData();
				gd.exclude = false;
				errorLabel.setLayoutData(gd);
				gd = (GridData) errorImage.getLayoutData();
				gd.exclude = false;
				errorImage.setLayoutData(gd);
			}
			getParent().layout();
		}
	}
	

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
