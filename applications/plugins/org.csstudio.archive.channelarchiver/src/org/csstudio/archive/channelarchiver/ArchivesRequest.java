package org.csstudio.archive.channelarchiver;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.csstudio.archive.ArchiveInfo;

/** Handles the "archives" request and its results.
 *  @author Kay Kasemir
 */
public class ArchivesRequest
{
	private ArchiveInfo archive_infos[];

	/** Read info from data server */
	@SuppressWarnings("nls")
    public void read(XmlRpcClient xmlrpc) throws Exception
	{
		Vector result;
		try
		{
			Vector params = new Vector();
			result = (Vector) xmlrpc.execute("archiver.archives", params);
		}
		catch (XmlRpcException e)
		{
			throw new Exception("archiver.archives call failed", e);
		}

		//	{  int32 key,
		//     string name,
		//     string path }[] = archiver.archives()
        archive_infos = new ArchiveInfo[result.size()];
		for (int i=0; i<result.size(); ++i)
		{
			Hashtable info = (Hashtable) result.get(i);
            archive_infos[i] = 
                new org.csstudio.archive.channelarchiver.ArchiveInfoImpl(
				(Integer) info.get("key"),
				(String) info.get("name"),
				(String) info.get("path"));
		}
	}

	/** @return Returns all the archive infos obtained in the request. */
    public ArchiveInfo[] getArchiveInfos()
	{
		return archive_infos;
	}

	/** @return Returns a more or less useful string. */
	@SuppressWarnings("nls")
    @Override public String toString()
	{
		StringBuffer result = new StringBuffer();
        for (int i = 0; i < archive_infos.length; i++)
        	result.append(String.format("Key %4d: '%s' (%s)\n",
                archive_infos[i].getKey(),
                archive_infos[i].getName(),
                archive_infos[i].getDescription()));
		return result.toString();
	}
}
