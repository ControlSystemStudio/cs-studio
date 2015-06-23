/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.logbook.AttachmentBuilder;
import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.LogbookClientManager;
import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.runmode.IOPIRuntime;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.eclipse.core.commands.Command;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * Action to send image of plot to logbook.
 *
 * @author Kay Kasemir, Xihui Chen
 */
public class SendToElogAction extends Action {
    final private IOPIRuntime opiRuntime;

    public static final String ID = "org.csstudio.opibuilder.actions.sendToElog";

    public static final String OPEN_LOGENTRY_BUILDER_DIALOG_ID = "org.csstudio.logbook.ui.OpenLogEntryBuilderDialog";

    /**
     * Initialize
     *
     * @param part
     *            Parent shell
     * @param graph
     *            Graph to print
     */
    public SendToElogAction(final IOPIRuntime part) {
        this.opiRuntime = part;
        setText("Create Log Entry");
        setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
                        "icons/logentry-add-16.png"));
        setId(ID);
    }

    @Override
    public void run() {
        UIBundlingThread.getInstance().addRunnable(
                opiRuntime.getSite().getShell().getDisplay(), new Runnable() {

                    @Override
                    public void run() {
                        // Get name for snapshot file
                        final String filename;
                        try {
                            filename = ResourceUtil
                                    .getScreenshotFile((GraphicalViewer) opiRuntime
                                            .getAdapter(GraphicalViewer.class));
                        } catch (Exception ex) {
                            MessageDialog.openError(opiRuntime.getSite()
                                    .getShell(), "error", ex.getMessage());
                            return;
                        }

                        // Display dialog, create entry

                        final Shell shell = opiRuntime.getSite().getShell();

                        makeLogEntry("See attached opi screenshot.", filename,
                                shell);

                    }

                });

    }

    /**
     * Make a logbook entry.
     *
     * @param text text of the log.
     * @param filename
     *            the local file system file path.
     * @param shell
     *            the parent shell.
     */
    public static void makeLogEntry(final String text, final String filename,
            final Shell shell) {
        try {
            LogEntryBuilder log = LogEntryBuilder.withText(text);
            if (filename != null) {
                log.attach(AttachmentBuilder.attachment(filename).inputStream(
                        new FileInputStream(filename)));
            }

            //get the command from plugin.xml
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

            List<LogEntryBuilder> logList = new ArrayList<LogEntryBuilder>();
            logList.add(log);
            Event event = new Event();
            event.data = logList;
            // execute the command
            IHandlerService handlerService = (IHandlerService) window
                    .getService(IHandlerService.class);
            handlerService.executeCommand(OPEN_LOGENTRY_BUILDER_DIALOG_ID, event);
        } catch (Exception e) {
            MessageDialog.openError(null, "Logbook Error",
                    "Failed to make logbook entry: \n" + e.getMessage());
        }
    }

    public static boolean isElogAvailable() {
        try {
            if (LogbookClientManager.getLogbookClientFactory() == null)
                return false;

            // Check if logbook dialog is available
            ICommandService commandService = (ICommandService) PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow()
                    .getService(ICommandService.class);
            Command command = commandService
                    .getCommand(OPEN_LOGENTRY_BUILDER_DIALOG_ID);
            if (command == null) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
