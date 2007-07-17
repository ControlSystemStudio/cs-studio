package org.csstudio.platform.ui.internal.preferences;

import org.csstudio.platform.ui.internal.localization.Messages;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog for editing system properties (key-value-pairs).
 * 
 * @author Joerg Rathlev
 */
class SystemPropertyDialog extends Dialog {
	
	/**
	 * The key.
	 */
	private String _key = ""; //$NON-NLS-1$
	
	/**
	 * The value.
	 */
	private String _value = ""; //$NON-NLS-1$

	/**
	 * Creates a new dialog.
	 * @param parentShell the parent shell.
	 */
    protected SystemPropertyDialog(final Shell parentShell) {
        super(parentShell);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureShell(final Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.SystemPropertyDialog_TITLE);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createDialogArea(final Composite parent) {
        Composite parentComposite = (Composite) super.createDialogArea(parent);
        
        Composite contents = new Composite(parentComposite, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.numColumns = 2;
        contents.setLayout(layout);
        contents.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true));
        contents.setFont(parentComposite.getFont());
        
        Label keyLabel = new Label(contents, SWT.NULL);
        keyLabel.setText(Messages.SystemPropertyDialog_KEY_LABEL);
        final Text keyText = new Text(contents, SWT.SINGLE | SWT.BORDER);
        GridData layoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        layoutData.widthHint = 250;
        keyText.setLayoutData(layoutData);
        keyText.setText(_key);
        keyText.addModifyListener(new ModifyListener() {
        	public void modifyText(final ModifyEvent e) {
        		if (e.widget == keyText) {
        			_key = keyText.getText();
        			// enable the OK button if the "key" field contains not
        			// just whitespace
        			getButton(IDialogConstants.OK_ID).setEnabled(
        					!_key.trim().equals("")); //$NON-NLS-1$
        		}
        	}
        });
        
        Label valueLabel = new Label(contents, SWT.NULL);
        valueLabel.setText(Messages.SystemPropertyDialog_VALUE_LABEL);
        final Text valueText = new Text(contents, SWT.SINGLE | SWT.BORDER);
        valueText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        valueText.setText(_value);
        valueText.addModifyListener(new ModifyListener() {
        	public void modifyText(final ModifyEvent e) {
        		if (e.widget == valueText) {
        			_value = valueText.getText();
        		}
        	}
        });

        return contents;
    }
    
    /**
     * Creates the standard dialog buttons OK and Cancel and disables the OK
     * button.
     * @param parent the button bar composite.
     */
    @Override
    protected void createButtonsForButtonBar(final Composite parent) {
    	super.createButtonsForButtonBar(parent);
    	getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

	/**
	 * @return the key.
	 */
	String getKey() {
		return _key;
	}

	/**
	 * @return the value.
	 */
	String getValue() {
		return _value;
	}
}
