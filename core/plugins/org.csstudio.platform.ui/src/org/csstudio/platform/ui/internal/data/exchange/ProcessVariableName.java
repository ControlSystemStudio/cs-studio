package org.csstudio.platform.ui.internal.data.exchange;


/** Minimal <code>IProcessVariableName</code> implementation.
 *  <p>
 *  The drag-and-drop transfer uses it internally.<br>
 *  Applications which need to provide IArchiveDataSource
 *  can use this, but can also implement the interface themselves.
 *  @author Kay Kasemir
 */
public class ProcessVariableName implements IProcessVariableName
{
    private String name;
    
    /** Constructor. */
    public ProcessVariableName(String name)
    {
        this.name = name;
    }

    /* @see org.csstudio.data.exchange.IControlSystemItem#getName() */
    public String getName()
    {
        return name;
    }
    
    @Override
    public String toString()
    {
        return name;
    }
}
