package org.csstudio.opibuilder.converter.model;

public class Edm_activeMotifSliderClass extends EdmWidget {
	
	@EdmAttributeAn @EdmOptionalAn private String controlPv;
	@EdmAttributeAn @EdmOptionalAn private EdmColor fgColor;
	@EdmAttributeAn @EdmOptionalAn private EdmColor bgColor;

	public Edm_activeMotifSliderClass(EdmEntity genericEntity) throws EdmException {
		super(genericEntity);
	}

	

	

	/**
	 * @return the lineAlarm
	 */
	public final String getControlPv() {
		return controlPv;
	}	
	public EdmColor getFgColor() {
		return fgColor;
	}
	public EdmColor getBgColor() {
		return bgColor;
	}
}
