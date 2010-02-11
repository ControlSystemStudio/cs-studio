package org.csstudio.archivereader;

/** Info about one archive handled by an {@link ArchiveReader}
 *  @author Kay Kasemir
 */
public class ArchiveInfo
{
    final private String name, description;
    final private int key;

    /** Initialize
     *  @param name
     *  @param description
     *  @param key
     */
    public ArchiveInfo(final String name, final String description, final int key)
    {
        this.name = name;
        this.description = description;
        this.key = key;
    }

    /** @return Returns the name of the archive. */
    public String getName()
    {
        return name;
    }

    /** @return Returns an arbitrary info string about the archive. */
    public String getDescription()
    {
        return description;
    }

    /** This key is used in various requests to the archive server.
     *  @return Returns the numeric key that identifies an archive.
     */
    public int getKey()
    {
        return key;
    }
    
    /** @return Debug representation */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return name + " (" + key + ") - " + description;
    }
}
