/**
 * 
 */
package gov.bnl.unitconversion;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author shroffk
 * 
 */
@XmlRootElement
public class ConversionResult {

    private String message;
    private double value;
    private String unit;

    private ConversionResult() {

    }

    /**
     * @param conversionInfo
     * @param message
     * @param value
     * @param unit
     */
    ConversionResult(String message, double value, String unit) {
	this.message = message;
	this.value = value;
	this.unit = unit;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((message == null) ? 0 : message.hashCode());
	result = prime * result + ((unit == null) ? 0 : unit.hashCode());
	long temp;
	temp = Double.doubleToLongBits(value);
	result = prime * result + (int) (temp ^ (temp >>> 32));
	return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	ConversionResult other = (ConversionResult) obj;
	if (message == null) {
	    if (other.message != null)
		return false;
	} else if (!message.equals(other.message))
	    return false;
	if (unit == null) {
	    if (other.unit != null)
		return false;
	} else if (!unit.equals(other.unit))
	    return false;
	if (Double.doubleToLongBits(value) != Double
		.doubleToLongBits(other.value))
	    return false;
	return true;
    }

    /**
     * @return the message
     */
    public String getMessage() {
	return message;
    }

    /**
     * @return the value
     */
    public double getValue() {
	return value;
    }

    /**
     * @return the unit
     */
    public String getUnit() {
	return unit;
    }

}
