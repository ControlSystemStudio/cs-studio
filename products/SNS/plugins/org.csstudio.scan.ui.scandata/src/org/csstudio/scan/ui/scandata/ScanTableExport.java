/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scandata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanDataIterator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;

/** Job for exporting {@link ScanData} to text file
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanTableExport extends Job
{
	final private ScanData data;
	final private IFile file;

	public ScanTableExport(final ScanData data, final IFile file)
    {
	    super(Messages.ExportDataToFile);
	    this.data = data;
	    this.file = file;
    }

	/** File extension used for exported data files */
	final public static String FILE_EXTENSION = Messages.DataFileExtension;

    @Override
    protected IStatus run(final IProgressMonitor monitor)
    {
		monitor.beginTask(NLS.bind(Messages.ExportFileFmt, file.getName()), 2);

		// Write text to string buffer
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		final PrintStream out = new PrintStream(buf);
		ScanDataIterator sheet = new ScanDataIterator(data);
		if ("csv".equals(file.getFileExtension()))
			sheet.printCSV(out);
        else
        	sheet.printTable(out);
		sheet = null;
		out.close();
		monitor.worked(1);

		// Write buffer to file
		final InputStream stream = new ByteArrayInputStream(buf.toByteArray());
		buf = null;
		try
        {
			final boolean force = true;
			if (file.exists())
            {
	            final boolean keepHistory = false;
	            file.setContents(stream, force, keepHistory, monitor);
            }
            else
				file.create(stream, force, monitor);
        }
        catch (CoreException ex)
        {
        	return new Status(IStatus.ERROR, Activator.ID, "Error creating data file", ex);
        }
		monitor.worked(2);

		monitor.done();

	    return Status.OK_STATUS;
    }
}
