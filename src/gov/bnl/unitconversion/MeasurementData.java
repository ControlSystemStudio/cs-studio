package gov.bnl.unitconversion;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MeasurementData {

    public static class MeasurementDataBuilder {
	private List<String> direction = Collections.emptyList();
	private List<Double> current = Collections.emptyList();
	private List<Double> currentError = Collections.emptyList();
	private String currentUnit;
	private List<Double> field = Collections.emptyList();
	private List<Double> fieldError = Collections.emptyList();
	private String fieldUnit;
	private List<Double> magneticLength = Collections.emptyList();
	private Double averageLength;
	private List<Double> runNumber = Collections.emptyList();
	private int serialNumber;
	private String referenceDraw;
	private String aliasName;
	private String vendor;
	private List<String> integralTransferFunction = Collections.emptyList();
	private String referenceRadius;
	private List<String> description = Collections.emptyList();
	private String magneticRigidity;
	private String magneticRigidityUnit;
	private String conditionCurrent;

	private MeasurementDataBuilder() {

	}

	public static MeasurementDataBuilder magnetMeasurements() {
	    return new MeasurementDataBuilder();
	}

	/**
	 * @param direction
	 *            the direction to set
	 */
	public MeasurementDataBuilder Direction(List<String> direction) {
	    this.direction = direction;
	    return this;
	}

	/**
	 * @param current
	 *            the current to set
	 */
	public MeasurementDataBuilder Current(List<Double> current) {
	    this.current = current;
	    return this;
	}

	/**
	 * @param currentError
	 *            the currentError to set
	 */
	public MeasurementDataBuilder CurrentError(List<Double> currentError) {
	    this.currentError = currentError;
	    return this;
	}

	/**
	 * @param currentUnit
	 *            the currentUnit to set
	 */
	public MeasurementDataBuilder CurrentUnit(String currentUnit) {
	    this.currentUnit = currentUnit;
	    return this;
	}

	/**
	 * @param field
	 *            the field to set
	 */
	public MeasurementDataBuilder Field(List<Double> field) {
	    this.field = field;
	    return this;
	}

	/**
	 * @param fieldError
	 *            the fieldError to set
	 */
	public MeasurementDataBuilder FieldError(List<Double> fieldError) {
	    this.fieldError = fieldError;
	    return this;
	}

	/**
	 * @param fieldUnit
	 *            the fieldUnit to set
	 */
	public MeasurementDataBuilder FieldUnit(String fieldUnit) {
	    this.fieldUnit = fieldUnit;
	    return this;
	}

	/**
	 * @param magneticLength
	 *            the magneticLength to set
	 */
	public MeasurementDataBuilder MagneticLength(List<Double> magneticLength) {
	    this.magneticLength = magneticLength;
	    return this;
	}

	/**
	 * @param averageLength
	 *            the averageLength to set
	 */
	public MeasurementDataBuilder averageLength(Double averageLength) {
	    this.averageLength = averageLength;
	    return this;
	}

	/**
	 * @param runNumber
	 *            the runNumber to set
	 */
	public MeasurementDataBuilder RunNumber(List<Double> runNumber) {
	    this.runNumber = runNumber;
	    return this;
	}

	/**
	 * @param serialNumber
	 *            the serialNumber to set
	 */
	public MeasurementDataBuilder SerialNumber(int serialNumber) {
	    this.serialNumber = serialNumber;
	    return this;
	}

	public MeasurementDataBuilder referenceDraw(String referenceDraw) {
	    this.referenceDraw = referenceDraw;
	    return this;
	}

	public MeasurementDataBuilder aliasName(String aliasName) {
	    this.aliasName = aliasName;
	    return this;
	}

	public MeasurementDataBuilder vendor(String vendor) {
	    this.vendor = vendor;
	    return this;
	}

	public MeasurementDataBuilder integralTransferFunction(
		List<String> integralTransferFunction) {
	    this.integralTransferFunction = integralTransferFunction;
	    return this;
	}

	public MeasurementDataBuilder referenceRadius(String referenceRadius) {
	    this.referenceRadius = referenceRadius;
	    return this;
	}

	public MeasurementDataBuilder description(List<String> description) {
	    this.description = description;
	    return this;
	}

	public MeasurementDataBuilder magneticRigidity(String magneticRigidity) {
	    this.magneticRigidity = magneticRigidity;
	    return this;
	}

	public MeasurementDataBuilder magneticRigidityUnit(
		String magneticRigidityUnit) {
	    this.magneticRigidityUnit = magneticRigidityUnit;
	    return this;
	}
	
	public MeasurementDataBuilder conditionCurrent(
		String conditionCurrent) {
	    this.conditionCurrent = conditionCurrent;
	    return this;
	}

	public MeasurementData build() {
	    return new MeasurementData(this.direction, this.current,
		    this.currentError, this.currentUnit, this.field,
		    this.fieldError, this.fieldUnit, this.magneticLength,
		    this.averageLength, this.runNumber, this.serialNumber,
		    this.referenceDraw, this.vendor, this.aliasName,
		    this.integralTransferFunction, this.referenceRadius,
		    this.description, this.magneticRigidity,
		    this.magneticRigidityUnit, this.conditionCurrent);
	}
    }

    private List<String> direction;
    private List<Double> current;
    private List<Double> currentError;
    private String currentUnit;
    private List<Double> field;
    private List<Double> fieldError;
    private String fieldUnit;
    private List<Double> magneticLength;
    private Double averageLength;
    private List<Double> runNumber;
    private int serialNumber;
    private String referenceDraw;
    private String aliasName;
    private String vendor;
    private List<String> integralTransferFunction;
    private String referenceRadius;
    private List<String> description;
    private String magneticRigidity;
    private String magneticRigidityUnit;
    private String conditionCurrent;

    /**
     * @return the conditionCurrent
     */
    public String getConditionCurrent() {
        return conditionCurrent;
    }

    /**
     * @param direction
     * @param current
     * @param currentError
     * @param currentUnit
     * @param field
     * @param fieldError
     * @param fieldUnit
     * @param magneticLength
     * @param averageLength
     * @param runNumber
     * @param serialNumber
     * @param referenceDraw
     * @param aliasName
     * @param vendor
     * @param integralTransferFunction
     * @param referenceRadius
     * @param description
     * @param magneticRigidity
     * @param magneticRigidityUnit
     */
    private MeasurementData(List<String> direction, List<Double> current,
	    List<Double> currentError, String currentUnit, List<Double> field,
	    List<Double> fieldError, String fieldUnit,
	    List<Double> magneticLength, Double averageLength,
	    List<Double> runNumber, int serialNumber, String referenceDraw,
	    String aliasName, String vendor,
	    List<String> integralTransferFunction, String referenceRadius,
	    List<String> description, String magneticRigidity,
	    String magneticRigidityUnit, String conditionCurrent) {
	this.direction = direction;
	this.current = current;
	this.currentError = currentError;
	this.currentUnit = currentUnit;
	this.field = field;
	this.fieldError = fieldError;
	this.fieldUnit = fieldUnit;
	this.magneticLength = magneticLength;
	this.averageLength = averageLength;
	this.runNumber = runNumber;
	this.serialNumber = serialNumber;
	this.referenceDraw = referenceDraw;
	this.aliasName = aliasName;
	this.vendor = vendor;
	this.integralTransferFunction = integralTransferFunction;
	this.referenceRadius = referenceRadius;
	this.description = description;
	this.magneticRigidity = magneticRigidity;
	this.magneticRigidityUnit = magneticRigidityUnit;
	this.conditionCurrent = conditionCurrent;
    }

    private MeasurementData() {
    }

    /**
     * @return the averageLength
     */
    public Double getAverageLength() {
	return averageLength;
    }

    /**
     * @return the referenceDraw
     */
    public String getReferenceDraw() {
	return referenceDraw;
    }

    /**
     * @return the aliasName
     */
    public String getAliasName() {
	return aliasName;
    }

    /**
     * @return the vendor
     */
    public String getVendor() {
	return vendor;
    }

    /**
     * @return the integralTransferFunction
     */
    public List<String> getIntegralTransferFunction() {
	return integralTransferFunction;
    }

    /**
     * @return the referenceRadius
     */
    public String getReferenceRadius() {
	return referenceRadius;
    }

    /**
     * @return the description
     */
    public List<String> getDescription() {
	return description;
    }

    /**
     * @return the magneticRigidity
     */
    public String getMagneticRigidity() {
	return magneticRigidity;
    }

    /**
     * @return the magneticRigidityUnit
     */
    public String getMagneticRigidityUnit() {
	return magneticRigidityUnit;
    }

    /**
     * @return the direction
     */
    public List<String> getDirection() {
	return Collections.unmodifiableList(direction);
    }

    /**
     * @return the current
     */
    public List<Double> getCurrent() {
	return Collections.unmodifiableList(current);
    }

    /**
     * @return the currentUnit
     */
    public String getCurrentUnit() {
	return currentUnit;
    }

    /**
     * @return the field
     */
    public List<Double> getField() {
	return Collections.unmodifiableList(field);
    }

    /**
     * @return the fieldError
     */
    public List<Double> getFieldError() {
	return Collections.unmodifiableList(fieldError);
    }

    /**
     * @return the fieldUnit
     */
    public String getFieldUnit() {
	return fieldUnit;
    }

    /**
     * @return the magneticLength
     */
    public List<Double> getMagneticLength() {
	return Collections.unmodifiableList(magneticLength);
    }

   
    /**
     * @return the runNumber
     */
    public List<Double> getRunNumber() {
	return Collections.unmodifiableList(runNumber);
    }

    /**
     * @return the serialNumber
     */
    public int getSerialNumber() {
	return serialNumber;
    }

    /**
     * @return the currentError
     */
    public List<Double> getCurrentError() {
	return Collections.unmodifiableList(currentError);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ ((aliasName == null) ? 0 : aliasName.hashCode());
	result = prime * result
		+ ((averageLength == null) ? 0 : averageLength.hashCode());
	result = prime
		* result
		+ ((conditionCurrent == null) ? 0 : conditionCurrent.hashCode());
	result = prime * result + ((current == null) ? 0 : current.hashCode());
	result = prime * result
		+ ((currentError == null) ? 0 : currentError.hashCode());
	result = prime * result
		+ ((currentUnit == null) ? 0 : currentUnit.hashCode());
	result = prime * result
		+ ((description == null) ? 0 : description.hashCode());
	result = prime * result
		+ ((direction == null) ? 0 : direction.hashCode());
	result = prime * result + ((field == null) ? 0 : field.hashCode());
	result = prime * result
		+ ((fieldError == null) ? 0 : fieldError.hashCode());
	result = prime * result
		+ ((fieldUnit == null) ? 0 : fieldUnit.hashCode());
	result = prime
		* result
		+ ((integralTransferFunction == null) ? 0
			: integralTransferFunction.hashCode());
	result = prime * result
		+ ((magneticLength == null) ? 0 : magneticLength.hashCode());
	result = prime
		* result
		+ ((magneticRigidity == null) ? 0 : magneticRigidity.hashCode());
	result = prime
		* result
		+ ((magneticRigidityUnit == null) ? 0 : magneticRigidityUnit
			.hashCode());
	result = prime * result
		+ ((referenceDraw == null) ? 0 : referenceDraw.hashCode());
	result = prime * result
		+ ((referenceRadius == null) ? 0 : referenceRadius.hashCode());
	result = prime * result
		+ ((runNumber == null) ? 0 : runNumber.hashCode());
	result = prime * result + serialNumber;
	result = prime * result + ((vendor == null) ? 0 : vendor.hashCode());
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
	MeasurementData other = (MeasurementData) obj;
	if (aliasName == null) {
	    if (other.aliasName != null)
		return false;
	} else if (!aliasName.equals(other.aliasName))
	    return false;
	if (averageLength == null) {
	    if (other.averageLength != null)
		return false;
	} else if (!averageLength.equals(other.averageLength))
	    return false;
	if (conditionCurrent == null) {
	    if (other.conditionCurrent != null)
		return false;
	} else if (!conditionCurrent.equals(other.conditionCurrent))
	    return false;
	if (current == null) {
	    if (other.current != null)
		return false;
	} else if (!current.equals(other.current))
	    return false;
	if (currentError == null) {
	    if (other.currentError != null)
		return false;
	} else if (!currentError.equals(other.currentError))
	    return false;
	if (currentUnit == null) {
	    if (other.currentUnit != null)
		return false;
	} else if (!currentUnit.equals(other.currentUnit))
	    return false;
	if (description == null) {
	    if (other.description != null)
		return false;
	} else if (!description.equals(other.description))
	    return false;
	if (direction == null) {
	    if (other.direction != null)
		return false;
	} else if (!direction.equals(other.direction))
	    return false;
	if (field == null) {
	    if (other.field != null)
		return false;
	} else if (!field.equals(other.field))
	    return false;
	if (fieldError == null) {
	    if (other.fieldError != null)
		return false;
	} else if (!fieldError.equals(other.fieldError))
	    return false;
	if (fieldUnit == null) {
	    if (other.fieldUnit != null)
		return false;
	} else if (!fieldUnit.equals(other.fieldUnit))
	    return false;
	if (integralTransferFunction == null) {
	    if (other.integralTransferFunction != null)
		return false;
	} else if (!integralTransferFunction
		.equals(other.integralTransferFunction))
	    return false;
	if (magneticLength == null) {
	    if (other.magneticLength != null)
		return false;
	} else if (!magneticLength.equals(other.magneticLength))
	    return false;
	if (magneticRigidity == null) {
	    if (other.magneticRigidity != null)
		return false;
	} else if (!magneticRigidity.equals(other.magneticRigidity))
	    return false;
	if (magneticRigidityUnit == null) {
	    if (other.magneticRigidityUnit != null)
		return false;
	} else if (!magneticRigidityUnit.equals(other.magneticRigidityUnit))
	    return false;
	if (referenceDraw == null) {
	    if (other.referenceDraw != null)
		return false;
	} else if (!referenceDraw.equals(other.referenceDraw))
	    return false;
	if (referenceRadius == null) {
	    if (other.referenceRadius != null)
		return false;
	} else if (!referenceRadius.equals(other.referenceRadius))
	    return false;
	if (runNumber == null) {
	    if (other.runNumber != null)
		return false;
	} else if (!runNumber.equals(other.runNumber))
	    return false;
	if (serialNumber != other.serialNumber)
	    return false;
	if (vendor == null) {
	    if (other.vendor != null)
		return false;
	} else if (!vendor.equals(other.vendor))
	    return false;
	return true;
    }

}