package org.csstudio.opibuilder.converter.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Specific class containing a list of EdmColor instances, parsed from
 * EdmColorsList file.
 * 
 * @author Matevz
 *
 */
public class EdmColorsList extends EdmEntity {

	private Map<Integer, EdmColor> colorsMap;
	
	/**
	 * Constructor which parses EdmColors from general EdmEntity interface.
	 * @param copy EdmEntity containing EdmColorsList data.
	 * @throws EdmException if an error occurs when parsing EdmColor data.
	 */
	public EdmColorsList(EdmEntity copy) throws EdmException {
		super(copy);
		colorsMap = new HashMap<Integer, EdmColor>();
		
		getSpecificColors(copy);
	}

	/**
	 * Method iterates through EdmEntity attributes and specializes the data into
	 * EdmColor instances.
	 * @param genericColors EdmEntity containing EdmColorsList data.
	 * @throws EdmException if any of EdmAttributes contain invalid EdmColor data.
	 */
	private void getSpecificColors(EdmEntity genericColors) throws EdmException {
		// parse subentities of generic ColorsList EdmEntity; 
		// each subentity should be generic EdmColor
		for (String id : genericColors.getAttributeIdSet()) {
			int index = Integer.parseInt(id);
			EdmColor c = new EdmColor(genericColors.getAttribute(id), true);
			colorsMap.put(index, c);
		}
	}

	public int getCount() {
		return colorsMap.size();
	}

	/**
	 * Returns static color with specified index.
	 * @param index Index of static color.
	 * @return Static color with specified index.
	 */
	public EdmColor getColor(int index) {
		return colorsMap.get(index);
	}

	/**
	 * Maps static color with specified index.
	 * @param index Index of static color.
	 * @param c Static EdmColor.
	 */
	public void addColor(int index, EdmColor c) {
		colorsMap.put(index, c);
	}
}