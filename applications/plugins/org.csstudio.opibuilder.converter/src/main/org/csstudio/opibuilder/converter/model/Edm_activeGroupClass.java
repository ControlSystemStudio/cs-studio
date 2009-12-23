package org.csstudio.opibuilder.converter.model;

import java.util.Vector;

/**
 * Specific class representing activeGroupClass widget.
 *
 * @author Matevz
 *
 */
public class Edm_activeGroupClass extends EdmWidget {

//	@EdmAttributeAn private int major;
//	@EdmAttributeAn private int minor;
//	@EdmAttributeAn private int release;

//	@EdmAttributeAn private int x;
//	@EdmAttributeAn private int y;
//	@EdmAttributeAn private int w;
//	@EdmAttributeAn private int h;

	@EdmAttributeAn @EdmOptionalAn private String visPv;
	@EdmAttributeAn @EdmOptionalAn private double visMax;
	@EdmAttributeAn @EdmOptionalAn private double visMin;
	@EdmAttributeAn @EdmOptionalAn private boolean visInvert;
	
	@EdmAttributeAn private Vector<EdmWidget> widgets;

	public Edm_activeGroupClass(EdmEntity genericEntity) throws EdmException {
		super(genericEntity);
	}

//	public int getMajor() {
//		return major;
//	}
//
//	public int getMinor() {
//		return minor;
//	}
//
//	public int getRelease() {
//		return release;
//	}

	

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

	public Vector<EdmWidget> getWidgets() {
		return widgets;
	}
}
