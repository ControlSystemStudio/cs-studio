package gov.bnl.unitconversion;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MeasuredData {

    public static class MeasuredDataBuilder {
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

	private MeasuredDataBuilder() {

	}

	public static MeasuredDataBuilder magnetMeasurements() {
	    return new MeasuredDataBuilder();
	}

	/**
	 * @param direction
	 *            the direction to set
	 */
	public MeasuredDataBuilder Direction(List<String> direction) {
	    this.direction = direction;
	    return this;
	}

	/**
	 * @param current
	 *            the current to set
	 */
	public MeasuredDataBuilder Current(List<Double> current) {
	    this.current = current;
	    return this;
	}

	/**
	 * @param currentError
	 *            the currentError to set
	 */
	public MeasuredDataBuilder CurrentError(
		List<Double> currentError) {
	    this.currentError = currentError;
	    return this;
	}

	/**
	 * @param currentUnit
	 *            the currentUnit to set
	 */
	public MeasuredDataBuilder CurrentUnit(String currentUnit) {
	    this.currentUnit = currentUnit;
	    return this;
	}

	/**
	 * @param field
	 *            the field to set
	 */
	public MeasuredDataBuilder Field(List<Double> field) {
	    this.field = field;
	    return this;
	}

	/**
	 * @param fieldError
	 *            the fieldError to set
	 */
	public MeasuredDataBuilder FieldError(List<Double> fieldError) {
	    this.fieldError = fieldError;
	    return this;
	}

	/**
	 * @param fieldUnit
	 *            the fieldUnit to set
	 */
	public MeasuredDataBuilder FieldUnit(String fieldUnit) {
	    this.fieldUnit = fieldUnit;
	    return this;
	}

	/**
	 * @param magneticLength
	 *            the magneticLength to set
	 */
	public MeasuredDataBuilder MagneticLength(
		List<Double> magneticLength) {
	    this.magneticLength = magneticLength;
	    return this;
	}

	/**
	 * @param averageLength
	 *            the averageLength to set
	 */
	public MeasuredDataBuilder averageLength(
		Double averageLength) {
	    this.averageLength = averageLength;
	    return this;
	}

	/**
	 * @param runNumber
	 *            the runNumber to set
	 */
	public MeasuredDataBuilder RunNumber(List<Double> runNumber) {
	    this.runNumber = runNumber;
	    return this;
	}

	/**
	 * @param serialNumber
	 *            the serialNumber to set
	 */
	public MeasuredDataBuilder SerialNumber(int serialNumber) {
	    this.serialNumber = serialNumber;
	    return this;
	}

	public MeasuredData build() {
	    return new MeasuredData(this.direction, this.current,
		    this.currentError, this.currentUnit, this.field,
		    this.fieldError, this.fieldUnit, this.magneticLength,
		    this.averageLength, this.runNumber,
		    this.serialNumber);
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

    private MeasuredData() {
    }

    /**
     * @param direction
     * @param current
     * @param currentUnit
     * @param field
     * @param fieldError
     * @param fieldUnit
     * @param magneticLength
     * @param averageLength
     * @param runNumber
     * @param serialNumber
     */
    private MeasuredData(List<String> direction, List<Double> current,
	    List<Double> currentError, String currentUnit, List<Double> field,
	    List<Double> fieldError, String fieldUnit,
	    List<Double> magneticLength, Double averageLength,
	    List<Double> runNumber, int serialNumber) {
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
     * @return the averageLength
     */
    public Double getaverageLength() {
	return averageLength;
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
	result = prime
		* result
		+ ((averageLength == null) ? 0 : averageLength
			.hashCode());
	result = prime * result + ((current == null) ? 0 : current.hashCode());
	result = prime * result
		+ ((currentError == null) ? 0 : currentError.hashCode());
	result = prime * result
		+ ((currentUnit == null) ? 0 : currentUnit.hashCode());
	result = prime * result
		+ ((direction == null) ? 0 : direction.hashCode());
	result = prime * result + ((field == null) ? 0 : field.hashCode());
	result = prime * result
		+ ((fieldError == null) ? 0 : fieldError.hashCode());
	result = prime * result
		+ ((fieldUnit == null) ? 0 : fieldUnit.hashCode());
	result = prime * result
		+ ((magneticLength == null) ? 0 : magneticLength.hashCode());
	result = prime * result
		+ ((runNumber == null) ? 0 : runNumber.hashCode());
	result = prime * result + serialNumber;
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
	MeasuredData other = (MeasuredData) obj;
	if (averageLength == null) {
	    if (other.averageLength != null)
		return false;
	} else if (!averageLength.equals(other.averageLength))
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
	if (magneticLength == null) {
	    if (other.magneticLength != null)
		return false;
	} else if (!magneticLength.equals(other.magneticLength))
	    return false;
	if (runNumber == null) {
	    if (other.runNumber != null)
		return false;
	} else if (!runNumber.equals(other.runNumber))
	    return false;
	if (serialNumber != other.serialNumber)
	    return false;
	return true;
    }
    

}