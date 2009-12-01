package org.csstudio.opibuilder.converter.model;

import java.util.Vector;

/**
 * Specific class representing activeGroupClass widget.
 *
 * @author Matevz
 *
 */
public class Edm_activeGroupClass extends EdmWidget {

	@EdmAttributeAn private int major;
	@EdmAttributeAn private int minor;
	@EdmAttributeAn private int release;

	@EdmAttributeAn private int x;
	@EdmAttributeAn private int y;
	@EdmAttributeAn private int w;
	@EdmAttributeAn private int h;

	@EdmAttributeAn private Vector<EdmWidget> widgets;

	public Edm_activeGroupClass(EdmEntity genericEntity) throws EdmException {
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

	public Vector<EdmWidget> getWidgets() {
		return widgets;
	}
}
