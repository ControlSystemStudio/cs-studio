package com.cosylab.vdct.undo;

import com.cosylab.vdct.Constants;
import com.cosylab.vdct.graphics.objects.ContainerObject;
import com.cosylab.vdct.graphics.objects.InLink;
import com.cosylab.vdct.graphics.objects.OutLink;


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
 * Insert the type's description here.
 * Creation date: (3.5.2001 16:26:04)
 * @author 
 */
public class DeleteConnectorAction extends ActionObject {
	protected com.cosylab.vdct.graphics.objects.Connector object;
	protected com.cosylab.vdct.graphics.objects.ContainerObject parent;
	protected int x, y;
	protected String inlinkStr;
	protected String outlinkStr;
	private String nullString="";
	 
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 16:27:58)
 * @param object com.cosylab.vdct.graphics.objects.VisibleObject
 * @param x int
 * @param y int
 */
public DeleteConnectorAction(com.cosylab.vdct.graphics.objects.Connector object, String inlink, String outlink) {
	this.object=object;
	this.x=object.getX(); this.y=object.getY();
	this.parent=object.getParent();
	this.inlinkStr=inlink;
	this.outlinkStr=outlink;
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 16:26:04)
 * @return java.lang.String
 */
public String getDescription() {
	return "DeleteConnector ["+object+"]("+x+", "+y+")";
}
/**
 * This method was created in VisualAge.
 */

public Object getSubObject(String str) {
	int pos = str.indexOf(Constants.FIELD_SEPARATOR);
	if (str.equals(nullString)) return null;
	
	ContainerObject group = (ContainerObject)parent.getParent();
	
	Object obj=null;
	
	if (pos>-1) {
		String record = str.substring(0,pos);
		String field = str.substring(pos+1);
			
		obj = (group.getSubObject(record));
		if (obj instanceof ContainerObject)
			((ContainerObject)obj).getSubObject(field);
	}
	if (obj==null) obj = parent.getSubObject(str);
	if (obj==null) obj = group.getSubObject(str);
	
	return obj;
}

protected void undoAction() {
	//object.setInput(inlink);
//	inlink.setOutput(object,null);
	
	InLink inlink = (InLink)getSubObject(inlinkStr);

	Object obj = getSubObject(outlinkStr);
	OutLink outlink;
	if (obj instanceof OutLink)
	    outlink = (OutLink)getSubObject(outlinkStr);
	else {
	    return;
	}
	
	object.setInput(inlink);
	if (inlink!=null) inlink.setOutput(object, outlink);
	
	object.setOutput(outlink,null);
	if (outlink!=null) outlink.setInput(object);
	
	//object.setOutput(inlink, null);
	
	parent.addSubObject(object.getHashID(), object);
	object.setDestroyed(false);
}
/**
 * This method was created in VisualAge.
 */
protected void redoAction() {
	InLink il = object.getInput();
	OutLink outlink = object.getOutput();
	if (il!=null) il.setOutput(outlink, object);
	if (outlink!=null) outlink.setInput(il);
	
	object.destroy();
}
}
