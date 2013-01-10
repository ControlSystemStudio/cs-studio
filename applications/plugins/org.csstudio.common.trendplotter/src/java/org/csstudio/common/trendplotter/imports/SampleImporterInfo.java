/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.imports;

import java.io.InputStream;
import java.util.List;

import org.csstudio.data.values.IValue;

/** {@link SampleImporter} with info about its type and description
 *  @author Kay Kasemir
 */
public class SampleImporterInfo
{
    final private String type, description;
    final private SampleImporter importer;

    public SampleImporterInfo(final String type, final String description,
            SampleImporter importer)
    {
        this.type = type;
        this.description = description;
        this.importer = importer;
    }

    /** @return Type identifier, for example "csv" */
    public String getType()
    {
        return type;
    }

    /** @return Human-readable description, for example "CSV Data File" */
    public String getDescription()
    {
        return description;
    }

    /** Perform value import
     *  @param input Input stream
     *  @return Values
     *  @throws Exception on error
     */
    public List<IValue> importValues(final InputStream input) throws Exception
    {
        return importer.importValues(input);
    }
}
