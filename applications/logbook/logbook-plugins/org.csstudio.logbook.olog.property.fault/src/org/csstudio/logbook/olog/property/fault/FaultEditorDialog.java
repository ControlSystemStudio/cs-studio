package org.csstudio.logbook.olog.property.fault;

import static org.csstudio.logbook.LogbookBuilder.logbook;
import static org.csstudio.logbook.TagBuilder.tag;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.csstudio.email.EMailSender;
import org.csstudio.email.Preferences;
import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.Logbook;
import org.csstudio.logbook.LogbookBuilder;
import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.LogbookClientManager;
import org.csstudio.logbook.PropertyBuilder;
import org.csstudio.logbook.Tag;
import org.csstudio.logbook.TagBuilder;
import org.csstudio.logbook.util.LogEntryUtil;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * A dialog to create a new Fault Entry
 *
 * @author Kunal Shroff
 *
 */
public class FaultEditorDialog extends Dialog {

    private static final Logger log = Logger.getLogger(FaultEditorDialog.class.getCanonicalName());

    private LogbookClient client;
    private List<String> tags = Collections.emptyList();
    private List<String> logbooks = Collections.emptyList();

    private final boolean create;
    private List<Integer> logEntries;
    private Fault fault;
    private LogEntry faultLog;

    private FaultEditorWidget faultEditorWidget;

    private String faultLevel;
    private TagBuilder faultTag;
    private LogbookBuilder faultLogbook;

    private final IPreferencesService prefs = Platform.getPreferencesService();
    /**
     *
     * @param parentShell parent shell
     * @param create specify if you are creating of updating a fault
     * @param fault the fault with which to initialize the dialog
     * @param faultLog the log entry which the fault maps to
     * @param associatedLogEntries the log entries assigned to this fault
     */
    protected FaultEditorDialog(Shell parentShell, boolean create, Fault fault, LogEntry faultLog, List<Integer> associatedLogEntries) {
        super(parentShell);
        setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE | SWT.RESIZE);

        this.create = create;
        this.fault = fault;
        this.faultLog = faultLog;
        this.logEntries = associatedLogEntries;

        if (client == null) {
            try {
                client = LogbookClientManager.getLogbookClientFactory().getClient();
                tags = client.listTags().stream().map(Tag::getName).collect(Collectors.toList());
                logbooks = client.listLogbooks().stream().map(Logbook::getName).collect(Collectors.toList());

                faultLevel = Platform.getPreferencesService().getString(
                        "org.csstudio.logbook.olog.property.fault", "fault.level",
                        "Problem",
                        null);
                String faultTagName = Platform.getPreferencesService()
                        .getString("org.csstudio.logbook.olog.property.fault", "fault.tag", "Fault", null);
                if (tags.contains(faultTagName)) {
                    faultTag = tag(faultTagName);
                }
                String faultLogbookName = Platform.getPreferencesService()
                        .getString("org.csstudio.logbook.olog.property.fault", "fault.logbook", "Operations", null);
                if (logbooks.contains(faultLogbookName)) {
                    faultLogbook = logbook(faultLogbookName);
                }
            } catch (Exception e) {
                Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Failed to open fault dialog", e);
                ErrorDialog.openError(parentShell, "Failed to open fault dialog",
                        e.getLocalizedMessage(), status);
            }
        }
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        if (create) {
            getShell().setText("Create a Fault Report");
        } else {
            getShell().setText("Update Fault Report");
        }

        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new FillLayout(SWT.HORIZONTAL));

        FaultConfiguration fc = FaultConfigurationFactory.getConfiguration();

        faultEditorWidget = new FaultEditorWidget(container, SWT.NONE, fc, logbooks, tags);
        if(create){
            // Create a brand new fault entry
            if(logEntries != null && !logEntries.isEmpty()) {
                faultEditorWidget.setLogIds(logEntries);
            }
        }else{
            // Update existing fault report
            if(fault != null) {
                faultEditorWidget.setFault(fault);
            }

            if(logEntries != null && !logEntries.isEmpty()) {
                // combine all logIds
                logEntries.addAll(fault.getLogIds());
                faultEditorWidget.setLogIds(logEntries);
            }

            if (faultLogbook != null) {
                faultEditorWidget.setLogbooks(LogEntryUtil.getLogbookNames(faultLog));
                faultEditorWidget.setTags(LogEntryUtil.getTagNames(faultLog));
            }
        }
        return container;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        // create OK and Cancel buttons by default
        createButton(parent, IDialogConstants.OK_ID, "Submit", true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    @Override
    protected void okPressed() {
        // Create the fault report
        Fault fault = faultEditorWidget.getFault();
        // Validate the Fault
        // Required fields are area, sub system, device, description, occur
        // time, beam loss state
        if (!fault.validate()) {
            MessageDialog.openError(getShell(),
                    "Invalid Fault Report",
                    "All the required fields have not been correctly filled.");
        } else {

            String faultText = FaultAdapter.createFaultText(fault);

            Collection<LogbookBuilder> newLogbooks = faultEditorWidget.getLogbooks().stream().map((logbookName) -> {
                return LogbookBuilder.logbook(logbookName);
            }).collect(Collectors.toList());
            if (!faultEditorWidget.getLogbooks().contains(faultLogbook)) {
                newLogbooks.add(faultLogbook);
            }

            Collection<TagBuilder> newTags = faultEditorWidget.getTags().stream().map((tagName) -> {
                return TagBuilder.tag(tagName);
            }).collect(Collectors.toList());
            if (!faultEditorWidget.getTags().contains(faultTag)) {
                newTags.add(faultTag);
            }

            PropertyBuilder prop = FaultAdapter.createFaultProperty(fault, faultEditorWidget.getLogIds());

            try {

                if (create) {
                    LogEntry faultLog = client.createLogEntry(LogEntryBuilder.withText(faultText).setLevel(faultLevel)
                            .setLogbooks(newLogbooks).addProperty(prop).build());
                    fault.setId(Integer.valueOf(faultLog.getId().toString()));
                    // Since this is a new Fault inform the contactee
                    Executors.newSingleThreadExecutor().execute(() -> {
                        if (Platform.getPreferencesService().getBoolean("org.csstudio.logbook.olog.property.fault",
                                "notify", true, null)) {
                            if (fault.getContact() != null && !fault.getContact().isEmpty()) {
                                EMailSender mailer;
                                StringBuffer faultString = new StringBuffer(faultText);
                                try {
                                    String LogURLFormatt = prefs.getString("org.csstudio.logbook.ui",
                                            "Log.url.format",
                                            "http://localhost:8080/Olog/resources/logs/{logId}", null);
                                    try {
                                         URL url = new URL(LogURLFormatt.replace("{logId}", String.valueOf(fault.getId())));
                                         if(url != null){
                                            faultString.append(System.getProperty("line.separator"));
                                            faultString.append("Fault link:");
                                            faultString.append(System.getProperty("line.separator"));
                                            faultString.append(url.toURI().toString());
                                         }
                                    } catch (MalformedURLException e) {
                                        log.log(Level.SEVERE, "failed to email the fault report:" + e);
                                    }
                                    mailer = new EMailSender(Preferences.getSMTP_Host(), "cs-studio@bnl.gov",
                                            fault.getContact(), "Fault Report");
                                    mailer.addText(faultString.toString());
                                    mailer.close();
                                } catch (Exception e) {
                                    log.log(Level.WARNING, "Failed to send fault message", e);
                                }
                            }
                        }
                    });
                } else {
                    client.updateLogEntry(LogEntryBuilder.logEntry(faultLog).setText(faultText).setLevel(faultLevel)
                            .setLogbooks(newLogbooks).setTags(newTags).addProperty(prop).build());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.okPressed();
        }
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

}
