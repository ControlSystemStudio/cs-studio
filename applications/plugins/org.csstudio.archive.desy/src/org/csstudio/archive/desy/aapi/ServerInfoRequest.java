package org.csstudio.archive.desy.aapi;

import java.util.Hashtable;
import java.util.Vector;

import AAPI.AAPI;



/** Handles the "archiver.info" request and its results.
 *  @author Albert Kagarmanov
 */
final class ServerInfoRequest //implements ClientRequest
{
	private String description;
	private int version;
	private String how_strings[];
	private String status_strings[];
	private Hashtable<Integer, SeverityImpl> severities;

	/** Read info from data server */
	public void read(AAPI aapi)// throws Exception
	{

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
		version = (Integer) aapi.getVersion();
		description = (String) aapi.getDescription();

		how_strings = aapi.requestedTypeList;
		
		status_strings = new String[aapi.getMaxEpicsStatus()];
		for(int i=0;i<aapi.getMaxEpicsStatus();i++){
			status_strings[i] = new String(aapi.getStatusList(i));
			if (status_strings[i].equals("NO_ALARM"))
                status_strings[i] = "";
		}
		
		severities = new Hashtable<Integer, SeverityImpl>();
		severities.put(0,new SeverityImpl("",true,true));
		for(int i=1;i<aapi.getMaxEpicsSeverity();i++)
			severities.put(i,new SeverityImpl(aapi.getSeverityList(i),true,true));
		
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
		return severities.get(new Integer(severity));
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
