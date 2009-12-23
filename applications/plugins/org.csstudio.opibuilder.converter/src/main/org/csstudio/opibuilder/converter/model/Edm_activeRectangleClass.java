package org.csstudio.opibuilder.converter.model;

/**
 * Specific class representing activeRectangleClass widget.
 *
 * @author Matevz
 *
 */
public class Edm_activeRectangleClass extends EdmWidget {

	
	@EdmAttributeAn private EdmColor lineColor;
	
	@EdmAttributeAn @EdmOptionalAn private int lineWidth;
	@EdmAttributeAn @EdmOptionalAn private EdmLineStyle lineStyle;	
	@EdmAttributeAn @EdmOptionalAn private EdmColor fillColor;
	@EdmAttributeAn @EdmOptionalAn private boolean fill;
	@EdmAttributeAn @EdmOptionalAn private boolean invisible;
	@EdmAttributeAn @EdmOptionalAn private boolean lineAlarm;
	@EdmAttributeAn @EdmOptionalAn private boolean fillAlarm;
	@EdmAttributeAn @EdmOptionalAn private String alarmPv;
	@EdmAttributeAn @EdmOptionalAn private String visPv;
	@EdmAttributeAn @EdmOptionalAn private double visMax;
	@EdmAttributeAn @EdmOptionalAn private double visMin;
	@EdmAttributeAn @EdmOptionalAn private boolean visInvert;

	public Edm_activeRectangleClass(EdmEntity genericEntity) throws EdmException {
		super(genericEntity);
	}

	

	

	/**
	 * @return the lineAlarm
	 */
	public final boolean isLineAlarm() {
		return lineAlarm;
	}





	/**
	 * @return the fillAlarm
	 */
	public final boolean isFillAlarm() {
		return fillAlarm;
	}





	/**
	 * @return the alarmPv
	 */
	public final String getAlarmPv() {
		return alarmPv;
	}





	public EdmColor getLineColor() {
		return lineColor;
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public EdmLineStyle getLineStyle() {
		return lineStyle;
	}

	public EdmColor getFillColor() {
		return fillColor;
	}

	public boolean isInvisible() {
		return invisible;
	}

	public boolean isFill(){
		return fill;
	}
	
	
	public String getVisPv() {
		return visPv;
	}

	public double getVisMax() {
		return visMax;
	}

	public double getVisMin() {
		return visMin;
	}

	public boolean isVisInvert() {
		return visInvert;
	}
}
