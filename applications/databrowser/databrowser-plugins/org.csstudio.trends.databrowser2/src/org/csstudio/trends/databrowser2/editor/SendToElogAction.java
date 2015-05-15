/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.logbook.Attachment;
import org.csstudio.logbook.AttachmentBuilder;
import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.LogbookClientManager;
import org.csstudio.swt.rtplot.RTTimePlot;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.ui.util.CustomMediaFactory;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.eclipse.core.commands.Command;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * Action to send image of plot to logbook.
 *
 * @author Davy Dequidt
 */
public class SendToElogAction extends Action {
    final private Shell shell;
    final private RTTimePlot graph;

    public static final String ID = "org.csstudio.trends.databrowser2.sendToElog";

    public static final String LOGBOOK_UI_PLUGIN_ID = "org.csstudio.logbook.ui";
    public static final String OPEN_LOGENTRY_BUILDER_DIALOG_ID = "org.csstudio.logbook.ui.OpenLogEntryBuilderDialog";

    /**
     * Initialize
     *
     * @param part
     *            Parent shell
     * @param graph
     *            Graph to print
     */
    public SendToElogAction(final Shell shell, final RTTimePlot graph) {
        this.shell = shell;
        this.graph = graph;
        setText("Create Log Entry");
        setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(LOGBOOK_UI_PLUGIN_ID,
                        "icons/logentry-add-16.png"));
        setId(ID);
    }

    @Override
    public void run() {
        UIBundlingThread.getInstance().addRunnable(shell.getDisplay(),
                new Runnable() {
                    @Override
                    public void run() {
                        // Display dialog, create entry
                        makeLogEntry();
                    }
                });

    }

    /**
     * Make a logbook entry.
     *
     */
    public void makeLogEntry() {
        try {
            final Attachment image_attachment = createImageAttachment();
            final String text = Messages.LogentryDefaultTitle + "\n"
                    + Messages.LogentryDefaultBody;

            final LogEntryBuilder entry = LogEntryBuilder.withText(text)
                    .attach(AttachmentBuilder.attachment(image_attachment));
            // get the command from plugin.xml
            IWorkbenchWindow window = PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow();

            List<LogEntryBuilder> logList = new ArrayList<LogEntryBuilder>();
            logList.add(entry);
            Event event = new Event();
            event.data = logList;
            // execute the command
            IHandlerService handlerService = (IHandlerService) window
                    .getService(IHandlerService.class);
            handlerService.executeCommand(OPEN_LOGENTRY_BUILDER_DIALOG_ID,
                    event);
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

    /**
     * @return Logbook attachment for the plot's image
     * @throws Exception
     *             on error
     */
    private Attachment createImageAttachment() throws Exception {
        // Dump image into buffer
        ImageLoader loader = new ImageLoader();
        Image image = graph.getImage();
        loader.data = new ImageData[] { image.getImageData() };
        image.dispose();
        image = null;

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        loader.save(buf, SWT.IMAGE_PNG);
        buf.close();
        final byte[] image_bits = buf.toByteArray();
        buf = null;
        loader = null;

        // Attachment provides input stream
        return new Attachment() {
            @Override
            public Boolean getThumbnail() {
                return true;
            }

            @Override
            public InputStream getInputStream() {
                try {
                    return new ByteArrayInputStream(image_bits);
                } catch (Exception ex) {
                    return null;
                }
            }

            @Override
            public Long getFileSize() {
                return (long) image_bits.length;
            }

            @Override
            public String getFileName() {
                return "plot.png";
            }

            @Override
            public String getContentType() {
                return "image/png";
            }
        };
    }
}
