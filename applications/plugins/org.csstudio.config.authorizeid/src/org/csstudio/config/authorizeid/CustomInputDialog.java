/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.csstudio.config.authorizeid;

import java.util.ArrayList;

import org.csstudio.utility.ldap.reader.LDAPSyncReader;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * This is a modification of {@link InputDialog} class. 
 * {@link InputDialog} has one label and one text field, 
 * but {@code CustomInputDialog} is customized to have two labels 
 * and two text fields.
 * @author Rok Povsic
 */
public class CustomInputDialog extends Dialog {
    /**
     * The title of the dialog.
     */
    private String _title;

    /**
     * The message to display, or <code>null</code> if none.
     */
    private String _message1;
    
    /**
     * The message to display, or <code>null</code> if none.
     */
    private String _message2;

    /**
     * The input value; the empty string by default.
     */
    private String _valueEair = "";//$NON-NLS-1$

    /**
     * Ok button widget.
     */
    private Button okButton;

    /**
     * Input text widget.
     */
    private ComboViewer _eaigCombo;
    
    /**
     * Input text widget 2.
     */
    private Combo _eairCombo;

    /**
     * Error message label widget.
     */
    private Text errorMessageText;
    
    /**
     * Error message string.
     */
    private String errorMessage;
    
    //TODO: Define at the Preference Page
    private String string_search_root = "ou=Css,ou=EpicsAuthorize"; //$NON-NLS-1$
    /**
     * Search for this.
     */
    //TODO: Define at the Preference Page
    String eagnFilter = "eagn=*"; //$NON-NLS-1$
    /**
     * Search for this.
     */
    //TODO: Define at the Preference Page
    String eaigFilter = "ou=*"; //$NON-NLS-1$

    private String _valueEaig;

    private String _eaigSel;

    private String _eairSel;
    
    private static final String SPLIT_FILTER = "[=,]"; //$NON-NLS-1$

    /**
     * Creates an input dialog with OK and Cancel buttons. Note that the dialog
     * will have no visual representation (no widgets) until it is told to open.
     * <p>
     * Note that the <code>open</code> method blocks for input dialogs.
     * </p>
     * 
     * @param parentShell
     *            the parent shell, or <code>null</code> to create a top-level
     *            shell
     * @param dialogTitle
     *            the dialog title, or <code>null</code> if none
     * @param dialogMessage1
     *            the dialog message, or <code>null</code> if none
     * @param initialValue
     *            the initial input value, or <code>null</code> if none
     *            (equivalent to the empty string)
     * @param eairSel The Selection for the eair Combo 
     * @param eaigSel The Selection for the eaig Combo
     */
    public CustomInputDialog(Shell parentShell, String dialogTitle,
            String dialogMessage1, String dialogMessage2, String eaigSel, String eairSel) {
        super(parentShell);
        _title = dialogTitle;
        _message1 = dialogMessage1;
        _message2 = dialogMessage2;
        _eaigSel = eaigSel;
        _eairSel = eairSel;
    }

    /*
     * (non-Javadoc) Method declared on Dialog.
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            String tmp = (String)((StructuredSelection)_eaigCombo.getSelection()).getFirstElement();
            _valueEaig = tmp.split(SPLIT_FILTER)[1];
            _valueEair = _eairCombo.getItem(_eairCombo.getSelectionIndex());
        } else {
            _valueEaig = null;
            _valueEair = null;
        }
        super.buttonPressed(buttonId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (_title != null) {
			shell.setText(_title);
		}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent) {
        // create OK and Cancel buttons by default
        okButton = createButton(parent, IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
        //do this here because setting the text will set enablement on the ok
        // button
        _eaigCombo.getCombo().setFocus();
        LDAPSyncReader lSR = new LDAPSyncReader(string_search_root,eaigFilter);
        ArrayList<String> answerString = lSR.getAnswerString();

        if (answerString != null) {
            _eaigCombo.setInput(answerString);
            if(_eaigSel!=null){
                for (String string : answerString) {
                    if(string.split(SPLIT_FILTER)[1].equals(_eaigSel)){
                        _eaigCombo.setSelection(new StructuredSelection(string));
                    }
                }
            }else{
                _eaigCombo.setSelection(new StructuredSelection(_eaigCombo.getElementAt(0)));
            }
        }
    }

    /*
     * (non-Javadoc) Method declared on Dialog.
     */
    protected Control createDialogArea(Composite parent) {
        // create composite
        Composite composite = (Composite) super.createDialogArea(parent);
        // create message
        if (_message1 != null) {
            Label label = new Label(composite, SWT.WRAP);
            label.setText(_message1);
            GridData data = new GridData(GridData.GRAB_HORIZONTAL
                    | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
                    | GridData.VERTICAL_ALIGN_CENTER);
            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            label.setLayoutData(data);
            label.setFont(parent.getFont());
        }

        _eaigCombo = new ComboViewer(composite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
        _eaigCombo.setLabelProvider(new LabelProvider(){
            @Override
            public String getText(Object element) {
                if (element instanceof String) {
                    String ldapPath = (String) element;
                    if(ldapPath.indexOf('=')>0){
                        return ldapPath.split(SPLIT_FILTER)[1];
                    }
                    return ldapPath;
                }
                return super.getText(element);
            }
            
        });
        _eaigCombo.setContentProvider(new ArrayContentProvider());
        _eaigCombo.getCombo().setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                | GridData.HORIZONTAL_ALIGN_FILL));
        _eaigCombo.addSelectionChangedListener(new ISelectionChangedListener(){

            public void selectionChanged(SelectionChangedEvent event) {
                String firstElement = (String) ((StructuredSelection)_eaigCombo.getSelection()).getFirstElement();
                System.out.println(firstElement);
                LDAPSyncReader lSR = new LDAPSyncReader(firstElement, eagnFilter);
                ArrayList<String> answerString = lSR.getAnswerString();
                ArrayList<String> cleanString = new ArrayList<String>();
                int selIndex =0;
                for(int i=0;i<answerString.size();i++){
                    String string = answerString.get(i); 
                    String[] split = string.split(SPLIT_FILTER);
                    if(split.length>3){
                        String tmp = split[1];
                        cleanString.add(tmp);
                        if(tmp.equals(_eairSel)){
                            selIndex= i;
                        }
                    }
                }
                _eairCombo.setItems(cleanString.toArray(new String[0]));
                _eairCombo.select(selIndex);
            }
            
        });
        
        if (_message2 != null) {
            Label label = new Label(composite, SWT.WRAP);
            label.setText(_message2);
            GridData data = new GridData(GridData.GRAB_HORIZONTAL
                    | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
                    | GridData.VERTICAL_ALIGN_CENTER);
            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            label.setLayoutData(data);
            label.setFont(parent.getFont());
        }
        _eairCombo = new Combo(composite, SWT.SINGLE | SWT.BORDER |SWT.READ_ONLY);
        _eairCombo.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                | GridData.HORIZONTAL_ALIGN_FILL));
        
        errorMessageText = new Text(composite, SWT.READ_ONLY | SWT.WRAP);
        errorMessageText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                | GridData.HORIZONTAL_ALIGN_FILL));
        errorMessageText.setBackground(errorMessageText.getDisplay()
                .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        // Set the error message text
        // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=66292
        setErrorMessage(errorMessage);

        applyDialogFont(composite);
        return composite;
    }

    /**
     * Returns the error message label.
     * 
     * @return the error message label
     * @deprecated use setErrorMessage(String) instead
     */
    protected Label getErrorMessageLabel() {
        return null;
    }

    /**
     * Returns the ok button.
     * 
     * @return the ok button
     */
    protected Button getOkButton() {
        return okButton;
    }

    /**
     * Returns the text area.
     * 
     * @return the text area
     */
    protected ComboViewer getText() {
        return _eaigCombo;
    }
    
    /**
     * Returns the text area 2.
     * 
     * @return the text area 2
     */
    protected Combo getText2() {
        return _eairCombo;
    }

    /**
     * Returns the string typed into this input dialog.
     * 
     * @return the input string
     */
    public String getValue() {
        return _valueEaig;
    }
    
    /**
     * Returns the string typed into this input dialog.
     * 
     * @return the input string
     */
    public String getValue2() {
        return _valueEair;
    }

    /**
     * Sets or clears the error message.
     * If not <code>null</code>, the OK button is disabled.
     * 
     * @param errorMessage
     *            the error message, or <code>null</code> to clear
     * @since 3.0
     */
    public void setErrorMessage(String errorMessage) {
    	this.errorMessage = errorMessage;
    	if (errorMessageText != null && !errorMessageText.isDisposed()) {
    		errorMessageText.setText(errorMessage == null ? " \n " : errorMessage); //$NON-NLS-1$
    		// Disable the error message text control if there is no error, or
    		// no error text (empty or whitespace only).  Hide it also to avoid
    		// color change.
    		// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=130281
    		boolean hasError = errorMessage != null && (StringConverter.removeWhiteSpaces(errorMessage)).length() > 0;
    		errorMessageText.setEnabled(hasError);
    		errorMessageText.setVisible(hasError);
    		errorMessageText.getParent().update();
    		// Access the ok button by id, in case clients have overridden button creation.
    		// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=113643
    		Control button = getButton(IDialogConstants.OK_ID);
    		if (button != null) {
    			button.setEnabled(errorMessage == null);
    		}
    	}
    }
}
