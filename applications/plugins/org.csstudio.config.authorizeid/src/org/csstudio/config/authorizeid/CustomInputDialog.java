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

import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAuthorizeIdConfiguration.OU;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAuthorizeIdConfiguration.UNIT;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.service.LdapServiceException;
import org.csstudio.utility.ldap.service.util.LdapUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
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

    private static final Logger LOG = CentralLogger.getInstance().getLogger(CustomInputDialog.class);


    /**
     * The title of the dialog.
     */
    private final String _title;

    /**
     * The message to display, or <code>null</code> if none.
     */
    private final String _message1;

    /**
     * The message to display, or <code>null</code> if none.
     */
    private final String _message2;

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
    private final String _stringSearchRoot = "ou=Css,ou=EpicsAuthorize"; //$NON-NLS-1$

    private final LdapName _searchRoot = LdapUtils.createLdapName(OU.getNodeTypeName(), "Css",
                                                                   UNIT.getNodeTypeName(), UNIT.getUnitTypeValue()); //$NON-NLS-1$


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

    private final String _eaigSel;

    private final String _eairSel;

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
    public CustomInputDialog(final Shell parentShell,
                             final String dialogTitle,
                             final String dialogMessage1,
                             final String dialogMessage2,
                             final String eaigSel,
                             final String eairSel) {
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
    @Override
    protected void buttonPressed(final int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            final String tmp = (String)((StructuredSelection)_eaigCombo.getSelection()).getFirstElement();
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
    @Override
    protected void configureShell(final Shell shell) {
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
    @Override
    protected void createButtonsForButtonBar(final Composite parent) {
        // create OK and Cancel buttons by default
        okButton = createButton(parent, IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
        //do this here because setting the text will set enablement on the ok
        // button
        _eaigCombo.getCombo().setFocus();

        final ILdapService service = AuthorizeIdActivator.getDefault().getLdapService();
        if (service == null) {
            MessageDialog.openError(null,
                                    "LDAP Access failed",
                                    "No LDAP service available. Try again later.");
            return;
        }

        final ILdapSearchResult result =
            service.retrieveSearchResultSynchronously(_searchRoot, eagnFilter, SearchControls.SUBTREE_SCOPE);


        final List<String> list = new ArrayList<String>(result.getAnswerSet().size());

        for (final SearchResult row : result.getAnswerSet()) {
            final String name = new String(row.getName());
            if(name.trim().length() > 0){
                list.add(name + "," + _stringSearchRoot);
            }
        }

        if (!list.isEmpty()) {
            _eaigCombo.setInput(list);
            if (_eaigSel!=null) {

                for (final String string : list) {
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
    @Override
    protected Control createDialogArea(final Composite parent) {
        // create composite
        final Composite composite = (Composite) super.createDialogArea(parent);
        // create message
        if (_message1 != null) {
            final Label label = new Label(composite, SWT.WRAP);
            label.setText(_message1);
            final GridData data = new GridData(GridData.GRAB_HORIZONTAL
                    | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
                    | GridData.VERTICAL_ALIGN_CENTER);
            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            label.setLayoutData(data);
            label.setFont(parent.getFont());
        }

        _eaigCombo = new ComboViewer(composite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
        _eaigCombo.setLabelProvider(new LabelProvider(){
            @Override
            public String getText(final Object element) {
                if (element instanceof String) {
                    final String ldapPath = (String) element;
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

            @Override
            public void selectionChanged(final SelectionChangedEvent event) {
                final String firstElement = (String) ((StructuredSelection)_eaigCombo.getSelection()).getFirstElement();

                NameParser nameParser;
                LdapName ldapName = null;
                try {

                    final ILdapService service = AuthorizeIdActivator.getDefault().getLdapService();
                    if (service == null) {
                        MessageDialog.openError(null,
                                                "LDAP Access failed",
                                                "No LDAP service available. Try again later.");
                        return;
                    }

                    nameParser = service.getLdapNameParser();
                    ldapName = (LdapName) nameParser.parse(firstElement);
                    final ILdapSearchResult result =
                        service.retrieveSearchResultSynchronously(ldapName, eagnFilter, SearchControls.SUBTREE_SCOPE);

                    final List<String> list = new ArrayList<String>(result.getAnswerSet().size());

                    for (final SearchResult row : result.getAnswerSet()) {
                        final String name = new String(row.getName());
                        if(name.trim().length() > 0){
                            list.add(name + "," + _stringSearchRoot);
                        }
                        final List<String> cleanString = new ArrayList<String>();
                        int selIndex =0;
                        for(int i=0; i < list.size(); i++){
                            final String string = list.get(i);
                            final String[] split = string.split(SPLIT_FILTER);
                            if(split.length > 3){
                                final String tmp = split[1];
                                cleanString.add(tmp);
                                if(tmp.equals(_eairSel)){
                                    selIndex = i;
                                }
                            }
                        }
                        _eairCombo.setItems(cleanString.toArray(new String[0]));
                        _eairCombo.select(selIndex);
                    }
                } catch (final NamingException e) {
                    LOG.error("Could not parse first selected element into valid LDAP name", e);
                } catch (LdapServiceException e) {
                    LOG.error("Could not parse first selected element into valid LDAP name", e);
                }
            }

        });

        if (_message2 != null) {
            final Label label = new Label(composite, SWT.WRAP);
            label.setText(_message2);
            final GridData data = new GridData(GridData.GRAB_HORIZONTAL
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
    @Deprecated
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
    public void setErrorMessage(final String errorMessage) {
    	this.errorMessage = errorMessage;
    	if (errorMessageText != null && !errorMessageText.isDisposed()) {
    		errorMessageText.setText(errorMessage == null ? " \n " : errorMessage); //$NON-NLS-1$
    		// Disable the error message text control if there is no error, or
    		// no error text (empty or whitespace only).  Hide it also to avoid
    		// color change.
    		// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=130281
    		final boolean hasError = errorMessage != null && StringConverter.removeWhiteSpaces(errorMessage).length() > 0;
    		errorMessageText.setEnabled(hasError);
    		errorMessageText.setVisible(hasError);
    		errorMessageText.getParent().update();
    		// Access the ok button by id, in case clients have overridden button creation.
    		// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=113643
    		final Control button = getButton(IDialogConstants.OK_ID);
    		if (button != null) {
    			button.setEnabled(errorMessage == null);
    		}
    	}
    }
}
