package org.csstudio.logbook.olog.property.fault;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.csstudio.logbook.LogEntry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

/**
 * Displays the fault and the associated log entries
 *
 * @author Kunal Shroff
 *
 */
public class FaultViewWidget extends Composite {

    // Model
    Fault fault = new Fault();
    List<LogEntry> logEntries = Collections.emptyList();

    // GUI controls
    private Text textOccurred;
    private Text textCleared;
    private Text textBeamLossState;
    private Text textBeamLost;
    private Text textRestored;
    private Text textFaultId;
    private Text textLevel;
    private Text textLogbooks;
    private Text textTags;
    private Text textAssinedGroup;
    private Text textContact;
    private Label lblFaultId;
    private StyledText styledFaultText;

    private ScrolledComposite scrolledComposite;
    private Composite composite;
    private Composite faultComposite;

    public FaultViewWidget(Composite parent, int style) {
        super(parent, SWT.NONE);
        setLayout(new FillLayout(SWT.HORIZONTAL));

        scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);

        composite = new Composite(scrolledComposite, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));

        faultComposite = new Composite(composite, SWT.NONE);
        faultComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        faultComposite.setLayout(new GridLayout(3, false));

        lblFaultId = new Label(faultComposite, SWT.NONE);
        lblFaultId.setText("Fault Id:");

        textFaultId = new Text(faultComposite, SWT.BORDER);
        textFaultId.setEditable(false);
        textFaultId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        Group grpTime = new Group(faultComposite, SWT.NONE);
        grpTime.setLayout(new GridLayout(4, false));
        grpTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        grpTime.setText("Time:");

        Label lblOccurred = new Label(grpTime, SWT.NONE);
        lblOccurred.setBounds(0, 0, 55, 15);
        lblOccurred.setText("Occurred:");

        textOccurred = new Text(grpTime, SWT.BORDER);
        textOccurred.setEditable(false);
        textOccurred.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblCleared = new Label(grpTime, SWT.NONE);
        lblCleared.setText("Cleared");

        textCleared = new Text(grpTime, SWT.BORDER);
        textCleared.setEditable(false);
        textCleared.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblBeamLossState = new Label(grpTime, SWT.NONE);
        lblBeamLossState.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblBeamLossState.setText("Beam Loss State:");

        textBeamLossState = new Text(grpTime, SWT.BORDER);
        textBeamLossState.setEditable(false);
        textBeamLossState.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        new Label(grpTime, SWT.NONE);
        new Label(grpTime, SWT.NONE);

        Label lblLossStart = new Label(grpTime, SWT.NONE);
        lblLossStart.setText("Lost:");

        textBeamLost = new Text(grpTime, SWT.BORDER);
        textBeamLost.setEditable(false);
        textBeamLost.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblRestored = new Label(grpTime, SWT.NONE);
        lblRestored.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblRestored.setText("Restored:");

        textRestored = new Text(grpTime, SWT.BORDER);
        textRestored.setEditable(false);
        textRestored.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Group grpOwner = new Group(faultComposite, SWT.NONE);
        grpOwner.setLayout(new GridLayout(4, false));
        grpOwner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
        grpOwner.setText("Owner:");

        Label lblAssign = new Label(grpOwner, SWT.NONE);
        lblAssign.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblAssign.setText("Assigned Group:");

        textAssinedGroup = new Text(grpOwner, SWT.BORDER);
        textAssinedGroup.setEditable(false);
        textAssinedGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblNewLabel_9 = new Label(grpOwner, SWT.NONE);
        lblNewLabel_9.setText("Contact:");

        textContact = new Text(grpOwner, SWT.BORDER);
        textContact.setEditable(false);
        textContact.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        styledFaultText = new StyledText(faultComposite, SWT.BORDER);
        styledFaultText.setEditable(false);
        styledFaultText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 6));

        Label lblLevel = new Label(faultComposite, SWT.NONE);
        lblLevel.setText("Level:");

        textLevel = new Text(faultComposite, SWT.BORDER);
        textLevel.setEditable(false);
        textLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

        Label lblLogbooks = new Label(faultComposite, SWT.NONE);
        lblLogbooks.setText("Logbooks:");

        textLogbooks = new Text(faultComposite, SWT.BORDER);
        textLogbooks.setEditable(false);
        textLogbooks.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

        Label lblTags = new Label(faultComposite, SWT.NONE);
        lblTags.setText("Tags:");

        textTags = new Text(faultComposite, SWT.BORDER);
        textTags.setEditable(false);
        textTags.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

        updateUI();
    }

    List<FaultLogWidget> faultWidgets = new ArrayList<FaultLogWidget>();

    private void updateUI() {
        if (fault.getId() != 0) {
            textFaultId.setText(String.valueOf(fault.getId()));
        } else {
            textFaultId.setText("New Fault Report");
        }

        styledFaultText.setText(FaultAdapter.createFaultText(fault));

        textAssinedGroup.setText(fault.getAssigned() != null ? fault.getAssigned() : "");
        textContact.setText(fault.getContact() != null ? fault.getContact() : "");

        textOccurred.setText(fault.getFaultOccuredTime() != null ? fault.getFaultOccuredTime().toString() : "");
        textCleared.setText(fault.getFaultClearedTime() != null ? fault.getFaultClearedTime().toString() : "");
        if (fault.getBeamLossState() != null) {
            textBeamLossState.setText(fault.getBeamLossState().toString());
        }

        textBeamLost.setText(fault.getBeamlostTime() != null ? fault.getBeamlostTime().toString() : "");
        textRestored.setText(fault.getBeamRestoredTime() != null ? fault.getBeamRestoredTime().toString() : "");

        for (FaultLogWidget faultLogWidget : faultWidgets) {
            faultLogWidget.dispose();
        }

        logEntries.sort((LogEntry o1, LogEntry o2) -> {
            return o1.getCreateDate().compareTo(o2.getCreateDate());
        });
        for (LogEntry logEntry : logEntries) {
            FaultLogWidget faultLogWidget = new FaultLogWidget(composite, SWT.NONE);
            faultWidgets.add(faultLogWidget);
            faultLogWidget.setLogEntry(logEntry);
            GridData fd_faultLogWidget = new GridData(SWT.FILL, SWT.FILL, true, true);
            faultLogWidget.setLayoutData(fd_faultLogWidget);
        }

        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setContent(composite);
        scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        composite.pack();
        composite.getParent().layout();
    }

    public synchronized Fault getFault() {
        return fault;
    }

    public synchronized void setFault(Fault fault) {
        this.fault = fault;
        updateUI();
    }

    public synchronized List<LogEntry> getLogEntries() {
        return logEntries;
    }

    public synchronized void setLogEntries(List<LogEntry> logEntries) {
        this.logEntries = logEntries;
        updateUI();
    }
}
