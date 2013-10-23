package org.csstudio.opibuilder.converter.model;

public class Edm_activeMenuButtonClass extends EdmWidget {

	@EdmAttributeAn @EdmOptionalAn private String controlPv;
	@EdmAttributeAn @EdmOptionalAn private EdmColor fgColor;
	@EdmAttributeAn @EdmOptionalAn private EdmColor bgColor;
	@EdmAttributeAn @EdmOptionalAn private boolean fgAlarm;

	public Edm_activeMenuButtonClass(EdmEntity genericEntity) throws EdmException {
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
	public boolean isFgAlarm(){
		return fgAlarm;
	}	


}