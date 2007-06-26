package org.csstudio.archive.archiveRecordInterface;

import java.util.Hashtable;
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
	
	public final static String archiveRecordRequestedTypeList[]={"RAW"};
	public final static String archiveRecordStatusList[]={""};
	public void read()
	{
		version = 1;
		description = "archiveRecord implementation";
		how_strings = archiveRecordRequestedTypeList;	
		status_strings = archiveRecordStatusList;
		severities = new Hashtable<Integer, SeverityImpl>();
		
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
