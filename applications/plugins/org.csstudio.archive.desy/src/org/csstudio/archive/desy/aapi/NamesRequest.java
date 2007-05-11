package org.csstudio.archive.desy.aapi;

import org.csstudio.archive.NameInfo;
import org.csstudio.platform.data.TimestampFactory;

import AAPI.AAPI;
import AAPI.AnswerChannelInfo;


/** Handles the "archiver.names" request and its results. 
 *  @author Albert Kagarmanov
 */
public class NamesRequest implements ClientRequest
{
	private final boolean debug=true;
	private final boolean debugOut=false;
	private int key;
	private String pattern;
	private NameInfo names[];
	public void read(){};
	/** Create a name lookup.
	 *   @param pattern Regular expression pattern for the name.
	 */
	public NamesRequest(int key, String pattern)
	{
		this.key = key;
		this.pattern = pattern;
	}

	/** Read info from data server */
	public void read(AAPI aapi) throws Exception
	{


		//	{ string name,
		//    int32 start_sec,  int32 start_nano,
		//	  int32 end_sec,    int32 end_nano
		//   }[] = archiver.names(int32 key,  string pattern)
		
		//public String[] getRegExpChannelList(String regExp) {

		String[] result;
		int size;
		try {
			result = aapi.getRegExpChannelList(pattern);
			if (result == null) {
				System.out.println("getRegExpChannelList return null" );
				size = 0;
			}
			else size = result.length;
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new Exception("AAPI getRegExpChannelList call failed");
		}
		names = new NameInfo[size];
		int from=0,u_from=0, to=0, u_to=0;
		String PV;
		for (int i=0; i<size; i++)
		{
			PV=result[i];
			if (debugOut)System.out.println("\t\t PV="+ PV +";" );	
			AnswerChannelInfo chInfo=aapi.getChannelInfo(PV);
			if (chInfo != null) { 
				from  =chInfo.getFromTime();
				u_from=chInfo.getFromUTime();
				to    =chInfo.getToTime();
				u_to  =chInfo.getToUTime();
			} else {
				System.out.println("getChannelInfo for "+ PV +"returns null" );	
			
			}
				
			names[i] = new NameInfo(PV,
                            TimestampFactory.createTimestamp(from,u_from),
                            TimestampFactory.createTimestamp(to, u_to));
		}
	}

	/** @return Returns the name infos that were found. */
	public final NameInfo[] getNameInfos()
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
			result.append(names[i].getName());
			result.append('\'');
		}
		return result.toString();
	}
}
