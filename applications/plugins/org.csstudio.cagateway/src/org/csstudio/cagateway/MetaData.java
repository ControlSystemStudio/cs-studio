package org.csstudio.cagateway;

/**
 * 
 * @author claus
 * @version 1.0
 * 
 *
 */
public class MetaData {
	
	private String 	_epicsName 	= null;
	private String 	_gateWayName= null;
	private String 	_urlPrefix 	= null;
	
	private String 	_facility 	= null;
	private String 	_device	 	= null;
	private String 	_location 	= null;
	private String 	_property 	= null;
	private String 	_descriptor = null;
	private String 	_egu	 	= null;	
	
	private short	_precision			= 0;
	private Double 	_lowerWarningValue 	= -1e10;
	private Double 	_upperWarningValue 	= +1e10;
	private Double 	_lowerAlarmValue 	= -1e11;
	private Double 	_upperAlarmValue 	= +1e11;
	
	private Double	_lowerControlValue = 0.0;
	private Double	_upperControlValue = 100.0;
	private Double 	_lowerDisplayValue = 0.0;
	private Double	_upperDisplayValue = 100.0;
	
	private Object _objectReference	= null;
	
	/**
	 * 
	 * @param epicsName
	 * @param gatewayName
	 * @param urlPrefix
	 */
	public MetaData ( String epicsName, String gatewayName, String urlPrefix) {
		_epicsName = epicsName;
		_gateWayName = gatewayName;
		_urlPrefix = urlPrefix;
		
	}

	/**
	 * 
	 * @param facility
	 * @param device
	 * @param location
	 * @param property
	 * @param descriptor
	 * @param egu
	 */
	public void setAllNames(String facility, String device, String location, String property, String descriptor, String egu) {
		_facility = facility;
		_device = device;
		_location = location;
		_property = property;
		_descriptor = descriptor;
		_egu = egu;
	}
	
	/**
	 * 
	 * @param lowerWarningValue
	 * @param upperWarningValue
	 * @param lowerAlarmValue
	 * @param upperAlarmValue
	 */
	public void setAlarmLimits (Double lowerWarningValue, Double upperWarningValue, Double lowerAlarmValue, Double upperAlarmValue) {
		_lowerWarningValue = lowerWarningValue;
		_upperWarningValue = upperWarningValue;
		_lowerAlarmValue = lowerAlarmValue;
		_upperAlarmValue = upperAlarmValue;
	}
	
	/**
	 * 
	 * @param lowerControlValue
	 * @param upperControlValue
	 */
	public void setControlValues (Double lowerControlValue, Double upperControlValue) {
		_lowerControlValue = lowerControlValue;
		_upperControlValue = upperControlValue;
	}
	
	/**
	 * 
	 * @param lowerDisplayValue
	 * @param upperDisplayValue
	 */
	public void setDisplayValues (Double lowerDisplayValue, Double upperDisplayValue) {
		_lowerDisplayValue = lowerDisplayValue;
		_upperDisplayValue = upperDisplayValue;
	}
	
	public String get_facility() {
		return _facility;
	}

	public void set_facility(String facility) {
		_facility = facility;
	}

	public String get_device() {
		return _device;
	}

	public void set_device(String device) {
		_device = device;
	}

	public String get_location() {
		return _location;
	}

	public void set_location(String location) {
		_location = location;
	}

	public String get_property() {
		return _property;
	}

	public void set_property(String property) {
		_property = property;
	}

	public String get_descriptor() {
		return _descriptor;
	}

	public void set_descriptor(String descriptor) {
		_descriptor = descriptor;
	}

	public String get_egu() {
		return _egu;
	}

	public void set_egu(String egu) {
		_egu = egu;
	}

	public short get_precision() {
		return _precision;
	}

	public void set_precision(short precision) {
		_precision = precision;
	}

	public Double get_lowerWarningValue() {
		return _lowerWarningValue;
	}

	public void set_lowerWarningValue(Double lowerWarningValue) {
		_lowerWarningValue = lowerWarningValue;
	}

	public Double get_upperWarningValue() {
		return _upperWarningValue;
	}

	public void set_upperWarningValue(Double upperWarningValue) {
		_upperWarningValue = upperWarningValue;
	}

	public Double get_lowerAlarmValue() {
		return _lowerAlarmValue;
	}

	public void set_lowerAlarmValue(Double lowerAlarmValue) {
		_lowerAlarmValue = lowerAlarmValue;
	}

	public Double get_upperAlarmValue() {
		return _upperAlarmValue;
	}

	public void set_upperAlarmValue(Double upperAlarmValue) {
		_upperAlarmValue = upperAlarmValue;
	}

	public Double get_lowerControlValue() {
		return _lowerControlValue;
	}

	public void set_lowerControlValue(Double lowerControlValue) {
		_lowerControlValue = lowerControlValue;
	}

	public Double get_upperControlValue() {
		return _upperControlValue;
	}

	public void set_upperControlValue(Double upperControlValue) {
		_upperControlValue = upperControlValue;
	}

	public Double get_lowerDisplayValue() {
		return _lowerDisplayValue;
	}

	public void set_lowerDisplayValue(Double lowerDisplayValue) {
		_lowerDisplayValue = lowerDisplayValue;
	}

	public Double get_upperDisplayValue() {
		return _upperDisplayValue;
	}

	public void set_upperDisplayValue(Double upperDisplayValue) {
		_upperDisplayValue = upperDisplayValue;
	}

	public String get_epicsName() {
		return _epicsName;
	}

	public void set_epicsName(String epicsName) {
		_epicsName = epicsName;
	}

	public String get_gateWayName() {
		return _gateWayName;
	}

	public void set_gateWayName(String gateWayName) {
		_gateWayName = gateWayName;
	}

	public String get_urlPrefix() {
		return _urlPrefix;
	}

	public void set_urlPrefix(String urlPrefix) {
		_urlPrefix = urlPrefix;
	}

	public Object get_objectReference() {
		return _objectReference;
	}

	public void set_objectReference(Object objectReference) {
		_objectReference = objectReference;
	}

}
