package org.csstudio.platform.model;

/**
 * Interface to a control system process variable with archive data source.
 * <p>
 *
 * @see IProcessVariableName
 * @see IArchiveDataSource
 * @author Jan Hatje and Helge Rickens
 * 
 * TODO It's actually ... process variable with actual data.
 * TODO This is limited to double-type samples.
 * Use either DAL, or the intermediate Value from org.csstudio.utility.pv,
 * which supports more data types as well as their MetaData.
 */
public interface IProcessVariableWithSample extends IProcessVariable {
	/**
	 * The global type id.
	 */
	String TYPE_ID = "css:processVariableWithSample"; //$NON-NLS-1$

	/**
	 *
	 * @return
	 */
	double[] getSampleValue();
	/**
	 *
	 * TODO Use the Timestamp (ITimestamp) found elsewhere in this plugin.
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
