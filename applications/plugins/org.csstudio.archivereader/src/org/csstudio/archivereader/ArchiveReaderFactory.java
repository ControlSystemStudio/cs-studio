package org.csstudio.archivereader;

/** Extension points implement this interface to provide
 *  instances of their ArchiveReader
 *  @author Kay Kasemir
 */
public interface ArchiveReaderFactory
{
    /** @param url URL that the ArchiveReader understands
     *  @return ArchiveReader for the URL
     *  @throws Exception on error
     */
    public ArchiveReader getArchiveReader(String url) throws Exception;
}
