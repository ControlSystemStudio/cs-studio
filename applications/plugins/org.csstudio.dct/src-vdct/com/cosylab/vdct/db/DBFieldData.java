package com.cosylab.vdct.db;

/**
 * Copyright (c) 2002, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution. 
 * Neither the name of the Cosylab, Ltd., Control System Laboratory nor the names
 * of its contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.cosylab.vdct.inspector.InspectableProperty;

/**
 * This type was created in VisualAge.
 */
public class DBFieldData extends DBComment {
	protected String name;	
	protected String value;
	protected boolean template_def = false;
	
	protected java.awt.Color color = java.awt.Color.black;
	protected boolean rotated;

	private static final String nullString = "";
	protected String description = nullString;

	protected boolean hasAdditionalData = false;

	protected int visibility = InspectableProperty.NON_DEFAULT_VISIBLE;       // = 0
	
/**
 * FieldData constructor comment.
 */
public DBFieldData(String name, String value) {
	this.name=name;
	this.value=value;
}
/**
 * FieldData constructor comment.
 */
public DBFieldData(String name, String value, java.awt.Color color, boolean rotated, String description, int visibility) {
	this.name=name;
	this.value=value;
	this.color=color;
	this.rotated=rotated;
	this.description=description;
	this.visibility=visibility;
	this.hasAdditionalData=true;
}
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 15:54:01)
 * @return java.awt.Color
 */
public java.awt.Color getColor() {
	return color;
}
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 15:54:01)
 * @return java.lang.String
 */
public java.lang.String getDescription() {
	return description;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:12:01)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:12:01)
 * @return java.lang.String
 */
public java.lang.String getValue() {
	return value;
}
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 18:39:06)
 * @return boolean
 */
public boolean isHasAdditionalData() {
	return hasAdditionalData;
}
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 15:54:01)
 * @return boolean
 */
public boolean isRotated() {
	return rotated;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:12:01)
 * @return boolean
 */
public boolean isTemplate_def() {
	return template_def;
}
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 15:54:01)
 * @param newColor java.awt.Color
 */
public void setColor(java.awt.Color newColor) {
	this.hasAdditionalData=true;
	color = newColor;
}
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 15:54:01)
 * @param newDescription java.lang.String
 */
public void setDescription(java.lang.String newDescription) {
	this.hasAdditionalData=true;
	description = newDescription;
}
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 18:39:06)
 * @param newHasAdditionalData boolean
 */
public void setHasAdditionalData(boolean newHasAdditionalData) {
	hasAdditionalData = newHasAdditionalData;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:12:01)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 15:54:01)
 * @param newRotated boolean
 */
public void setRotated(boolean newRotated) {
	this.hasAdditionalData=true;
	rotated = newRotated;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:12:01)
 * @param newTemplate_def boolean
 */
public void setTemplate_def(boolean newTemplate_def) {
	template_def = newTemplate_def;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:12:01)
 * @param newValue java.lang.String
 */
public void setValue(java.lang.String newValue) {
	value = newValue;
}
/**
 * Returns the visibility.
 * @return int
 */
public int getVisibility()
{
	return visibility;
}

/**
 * Sets the visibility.
 * @param visibility The visibility to set
 */
public void setVisibility(int visibility)
{
	this.visibility = visibility;
}

}
