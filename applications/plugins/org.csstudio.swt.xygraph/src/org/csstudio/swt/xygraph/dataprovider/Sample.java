package org.csstudio.swt.xygraph.dataprovider;

/** An (x,y) sample data with error.
 *  <p>
 *  The error values are absolute, positive numbers.
 *  For example, a Sample with Y=5, negative Y error 1 and positive Y error 2
 *  represents a number of 5 that could also be anywhere in 4 .. 7.
 *  The errors are not percentages.
 *  The 'negative' error is actually a positive number.
 *  <p>
 *  Note:
 *  Only the x/y value is used in equals()!
 *  Error ranges and info texts are ignored when determining equality
 *  with another Sample.
 *  
 *  @author Xihui Chen
 *  @author Kay Kasemir Comments, made immutable
 */
public class Sample implements ISample {
    final private double xValue;
    final private double yValue;
    final private double xPlusError;
    final private double yPlusError;
    final private double xMinusError;
    final private double yMinusError;
    final private String info;
	
	/** Initialize with x/y value
	 *  @param xdata
	 *  @param ydata
	 */
	public Sample(final double xdata, final double ydata) {
	    this(xdata, ydata, 0, 0, 0, 0, ""); //$NON-NLS-1$
	}

   /** Initialize with value and error range
    *  @param xValue
    *  @param yValue
    *  @param yPlusError
    *  @param yMinusError
    *  @param xPlusError
    *  @param xMinusError
    */
    public Sample(final double xValue, final double yValue,
            final double yPlusError, final double yMinusError, 
            final double xPlusError, final double xMinusError) {
       this(xValue, yValue, yPlusError, yMinusError,
           xPlusError, xMinusError,  ""); //$NON-NLS-1$
    }

    /** Initialize with value, error ranges and info text
     *  @param xValue
     *  @param yValue
     *  @param yPlusError
     *  @param yMinusError
     *  @param xPlusError
     *  @param xMinusError
     *  @param info
     */
	public Sample(final double xValue, final double yValue,
	        final double yPlusError, final double yMinusError, 
	        final double xPlusError, final double xMinusError,
	        final String info) {
		this.xValue = xValue;
		this.yValue = yValue;
		this.xPlusError = xPlusError;
		this.yPlusError = yPlusError;
		this.xMinusError = xMinusError;
		this.yMinusError = yMinusError;
		this.info = info;
	}

	/** @return X value */
	public double getXValue() {
    	return xValue;
    }

    /** @return Y value */
    public double getYValue() {
    	return yValue;
    }

    /** @return Negative X error. */
    public double getXMinusError() {
		return xMinusError;
	}
    
    /** @return Positive X error. */
	public double getXPlusError() {
		return xPlusError;
	}
	
    /** @return Negative Y error. */
	public double getYMinusError() {
		return yMinusError;
	}

    /** @return Positive Y error. */
	public double getYPlusError() {
		return yPlusError;
	}

    /** @return Sample info text. */
	public String getInfo() {
		return info;
	}

    @Override
    public boolean equals(final Object obj) {
    	if(obj instanceof Sample)
    		return  (((Sample)obj).xValue == xValue && ((Sample)obj).yValue == yValue);
    	return false;
    }

    /** @return String representation, mostly for debugging */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("(");
        buf.append(xValue);
        if (xMinusError != 0  ||  xPlusError != 0)
            buf.append(" [-" + xMinusError + " ... +" + xPlusError + "]");
        buf.append(", ");
        buf.append(xValue);
        if (yMinusError != 0  ||  yPlusError != 0)
            buf.append(" [-" + yMinusError + " ... +" + yPlusError + "]");
        if (info != null  &&  info.length() > 0)
            buf.append(", '" + info + "'");
        buf.append(")");
        return buf.toString();
    }
}