package com.cosylab.vdct.undo;

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

import com.cosylab.vdct.graphics.objects.Template;
import com.cosylab.vdct.inspector.InspectorManager;

/**
 * Insert the type's description here.
 * Creation date: (3.5.2001 16:26:04)
 * @author 
 */
public class ChangeTemplatePropertyAction extends ActionObject {
	protected Template object;
	protected String name;
	protected String oldValue;
	protected String newValue;

	//private static final String nullString = "";	
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 16:27:58)
 */
public ChangeTemplatePropertyAction(Template object, String name, String oldValue, String newValue) {
	this.object=object;
	this.name=name;
	this.oldValue=oldValue;
	this.newValue=newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 16:26:04)
 * @return java.lang.String
 */
public String getDescription() {
	return "Change Template Property ["+object+"]("+name+")";
}
/**
 * This method was created in VisualAge.
 */
protected void redoAction() {
	// just override value
	object.getTemplateData().getProperties().put(name, oldValue);

	object.updateTemplateFields();
	InspectorManager.getInstance().updateObject(object);
	object.unconditionalValidation();
}
/**
 * This method was created in VisualAge.
 */
protected void undoAction() {
	// just override value
	object.getTemplateData().getProperties().put(name, newValue);

	object.updateTemplateFields();
	InspectorManager.getInstance().updateObject(object);
	object.unconditionalValidation();
}
}
