package org.csstudio.swt.xygraph.dataprovider;

/** An (x,y) sample data with error.
 * @author Xihui Chen
 *
 */
public class Sample implements ISample {
	
	private double xValue;
	private double yValue;
	private double xPlusError;
	private double yPlusError;
	private double xMinusError;
	private double yMinusError;
	private String info = "";
	
	
	/**
	 * @param xdata
	 * @param ydata
	 */
	public Sample(double xdata, double ydata) {
		this.xValue = xdata;
		this.yValue = ydata;
		xPlusError = 0;
		yPlusError = 0;
		xMinusError = 0;
		yMinusError = 0;
	}
	
	public Sample(double xValue, double yValue, double yPlusError, double yMinusError, 
			double xPlusError,double xMinusError) {
		this.xValue = xValue;
		this.yValue = yValue;
		this.xPlusError = xPlusError;
		this.yPlusError = yPlusError;
		this.xMinusError = xMinusError;
		this.yMinusError = yMinusError;
	}

	/**
	 * @param plusError the xPlusError to set
	 */
	public void setXPlusError(double plusError) {
		xPlusError = plusError;
	}

	/**
	 * @param plusError the yPlusError to set
	 */
	public void setYPlusError(double plusError) {
		yPlusError = plusError;
	}

	/**
	 * @param minusError the xMinusError to set
	 */
	public void setXMinusError(double minusError) {
		xMinusError = minusError;
	}

	/**
	 * @param minusError the yMinusError to set
	 */
	public void setYMinusError(double minusError) {
		yMinusError = minusError;
	}
	

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Sample)
			return  (((Sample)obj).xValue == xValue && ((Sample)obj).yValue == yValue);
		return false;
	}
	
	@Override
	public String toString() {
		return "(" + xValue + ", " + yValue + ")";
	}

	public double getXMinusError() {
		return xMinusError;
	}
	
	public double getXPlusError() {
		return xPlusError;
	}
	
	public double getXValue() {
		return xValue;
	}

	public double getYMinusError() {
		return yMinusError;
	}

	public double getYPlusError() {
		return yPlusError;
	}

	public double getYValue() {
		return yValue;
	}

	public String getInfo() {
		return info;
	}
	
	public void setInfo(String info){
		this.info = info;
	}
}