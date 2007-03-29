package org.csstudio.platform.model;

/**
 * Interface to a control system process variable with archive data source.
 * <p>
 * 
 * @see IProcessVariableName
 * @see IArchiveDataSource
 * @author Kay Kasemir
 */
public interface IProcessVariableWithSample extends IProcessVariable {
	/**
	 * The global type id.
	 */
	String TYPE_ID = "css:processVariableWithSample"; //$NON-NLS-1$	

	/**
	 * 
	 * 
	 * @return 
	 */
	double[] getSampleValue();
	/**
	 * 
	 * 
	 * @return 
	 */
	double[] getTimeStamp();
	/**
	 * 
	 * 
	 * @return 
	 */
	int getDBRTyp();
	/**
	 * 
	 * 
	 * @return 
	 */
	String getEGU();
	/**
	 * 
	 * 
	 * @return 
	 */
	int getPrecision();
	/**
	 * 
	 * 
	 * @return 
	 */
	double getLow();
	/**
	 * 
	 * 
	 * @return 
	 */
	double getHigh();
	
}
