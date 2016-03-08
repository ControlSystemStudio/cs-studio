/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.saverestore.ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.csstudio.logbook.Attachment;
import org.csstudio.logbook.AttachmentBuilder;
import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.LogbookClientManager;
import org.csstudio.saverestore.FileUtilities;
import org.csstudio.saverestore.Utilities;
import org.csstudio.saverestore.data.VSnapshot;
import org.csstudio.ui.fx.util.FXMessageDialog;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.commands.Command;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * <code>SendToELogAction</code> implements an action that creates the default content based on the selected snapshot
 * and forwards that content to the logbook entry dialog.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SendToELogAction extends Action {
    private static final String ID = "org.csstudio.saverestore.ui.sendtoelog";
    private static final String LOGBOOK_UI_PLUGIN_ID = "org.csstudio.logbook.ui";
    private static final String OPEN_LOGENTRY_BUILDER_DIALOG_ID = "org.csstudio.logbook.ui.OpenLogEntryBuilderDialog";

    private Table table;
    private Shell shell;

    /**
     * Construct a new action
     *
     * @param shell the parent shell (used for dialog display only)
     * @param table the table which triggered the log action
     */
    public SendToELogAction(Shell shell, Table table) {
        this.table = table;
        this.shell = shell;
        setText("Create Log Entry");
        setImageDescriptor(CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(LOGBOOK_UI_PLUGIN_ID,
            "icons/logentry-add-16.png"));
        setId(ID);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        VSnapshot snapshot = table.getSelectedSnapshot();
        if (snapshot == null) {
            return;
        }
        try {
            Attachment fileAttachment = createFileAttachment(snapshot);
            final StringBuilder sb = new StringBuilder(1000);
            sb.append("Save, Set & Restore\n");
            sb.append(snapshot.getSaveSet().getFullyQualifiedName());
            sb.append("\nTime: ").append(snapshot.getTimestamp() != null
                ? Utilities.timestampToBigEndianString(snapshot.getTimestamp().toDate(), true) : "Unknown");
            snapshot.getSnapshot()
                .ifPresent(s -> sb.append("\nComment: ").append(s.getComment() == null ? "Not Saved" : s.getComment()));
            LogEntryBuilder entry = LogEntryBuilder.withText(sb.toString())
                .attach(AttachmentBuilder.attachment(fileAttachment));
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            Event event = new Event();
            event.data = Arrays.asList(entry);
            IHandlerService handlerService = (IHandlerService) window.getService(IHandlerService.class);
            handlerService.executeCommand(OPEN_LOGENTRY_BUILDER_DIALOG_ID, event);
        } catch (Exception e) {
            FXMessageDialog.openError(shell, "Logbook Error", "Failed to make logbook entry: \n" + e.getMessage());
        }
    }

    private Attachment createFileAttachment(VSnapshot snapshot) throws Exception {
        return new Attachment() {
            @Override
            public Boolean getThumbnail() {
                return false;
            }

            @Override
            public InputStream getInputStream() {
                String data = FileUtilities.generateSnapshotFileContent(snapshot);
                try {
                    return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8.name()));
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
            }

            @Override
            public Long getFileSize() {
                return 0L;
            }

            @Override
            public String getFileName() {
                return "snapshot.snp";
            }

            @Override
            public String getContentType() {
                return "text/plain";
            }
        };
    }

    /**
     * Checks if the logbook functionality is present in the application.
     * @return true if logbook is available or false otherwise
     */
    public static boolean isElogAvailable() {
        try {
            if (LogbookClientManager.getLogbookClientFactory() == null) {
                return false;
            }
            ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getService(ICommandService.class);
            Command command = commandService.getCommand(OPEN_LOGENTRY_BUILDER_DIALOG_ID);
            return command != null;
        } catch (Exception e) {
            return false;
        }
    }

}
