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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.autocomplete.AutoCompleteHelper;
import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.IAutoCompleteProvider;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.ContentType;
import org.csstudio.autocomplete.proposals.Proposal;
import org.csstudio.autocomplete.proposals.ProposalStyle;
import org.csstudio.platform.utility.rdb.RDBCache;

/** PV Name lookup for SNS 'signal' database
 * 
 *  <p>AutoCompleteService will re-use one instance of this class
 *  for all lookups, calling <code>listResult</code> whenever
 *  the user types a new character, using a new thread for each lookup.
 *  Before starting a new lookup, however, <code>cancel()</code> is invoked.
 *  This means there are never multiple concurrent lookups started on purpose,
 *  but a previously started lookup may still continue in its thread
 *  in case <code>cancel()</code> has no immediate effect.
 *  
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
	public boolean accept(final ContentType type)
	{
		if (type == ContentType.PVName)
			return true;
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public AutoCompleteResult listResult(final ContentDescriptor desc, final int limit)
    {
		final String content = desc.getValue();
		final String type = desc.getAutoCompleteType().value();
		final Logger logger = Logger.getLogger(getClass().getName());
		logger.log(Level.FINE, "Lookup type {0}, pattern {1}, limit {2}",
		        new Object[] { type, content, limit });
		
		
		// Support partial matches:
		// Lookup of "DTL" will actually look for "DTL*".
		// Could also expand that to "*DTL*", but since SNS RDB is slow enough,
		// require user to explicitly enter "*.." for a fully non-anchored search.
		String search_pattern = content;
		if (! search_pattern.endsWith("*"))
		    search_pattern += "*";

        // Create RDB pattern from *, ? wildcards
    	final String like = AutoCompleteHelper.convertToSQL(search_pattern);
    
        final AutoCompleteResult pvs = new AutoCompleteResult();
        try
        {
            if (cache == null)
                cache = new RDBCache("SNSPVListProvider",
                        Preferences.getURL(), Preferences.getUser(), Preferences.getPassword(),
                        2, TimeUnit.MINUTES);
            lookup(pvs, like, limit);
        }
        catch (Throwable ex)
        {
            // Suppress error resulting from call to cancel()
            final String message = ex.getMessage();
            if (message != null  &&
                (message.startsWith("ORA-01013")  ||  message.startsWith("ORA-01001")))
                logger.log(Level.FINE, "Lookup for {0} cancelled", content);
            else
                logger.log(Level.WARNING, "Lookup for " + content + " failed", ex);
            return pvs;
        }
        
        // Mark, i.e. highlight the original search pattern within each result
        final Pattern namePattern = AutoCompleteHelper.convertToPattern(content);
        for (Proposal p : pvs.getProposals())
        {
            final Matcher m = namePattern.matcher(p.getValue());
            if (m.find())
                p.addStyle(ProposalStyle.getDefault(m.start(), m.end()-1));
        }
        
        if (logger.isLoggable(Level.FINER))
            logger.log(Level.FINER, "PVs for {0} ({1}): {2}", new Object[] { content, pvs.getCount(), pvs.getProposalsAsString() });
        return pvs;
    }

    /** Perform lookup
     * 
     *  @param pvs Where to store result
     *  @param like SQL 'LIKE' pattern
     *  @param limit Maximum number of PVs to return
     *  @throws Exception on error
     */
    private void lookup(final AutoCompleteResult pvs, final String like, final int limit) throws Exception
    {
        // Initially, "SELECT COUNT(*) .." obtained count, then fetched actual names in second query.
        // jProfiler showed that the count took longer (3x !!) than fetching the names.
        // Even considering that a second query for similar information is likely faster because of caching,
        // having only one query, counting all but only keeping names up to 'limit', is overall faster.
        int count = 0;
        try
        (
            final PreparedStatement statement =
                cache.getConnection().prepareStatement(
                    "SELECT SGNL_ID FROM EPICS.SGNL_REC WHERE SGNL_ID LIKE ? ORDER BY SGNL_ID");
        )
        {
            setCurrentStatement(statement);
            statement.setString(1, like);
            final ResultSet result = statement.executeQuery();
            while (result.next())
            {
                if (++count <= limit)
                    pvs.addProposal(new Proposal(result.getString(1), false));
            }
            result.close();
        }
        finally
        {
            cache.releaseConnection();
            setCurrentStatement(null);
            pvs.setCount(count);
        }
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void cancel()
    {
        if (current_statement == null)
            return;
        try
        {
            Logger.getLogger(getClass().getName()).fine("Cancelling ongoing lookup");
            current_statement.cancel();
            current_statement = null;
        }
        catch (Throwable ex)
        {
            // Ignore
        }
    }

}
