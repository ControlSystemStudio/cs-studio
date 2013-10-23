package org.csstudio.opibuilder.converter.model;

public class Edm_activeBarClass extends EdmWidget {
	
	@EdmAttributeAn @EdmOptionalAn private String indicatorPv;
	@EdmAttributeAn @EdmOptionalAn private EdmColor indicatorColor;
	@EdmAttributeAn @EdmOptionalAn private String orientation;
	

	public Edm_activeBarClass(EdmEntity genericEntity) throws EdmException {
		super(genericEntity);
	}

	

	

	/**
	 * @return the lineAlarm
	 */
	public final String getIndicatorPv() {
		return indicatorPv;
	}	
	public EdmColor getIndicatorColor() {
		return indicatorColor;
	}
	public final String getOrientation() {
		return orientation;
	}	
}
