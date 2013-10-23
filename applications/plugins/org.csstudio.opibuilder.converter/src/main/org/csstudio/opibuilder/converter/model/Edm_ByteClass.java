package org.csstudio.opibuilder.converter.model;

public class Edm_ByteClass extends EdmWidget {

	@EdmAttributeAn @EdmOptionalAn private EdmColor lineColor;
	@EdmAttributeAn @EdmOptionalAn private EdmColor onColor;
	@EdmAttributeAn @EdmOptionalAn private EdmColor offColor;
	
	@EdmAttributeAn @EdmOptionalAn private String controlPv;
	@EdmAttributeAn @EdmOptionalAn private String endian;
	
	@EdmAttributeAn @EdmOptionalAn private int numBits;
	@EdmAttributeAn @EdmOptionalAn private int shift;
	public Edm_ByteClass(EdmEntity genericEntity) throws EdmException {
		super(genericEntity);
	}
	

	/**
	 * @return the lineAlarm
	 */
	public EdmColor getLineColor() {
		return lineColor;
	}
	public EdmColor getOnColor() {
		return onColor;
	}
	public EdmColor getOffColor() {
		return offColor;
	}
	
	public final String getControlPv() {
		return controlPv;
	}
	public final String getEndian() {
		return endian;
	}
	
	public int getNumBits() {
		return numBits;
	}
	public int getShift() {
		return shift;
	}
}