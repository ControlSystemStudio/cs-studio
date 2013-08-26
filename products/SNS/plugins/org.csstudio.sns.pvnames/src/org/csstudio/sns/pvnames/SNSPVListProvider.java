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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.autocomplete.AutoCompleteHelper;
import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.IAutoCompleteProvider;
import org.csstudio.autocomplete.Proposal;
import org.csstudio.platform.utility.rdb.RDBCache;

/** PV Name lookup for SNS 'signal' database
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SNSPVListProvider implements IAutoCompleteProvider
{
    /** Cached RDB connection */
    private RDBCache cache = null;
    
    /** Currently executed statement.
     *  SYNC on this for access
     */
    private PreparedStatement current_statement = null;
    
    private synchronized void setCurrentStatement(final PreparedStatement statement)
    {
        current_statement = statement;
    }

    /** {@inheritDoc} */
    @Override
	public AutoCompleteResult listResult(final String type, final String name, final int limit)
    {
        final Logger logger = Logger.getLogger(getClass().getName());
        logger.log(Level.FINE, "Lookup type {0}, pattern {1}, limit {2}",
                new Object[] { type, name, limit });
        
        // Create RDB pattern from *, ? wildcards
    	final String like = AutoCompleteHelper.convertToSQL(name);
    
        final AutoCompleteResult pvs = new AutoCompleteResult();
        pvs.setProvider("SNS");
        try
        {
            if (cache == null)
                cache = new RDBCache("SNSPVListProvider",
                        Preferences.getURL(), Preferences.getUser(), Preferences.getPassword(),
                        10, TimeUnit.SECONDS);
            lookup(pvs, like, limit);
        }
        catch (Throwable ex)
        {
            // Suppress error resulting from call to cancel()
            if (! ex.getMessage().startsWith("ORA-01013"))
                logger.log(Level.WARNING, "PV Name lookup failed", ex);
        }
        if (logger.isLoggable(Level.FINER))
            logger.log(Level.FINER, "PVs: {0}", pvs.getProposalsAsString());
        return pvs;
    }

    /** Perform lookup
     * 
     *  @param pvs Where to store result
     *  @param like SQL 'LIKE' pattern
     *  @param limit Maximum number of PVs to return
     *  @throws Exception on error
     */
    private void lookup(final AutoCompleteResult pvs, final String like, int limit) throws Exception
    {
        // Count PVs
        try
        (
            final PreparedStatement statement =
                cache.getConnection().prepareStatement(
                    "SELECT COUNT(*) FROM EPICS.SGNL_REC WHERE SGNL_ID LIKE ?");
        )
        {
            statement.setString(1, like);
            setCurrentStatement(statement);
            final ResultSet result = statement.executeQuery();
            if (! result.next())
                throw new Exception("Cannot determine channel count");
            pvs.setCount(result.getInt(1));
            result.close();
            setCurrentStatement(null);
        }
        
        // List channels
        try
        (
            final PreparedStatement statement =
                cache.getConnection().prepareStatement(
                    "SELECT SGNL_ID FROM EPICS.SGNL_REC WHERE SGNL_ID LIKE ? ORDER BY SGNL_ID");
        )
        {
            statement.setString(1, like);
            setCurrentStatement(statement);
            final ResultSet result = statement.executeQuery();
            while (result.next())
            {
                pvs.addProposal(new Proposal(result.getString(1), false));
                -- limit;
                if (limit <= 0)
                    break;
            }
            result.close();
            setCurrentStatement(statement);
        }
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void cancel()
    {
        if (current_statement == null)
        {
            Logger.getLogger(getClass().getName()).fine("Cancelled while idle");
            return;
        }
        try
        {
            Logger.getLogger(getClass().getName()).fine("Cancelling ongoing lookup");
            current_statement.cancel();
        }
        catch (Throwable ex)
        {
            // Ignore
        }
    }
}
