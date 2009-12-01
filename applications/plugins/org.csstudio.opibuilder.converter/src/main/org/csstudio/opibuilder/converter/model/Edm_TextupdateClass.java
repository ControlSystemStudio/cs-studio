package org.csstudio.opibuilder.converter.model;

/**
 * Specific class representing TextupdateClass widget.
 *
 * @author Matevz
 *
 */
public class Edm_TextupdateClass extends EdmWidget {

	@EdmAttributeAn private int major;
	@EdmAttributeAn private int minor;
	@EdmAttributeAn private int release;

	@EdmAttributeAn private int x;
	@EdmAttributeAn private int y;
	@EdmAttributeAn private int w;
	@EdmAttributeAn private int h;

	@EdmAttributeAn private String controlPv;

	@EdmAttributeAn private EdmColor fgColor;
	@EdmAttributeAn private EdmColor bgColor;
	@EdmAttributeAn @EdmOptionalAn private boolean fill;

	@EdmAttributeAn private EdmFont font;
	@EdmAttributeAn @EdmOptionalAn private String fontAlign;

	@EdmAttributeAn @EdmOptionalAn private int lineWidth;
	@EdmAttributeAn private boolean lineAlarm;

	@EdmAttributeAn @EdmOptionalAn private boolean fgAlarm;
	
	public Edm_TextupdateClass(EdmEntity genericEntity) throws EdmException {
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

	public EdmFont getFont() {
		return font;
	}

	public EdmColor getFgColor() {
		return fgColor;
	}

	public EdmColor getBgColor() {
		return bgColor;
	}

	public String getControlPv() {
		return controlPv;
	}

	public boolean isFill() {
		return fill;
	}

	public String getFontAlign() {
		return fontAlign;
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public boolean isLineAlarm() {
		return lineAlarm;
	}

	public boolean isFgAlarm() {
		return fgAlarm;
	}
}
