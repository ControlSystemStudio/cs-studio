/*******************************************************************************
 * Copyright (c) 2015 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.swt.rtplot.Activator;
import org.csstudio.swt.rtplot.Messages;
import org.csstudio.swt.rtplot.RTPlot;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.UrlLauncher;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Shell;

/** Action that saves a snapshot of the current plot
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SnapshotAction<XTYPE extends Comparable<XTYPE>> extends Action
{
    final private RTPlot<XTYPE> plot;

    public SnapshotAction(final RTPlot<XTYPE> plot)
    {
        super(Messages.Snapshot, Activator.getIcon("camera"));
        this.plot = plot;
    }

    @Override
    public void run()
    {
        final Shell shell = plot.getShell();

        // Use background thread because of potentially lengthy file I/O
        shell.getDisplay().syncExec(() ->
        {
            try
            {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                final ImageLoader loader = new ImageLoader();
                final Image image = plot.getImage();
                loader.data = new ImageData[] { image.getImageData() };
                image.dispose();
                loader.save(stream, SWT.IMAGE_PNG);
                sendDownload(stream.toByteArray(), "snapshot.png");
            }
            catch (Exception ex)
            {
                    MessageDialog.openError(shell, Messages.Snapshot,
                            NLS.bind("Cannot save snapshot.\n\nDetail:\n{0}",ex.getMessage()));
            }

        });
    }

    public static void sendDownload(byte[] data, String filename) {
        DownloadService service = new DownloadService(data, filename);
        service.register();

        UrlLauncher launcher = RWT.getClient().getService(UrlLauncher.class);
        launcher.openURL(service.getURL());
    }

    private static final class DownloadService implements ServiceHandler {

        private final byte[] data;
        private final String filename;
        private String id;

        public DownloadService(byte[] data, String filename) {
            this.data = data;
            this.filename = filename;
            this.id = calculateId();
        }

        public String getURL() {
            return RWT.getServiceManager().getServiceHandlerUrl(getId());
        }

        private String getId() {
            return id;
        }

        private String calculateId() {
            return String.valueOf(System.currentTimeMillis()) + data.length;
        }

        public boolean register() {
            try {
                RWT.getServiceManager().registerServiceHandler(getId(), this);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        private boolean unregister() {
            try {
                RWT.getServiceManager().unregisterServiceHandler(getId());
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public void service(HttpServletRequest request, HttpServletResponse response)
                throws IOException, ServletException {
            try {
                response.setContentType("application/octet-stream");
                response.setContentLength(data.length);
                response.setHeader("Content-Disposition", "attachment; filename=\"" + filename
                        + "\"");
                response.getOutputStream().write(data);
            } catch (Exception e) {
                e.printStackTrace(new PrintStream(response.getOutputStream()));
            } finally {
                unregister();
            }
        }
    }
}
