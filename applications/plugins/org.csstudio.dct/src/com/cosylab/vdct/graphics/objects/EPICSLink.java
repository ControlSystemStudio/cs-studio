package com.cosylab.vdct.graphics.objects;

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

import com.cosylab.vdct.Constants;

/**
 * Insert the type's description here.
 * Creation date: (29.1.2001 21:23:04)
 * @author Matej Sekoranja 
 */
public abstract class EPICSLink extends Field implements Descriptable, Linkable, Rotatable {
	protected int r = Constants.LINK_RADIOUS;
	protected boolean disconnected = false;
	protected int rtailLen = Constants.TAIL_LENGTH;
	private boolean right = true;
/**
 * EPICSLink constructor comment.
 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
 * @param fieldData com.cosylab.vdct.vdb.VDBFieldData
 */
public EPICSLink(ContainerObject parent, com.cosylab.vdct.vdb.VDBFieldData fieldData) {
	super(parent, fieldData);
	setColor(Constants.FRAME_COLOR);
}

/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 19:09:33)
 */
public void destroy() {
	if (!isDestroyed()) {
		super.destroy();
		disconnected = true;
		if (getParent() instanceof Hub)
			((Hub)getParent()).removeLink(this);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 21:23:04)
 */
public void disconnect(Linkable disconnector) {
	disconnected=true;
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 12:07:15)
 * @return java.lang.String
 */
public String getDescription() {
	return getFieldData().getFullName();
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 20:36:36)
 * @return java.lang.String
 */
public String getID() {
	return getFieldData().getFullName();
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 21:23:04)
 * @return java.lang.String
 */
public String getLayerID() {
	return getParent().getParent().toString();
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 21:23:04)
 * @return boolean
 */
public boolean isConnectable() {
	return !disconnected;
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 21:23:04)
 * @return boolean
 */
public boolean isDisconnected() {
	return disconnected;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 * @return boolean
 */
public boolean isRight() {
	return right;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 * @return boolean
 */
public boolean isStaticRight() {
	return right;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 */
public void rotate() { setRight(!right); }
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 17:40:45)
 * @param description java.lang.String
 */
public void setDescription(String description) {}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 21:23:04)
 * @param id java.lang.String
 */
public void setLayerID(String id) {
	// not needed, id is retrieved dynamicaly via parent	
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 * @param state boolean
 */
public void setRight(boolean state) { right=state; }
/**
 * Insert the method's description here.
 * Creation date: (31.1.2001 18:49:28)
 */
public void validate() {
	super.validate();
	double Rscale = getRscale();
	r = (int)(Rscale*Constants.LINK_RADIOUS);
	rtailLen = (int)(Rscale*Constants.TAIL_LENGTH);
}

/// Called after chaning value silently (fixLinks)
public abstract void fixLinkProperties();

}
