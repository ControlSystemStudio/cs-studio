package org.csstudio.opibuilder.converter.model;

/**
 * Specific class representing activeXTextClass widget.
 *
 * @author Matevz
 *
 */
public class Edm_activeXTextClass extends EdmWidget {

	@EdmAttributeAn private int major;
	@EdmAttributeAn private int minor;
	@EdmAttributeAn private int release;

	@EdmAttributeAn private int x;
	@EdmAttributeAn private int y;
	@EdmAttributeAn private int w;
	@EdmAttributeAn private int h;

	@EdmAttributeAn private EdmFont font;

	@EdmAttributeAn private EdmColor fgColor;
	@EdmAttributeAn private EdmColor bgColor;

	@EdmAttributeAn private String value;
	@EdmAttributeAn @EdmOptionalAn private boolean autoSize;

	@EdmAttributeAn @EdmOptionalAn private EdmInt lineWidth;
	@EdmAttributeAn @EdmOptionalAn private boolean border;
	@EdmAttributeAn @EdmOptionalAn private boolean useDisplayBg;
	
	public Edm_activeXTextClass(EdmEntity genericEntity) throws EdmException {
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

	public String getValue() {
		return value;
	}

	public boolean isAutoSize() {
		return autoSize;
	}

	public EdmInt getLineWidth() {
		return lineWidth;
	}

	public boolean isBorder() {
		return border;
	}

	public boolean isUseDisplayBg() {
		return useDisplayBg;
	}
}
