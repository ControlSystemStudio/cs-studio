package org.csstudio.archive.archiveRecordInterface;
import org.csstudio.archive.NameInfo;

/** Handles the "archiver.names" request and its results. 
 *  @author Albert Kagarmanov
 */
public class NamesRequest implements ClientRequest
{
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

	/** @return Returns the name infos that were found. */
	public final NameInfo[] getNameInfos()
	{
		return null;
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
