
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

package org.csstudio.utility.screenshot.desy.dialog;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import org.csstudio.utility.screenshot.desy.DestinationPlugin;
import org.csstudio.utility.screenshot.desy.LogbookEntry;
import org.csstudio.utility.screenshot.desy.PropertyNames;
import org.csstudio.utility.screenshot.desy.internal.localization.LogbookSenderMessages;
import org.csstudio.utility.screenshot.desy.preference.DestinationPreferenceConstants;
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
import org.eclipse.swt.widgets.Combo;
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

public class LogbookSenderDialog extends Dialog implements SelectionListener {
    
    private GregorianCalendar cal;
    
    private Shell parentShell;
    
    private LogbookEntry logbookEntry = null;
    
    private LogbookEntryStorage storage;
    
    private Button buttonSend = null;
    private Button buttonCancel = null;
    private Button btnClearLbEntry = null;
    private Label labelAuthor = null;
    private Label labelDate = null;
    private Label labelTime = null;
    private Label labelSeverity = null;
    private Label labelKeywords = null;
    private Label labelTitle = null;
    private Label labelText = null;
    private Label labelGroup = null;
    private Label labelLogbook = null;
    private Label labelIdentifyer = null;
    private Label labelDummyRow1 = null;
    private Label labelDummyRow2 = null;
    private Label labelDummyRow3 = null;
    private Label labelDummyRow4 = null;
    private Label labelDummyRow5 = null;
    private Text textText = null;
    private Text textIdentifyer = null;
    private Text textTitel = null;
    private Text textKeywordlist = null;
    private Text textDate = null;
    private Text textTime = null;
    private Text textAuthor = null;
    private Combo cbLogbook = null;
    private Combo cbSeverity = null;
    private Combo cbGroup = null;
    private ComboViewer cbKeyword = null;  
    private ComboHistoryHelper keywordHelper = null;  
    private String[] logbookList = null;
    private String[] groupList = null;

    private final String DATE_FORMAT = "dd-MM-yyyy";
    private final String TIME_FORMAT = "HH:mm:ss";
    private final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final String KW_LIST_TAG = "keyword_list";

    private final int INIT_WIDTH = DialogUnit.mapUnitX(432);
    private final int INIT_HEIGHT = DialogUnit.mapUnitY(310);

    public LogbookSenderDialog(Shell shell) {
        
        super(shell);
        parentShell = shell;
        storage = new LogbookEntryStorage();
        
        setBlockOnOpen(true);

        IPreferencesService pref = Platform.getPreferencesService();
        String temp = pref.getString(DestinationPlugin.PLUGIN_ID, DestinationPreferenceConstants.LOGBOOK_NAMES, "NONE", null);
        StringTokenizer token = new StringTokenizer(temp, ";");
        if(token.countTokens() > 0) {
            logbookList = new String[token.countTokens()];
            
            int count = 0;
            while(token.hasMoreTokens()) {
                logbookList[count++] = token.nextToken();
            }
        }
        
        token = null;
        
        temp = pref.getString(DestinationPlugin.PLUGIN_ID, DestinationPreferenceConstants.GROUP_NAMES, "NONE", null);
        token = new StringTokenizer(temp, ";");
        if(token.countTokens() > 0) {
            groupList = new String[token.countTokens()];
            int count = 0;
            while(token.hasMoreTokens()) {
                groupList[count++] = token.nextToken();
            }
        }
        
        cal = new GregorianCalendar();
    }

    @Override
	protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(DestinationPlugin.getDefault().getNameAndVersion()
                      + LogbookSenderMessages.getString("LogbookSenderDialog.DIALOG_TITLE"));
    }

    @Override
	protected void initializeBounds() {
        Rectangle rect = parentShell.getBounds();        
        this.getShell().setBounds(rect.x + ((rect.width - INIT_WIDTH) / 2), rect.y + ((rect.height - INIT_HEIGHT) / 2), INIT_WIDTH, INIT_HEIGHT);
    }

    @Override
	protected Control createDialogArea(Composite parent) {
        
        String temp = null;
        GridData gd = null;        

        GridLayout layout = new GridLayout(7, true);
        layout.verticalSpacing = 12;
        
        parent.setLayout(layout);

        logbookEntry = storage.readLogbookEntry();

        // First row
        labelDummyRow1 = new Label(parent, SWT.SHADOW_NONE | SWT.LEFT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 6;
        gd.horizontalAlignment = SWT.FILL;        
        labelDummyRow1.setLayoutData(gd);

        btnClearLbEntry = new Button(parent, SWT.PUSH);
        btnClearLbEntry.setText(LogbookSenderMessages.getString("LogbookSenderDialog.BUTTON_RESET"));
        btnClearLbEntry.addSelectionListener(this);
        gd = new GridData();
        gd.horizontalSpan = 1;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;        
        btnClearLbEntry.setLayoutData(gd);
        
        // Second row
        labelIdentifyer = new Label(parent, SWT.SHADOW_NONE | SWT.LEFT);
        labelIdentifyer.setText(LogbookSenderMessages.getString("LogbookSenderDialog.LABEL_IDENTIFYER"));        
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        gd.horizontalAlignment = SWT.BEGINNING;        
        labelIdentifyer.setLayoutData(gd);
        
        textIdentifyer = new Text(parent, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;        
        textIdentifyer.setLayoutData(gd);
        
        labelDummyRow2 = new Label(parent, SWT.SHADOW_NONE | SWT.LEFT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 4;
        gd.horizontalAlignment = SWT.BEGINNING;        
        labelDummyRow2.setLayoutData(gd);

        // Third row
        labelLogbook = new Label(parent, SWT.SHADOW_NONE | SWT.LEFT);
        labelLogbook.setText(LogbookSenderMessages.getString("LogbookSenderDialog.LABEL_LOGBOOK"));
        gd = new GridData();
        gd.horizontalSpan = 1;
        gd.horizontalAlignment = SWT.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        labelLogbook.setLayoutData(gd);
        
        cbLogbook = new Combo(parent, SWT.DROP_DOWN);
        cbLogbook.addSelectionListener(this);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;       
        cbLogbook.setLayoutData(gd);
        if(logbookList != null) {
            cbLogbook.setItems(logbookList);
            if (logbookEntry.getLogbookName() != null) {
                cbLogbook.select(cbLogbook.indexOf(logbookEntry.getLogbookName()));
            } else {
                cbLogbook.select(0);
            }
            textIdentifyer.setText(createIdentifyer(cbLogbook.getItem(cbLogbook.getSelectionIndex())));
        }
        
        labelDummyRow3 = new Label(parent, SWT.SHADOW_NONE | SWT.LEFT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        gd.horizontalAlignment = SWT.BEGINNING;        
        labelDummyRow3.setLayoutData(gd);

        labelSeverity = new Label(parent, SWT.SHADOW_NONE | SWT.LEFT);
        labelSeverity.setText(LogbookSenderMessages.getString("LogbookSenderDialog.LABEL_SEVERITY"));        
        gd = new GridData();
        gd.horizontalSpan = 1;
        gd.horizontalAlignment = SWT.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        labelSeverity.setLayoutData(gd);
        
        cbSeverity = new Combo(parent, SWT.DROP_DOWN);
        cbSeverity.add("NONE");
        cbSeverity.add("DOCU");
        cbSeverity.add("DONE");
        cbSeverity.add("ERROR");
        cbSeverity.add("FATAL");
        cbSeverity.add("FIXED");
        cbSeverity.add("IDEA");
        cbSeverity.add("INFO");
        cbSeverity.add("TODO");
        cbSeverity.add("WARN");
        if (logbookEntry.getLogbookProperty("LOGSEVERITY") != null) {
            cbSeverity.select(cbSeverity.indexOf(logbookEntry.getLogbookProperty("LOGSEVERITY")));
        } else {
            cbSeverity.select(cbSeverity.indexOf("NONE"));
        }
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        cbSeverity.setLayoutData(gd);
        
        // Fourth row
        labelDate = new Label(parent, SWT.SHADOW_NONE | SWT.LEFT);
        labelDate.setText(LogbookSenderMessages.getString("LogbookSenderDialog.LABEL_DATE"));
        gd = new GridData();
        gd.horizontalSpan = 1;
        gd.horizontalAlignment = SWT.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        labelDate.setLayoutData(gd);
        
        textDate = new Text(parent, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        textDate.setLayoutData(gd);
        
        labelDummyRow4 = new Label(parent, SWT.SHADOW_NONE | SWT.LEFT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        gd.horizontalAlignment = SWT.BEGINNING;        
        labelDummyRow4.setLayoutData(gd);

        labelTime = new Label(parent, SWT.SHADOW_NONE | SWT.LEFT);
        labelTime.setText(LogbookSenderMessages.getString("LogbookSenderDialog.LABEL_TIME"));
        gd = new GridData();
        gd.horizontalSpan = 1;
        gd.horizontalAlignment = SWT.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        labelTime.setLayoutData(gd);
        
        textTime = new Text(parent, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        textTime.setLayoutData(gd);
        
        initDateAndTimeFields(false);
        
        // Fifth row
        labelAuthor = new Label(parent, SWT.SHADOW_NONE | SWT.LEFT);
        labelAuthor.setText(LogbookSenderMessages.getString("LogbookSenderDialog.LABEL_AUTHOR"));        
        gd = new GridData();
        gd.horizontalSpan = 1;
        gd.horizontalAlignment = SWT.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        labelAuthor.setLayoutData(gd);
        
        textAuthor = new Text(parent, SWT.SINGLE | SWT.BORDER);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        textAuthor.setLayoutData(gd);
        if(logbookEntry != null) {
            temp = logbookEntry.getLogbookProperty(PropertyNames.PROPERTY_ACCOUNTNAME);
            if(temp != null) {
                textAuthor.setText(temp);
            }
        }
        
        labelDummyRow5 = new Label(parent, SWT.SHADOW_NONE | SWT.LEFT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        gd.horizontalAlignment = SWT.BEGINNING;        
        labelDummyRow5.setLayoutData(gd);

        labelGroup = new Label(parent, SWT.SHADOW_NONE | SWT.LEFT);
        labelGroup.setText(LogbookSenderMessages.getString("LogbookSenderDialog.LABEL_GROUP"));
        gd = new GridData();
        gd.horizontalSpan = 1;
        gd.horizontalAlignment = SWT.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        labelGroup.setLayoutData(gd);
        
        cbGroup = new Combo(parent, SWT.DROP_DOWN);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        cbGroup.setLayoutData(gd);
        if(groupList != null) {
            cbGroup.setItems(groupList);
            if (logbookEntry.getLogbookProperty(PropertyNames.PROPERTY_LOGGROUP) != null) {
                cbGroup.select(cbGroup.indexOf(logbookEntry.getLogbookProperty(PropertyNames.PROPERTY_LOGGROUP)));
            } else {
                cbGroup.select(cbGroup.indexOf("MKS-2"));
            }
        }
        
        // Sixth row
        labelKeywords = new Label(parent, SWT.SHADOW_NONE | SWT.LEFT);
        labelKeywords.setText(LogbookSenderMessages.getString("LogbookSenderDialog.LABEL_KEYWORDS"));
        gd = new GridData();
        gd.horizontalSpan = 1;
        gd.horizontalAlignment = SWT.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        labelKeywords.setLayoutData(gd);
        
        textKeywordlist = new Text(parent, SWT.SINGLE | SWT.BORDER);
        gd = new GridData();
        gd.horizontalSpan = 4;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        textKeywordlist.setLayoutData(gd);
        if(logbookEntry != null) {
            temp = logbookEntry.getLogbookProperty(PropertyNames.PROPERTY_KEYWORDS);
            if(temp != null) {
                textKeywordlist.setText(temp);
            }
        }
        
        cbKeyword = new ComboViewer(parent, SWT.SINGLE | SWT.BORDER);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        cbKeyword.getCombo().setLayoutData(gd);
        
        keywordHelper = new ComboHistoryHelper(DestinationPlugin.getDefault().getDialogSettings(),
                                                KW_LIST_TAG, cbKeyword)
        {
            @Override
            public void newSelection(String keyword)
            { 
                setKeyword(keyword);   
            }            
        };
        
        cbKeyword.getCombo().addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                getKeywordHelper().saveSettings();
            }
        });
        
        keywordHelper.loadSettings();
        
        // Seventh row
        labelTitle = new Label(parent, SWT.SHADOW_NONE | SWT.LEFT);
        labelTitle.setText(LogbookSenderMessages.getString("LogbookSenderDialog.LABEL_TITLE"));
        gd = new GridData();
        gd.horizontalSpan = 1;
        gd.horizontalAlignment = SWT.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        labelTitle.setLayoutData(gd);
        
        textTitel = new Text(parent, SWT.SINGLE | SWT.BORDER);
        gd = new GridData();
        gd.horizontalSpan = 6;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        textTitel.setLayoutData(gd);
        if(logbookEntry != null)
        {
            temp = logbookEntry.getLogbookProperty(PropertyNames.PROPERTY_TITLE);
            
            if(temp != null)
            {
                textTitel.setText(temp);
            }
        }
        
        // Eigth row
        labelText = new Label(parent, SWT.SHADOW_NONE | SWT.LEFT);
        labelText.setText(LogbookSenderMessages.getString("LogbookSenderDialog.LABEL_TEXT"));
        gd = new GridData();
        gd.horizontalSpan = 7;
        gd.horizontalAlignment = SWT.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        labelText.setLayoutData(gd);
        
        // Ninth row
        textText = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);        
        gd = new GridData();
        gd.horizontalSpan = 7;
        gd.verticalSpan = 15;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        textText.setLayoutData(gd);
        if(logbookEntry != null)
        {
            temp = logbookEntry.getLogbookProperty(PropertyNames.PROPERTY_TEXT);
            
            if(temp != null)
            {
                textText.setText(temp);
            }
        }

        parent.pack();
        
        return parent;
    }

    @Override
	protected Control createButtonBar(Composite parent)
    {
        Label labelDummy = null;
        GridData gd = null;
        
        labelDummy = new Label(parent, 0);
        gd = new GridData();        
        gd.horizontalSpan = 5;
        labelDummy.setLayoutData(gd);
        
        // Button OK
        buttonSend = new Button(parent, SWT.PUSH);
        buttonSend.setText(LogbookSenderMessages.getString("LogbookSenderDialog.BUTTON_SEND"));
        buttonSend.addSelectionListener(this);
        gd = new GridData();        
        gd.horizontalSpan = 1;
        gd.verticalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        buttonSend.setLayoutData(gd);
        
        // Button Cancel
        buttonCancel = new Button(parent, SWT.PUSH);
        buttonCancel.setText(LogbookSenderMessages.getString("LogbookSenderDialog.BUTTON_CANCEL"));
        buttonCancel.addSelectionListener(this);
        gd = new GridData();        
        gd.horizontalSpan = 1;
        gd.verticalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;        
        gd.verticalAlignment = SWT.FILL;
        buttonCancel.setLayoutData(gd);

        return parent;
    }

    public LogbookEntry getLogbookEntry() {
        return logbookEntry;
    }
    
    private void createLogbookEntry() {
        
        String temp = null;
        
        logbookEntry = new LogbookEntry();
        
        logbookEntry.setLogbookProperty(PropertyNames.PROPERTY_IDENTIFYER, textIdentifyer.getText());
        
        SimpleDateFormat tempFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
        logbookEntry.setLogbookProperty(PropertyNames.PROPERTY_ENTRYDATE, tempFormat.format(cal.getTime()));
        logbookEntry.setLogbookProperty(PropertyNames.PROPERTY_EVENTFROM, tempFormat.format(cal.getTime()));
        tempFormat = null;
        
        logbookEntry.setLogbookProperty(PropertyNames.PROPERTY_LOGSEVERITY, cbSeverity.getItem(cbSeverity.getSelectionIndex()));

        temp = textAuthor.getText().trim();
        if(temp.length() > 0)
        {
            logbookEntry.setLogbookProperty(PropertyNames.PROPERTY_ACCOUNTNAME, temp);
        }
        else
        {
            logbookEntry.setLogbookProperty(PropertyNames.PROPERTY_ACCOUNTNAME, "NONE");
        }
        
        logbookEntry.setLogbookProperty(PropertyNames.PROPERTY_LOGGROUP, cbGroup.getItem(cbGroup.getSelectionIndex()));

        temp = textKeywordlist.getText().trim();
        if(temp.length() > 0)
        {
            logbookEntry.setLogbookProperty(PropertyNames.PROPERTY_KEYWORDS, temp);
        }
        
        temp = textTitel.getText().trim();
        if(temp.length() > 0)
        {
            logbookEntry.setLogbookProperty(PropertyNames.PROPERTY_TITLE, temp);
        }
        else
        {
            logbookEntry.setLogbookProperty(PropertyNames.PROPERTY_TITLE, "NONE");            
        }
        
        temp = textText.getText().trim();
        if(temp.length() > 0)
        {
            logbookEntry.setLogbookProperty(PropertyNames.PROPERTY_TEXT, temp);
        }
        else
        {
            logbookEntry.setLogbookProperty(PropertyNames.PROPERTY_TEXT, "NONE");            
        }
        
        logbookEntry.setLogbookName(cbLogbook.getItem(cbLogbook.getSelectionIndex()));
    }
    
    public String createIdentifyer(String logbook)
    {
        SimpleDateFormat    dateFormat  = new SimpleDateFormat(":yyMMdd-HH:mm:ss");
        String              id          = null;
        
        id = logbook + dateFormat.format(cal.getTime());
        
        return id;
    }

    private void initDateAndTimeFields(boolean newDate) {
        
    	if(newDate) {
            cal = null;
            cal = new GregorianCalendar();
        }
        
        SimpleDateFormat tempFormat = new SimpleDateFormat(DATE_FORMAT);
        textDate.setText(tempFormat.format(cal.getTime()));
        tempFormat = null;
        
        tempFormat = new SimpleDateFormat(TIME_FORMAT);        
        textTime.setText(tempFormat.format(cal.getTime()));
        tempFormat = null;
    }

    public void setKeyword(String keyword)
    {
        String line = this.textKeywordlist.getText().trim();
        
        if(line.length() > 0)
        {
            textKeywordlist.setText(line + "," + keyword);
        }
        else
        {
            textKeywordlist.setText(keyword);
        }
        
        keywordHelper.addEntry(keyword);
    }

    public ComboHistoryHelper getKeywordHelper() {
    
    	return keywordHelper;
    }
    
    public void widgetDefaultSelected(SelectionEvent event) {
        widgetSelected(event);
    }

    public void widgetSelected(SelectionEvent event) {
        if(event.widget instanceof Button) {
            Button source = (Button)event.widget;
            
            if(source
                .getText()
                .compareToIgnoreCase(LogbookSenderMessages.getString("LogbookSenderDialog.BUTTON_SEND")) == 0) {
                createLogbookEntry();
                storage.storeLogbookEntry(logbookEntry);
                this.setReturnCode(Window.OK);
                this.close();
            }
            else if(source.getText().compareToIgnoreCase(LogbookSenderMessages.getString("LogbookSenderDialog.BUTTON_CANCEL")) == 0)
            {
                this.setReturnCode(Window.CANCEL);
    
                this.close();
            }
            else if(source.getText().compareToIgnoreCase(LogbookSenderMessages.getString("LogbookSenderDialog.BUTTON_RESET")) == 0)
            {
                initDateAndTimeFields(true);
                                
                textIdentifyer.setText(this.createIdentifyer(cbLogbook.getItem(cbLogbook.getSelectionIndex())));

                textTitel.setText("");
                textKeywordlist.setText("");
                textText.setText("");
            }
        } else if(event.widget instanceof Combo) {
            Combo source = (Combo) event.widget;
            String name = source.getItem(source.getSelectionIndex());
            textIdentifyer.setText(createIdentifyer(name));
        }
    }
}
