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
package org.csstudio.sds.ui.internal.properties;

import org.csstudio.platform.SimpleDalPluginActivator;
import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.sds.util.DialogFontUtil;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A table cell editor for values of type PointList.
 *
 * @deprecated
 *
 * @author Kai Meyer
 */
@Deprecated
public final class ProcessVariableCellEditor extends CellEditor {

    /**
     * A shell.
     */
    private final Shell _shell;

    /**
     * The title for this CellEditor.
     */
    private final String _title;

    /**
     * The current map.
     */
    private IProcessVariableAddress _processVariable;

    /**
     * Creates a new string cell editor parented under the given control. The
     * cell editor value is a {@link IProcessVariableAddress}.
     *
     * @param parent
     *            The parent table.
     * @param title
     *            The title for this CellEditor
     */
    public ProcessVariableCellEditor(final Composite parent, final String title) {
        super(parent, SWT.NONE);
        _shell = parent.getShell();
        _title = title;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate() {
        new ProcessVariableInputDialog(_shell, _title,
                "Add, edit or remove the value").open();
        if (_processVariable != null) {
            fireApplyEditorValue();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createControl(final Composite parent) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doGetValue() {
        return _processVariable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetFocus() {
        // Ignore
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetValue(final Object value) {
        Assert.isTrue((value instanceof IProcessVariableAddress)
                || (value == null));
        _processVariable = (IProcessVariableAddress) value;
    }

    /**
     * This class represents a Dialog to edit a {@link IProcessVariableAddress}.
     *
     * @author Kai Meyer
     */
    private final class ProcessVariableInputDialog extends Dialog {
        /**
         * The title of the dialog.
         */
        private final String _dialogTitle;
        /**
         * The message to display, or <code>null</code> if none.
         */
        private final String _message;

        private Text _fullText;
        private CCombo _controlSystemCombo;
        private Text _deviceText;
        private Text _propertyText;
        private Text _characteristicsText;
        private Label _messageLabel;

        private IProcessVariableAddress _newProcessVariable;

        /**
         * Creates an input dialog with OK and Cancel buttons. Note that the
         * dialog will have no visual representation (no widgets) until it is
         * told to open.
         * <p>
         * Note that the <code>open</code> method blocks for input dialogs.
         * </p>
         *
         * @param parentShell
         *            the parent shell, or <code>null</code> to create a
         *            top-level shell
         * @param dialogTitle
         *            the dialog title, or <code>null</code> if none
         * @param dialogMessage
         *            the dialog message, or <code>null</code> if none
         */
        public ProcessVariableInputDialog(final Shell parentShell,
                final String dialogTitle, final String dialogMessage) {
            super(parentShell);
            this.setShellStyle(SWT.MODELESS | SWT.CLOSE | SWT.MAX | SWT.TITLE
                    | SWT.BORDER | SWT.RESIZE);
            _dialogTitle = dialogTitle;
            _message = dialogMessage;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void configureShell(final Shell shell) {
            super.configureShell(shell);
            if (_dialogTitle != null) {
                shell.setText(_dialogTitle);
            }
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("synthetic-access")
        @Override
        protected Control createDialogArea(final Composite parent) {
            final Composite composite = (Composite) super.createDialogArea(parent);
            composite.setLayout(new GridLayout(2, false));

            if (_message != null) {
                final Label label = new Label(composite, SWT.WRAP);
                label.setText(_message);
                final GridData data = new GridData(GridData.GRAB_HORIZONTAL
                        | GridData.GRAB_VERTICAL
                        | GridData.HORIZONTAL_ALIGN_FILL
                        | GridData.VERTICAL_ALIGN_CENTER);
                data.horizontalSpan = 2;
                data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
                label.setLayoutData(data);
                //label.setFont(parent.getFont());
            }
            _fullText = this.createTextEntry(composite, "Process Variable:");
            _fullText.setEditable(false);
            final Label label = new Label(composite, SWT.NONE);
            label.setAlignment(SWT.RIGHT);
            label.setText("Control System:");
            _controlSystemCombo = new CCombo(composite, SWT.BORDER);
            for (int i = 0; i < ControlSystemEnum.values().length; i++) {
                _controlSystemCombo
                        .add(ControlSystemEnum.values()[i].name(), i);
            }
            _controlSystemCombo.setEditable(false);
            _deviceText = this.createTextEntry(composite, "Device:");
            _propertyText = this.createTextEntry(composite, "Property:");
            _propertyText.setFocus();
            _characteristicsText = this.createTextEntry(composite,
                    "Characteristics:");

            if (_processVariable == null) {
                _fullText.setText("No Process Variable");
                _controlSystemCombo
                        .setText(SimpleDalPluginActivator
                                .getDefault()
                                .getPluginPreferences()
                                .getString(
                                        ProcessVariableAdressFactory.PROP_CONTROL_SYSTEM));
                _deviceText.setText("");
                _propertyText.setText("");
                _characteristicsText.setText("");
            } else {
                _fullText.setText(_processVariable.getFullName());
                _controlSystemCombo.setText(_processVariable.getControlSystem()
                        .name());
                _deviceText.setText(this.getNotNullString(_processVariable
                        .getDevice()));
                _propertyText.setText(_processVariable.getProperty());
                _characteristicsText
                        .setText(this.getNotNullString(_processVariable
                                .getCharacteristic()));
            }

            _controlSystemCombo.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(final SelectionEvent event) {
                    generateProcessVariable();
                }
            });
            _deviceText.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(final ModifyEvent e) {
                    generateProcessVariable();
                }
            });
            _propertyText.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(final ModifyEvent e) {
                    generateProcessVariable();
                }
            });
            _characteristicsText.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(final ModifyEvent e) {
                    generateProcessVariable();
                }
            });

            _messageLabel = new Label(composite, SWT.NONE | SWT.WRAP);
            _messageLabel
                    .setText("A Process Variable needs at least one char for the property!");
            _messageLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
                    false, false, 2, 1));
            _messageLabel.setVisible(_processVariable == null);

            DialogFontUtil.setDialogFont(composite);
            //applyDialogFont(composite);
            return composite;
        }

        /**
         * Creates a Label and a Text.
         *
         * @param parent
         *            The parent composite for the Widgets
         * @param labelTitle
         *            The title for the Label
         * @return Text The Text-Widget
         */
        private Text createTextEntry(final Composite parent,
                final String labelTitle) {
            final Label label = new Label(parent, SWT.NONE);
            label.setText(labelTitle);
            final Text text = new Text(parent, SWT.BORDER);
            text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
            return text;
        }

        private void generateProcessVariable() {
            final ControlSystemEnum system = ControlSystemEnum
                    .valueOf(_controlSystemCombo.getText());
            final String device = this.convertString(_deviceText.getText());
            final String characteristics = this.convertString(_characteristicsText
                    .getText());
            try {
                _newProcessVariable = ProcessVariableAdressFactory
                        .getInstance().createProcessVariableAdress(system,
                                device, _propertyText.getText(),
                                characteristics);
                _fullText.setText(_newProcessVariable.getFullName());
                _messageLabel.setVisible(false);
            } catch (final Exception e) {
                _newProcessVariable = null;
                _fullText.setText("No Process Variable");
                _messageLabel.setVisible(true);
            }
        }

        @SuppressWarnings("synthetic-access")
        @Override
        protected void okPressed() {
            _processVariable = _newProcessVariable;
            super.okPressed();
        }

        private String convertString(final String input) {
            if ((input != null) && (input.trim().length() > 0)) {
                return input;
            }
            return null;
        }

        private String getNotNullString(final String input) {
            if (input == null) {
                return "";
            }
            return input;
        }

    }

}
