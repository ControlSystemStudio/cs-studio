/**
 * 
 */
package gov.bnl.unitconversion;


/**
 * @author shroffk
 * 
 */
public class ConversionResult {

    private String message;
    private double value;
    private String unit;

    /**
     * @param conversionInfo
     * @param message
     * @param value
     * @param unit
     */
    private ConversionResult(String message, double value, String unit) {
	this.message = message;
	this.value = value;
	this.unit = unit;
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
