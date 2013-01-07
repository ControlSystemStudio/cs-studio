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
import org.epics.vtype.AlarmSeverity;

/** Handles the "archiver.info" request and its results.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
final class ServerInfoRequest
{
    /** String used for an OK status and severity
     *  (more generic than the EPICS 'NO_ALARM')
     */
    final static String NO_ALARM = "OK";
    private String description;
	private int version;
	private String how_strings[];
	private String status_strings[];
	private Hashtable<Integer, SeverityImpl> severities;

	/** Read info from data server */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void read(final XmlRpcClient xmlrpc) throws Exception
	{
		Hashtable<String, Object> result;
		try
		{
			final Vector<Object> params = new Vector<Object>();
			result = (Hashtable<String, Object>)
			    xmlrpc.execute("archiver.info", params);
		}
		catch (XmlRpcException e)
		{
			throw new Exception("archiver.info call failed", e);
		}

		//	{ int32             ver,
		//	  string            desc,
		//	  string            how[],
		//	  string            stat[],
		//	  { int32 num,
		//	    string sevr,
		//	    bool has_value,
		//	    bool txt_stat
		//	  }                 sevr[]
		//	} = archiver.info()
		version = (Integer) result.get("ver");
		description = (String) result.get("desc");
		// Get 'how'. Silly code to copy that into a type-safe vector.
		Vector tmp =  (Vector) result.get("how");
		how_strings = new String[tmp.size()];
		for (int i=0; i<tmp.size(); ++i)
			how_strings[i] = (String)tmp.get(i);
		// Same silly code for the status strings. Better way?
		tmp = (Vector) result.get("stat");
		status_strings = new String[tmp.size()];
		for (int i=0; i<tmp.size(); ++i)
        {
            status_strings[i] = (String)tmp.get(i);
            // Patch "NO ALARM" into "OK"
            if (status_strings[i].equals("NO_ALARM"))
                status_strings[i] = NO_ALARM;
        }
        // Same silly code for the severity strings.
		final Vector sevr_info = (Vector) result.get("sevr");
		severities = new Hashtable<Integer, SeverityImpl>();
		for (Object sio : sevr_info)
		{
		    final Hashtable si = (Hashtable) sio;
			final String txt = (String)si.get("sevr");
            // Patch "NO ALARM" into "OK"
			AlarmSeverity severity;
            if ("NO_ALARM".equals(txt)  ||  NO_ALARM.equals(txt))
            	severity = AlarmSeverity.NONE;
            else if ("MINOR".equals(txt))
            	severity = AlarmSeverity.MINOR;
            else if ("MAJOR".equals(txt))
            	severity = AlarmSeverity.MAJOR;
            else if ("MAJOR".equals(txt))
            	severity = AlarmSeverity.INVALID;
            else
            	severity = AlarmSeverity.UNDEFINED;
			severities.put((Integer) si.get("num"),
					      new SeverityImpl(severity, txt,
							              (Boolean)si.get("has_value"),
							              (Boolean)si.get("txt_stat")
							              ));
		}
	}

	/** @return Returns the version number. */
	public int getVersion()
	{
		return version;
	}

	/** @return Returns the description. */
	public String getDescription()
	{
		return description;
	}

    /** @return Returns the list of supported request types. */
    public String[] getRequestTypes()
    {
        return how_strings;
    }

	/** @return Returns the status strings. */
	public String[] getStatusStrings()
	{
		return status_strings;
	}

    /** @return Returns the severity infos. */
	public SeverityImpl getSeverity(int severity)
	{
        final SeverityImpl sev = severities.get(Integer.valueOf(severity));
        if (sev != null)
            return sev;
        return new SeverityImpl(AlarmSeverity.UNDEFINED,
                        "<Severity " + severity + "?>",
                        false, false);
	}

	/** @return Returns a more or less useful string for debugging. */
	@Override public String toString()
	{
		final StringBuffer result = new StringBuffer();
		result.append(String.format("Server version : %d\n", version));
		result.append(String.format("Description    :\n%s", description));
		result.append("Available request methods:\n");
		for (int i=0; i<how_strings.length; ++i)
			result.append(String.format("%d = '%s'\n", i, how_strings[i]));
		return result.toString();
	}
}
