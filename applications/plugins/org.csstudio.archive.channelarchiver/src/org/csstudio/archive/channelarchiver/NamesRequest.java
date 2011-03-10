package org.csstudio.archive.channelarchiver;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.csstudio.archive.ArchiveAccessException;
import org.csstudio.archive.Messages;
import org.csstudio.archive.NameInfo;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.TimestampFactory;

/**
 * Handles the "archiver.names" request and its results.
 */
@SuppressWarnings("nls")
public class NamesRequest
{
	final private int key;
	final private String pattern;
	private NameInfo names[];

	/** Create a name lookup.
	 *   @param pattern Regular expression pattern for the name.
	 */
	public NamesRequest(final int key, final String pattern)
	{
		this.key = key;
		this.pattern = pattern;
	}

	/** Read info from data server */
	@SuppressWarnings("unchecked")
    public void read(final XmlRpcClient xmlrpc) throws ArchiveAccessException
	{
		Vector<?> result;
		try
		{
			final Vector<Object> params = new Vector<Object>();
			params.add(new Integer(key));
			params.add(pattern);
			result = (Vector<?>) xmlrpc.execute("archiver.names", params);
		}
		catch (final XmlRpcException e) {
			throw new ArchiveAccessException("archiver.names execute call failed", e);
		}
		catch (final IOException e) {
		    throw new ArchiveAccessException("archiver.names execute call failed", e);
		}

		//	{ string name,
		//    int32 start_sec,  int32 start_nano,
		//	  int32 end_sec,    int32 end_nano
		//   }[] = archiver.names(int32 key,  string pattern)
		names = new NameInfo[result.size()];
		for (int i=0; i<result.size(); ++i)
		{
			final Hashtable<String, Object> entry =
			    (Hashtable<String, Object>) result.get(i);
            final ITimestamp start = TimestampFactory.createTimestamp(
                            (Integer) entry.get("start_sec"),
                            (Integer) entry.get("start_nano"));
            final ITimestamp end = TimestampFactory.createTimestamp(
                            (Integer) entry.get("end_sec"),
                            (Integer) entry.get("end_nano"));
			names[i] = new NameInfo(
					(String) entry.get("name"), start, end);
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
		final StringBuffer result = new StringBuffer();
		result.append(String.format("Names with key %d matching '%s':\n",
				key, pattern));
		for (int i=0; i<names.length; ++i)
		{
			if (i>0) {
                result.append(Messages.ArrayElementSeparator);
            }
			result.append('\'');
			result.append(names[i].getName());
			result.append('\'');
		}
		return result.toString();
	}
}
