package org.csstudio.opibuilder.converter.model;

/**
 * @author Lei Hu
 *
 */
public class Edm_activeMotifSliderClass extends EdmWidget {
	
	@EdmAttributeAn @EdmOptionalAn private String controlPv;

	public Edm_activeMotifSliderClass(EdmEntity genericEntity) throws EdmException {
		super(genericEntity);
	}

	
	/**
	 * @return the lineAlarm
	 */
	public final String getControlPv() {
		return controlPv;
	}	

}
