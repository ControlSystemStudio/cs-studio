/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.channelarchiver;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

/** Handles the "archiver.names" request and its results. */
@SuppressWarnings("nls")
public class NamesRequest
{
	final private int key;
	final private String pattern;
	private String names[];
	
	/** Create a name lookup.
	 *   @param pattern Regular expression pattern for the name.
	 */
	public NamesRequest(int key, String pattern)
	{
		this.key = key;
		this.pattern = pattern;
	}

	/** Read info from data server */
	@SuppressWarnings("unchecked")
    public void read(XmlRpcClient xmlrpc) throws Exception
	{
		Vector<?> result;
		try
		{
			final Vector<Object> params = new Vector<Object>();
			params.add(Integer.valueOf(key));
			params.add(pattern);
			result = (Vector<?>) xmlrpc.execute("archiver.names", params);
		}
		catch (XmlRpcException e)
		{
			throw new Exception("archiver.names call failed", e);
		}

		//	{ string name,
		//    int32 start_sec,  int32 start_nano,
		//	  int32 end_sec,    int32 end_nano
		//   }[] = archiver.names(int32 key,  string pattern)
		names = new String[result.size()];
		for (int i=0; i<result.size(); ++i)
		{
			final Hashtable<String, Object> entry =
			    (Hashtable<String, Object>) result.get(i);
//            ITimestamp start = TimestampFactory.createTimestamp(
//                            (Integer) entry.get("start_sec"),
//                            (Integer) entry.get("start_nano"));
//            ITimestamp end = TimestampFactory.createTimestamp(
//                            (Integer) entry.get("end_sec"),
//                            (Integer) entry.get("end_nano"));
			names[i] =(String) entry.get("name");
		}
	}

	/** @return Returns the name infos that were found. */
	public final String[] getNameInfos()
	{
		return names;
	}
	
	/** @return Returns a more or less useful string. */
    @Override public String toString()
	{
		StringBuffer result = new StringBuffer();
		result.append(String.format("Names with key %d matching '%s':\n",
				key, pattern));
		for (int i=0; i<names.length; ++i)
		{
			if (i>0)
				result.append(", ");
			result.append('\'');
			result.append(names[i]);
			result.append('\'');
		}
		return result.toString();
	}
}
