package org.csstudio.opibuilder.converter.model;

/**
 * @author Lei Hu
 *
 */
public class Edm_activeMeterClass extends EdmWidget {

	@EdmAttributeAn @EdmOptionalAn private EdmColor scaleColor;
	@EdmAttributeAn @EdmOptionalAn private EdmColor labelColor;
	@EdmAttributeAn @EdmOptionalAn private String readPv;
	
	@EdmAttributeAn @EdmOptionalAn private boolean showScale;
	
	@EdmAttributeAn @EdmOptionalAn private boolean scaleAlarm;

	public Edm_activeMeterClass(EdmEntity genericEntity) throws EdmException {
		super(genericEntity);
	}
	

	public final String getReadPv() {
		return readPv;
	}	

	public boolean isShowScale(){
		return showScale;
	}
	
	public boolean isScaleAlarm() {
		return scaleAlarm;
	}
	
	public EdmColor getScaleColor() {
		return scaleColor;
	}
	public EdmColor getLabelColor() {
		return labelColor;
	}
}