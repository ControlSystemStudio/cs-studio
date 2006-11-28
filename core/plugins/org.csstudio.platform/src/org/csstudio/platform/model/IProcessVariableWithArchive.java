package org.csstudio.platform.model;

/**
 * Interface to a control system process variable with archive data source.
 * <p>
 * 
 * @see IProcessVariableName
 * @see IArchiveDataSource
 * @author Kay Kasemir
 */
public interface IProcessVariableWithArchive extends IProcessVariable {
	/**
	 * The global type id.
	 */
	String TYPE_ID = "css:processVariableWithArchive"; //$NON-NLS-1$	

	/**
	 * Return the associated archive data source.
	 * 
	 * @return The associated archive data source.
	 */
	IArchiveDataSource getArchiveDataSource();
}
