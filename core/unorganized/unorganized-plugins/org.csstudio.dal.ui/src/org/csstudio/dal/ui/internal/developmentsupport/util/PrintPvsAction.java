/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.dal.ui.internal.developmentsupport.util;

import java.util.Set;

import org.csstudio.dal.ui.dnd.rfc.ProcessVariablePopupAction;
import org.csstudio.dal.ui.util.LayoutUtil;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * Action implementation that open´s an information dialog that shows all
 * selected PVs. The dialog enables the user to copy the PV strings to the
 * clipboard.
 *
 * @author Sven Wende
 *
 */
public class PrintPvsAction extends ProcessVariablePopupAction {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handlePvs(final Set<IProcessVariableAddress> pvs) {
        final StringBuffer sb = new StringBuffer();
        for (final IProcessVariableAddress pv : pvs) {
            final String fullName = pv.getFullName();

            if (fullName != null && fullName.length() > 0) {
                sb.append(fullName);
                sb.append("\r\n");
            }
        }

        final CopyToClipboardDialog dialog = new CopyToClipboardDialog(Display
                .getCurrent().getActiveShell(), sb.toString());

        final int result = dialog.open();

        if (result == CopyToClipboardDialog.COPY_TO_CLIPBOARD_ID) {
            final Clipboard clipboard = new Clipboard(PlatformUI.getWorkbench()
                    .getDisplay());
            clipboard.setContents(new String[] { dialog.getTextToCopy() },
                    new Transfer[] { TextTransfer.getInstance() });
        }
    }

    /**
     * Dialog that displays a specified text in a simple textbox and provides
     * the ability to store the text on the clipboard.
     *
     * @author swende
     *
     */
    class CopyToClipboardDialog extends Dialog {
        public static final int COPY_TO_CLIPBOARD_ID = 111119;

        private String _text;

        private Text _textField;

        /**
         * Constructor.
         *
         * @param parentShell
         *            The parent of the dialog.
         * @param text
         *            the text that should be displayed
         */
        public CopyToClipboardDialog(final Shell parentShell, final String text) {
            super(parentShell);
            this.setShellStyle(SWT.MODELESS | SWT.CLOSE | SWT.MAX | SWT.TITLE
                    | SWT.BORDER | SWT.RESIZE);
            assert text != null;
            _text = text;
        }

        /**
         *
         * {@inheritDoc}
         */
        @Override
        protected void configureShell(final Shell shell) {
            super.configureShell(shell);
            shell.setText("Selected Process Variables");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Control createDialogArea(final Composite parent) {
            final Composite c = (Composite) super.createDialogArea(parent);
            _textField = new Text(c, SWT.MULTI | SWT.V_SCROLL);
            _textField.setLayoutData(LayoutUtil
                    .createGridDataForFillingCell(300, 150));
            _textField.setText(_text);
            return c;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void okPressed() {
            super.okPressed();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Control createButtonBar(final Composite parent) {
            // TODO Auto-generated method stub
            return super.createButtonBar(parent);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void createButtonsForButtonBar(final Composite parent) {
            super.createButtonsForButtonBar(parent);

            createButton(parent, COPY_TO_CLIPBOARD_ID, "Copy to Clipboard",
                    false);
        }

        /**
         * Updates the text to be copied based on the current selection in the
         * text field.
         */
        private void updateTextToCopy() {
            if (_textField.getSelectionCount() != 0) {
                _text = _textField.getSelectionText();
            }
        }

        /**
         * Returns the text to be copied into the clipboard.
         *
         * @return the text to be copied into the clipboard.
         */
        public String getTextToCopy() {
            return _text;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void buttonPressed(final int buttonId) {
            super.buttonPressed(buttonId);

            if (buttonId == COPY_TO_CLIPBOARD_ID) {
                updateTextToCopy();
                setReturnCode(COPY_TO_CLIPBOARD_ID);
                close();
            }
        }

    }

}
