package com.cosylab.vdct.about;

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
 * Creation date: (29.5.2002 16:05:11)
 * @author: 
 */
public abstract class AboutDialogEngine {

	protected java.util.ArrayList tabs = new java.util.ArrayList();
	protected Object aboutedObject = null;
	protected AboutTabReceiver receiver = null;

public AboutDialogEngine()
{
	super();
}

/**
 * Default constructor with "abouted object" as parameter
 */
public AboutDialogEngine(Object toAbout) {
	super();
	setAboutedObject(toAbout);
	initializeReceiver();
	perform();
	
}
/**
 * This method is intended for adding Tab object into tabs ArrayList.
 * Creation date: (29.5.2002 16:43:05)
 * @param newTabs java.util.ArrayList
 */
public void addAboutTab(AboutTab newTab) {

	
	
	getTabs().add(newTab);
}
/**
 * Accessor for aboutedObject field.
 * Creation date: (29.5.2002 16:50:19)
 * @return java.lang.Object
 */
public java.lang.Object getAboutedObject() {
	return aboutedObject;
}
/**
 * Accessor for tabs field.
 * Creation date: (29.5.2002 16:43:05)
 * @return java.util.ArrayList
 */
public java.util.List getTabs() {
	return tabs;
}
/**
 * Insert the method's description here.
 * Creation date: (3.6.2002 16:41:58)
 */
protected abstract void  initializeReceiver();
/**
 * Insert the method's description here.
 * Creation date: (29.5.2002 18:52:58)
 */
protected abstract void perform() ;
	/**
	 * Returns the receiver.
	 * @return AboutTabReceiver
	 */
	public AboutTabReceiver getReceiver() {
		return receiver;
	}

	/**
	 * Sets the aboutedObject.
	 * @param aboutedObject The aboutedObject to set
	 */
	public void setAboutedObject(Object aboutedObject) {
		this.aboutedObject = aboutedObject;
	}
		public void triggerReceiver(){
	receiver.receiverPerform();
	
	}

	protected void arrangeTabs(){
	
	
	    for (int i = 0; i < tabs.size(); i++) {

        AboutTab at = (AboutTab) tabs.get(i);

		receiver.addAboutTab(at);
		
	    }
	}
}
