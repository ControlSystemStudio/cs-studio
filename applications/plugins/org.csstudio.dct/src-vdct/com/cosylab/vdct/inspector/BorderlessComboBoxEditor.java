package com.cosylab.vdct.inspector;

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

import javax.swing.*;

/**
 * Insert the type's description here.
 * Creation date: (26.1.2001 21:00:33)
 * @author Matej Sekoranja
 */
public class BorderlessComboBoxEditor extends JTextField implements ComboBoxEditor {
/**
 * BoerderlessComboBoxEditor constructor comment.
 */
public BorderlessComboBoxEditor() {
	setBorder(null);
	setColumns(9);
}
 /** Return the component that should be added to the tree hierarchy for
	* this editor
	*/
public java.awt.Component getEditorComponent() {
	return this;
}
 /** Return the edited item **/
public Object getItem() {
	return getText();
}
 /** Ask the editor to start editing and to select everything **/
public void selectAll() {
	selectAll();
	requestFocus();
}
 /** Set the item that should be edited. Cancel any editing if necessary **/
public void setItem(Object anObject) {
	if ( anObject != null )
		setText(anObject.toString());
	else
		setText("");
}
}
