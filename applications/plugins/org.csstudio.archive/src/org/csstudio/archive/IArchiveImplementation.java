package org.csstudio.archive;

/** Providers of the ArchiveImplementation extension point must implement this.
 *  @author Jan Hatje
 *  @author Albert Kagarmanov
 *  @author Kay Kasemir
 */
public interface IArchiveImplementation
{
    /** Create an ArchiveServer instance for the given URL.
     *  <p>
     *  Implementors should expect that the URL starts with the
     *  <code>prefix</code> that they provided in the extention point
     *  description.
     *  
     *  @param url URL
     *  @return ArchiveServer, connected to the URL
     *  @throws Exception on error
     */
	public ArchiveServer getServerInstance(String url) throws Exception;
	
    /** Implementors can provide a list of default URLs.
     *  <p>
     *  GUI Tools might use that list for initial suggestions.
     *  
     *  @return List of URLs, or empty list, or <code>null</code>.
     */
	public String[] getURLList();
}
