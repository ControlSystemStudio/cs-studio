package org.csstudio.opibuilder.converter.model;

public class Edm_activeMeterClass extends EdmWidget {

	@EdmAttributeAn @EdmOptionalAn private EdmColor fgColor;
	@EdmAttributeAn @EdmOptionalAn private EdmColor bgColor;
	@EdmAttributeAn @EdmOptionalAn private String readPv;
	
	@EdmAttributeAn @EdmOptionalAn private boolean showScale;

	public Edm_activeMeterClass(EdmEntity genericEntity) throws EdmException {
		super(genericEntity);
	}
	

	/**
	 * @return the lineAlarm
	 */
	public EdmColor getFgColor() {
		return fgColor;
	}
	public EdmColor getBgColor() {
		return bgColor;
	}
	public final String getReadPv() {
		return readPv;
	}	

	public boolean isShowScale(){
		return showScale;
	}
}