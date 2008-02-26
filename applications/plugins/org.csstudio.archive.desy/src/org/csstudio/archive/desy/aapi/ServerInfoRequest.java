/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
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
