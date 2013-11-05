package org.csstudio.opibuilder.converter.model;

public class Edm_activeSliderClass extends EdmWidget {
	
	@EdmAttributeAn @EdmOptionalAn private String controlPv;

	public Edm_activeSliderClass(EdmEntity genericEntity) throws EdmException {
		super(genericEntity);
	}

	

	/**
	 * @return the lineAlarm
	 */
	public final String getControlPv() {
		return controlPv;
	}	

}
