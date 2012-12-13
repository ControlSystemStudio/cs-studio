/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.imports;

import java.io.InputStream;
import java.util.List;

import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.UnknownChannelException;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.trends.databrowser2.Activator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

/** Archive reader that imports data from a file
 *
 *  <p>Performs the import once, reading the complete file.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ImportArchiveReader implements ArchiveReader
{
    final private String url;
    final private String path;
    final private SampleImporterInfo importer;
    private List<IValue> values = null;

    public ImportArchiveReader(final String url, final String path, final SampleImporterInfo importer)
    {
        this.url = url;
        this.path = path;
        this.importer = importer;
    }

    @Override
    public String getServerName()
    {
        return "Imported Data";
    }

    @Override
    public String getURL()
    {
        return url;
    }

    @Override
    public String getDescription()
    {
        return importer.getDescription();
    }

    @Override
    public int getVersion()
    {
        return 1;
    }

    @Override
    public ArchiveInfo[] getArchiveInfos()
    {
        return new ArchiveInfo[0];
    }

    @Override
    public String[] getNamesByPattern(final int key, final String glob_pattern)
            throws Exception
    {
        return new String[0];
    }

    @Override
    public String[] getNamesByRegExp(final int key, final String reg_exp) throws Exception
    {
        return getNamesByPattern(0, null);
    }

    @Override
    public ValueIterator getRawValues(final int key, final String name, final ITimestamp start,
            final ITimestamp end) throws UnknownChannelException, Exception
    {
        if (values == null)
        {
  	  		// TODO RAP and RCP
			if (Activator.isRAP()) {
   	             throw new RuntimeException("Not yet implemented for web version.");
			}
            // Locate file
            final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path));
            final InputStream input = file.getContents();
            // Import data
            values = importer.importValues(input );
        }
        return new ArrayValueIterator(values);
    }

    @Override
    public ValueIterator getOptimizedValues(int key, String name,
            ITimestamp start, ITimestamp end, int count)
            throws UnknownChannelException, Exception
    {
        // No optimization. Fall back to raw data.
        return getRawValues(key, name, start, end);
    }

    @Override
    public void cancel()
    {
        // NOP
    }

    @Override
    public void close()
    {
        // NOP
    }
}
