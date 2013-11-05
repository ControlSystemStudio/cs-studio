package org.csstudio.opibuilder.converter.model;

/**
 * @author Lei Hu
 *
 */
public class Edm_activeButtonClass extends EdmWidget {

	
	@EdmAttributeAn @EdmOptionalAn private EdmColor onColor;
	@EdmAttributeAn @EdmOptionalAn private EdmColor offColor;
	@EdmAttributeAn @EdmOptionalAn private EdmColor inconsistentColor;
	@EdmAttributeAn @EdmOptionalAn private EdmColor topShadowColor;
	@EdmAttributeAn @EdmOptionalAn private EdmColor botShadowColor;
	
	@EdmAttributeAn @EdmOptionalAn private String controlPv;
	@EdmAttributeAn @EdmOptionalAn private String onLabel;
	@EdmAttributeAn @EdmOptionalAn private String offLabel;
	

	public Edm_activeButtonClass(EdmEntity genericEntity) throws EdmException {
		super(genericEntity);
	}

	
	public EdmColor getOnColor() {
		return onColor;
	}
	public EdmColor getOffColor() {
		return offColor;
	}
	public EdmColor getInconsistentColor() {
		return inconsistentColor;
	}
	public EdmColor getTopShadowColor() {
		return topShadowColor;
	}
	public EdmColor getBotShadowColor() {
		return botShadowColor;
	}

	public final String getControlPv() {
		return controlPv;
	}	

	public final String getOnLabel() {
		return onLabel;
	}	
	public final String getOffLabel() {
		return offLabel;
	}	


}