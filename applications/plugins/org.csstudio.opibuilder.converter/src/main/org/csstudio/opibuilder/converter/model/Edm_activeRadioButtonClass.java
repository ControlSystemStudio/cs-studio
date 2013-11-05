package org.csstudio.opibuilder.converter.model;

public class Edm_activeRadioButtonClass extends EdmWidget {

	@EdmAttributeAn @EdmOptionalAn private String controlPv;


	public Edm_activeRadioButtonClass(EdmEntity genericEntity) throws EdmException {
		super(genericEntity);
	}
	

	/**
	 * @return the lineAlarm
	 */
	public final String getControlPv() {
		return controlPv;
	}



}