/**
 * 
 */
package gov.bnl.unitconversion;

import java.util.Collections;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author shroffk
 * 
 */
@XmlRootElement
public class Conversion {

    public static class ConversionDataBuilder {

	private MeasuredData measuredData;

	// These are design values
	private Double designLength;
	private Double defaultEnergy;

	private Double realEnergy;

	private Map<String, ConversionAlgorithm> algorithms = Collections
		.emptyMap();

	private String description;

	private ConversionResult conversionResult;

	private ConversionDataBuilder() {
	}

	public static ConversionDataBuilder conversionDataOfType() {
	    return new ConversionDataBuilder();
	}

	public ConversionDataBuilder withmeasuredData(
		MeasuredData measuredData) {
	    this.measuredData = measuredData;
	    return this;
	}

	public ConversionDataBuilder withDesignLength(
		Double designLength) {
	    this.designLength = designLength;
	    return this;
	}

	public ConversionDataBuilder withDefaultEnergy(Double defaultEnergy) {
	    this.defaultEnergy = defaultEnergy;
	    return this;
	}

	public ConversionDataBuilder withRealEnergy(Double realEnergy) {
	    this.realEnergy = realEnergy;
	    return this;
	}

	public ConversionDataBuilder description(String description) {
	    this.description = description;
	    return this;
	}

	public ConversionDataBuilder withAlgorithms(
		Map<String, ConversionAlgorithm> algorithms) {
	    this.algorithms = algorithms;
	    return this;
	}

	public ConversionDataBuilder conversionResult(
		ConversionResult conversionResult) {
	    this.conversionResult = conversionResult;
	    return this;
	}

	public Conversion build() {
	    return new Conversion(measuredData, designLength,
		    defaultEnergy, realEnergy, algorithms,
		    description, conversionResult);
	}

    }

    private MeasuredData measuredData;

    // These are design values
    private Double designLength;
    private Double defaultEnergy;

    private Double realEnergy;

    private Map<String, ConversionAlgorithm> algorithms;

    private String description;

    private ConversionResult conversionResult;

    private Conversion() {

    }

    /**
     * @param type
     * @param device
     * @param measuredData
     * @param designLength
     * @param defaultEnergy
     * @param liveBeamEnergy
     * @param conversions
     * @param description
     */
    private Conversion(MeasuredData measuredData,
	    Double designLength, Double defaultEnergy,
	    Double realEnergy,
	    Map<String, ConversionAlgorithm> algorithms,
	    String description, ConversionResult conversionResult) {
	this.measuredData = measuredData;
	this.designLength = designLength;
	this.defaultEnergy = defaultEnergy;
	this.realEnergy = realEnergy;
	this.algorithms = algorithms;
	this.description = description;
	this.conversionResult = conversionResult;
    }

   
    /**
     * @return the measuredData
     */
    public MeasuredData getmeasuredData() {
        return measuredData;
    }

    /**
     * @return the designLength
     */
    public Double getdesignLength() {
	return designLength;
    }

    /**
     * @return the defaultEnergy
     */
    public Double getDefaultEnergy() {
        return defaultEnergy;
    }

    /**
     * @return the realEnergy
     */
    public Double getRealEnergy() {
        return realEnergy;
    }

    /**
     * @return the conversions
     */
    public Map<String, ConversionAlgorithm> getAlgorithms() {
	return Collections.unmodifiableMap(algorithms);
    }

    /**
     * @return the description
     */
    public String getDescription() {
	return description;
    }

    /**
     * @return the conversionResult
     */
    public ConversionResult getConversionResult() {
	return conversionResult;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime
		* result
		+ ((algorithms == null) ? 0 : algorithms
			.hashCode());
	result = prime
		* result
		+ ((conversionResult == null) ? 0 : conversionResult.hashCode());
	result = prime * result
		+ ((defaultEnergy == null) ? 0 : defaultEnergy.hashCode());
	result = prime * result
		+ ((description == null) ? 0 : description.hashCode());
	result = prime * result
		+ ((realEnergy == null) ? 0 : realEnergy.hashCode());
	result = prime
		* result
		+ ((designLength == null) ? 0 : designLength
			.hashCode());
	result = prime * result
		+ ((measuredData == null) ? 0 : measuredData.hashCode());
	return result;
    }

    /*
     * (non-Javadoc)
     * 
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
	Conversion other = (Conversion) obj;
	if (algorithms == null) {
	    if (other.algorithms != null)
		return false;
	} else if (!algorithms.equals(other.algorithms))
	    return false;
	if (conversionResult == null) {
	    if (other.conversionResult != null)
		return false;
	} else if (!conversionResult.equals(other.conversionResult))
	    return false;
	if (defaultEnergy == null) {
	    if (other.defaultEnergy != null)
		return false;
	} else if (!defaultEnergy.equals(other.defaultEnergy))
	    return false;
	if (description == null) {
	    if (other.description != null)
		return false;
	} else if (!description.equals(other.description))
	    return false;
	if (realEnergy == null) {
	    if (other.realEnergy != null)
		return false;
	} else if (!realEnergy.equals(other.realEnergy))
	    return false;
	if (designLength == null) {
	    if (other.designLength != null)
		return false;
	} else if (!designLength.equals(other.designLength))
	    return false;
	if (measuredData == null) {
	    if (other.measuredData != null)
		return false;
	} else if (!measuredData.equals(other.measuredData))
	    return false;
	return true;
    }

}
