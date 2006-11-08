package org.csstudio.platform.model;

/**
 * Information about an archive data source for a process variable.
 * <p>
 * To remain generic, this includes
 * <ul>
 * <li>A URL:<br>
 * All history data sources should have some sort of URL. Not necessarily a URL
 * that a web browser understands, but something that the archive data retrieval
 * library can handle.
 * <li>Name, key:<br>
 * Some archive data sources might provide several sub-archives, one example
 * being the ChannelArchiver's network data server. The numeric key and the
 * user-readable name (inherited from the IControlSystemItem interface) might be
 * redundant, but both are provided for generality.
 * </ul>
 *  *
 * <p>
 * This interface is not intended to be implemented by clients. Instances of
 * archive datasources can be created via the @see {@link ControlSystemItemFactory} factory.
 * 
 * @author Kay Kasemir, swende
 */
public interface IArchiveDataSource extends IControlSystemItem {
	/**
	 * The global type id.
	 */
	String TYPE_ID = "css:archiveDataSource"; //$NON-NLS-1$

	/** @return The url of the archive data server. */
	String getUrl();

	/** @return The key of the archive under the url. */
	int getKey();
}
