/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.rdb.jparc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.apputil.text.RegExHelper;
import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.UnknownChannelException;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.platform.utility.rdb.RDBUtil;

/** {@link ArchiveReader} for JPARC archive
 * 
 *  @author Kay Kasemir
 */
public class JParcArchiveReader implements ArchiveReader
{
	// TODO The actual archive will probably only have one 'rdb'
	// entry, which is why the 'key' is ignored in the following.
	// For the demo, we pretend there are 2 sub-archives
	final private static ArchiveInfo[] infos =
	{
		new ArchiveInfo("rdb", "JPARC Main Archive", 1),
		new ArchiveInfo("aux", "Auxiliary Archive", 2),
	};

	final private String url;
	private RDBUtil rdb;
	
	/** Initialize
	 *  @param url Archive URL, will always start with the {@link JParcArchiveReaderFactory} PREFIX.
	 *  @param user RDB user
	 *  @param password .. password
	 *  @throws Exception on error
	 */
	public JParcArchiveReader(final String url, final String user, final String password) throws Exception
    {
		this.url = url;
		// TODO Connect to RDB
		final String real_url = url.replace(JParcArchiveReaderFactory.PREFIX, "posgres");
		// rdb = RDBUtil.connect(real_url, user, password, true);
    }

	/** {@inheritDoc} */
	@Override
	public String getServerName()
	{
		return "JPARC Archive";
	}

	/** {@inheritDoc} */
	@Override
	public String getURL()
	{
		return url;
	}

	/** {@inheritDoc} */
	@Override
	public String getDescription()
	{
		return "JPARC PostgreSQL Archive";
	}

	/** {@inheritDoc} */
	@Override
	public int getVersion()
	{
		return 1;
	}

	/** {@inheritDoc} */
	@Override
	public ArchiveInfo[] getArchiveInfos()
	{
		return infos;
	}

	// TODO Remove dummy PV name list, actually query the RDB for names
	final private static String[] channels = new String[] { "Fred", "Jane" }; 
	
	/** {@inheritDoc} */
	@Override
	public String[] getNamesByPattern(final int key, final String glob_pattern)
	        throws Exception
	{
		// TODO A relational database usually has a 'faster' way
		// of searching by glob, so this should be implemented by
		// going to the RDB and not by using the reg-ex version
//		final List<String> names = new ArrayList<String>();
//		final PreparedStatement statement = 
//			rdb.getConnection().prepareStatement("SELECT name FROM channels WHERE name LIKE ?");
//		try
//		{
//			statement.setString(1, glob_pattern);
//			final ResultSet result = statement.executeQuery();
//			while (result.next())
//			{
//				names.add(result.getString(1));
//			}
//			result.close();
//		}
//		finally
//		{
//			statement.close();
//		}
//		return names.toArray(new String[names.size()]);
		return getNamesByRegExp(key, RegExHelper.fullRegexFromGlob(glob_pattern));
	}

	/** {@inheritDoc} */
	@Override
	public String[] getNamesByRegExp(final int key, final String reg_exp) throws Exception
	{
		final List<String> result = new ArrayList<String>();
		// TODO Perform actual RDB query
		for (String channel : channels)
			if (channel.matches(reg_exp))
				result.add(channel);
		
		return result.toArray(new String[result.size()]);
	}

	/** {@inheritDoc} */
	@Override
	public ValueIterator getRawValues(final int key, String name, final ITimestamp start,
			final ITimestamp end) throws UnknownChannelException, Exception
	{
		return new RDBValueIterator(rdb, name, start, end);
	}

	/** {@inheritDoc} */
	@Override
	public ValueIterator getOptimizedValues(int key, String name,
	        ITimestamp start, ITimestamp end, int count)
	        throws UnknownChannelException, Exception
	{
		// TODO Get optimized data, i.e. about 'count' min/max/average
		// samples between 'start' and 'end'
		return getRawValues(key, name, start, end);
	}

	/** {@inheritDoc} */
	@Override
	public void cancel()
	{
		// TODO Auto-generated method stub
	}

	/** {@inheritDoc} */
	@Override
	public void close()
	{
		if (rdb != null)
		{
			rdb.close();
			rdb = null;
		}
	}
}
