package org.csstudio.platform.ui.internal.data.exchange;


/** Minimal <code>IProcessVariableNameWithArchiveDataSource</code> implementation.
 *  <p>
 *  The drag-and-drop transfer uses it internally.<br>
 *  Applications which need to provide IArchiveDataSource
 *  can use this, but can also implement the interface themselves.
 *  @author Kay Kasemir
 */
public class ProcessVariableWithArchive
    extends ProcessVariableName
    implements IProcessVariableWithArchive
{
    private ArchiveDataSource archive;
    
    /** Constructor. */
    public ProcessVariableWithArchive(String pv_name,
                    String url, int key, String arch_name)
    {
        super(pv_name);
        archive = new ArchiveDataSource(url, key, arch_name);
    }

    /** @see IProcessVariableWithArchive */
    public IArchiveDataSource getArchiveDataSource()
    {
        return archive;
    }
}
