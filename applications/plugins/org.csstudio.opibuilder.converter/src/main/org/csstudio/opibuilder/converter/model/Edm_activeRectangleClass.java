package org.csstudio.opibuilder.converter.model;

/**
 * Specific class representing activeRectangleClass widget.
 *
 * @author Matevz
 *
 */
public class Edm_activeRectangleClass extends EdmWidget {

	@EdmAttributeAn private int major;
	@EdmAttributeAn private int minor;
	@EdmAttributeAn private int release;

	@EdmAttributeAn private int x;
	@EdmAttributeAn private int y;
	@EdmAttributeAn private int w;
	@EdmAttributeAn private int h;

	@EdmAttributeAn private EdmColor lineColor;
	@EdmAttributeAn @EdmOptionalAn private EdmInt lineWidth;
	@EdmAttributeAn @EdmOptionalAn private EdmLineStyle lineStyle;
	
	@EdmAttributeAn @EdmOptionalAn private EdmColor fillColor;

	public Edm_activeRectangleClass(EdmEntity genericEntity) throws EdmException {
		super(genericEntity);
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getRelease() {
		return release;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getW() {
		return w;
	}

	public int getH() {
		return h;
	}

	public EdmColor getLineColor() {
		return lineColor;
	}

	public EdmInt getLineWidth() {
		return lineWidth;
	}

	public EdmLineStyle getLineStyle() {
		return lineStyle;
	}

	public EdmColor getFillColor() {
		return fillColor;
	}
}
