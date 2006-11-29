package org.csstudio.platform.ui.internal.data.exchange;

/** Interface to a control system process variable with archive data source.
 *  <p>
 *  @see IProcessVariableName
 *  @see IArchiveDataSource
 *  @author Kay Kasemir
 */
public interface IProcessVariableWithArchive extends
 IProcessVariableName
{
    public IArchiveDataSource getArchiveDataSource();
}
