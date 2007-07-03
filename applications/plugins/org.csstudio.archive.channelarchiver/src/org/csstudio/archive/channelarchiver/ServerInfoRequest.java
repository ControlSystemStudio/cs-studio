package org.csstudio.archive.channelarchiver;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

/** Handles the "archiver.info" request and its results.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
final class ServerInfoRequest
{
	private static final int EXPECTED_VERSION = 1;
    private String description;
	private int version;
	private String how_strings[];
	private String status_strings[];
	private Hashtable<Integer, SeverityImpl> severities;

	/** Read info from data server */
    public void read(XmlRpcClient xmlrpc) throws Exception
	{
		Hashtable result;
		try
		{
			Vector params = new Vector();
			result = (Hashtable)xmlrpc.execute("archiver.info", params);
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
        if (version < EXPECTED_VERSION)
            Plugin.logInfo("Warning: "
                            + "Expected ChannelArchiver "
                            + "XML-RPC Network Data Server Version "
                            + EXPECTED_VERSION
                            + ", but got " + version);
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
            if (status_strings[i].equals("NO_ALARM"))
                status_strings[i] = "";
        }
		Vector sevr_info = (Vector) result.get("sevr");
		severities = new Hashtable<Integer, SeverityImpl>();
		for (Object sio : sevr_info)
		{
			Hashtable si = (Hashtable) sio;
			
			String sevr_txt = (String)si.get("sevr");
            if (sevr_txt.equals("NO_ALARM"))
                sevr_txt = "";
			severities.put((Integer) si.get("num"),
					      new SeverityImpl(sevr_txt,
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
        SeverityImpl sev = severities.get(new Integer(severity));
        if (sev != null)
            return sev;
        return new SeverityImpl(
                        "<Severity " + severity + "?>",
                        false, false);
	}

	/** @return Returns a more or less useful string for debugging. */
	@Override public String toString()
	{
		StringBuffer result = new StringBuffer();
		result.append(String.format("Server version : %d\n", version));
		result.append(String.format("Description    :\n%s", description));
		result.append("Available request methods:\n");
		for (int i=0; i<how_strings.length; ++i)
			result.append(String.format("%d = '%s'\n", i, how_strings[i]));
		return result.toString();
	}
}
