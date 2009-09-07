package com.cosylab.vdct.dbd;

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

/**
 * This type was created in VisualAge.
 */
public class DBDFieldData {

	private static String nullString = "";
	
	protected String name;	
	protected int GUI_type = DBDConstants.GUI_UNDEFINED;
	protected int field_type = DBDConstants.NOT_DEFINED;
	protected String init_value = nullString;
	protected String prompt_value = nullString; 	

	// for integer fields
	protected int base_type = DBDConstants.DECIMAL;

	// for DBF_STRINGS fields
	protected int size_value;

	// for DBF_MENU fields
	protected String menu_name;
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @return int
 */
public int getBase_type() {
	return base_type;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @return int
 */
public int getField_type() {
	return field_type;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @return int
 */
public int getGUI_type() {
	return GUI_type;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @return java.lang.String
 */
public java.lang.String getInit_value() {
	return init_value;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @return java.lang.String
 */
public java.lang.String getMenu_name() {
	return menu_name;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @return java.lang.String
 */
public java.lang.String getPrompt_value() {
	return prompt_value;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @return int
 */
public int getSize_value() {
	return size_value;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @param newBase_type int
 */
public void setBase_type(int newBase_type) {
	base_type = newBase_type;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @param newField_type int
 */
public void setField_type(int newField_type) {
	field_type = newField_type;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @param newGUI_type int
 */
public void setGUI_type(int newGUI_type) {
	GUI_type = newGUI_type;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @param newInit_value java.lang.String
 */
public void setInit_value(java.lang.String newInit_value) {
	init_value = newInit_value;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @param newMenu_name java.lang.String
 */
public void setMenu_name(java.lang.String newMenu_name) {
	menu_name = newMenu_name;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @param newPrompt_value java.lang.String
 */
public void setPrompt_value(java.lang.String newPrompt_value) {
	prompt_value = newPrompt_value;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:02)
 * @param newSize_value int
 */
public void setSize_value(int newSize_value) {
	size_value = newSize_value;
}
}
