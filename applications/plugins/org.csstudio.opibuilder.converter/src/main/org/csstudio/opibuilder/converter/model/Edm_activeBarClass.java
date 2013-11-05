package org.csstudio.opibuilder.converter.model;

/**
 * @author Lei Hu
 *
 */
public class Edm_activeBarClass extends EdmWidget {
	
	@EdmAttributeAn @EdmOptionalAn private String indicatorPv;
	@EdmAttributeAn @EdmOptionalAn private EdmColor indicatorColor;
	@EdmAttributeAn @EdmOptionalAn private String orientation;
	@EdmAttributeAn @EdmOptionalAn private boolean showScale;

	public Edm_activeBarClass(EdmEntity genericEntity) throws EdmException {
		super(genericEntity);
	}

	

	public boolean isShowScale() {
		return showScale;
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
