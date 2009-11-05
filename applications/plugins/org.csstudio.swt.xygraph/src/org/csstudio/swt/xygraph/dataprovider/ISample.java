package org.csstudio.swt.xygraph.dataprovider;

/**
 * A sample interface which could return the data for a point on XY-Graph.
 * 
 * @author Xihui Chen
 *
 */
public interface ISample {
	
	/**
	 * @return the value on X axis. For a 'date' axis, this has to be milliseconds as used by java.util.Date
	 */
	public double getXValue();
	
	/**
	 * @return the value on Y axis
	 */
	public double getYValue();
	
	/**
	 * @return The plus error on X value
	 */
	public double getXPlusError();
	
	/**
	 * @return The plus error on Y value
	 */
	public double getYPlusError();	
	
	
	/**
	 * @return The minus error on X value
	 */
	public double getXMinusError();
	
	/**
	 * @return The minus error on Y value
	 */
	public double getYMinusError();

	/** @return Any informational string that might work as e.g. a Tooltip. */
    public String getInfo();
	
}