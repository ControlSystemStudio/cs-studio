package org.csstudio.archive;

import org.csstudio.data.values.IValue;

/** The samples returned by the archive for one channel.
 *  @author Kay Kasemir
 */
public class ArchiveValues
{
	private final ArchiveServer server;
	private final String channel_name;
	private final IValue samples[];

	/** Constructor
	 *
	 *  @param channel_name The name of the channel
	 *  @param samples The samples we retrieved for this channel.
	 */
	public ArchiveValues(ArchiveServer server, String channel_name,
                         IValue[] samples)
	{
		this.server = server;
		this.channel_name = channel_name;
		this.samples = samples;
	}

	/** @return The server that returned this data. */
	public ArchiveServer getArchiveServer()
	{	return server;	}

	/** @return The channel name. */
	public String getChannelName()
	{	return channel_name;	}

	/** @return The samples. */
	public IValue[] getSamples()
	{	return samples;	}
}
