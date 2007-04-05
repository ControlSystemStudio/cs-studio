package org.csstudio.platform.model;

/**
 * Interface to a control system process variable with archive data source.
 * <p>
 *
 * @see IProcessVariableName
 * @see IArchiveDataSource
 * @author Jan Hatje and Helge Rickens
 *
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
	String[] getStatus();
	/**
	 *
	 *
	 * @return
	 */
	String[] getSeverity();
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
