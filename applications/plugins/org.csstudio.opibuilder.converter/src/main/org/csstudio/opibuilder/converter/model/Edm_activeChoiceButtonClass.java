package org.csstudio.opibuilder.converter.model;

/**
 * @author Lei Hu
 *
 */
public class Edm_activeChoiceButtonClass extends EdmWidget {

	@EdmAttributeAn @EdmOptionalAn private String controlPv;


	public Edm_activeChoiceButtonClass(EdmEntity genericEntity) throws EdmException {
		super(genericEntity);
	}
	

	/**
	 * @return the lineAlarm
	 */
	public final String getControlPv() {
		return controlPv;
	}


}