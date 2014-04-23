/**
 * 
 */
package gov.bnl.unitconversion;

/**
 * @author shroffk
 * 
 */
public class ConversionAlgorithm {
    private int algorithmId;
    private String function;
    private int auxInfo;
    private String initialUnit;
    private String resultUnit;
    
    private ConversionAlgorithm() {

    }

    /**
     * @param algorithmId
     * @param function
     * @param auxInfo
     * @param initialUnit
     * @param resultUnit
     */
    private ConversionAlgorithm(int algorithmId, String function, int auxInfo,
	    String initialUnit, String resultUnit) {
	this.algorithmId = algorithmId;
	this.function = function;
	this.auxInfo = auxInfo;
	this.initialUnit = initialUnit;
	this.resultUnit = resultUnit;
    }

    /**
     * @return the algorithmId
     */
    public int getalgorithmId() {
	return algorithmId;
    }

    /**
     * @return the function
     */
    public String getFunction() {
	return function;
    }

    /**
     * @return the additionalAttributes
     */
    public int getAuxInfo() {
	return auxInfo;
    }    
    
    /**
     * @return the initialUnit
     */
    public String getInitialUnit() {
        return initialUnit;
    }

    /**
     * @return the resultUnit
     */
    public String getresultUnit() {
        return resultUnit;
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
	result = prime * result + auxInfo;
	result = prime * result
		+ ((function == null) ? 0 : function.hashCode());
	result = prime * result + algorithmId;
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
	ConversionAlgorithm other = (ConversionAlgorithm) obj;
	if (auxInfo != other.auxInfo)
	    return false;
	if (function == null) {
	    if (other.function != null)
		return false;
	} else if (!function.equals(other.function))
	    return false;
	if (algorithmId != other.algorithmId)
	    return false;
	return true;
    }
    
    public static class ConversionAlogrithmBuilder {
	private int algorithmId;
	private String function;
	private int auxInfo = 0;
	private String initialUnit;
	private String resultUnit;

	/**
	 * @param algorithmId
	 * @param function
	 */
	private ConversionAlogrithmBuilder(int algorithmId, String function) {
	    this.algorithmId = algorithmId;
	    this.function = function;
	}

	/**
	 * @param algorithmId
	 */
	private ConversionAlogrithmBuilder(int algorithmId) {
	    this.algorithmId = algorithmId;
	}

	public static ConversionAlogrithmBuilder conversionAlgorithm(
		int algorithmId) {
	    return new ConversionAlogrithmBuilder(algorithmId);
	}

	public static ConversionAlogrithmBuilder conversionAlgorithm(
		int algorithmId, String function) {
	    return new ConversionAlogrithmBuilder(algorithmId, function);
	}

	public ConversionAlogrithmBuilder withFunction(String function) {
	    this.function = function;
	    return this;
	}

	public ConversionAlogrithmBuilder withAuxInfo(int auxInfo) {
	    this.auxInfo = auxInfo;
	    return this;
	}

	public ConversionAlogrithmBuilder withinitialUnit(String initialUnit) {
	    this.initialUnit = initialUnit;
	    return this;
	}

	public ConversionAlogrithmBuilder withresultUnit(String resultUnit) {
	    this.resultUnit = resultUnit;
	    return this;
	}

	public ConversionAlgorithm build() {
	    return new ConversionAlgorithm(algorithmId, function, auxInfo,
		    initialUnit, resultUnit);
	}
    }


}
