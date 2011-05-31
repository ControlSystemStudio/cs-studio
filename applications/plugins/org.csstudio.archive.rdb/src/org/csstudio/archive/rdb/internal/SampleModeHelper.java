/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb.internal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.csstudio.archive.rdb.SampleMode;
import org.csstudio.platform.utility.rdb.RDBUtil;

/** Helper for RDB sample mode info 
 *  @author Kay Kasemir
 */
public class SampleModeHelper
{
    /** Locate Sample Modes
     *  @throws Exception on error
     */
    public static SampleMode [] getSampleModes(final RDBUtil rdb, final SQL sql)
        throws Exception
    {
        final PreparedStatement statement =
            rdb.getConnection().prepareStatement(sql.sample_mode_sel);
        try
        {
            // id, name, descr
            final ResultSet result = statement.executeQuery();
            final ArrayList<SampleMode> modes = new ArrayList<SampleMode>();
            while (result.next())
                modes.add(new SampleMode(result.getInt(1), result.getString(2),
                                         result.getString(3)));
            final SampleMode arr[] = new SampleMode[modes.size()];
            return modes.toArray(arr);
        }
        finally
        {
            statement.close();
        }
    }
}
