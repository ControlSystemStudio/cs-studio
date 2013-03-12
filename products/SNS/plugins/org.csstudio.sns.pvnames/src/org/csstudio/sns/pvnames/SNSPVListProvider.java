/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.sns.pvnames;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.pvnames.IPVListProvider;
import org.csstudio.pvnames.PVListResult;

/** PV Name lookup for SNS 'signal' database
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SNSPVListProvider implements IPVListProvider
{
    /** Perform lookup
     * 
     *  @param pvs Where to store result
     *  @param rdb RDB
     *  @param like SQL 'LIKE' pattern
     *  @param limit Maximum number of PVs to return
     *  @throws Exception on error
     */
    final private void lookup(final PVListResult pvs, final RDBUtil rdb, final String like, int limit) throws Exception
    {
        // Count PVs
        try
        (
            final PreparedStatement statement =
                rdb.getConnection().prepareStatement(
                    "SELECT COUNT(*) FROM EPICS.SGNL_REC WHERE SGNL_ID LIKE ?");
        )
        {
            statement.setString(1, like);
            final ResultSet result = statement.executeQuery();
            if (! result.next())
                throw new Exception("Cannot determine channel count");
            pvs.setCount(result.getInt(1));
            result.close();
        }
        
        // List channels
        try
        (
            final PreparedStatement statement =
                rdb.getConnection().prepareStatement(
                    "SELECT SGNL_ID FROM EPICS.SGNL_REC WHERE SGNL_ID LIKE ? ORDER BY SGNL_ID");
        )
        {
            statement.setString(1, like);
            final ResultSet result = statement.executeQuery();
            while (result.next())
            {
                pvs.add(result.getString(1));
                -- limit;
                if (limit <= 0)
                    break;
            }
            result.close();
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public PVListResult listPVs(final Pattern pattern, final int limit)
    {
        final String like = pattern.pattern().replace(".*", "%").replace(".+", "%").replace(".", "_");

        final PVListResult pvs = new PVListResult();
        try
        {
            final RDBUtil rdb = RDBUtil.connect(Preferences.getURL(), Preferences.getUser(), Preferences.getPassword(), false);
            try
            {
                lookup(pvs, rdb, like, limit);
            }
            finally
            {
                rdb.close();
            }
        }
        catch (Throwable ex)
        {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "PV Name lookup failed", ex);
        }
        return pvs;
    }
}
