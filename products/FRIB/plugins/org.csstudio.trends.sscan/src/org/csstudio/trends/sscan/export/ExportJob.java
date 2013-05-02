/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.export;

import java.io.PrintStream;
import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.trends.sscan.Activator;
import org.csstudio.trends.sscan.model.Model;
import org.csstudio.trends.sscan.model.ModelItem;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/** Base for Eclipse Job for exporting data from Model to file
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
abstract public class ExportJob extends Job
{
    final protected static int PROGRESS_UPDATE_LINES = 1000;
    final protected String comment;
    final protected Model model;
    final protected Source source;
    final protected int optimize_count;
    final protected String filename;
    final protected ExportErrorHandler error_handler;
    /** Active readers, used to cancel and close them */
    final private CopyOnWriteArrayList<ArchiveReader> archive_readers = new CopyOnWriteArrayList<ArchiveReader>();

    /** Thread that polls a progress monitor and cancels active archive readers
     *  if the user requests the export job to end via the progress monitor
     */
    class CancellationPoll extends Thread
    {
        final private IProgressMonitor monitor;
        volatile boolean exit = false;

        public CancellationPoll(final IProgressMonitor monitor)
        {
            super("DataExportCancellation");
            this.monitor = monitor;
        }

        @Override
        public void run()
        {
            while (! exit)
            {
                if (monitor.isCanceled())
                {
                    for (ArchiveReader reader : archive_readers)
                        reader.cancel();
                }
                try
                {
                    sleep(1000);
                }
                catch (InterruptedException e)
                {
                    // Ignore
                }
            }
        }
    }

    /** @param comment Comment prefix ('#' for most ASCII, '%' for Matlab, ...)
     *  @param model Model with data
     *  @param start Start time
     *  @param end End time
     *  @param source Where to get samples
     *  @param optimize_count Used by optimized source
     *  @param filename Name of file to create
     *  @param error_handler Callback for errors
     */
    public ExportJob(final String comment, final Model model,
        final Source source,
        final int optimize_count,
        final String filename,
        final ExportErrorHandler error_handler)
    {
        super("Data Export");
        this.comment = comment;
        this.model = model;
        this.source = source;
        this.optimize_count = optimize_count;
        this.filename = filename;
        this.error_handler = error_handler;
    }

    /** Job's main routine
     *  {@inheritDoc}
     */
    @Override
    final protected IStatus run(final IProgressMonitor monitor)
    {
        monitor.beginTask("Data Export", IProgressMonitor.UNKNOWN);
        try
        {
            final PrintStream out = new PrintStream(filename);
            printExportInfo(out);
            // Start thread that checks monitor to cancels readers when
            // user tries to abort the export job
            //final CancellationPoll cancel_poll = new CancellationPoll(monitor);
            //cancel_poll.start();
            performExport(monitor, out);
            // ask thread to exit
            //cancel_poll.exit = true;
            //for (ArchiveReader reader : archive_readers)
            //    reader.close();
            out.close();
            // Wait for poller to quit
            //cancel_poll.join();
        }
        catch (final Exception ex)
        {
            //error_handler.handleExportError(ex);
        	ex.printStackTrace();
        }
        monitor.done();
        return Status.OK_STATUS;
    }

    /** Print file header, gets invoked before <code>performExport</code> */
    protected void printExportInfo(final PrintStream out)
    {
        out.println(comment + "Created by CSS Data Browser Version " + Activator.getDefault().getVersion());
        out.println(comment);
        out.println(comment + "Source     : " + source.toString());
        if (source == Source.OPTIMIZED_ARCHIVE)
            out.println(comment + "Desired Value Count: " + optimize_count);
    }

    /** Perform the data export
     *  @param out PrintStream for output
     *  @throws Exception on error
     */
    abstract protected void performExport(final IProgressMonitor monitor,
                               final PrintStream out) throws Exception;

    /** Print info about item
     *  @param out PrintStream for output
     *  @param item ModelItem
     */
    protected void printItemInfo(final PrintStream out, final ModelItem item)
    {
        out.println(comment + "Channel: " + item.getName());
        if (! item.getName().equals(item.getDisplayName()))
            out.println(comment + "Name   : " + item.getDisplayName());
        if (item instanceof ModelItem)
        {
            final ModelItem pv = (ModelItem) item;
            out.println(comment + "Archives:");
        }
        out.println(comment);
    }
}
