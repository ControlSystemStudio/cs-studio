
/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.utility.screenshot.dialog;

import org.csstudio.util.swt.ComboHistoryHelper;
import org.csstudio.utility.screenshot.MailEntry;
import org.csstudio.utility.screenshot.ScreenshotPlugin;
import org.csstudio.utility.screenshot.internal.localization.ScreenshotMessages;
import org.csstudio.utility.screenshot.preference.ScreenshotPreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 *
 * @author Markus Moeller
 *
 */

public class MailSenderDialog extends Dialog implements SelectionListener {

    private Shell parentShell = null;
    private MailEntry mailEntry = null;
    private Button buttonSend = null;
    private Button buttonCancel = null;
    private Button btnClearMEntry = null;
    private Button btnCarbonCopy = null;
    private Label labelFrom = null;
    private Label labelTo = null;
    private Label labelList = null;
    private Label labelSubject = null;
    private Label labelMailText = null;
    private Label labelDummyRow1 = null;
    private Label labelDummyRow4 = null;
    private Text textFrom = null;
    private Text textTo = null;
    private Text textSubject = null;
    private Text textMailText = null;
    private ComboHistoryHelper addressHelper = null;
    private ComboViewer cbvAddresses = null;

    private final int INIT_WIDTH = DialogUnit.mapUnitX(432);
    private final int INIT_HEIGHT = DialogUnit.mapUnitY(310);

    private static final String ADR_LIST_TAG = "mail_list";

    /**
     *
     * @param w
     */
    public MailSenderDialog(Shell shell) {

        super(shell);
        parentShell = shell;
        setBlockOnOpen(true);
    }

    /**
     *
     */
    @Override
    protected void configureShell(Shell shell) {

        super.configureShell(shell);
        shell.setText(ScreenshotPlugin.getDefault().getNameAndVersion() + ScreenshotMessages.getString("MailSenderDialog.DIALOG_TITLE"));
    }

    /**
     *
     */
    @Override
    protected void initializeBounds()
    {
        Rectangle rect = parentShell.getBounds();

        this.getShell().setBounds(rect.x + ((rect.width - INIT_WIDTH) / 2), rect.y + ((rect.height - INIT_HEIGHT) / 2), INIT_WIDTH, INIT_HEIGHT);
    }

    /**
     *
     */
    @Override
    protected Control createDialogArea(Composite parent) {

        String temp = null;
        GridData gd = null;

        GridLayout layout = new GridLayout(7, true);
        layout.verticalSpacing = 12;

        parent.setLayout(layout);

        if(ScreenshotPlugin.getDefault().isMailEntryAvailable())
        {
            mailEntry = ScreenshotPlugin.getDefault().getMailEntry();
        }

        // First row
        labelDummyRow1 = new Label(parent, SWT.SHADOW_NONE | SWT.LEFT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 6;
        gd.horizontalAlignment = SWT.FILL;
        labelDummyRow1.setLayoutData(gd);

        btnClearMEntry = new Button(parent, SWT.PUSH);
        btnClearMEntry.setText(ScreenshotMessages.getString("MailSenderDialog.BUTTON_CLEAR"));
        btnClearMEntry.addSelectionListener(this);
        gd = new GridData();
        gd.horizontalSpan = 1;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        btnClearMEntry.setLayoutData(gd);

        // Second row
        labelFrom = new Label(parent, SWT.SHADOW_NONE | SWT.LEFT);
        labelFrom.setText(ScreenshotMessages.getString("MailSenderDialog.LABEL_FROM"));
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        gd.horizontalAlignment = SWT.BEGINNING;
        labelFrom.setLayoutData(gd);

        textFrom = new Text(parent, SWT.SINGLE | SWT.BORDER);

        IPreferencesService pref = Platform.getPreferencesService();
        String txt = pref.getString(ScreenshotPlugin.PLUGIN_ID, ScreenshotPreferenceConstants.MAIL_ADDRESS_SENDER, "css-user@desy.de", null);
        textFrom.setText(txt);

        gd = new GridData();
        gd.horizontalSpan = 4;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        textFrom.setLayoutData(gd);
        if(mailEntry != null) {

            temp = mailEntry.getMailFromAddress();
            if(temp != null) {
                textFrom.setText(temp);
            }
        }

        btnCarbonCopy = new Button(parent, SWT.CHECK);
        btnCarbonCopy.setText(ScreenshotMessages.getString("MailSenderDialog.LABEL_COPY"));

        boolean sendCopy = pref.getBoolean(ScreenshotPlugin.PLUGIN_ID, ScreenshotPreferenceConstants.COPY_TO_SENDER, false, null);
        btnCarbonCopy.setSelection(sendCopy);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        btnCarbonCopy.setLayoutData(gd);

        // Third row
        labelTo = new Label(parent, SWT.SHADOW_NONE | SWT.LEFT);
        labelTo.setText(ScreenshotMessages.getString("MailSenderDialog.LABEL_TO"));
        gd = new GridData();
        gd.horizontalSpan = 1;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        labelTo.setLayoutData(gd);

        textTo = new Text(parent, SWT.SINGLE | SWT.BORDER);
        gd = new GridData();
        gd.horizontalSpan = 6;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        textTo.setLayoutData(gd);
        if(mailEntry != null)
        {
            temp = mailEntry.getMailToAddress();

            if(temp != null)
            {
                textTo.setText(temp);
            }
        }

        // Fourth row
        labelList = new Label(parent, SWT.SHADOW_NONE | SWT.LEFT);
        labelList.setText(ScreenshotMessages.getString("MailSenderDialog.LABEL_LIST"));
        gd = new GridData();
        gd.horizontalSpan = 1;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        labelList.setLayoutData(gd);

        cbvAddresses = new ComboViewer(parent, SWT.SINGLE | SWT.BORDER);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        cbvAddresses.getCombo().setLayoutData(gd);

        labelDummyRow4 = new Label(parent, SWT.SHADOW_NONE | SWT.LEFT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 4;
        gd.horizontalAlignment = SWT.FILL;
        labelDummyRow4.setLayoutData(gd);

        addressHelper = new ComboHistoryHelper(ScreenshotPlugin.getDefault().getDialogSettings(),
                                               ADR_LIST_TAG, cbvAddresses)
        {
            @Override
            public void newSelection(String adr)
            {
                setMailAddress(adr);
            }
        };

        cbvAddresses.getCombo().addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                addressHelper.saveSettings();
            }
        });

        addressHelper.loadSettings();

        // Fifth row
        labelSubject = new Label(parent, SWT.SHADOW_NONE | SWT.LEFT);
        labelSubject.setText(ScreenshotMessages.getString("MailSenderDialog.LABEL_SUBJECT"));
        gd = new GridData();
        gd.horizontalSpan = 1;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        labelSubject.setLayoutData(gd);

        textSubject = new Text(parent, SWT.SINGLE | SWT.BORDER);
        textSubject.setText("From CSS With Love");
        gd = new GridData();
        gd.horizontalSpan = 6;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        textSubject.setLayoutData(gd);
        if(mailEntry != null)
        {
            temp = mailEntry.getMailSubject();

            if(temp != null)
            {
                textSubject.setText(temp);
            }
        }

        // Sixth row
        labelMailText = new Label(parent, SWT.SHADOW_NONE | SWT.LEFT);
        labelMailText.setText(ScreenshotMessages.getString("MailSenderDialog.LABEL_TEXT"));
        gd = new GridData();
        gd.horizontalSpan = 1;
        gd.verticalSpan = 5;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        labelMailText.setLayoutData(gd);

        textMailText = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
        gd = new GridData();
        gd.horizontalSpan = 6;
        gd.verticalSpan = 5;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        textMailText.setLayoutData(gd);
        if(mailEntry != null)
        {
            temp = mailEntry.getMailText();

            if(temp != null)
            {
                textMailText.setText(temp);
            }
        }

        return parent;
    }

    /**
     *
     */
    @Override
    protected Control createButtonBar(Composite parent) {

        Label labelDummy = null;
        GridData gd = null;

        labelDummy = new Label(parent, 0);
        gd = new GridData();
        gd.horizontalSpan = 5;
        labelDummy.setLayoutData(gd);

        // Button OK
        buttonSend = new Button(parent, SWT.PUSH);
        buttonSend.setText(ScreenshotMessages.getString("MailSenderDialog.BUTTON_SEND"));
        buttonSend.addSelectionListener(this);
        gd = new GridData();
        gd.horizontalSpan = 1;
        gd.verticalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        buttonSend.setLayoutData(gd);

        // Button Cancel
        buttonCancel = new Button(parent, SWT.PUSH);
        buttonCancel.setText(ScreenshotMessages.getString("MailSenderDialog.BUTTON_CANCEL"));
        buttonCancel.addSelectionListener(this);
        gd = new GridData();
        gd.horizontalSpan = 1;
        gd.verticalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        buttonCancel.setLayoutData(gd);

        return parent;
    }

    public void setMailAddress(String address)
    {
        String line = textTo.getText().trim();

        if(line.length() > 0)
        {
            textTo.setText(line + "," + address);
        }
        else
        {
            textTo.setText(address);
        }

        addressHelper.addEntry(address);
    }

    public MailEntry getMailEntry()
    {
        return mailEntry;
    }

    private boolean createMailEntry()
    {
        boolean result = true;

        String f = null;
        String t = null;
        String s = null;
        String m = null;
        boolean cpy = false;

        f = textFrom.getText().trim();

        t = textTo.getText().trim();
        if(t.length() < 0)
        {
            result = false;
        }

        s = textSubject.getText().trim();

        m = textMailText.getText().trim();

        cpy = btnCarbonCopy.getSelection();

        if(result == true)
        {
            mailEntry = new MailEntry(f, t, s, m, cpy);
        }

        return result;
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent event) {
        widgetSelected(event);
    }

    @Override
    public void widgetSelected(SelectionEvent event) {

        if(event.widget instanceof Button) {
            Button source = (Button)event.widget;

            if(source.getText().compareToIgnoreCase(ScreenshotMessages.getString("MailSenderDialog.BUTTON_SEND")) == 0)
            {
                createMailEntry();
                this.setReturnCode(Window.OK);
                this.close();
            }
            else if(source.getText().compareToIgnoreCase(ScreenshotMessages.getString("MailSenderDialog.BUTTON_CANCEL")) == 0)
            {
                this.setReturnCode(Window.CANCEL);
                this.close();
            }
            else if(source.getText().compareToIgnoreCase(ScreenshotMessages.getString("MailSenderDialog.BUTTON_CLEAR")) == 0)
            {
                textTo.setText("");
                textSubject.setText("");
                textMailText.setText("");
            }
        }
    }
}
