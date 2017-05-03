package org.csstudio.opibuilder.converter.model;

public class Edm_activeButtonClass extends EdmWidget {

	
	@EdmAttributeAn @EdmOptionalAn private EdmColor fgColor;
	@EdmAttributeAn @EdmOptionalAn private EdmColor onColor;
	@EdmAttributeAn @EdmOptionalAn private EdmColor offColor;
	@EdmAttributeAn @EdmOptionalAn private EdmColor inconsistentColor;
	@EdmAttributeAn @EdmOptionalAn private EdmColor topShadowColor;
	@EdmAttributeAn @EdmOptionalAn private EdmColor botShadowColor;
	
	@EdmAttributeAn @EdmOptionalAn private String controlPv;
	@EdmAttributeAn @EdmOptionalAn private String visPv;
	@EdmAttributeAn @EdmOptionalAn private double visMax;
	@EdmAttributeAn @EdmOptionalAn private double visMin;	
	@EdmAttributeAn @EdmOptionalAn private String onLabel;
	@EdmAttributeAn @EdmOptionalAn private String offLabel;
	

	public Edm_activeButtonClass(EdmEntity genericEntity) throws EdmException {
		super(genericEntity);
	}

	

	

	/**
	 * @return the lineAlarm
	 */
	public EdmColor getFgColor() {
		return fgColor;
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
	public final String getVisPv() {
		return visPv;
	}	
	public final String getOnLabel() {
		return onLabel;
	}	
	public final String getOffLabel() {
		return offLabel;
	}	


	public double getVisMax() {
		return visMax;
	}

	public double getVisMin() {
		return visMin;
	}

}