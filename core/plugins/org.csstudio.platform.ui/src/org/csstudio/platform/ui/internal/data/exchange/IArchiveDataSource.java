package org.csstudio.platform.ui.internal.data.exchange;

/** Information about an archive data source for a process variable.
 *  <p>
 *  To remain generic, this includes
 *  <ul>
 *  <li>A URL:<br>
 *      All history data sources should have some sort of URL.
 *      Not necessarily a URL that a web browser understands,
 *      but something that the archive data retrieval library
 *      can handle.
 *  <li>Name, key:<br>
 *      Some archive data sources might provide several sub-archives,
 *      one example being the ChannelArchiver's network data server.
 *      The numeric key and the user-readable name (inherited from the
 *      IControlSystemItem interface) might be redundant,
 *      but both are provided for generality.
 *  </ul>
 *  @author Kay Kasemir
 */
public interface IArchiveDataSource extends IControlSystemItem
{
    /** @return The url of the archive data server. */
    public abstract String getUrl();

    /** @return The key of the archive under the url. */
    public abstract int getKey();
}
