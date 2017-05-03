package org.csstudio.opibuilder.converter.model;

public class Edm_activeXTextDspClass extends EdmWidget {

	@EdmAttributeAn @EdmOptionalAn private String controlPv;
	@EdmAttributeAn @EdmOptionalAn private EdmColor fgColor;
	@EdmAttributeAn @EdmOptionalAn private EdmColor bgColor;

	public Edm_activeXTextDspClass(EdmEntity genericEntity) throws EdmException {
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