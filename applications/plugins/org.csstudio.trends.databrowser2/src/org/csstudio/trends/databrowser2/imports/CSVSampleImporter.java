/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.imports;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.TimestampHelper;
import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.Display;
import org.epics.pvmanager.data.VType;
import org.epics.pvmanager.data.ValueFactory;
import org.epics.util.time.Timestamp;

/** {@link SampleImporter} for Command (space, tab) separated value file of time, value
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CSVSampleImporter implements SampleImporter
{
    final private Logger logger = Logger.getLogger(getClass().getName());
    final private Display meta_data = ValueFactory.displayNone();

    /** {@inheritDoc} */
    @Override
    public List<VType> importValues(final InputStream input) throws Exception
    {
        // To be reentrant, need per-call parsers
        final DateFormat date_parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        final Pattern pattern = Pattern.compile(
                //    YYYY-MM-DD HH:MM:SS.SSS   value  ignore
                // or
                //    YYYY/MM/DD HH:MM:SS.SSSSSSSSS   value  ignore
                "\\s*([0-9][0-9][0-9][0-9][-/][0-9][0-9][-/][0-9][0-9] [0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\.[0-9]*)[ \\t,]+([-+0-9.eE]+)\\s*.*");

        final List<VType> values = new ArrayList<VType>();

        final BufferedReader reader =
                new BufferedReader(new InputStreamReader(input));
        String line;
        while ((line = reader.readLine()) != null)
        {
            line = line.trim();
            // Skip empty lines, comments
            if (line.length() <= 0  ||  line.startsWith("#"))
                continue;
            // Locate time and value
            final Matcher matcher = pattern.matcher(line);
            if (! matcher.matches())
            {
                logger.log(Level.INFO, "Ignored input: {0}", line);
                continue;
            }
            // Parse
            // Date may use '-' or '/' as separator. Force '-'
            String date_text = matcher.group(1).replace('/', '-');
            // Can only parse up to millisecs, so limit length
            if (date_text.length() > 23)
                date_text = date_text.substring(0, 23);
            final Date date = date_parser.parse(date_text);
            final double number = Double.parseDouble(matcher.group(2));
            // Turn into IValue
            final Timestamp time = TimestampHelper.fromMillisecs(date.getTime());
            final VType value = new ArchiveVNumber(time, AlarmSeverity.NONE, "",
                    meta_data, number);
            values.add(value);
        }
        reader.close();

        return values;
    }
}
