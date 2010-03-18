package org.csstudio.archivereader;

/** Exception thrown by {@link ArchiveReader} when trying to read data
 *  for an unknown channel.
 *  <p>
 *  Support is optional: Some archive data sources might just silently return
 *  no data, others might know for sure that they have no data for a given
 *  channel and then throw this exception.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class UnknownChannelException extends Exception
{
    /** To avoid warning from missing serialization ID */
    private static final long serialVersionUID = 1447109498105862523L;

    /** Initialize
     *  @param channel Channel name
     */
    public UnknownChannelException(final String channel)
    {
        super("Unknown channel name: " + channel);
    }
}
