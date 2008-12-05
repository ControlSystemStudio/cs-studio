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

import java.util.Enumeration;
import java.util.Vector;

import com.cosylab.vdct.vdb.LinkProperties;
import com.cosylab.vdct.vdb.VDBFieldData;

/**
 * Adds InLink capability (for ports) to EPICSLinkOut objects
 * @author Matej
 */
public abstract class EPICSLinkOutIn extends EPICSLinkOut implements MultiInLink
{

	// list of all outlink connected to his inlink
	protected Vector outlinks = null;

	/**
	 * Constructor for EPICSLinkOutIn.
	 * @param parent
	 * @param fieldData
	 */
	public EPICSLinkOutIn(ContainerObject parent, VDBFieldData fieldData)
	{
		super(parent, fieldData);
	}

	/**
	 * Extra lasy initalization pattern for <code>outlinks</code> object.
	 */
	public Vector getOutlinks()
	{
		if (outlinks==null)
			outlinks = new Vector();
		return outlinks;
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.MultiInLink#getLinkCount()
	 */
	public int getLinkCount()
	{
		if (outlinks==null)
			return 0;
		else
			return outlinks.size();
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.InLink#getInX()
	 */
	public int getInX()
	{
		return getOutX();
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.InLink#getInY()
	 */
	public int getInY()
	{
		return getOutY();
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.InLink#getOutput()
	 */
	public OutLink getOutput()
	{
		if (outlinks==null)
			return null;
		else if (outlinks.size()==1)
			return (OutLink)outlinks.firstElement();
		else
			return null;
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.InLink#setOutput(OutLink, OutLink)
	 */
	public void setOutput(OutLink output, OutLink prevOutput)
	{
		// create if necessary
		Vector outlinks = getOutlinks();
		
		if (prevOutput!=null) outlinks.removeElement(prevOutput);
		if (!outlinks.contains(output)) {
			outlinks.addElement(output);
			//?if (outlinks.size()>0) disconnected=false;
		}
	}


	/**
	 */
	public void valueWithNoRecord()
	{
		if (isDestroyed()) return;
		
		if (outlinks!=null && outlinks.size()>0)
		{
			setDestroyed(true);
			destroyChain(inlink, this);
			setInput(null);
			getFieldData().setValue("");
			properties = new LinkProperties(fieldData);
			setDestroyed(false);
		}
		else
			destroy();
	}

	/**
	 */
	public void destroy() {
		if (!isDestroyed()) {
			super.destroy();
			if (outlinks!=null && outlinks.size()>0) {
				Object[] objs = new Object[outlinks.size()];
				outlinks.copyInto(objs);
				for(int i=0; i<objs.length; i++) {
					OutLink outlink = (OutLink)objs[i];
					OutLink start = EPICSLinkOut.getStartPoint(outlink);
					if (start!=null)
						start.disconnect(this);
					else 
						outlink.disconnect(this);
				}
				outlinks.clear();
			}
		}
		
	}
	/**
	 */
	public void disconnect(Linkable disconnector) {
		if (!disconnected && outlinks!=null && outlinks.contains(disconnector)) {
			outlinks.removeElement(disconnector);
			super.disconnect(disconnector);

			if (inlink==null && outlinks.size()==0) 
				destroy();
		}
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.EPICSLinkOut#sourceDestroyed()
	 */
	public void sourceDestroyed()
	{
		if (outlinks!=null && outlinks.size()>0)
			getFieldData().setValue("");
		else
			destroy();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (30.1.2001 16:58:58)
	 * @return boolean
	 */
	public boolean isRight() {
		// if there is a special case with ports/macros handle it
		if ((disconnected || inlink==null ||
			!inlink.getLayerID().equals(getLayerID())) &&
			outlinks!=null && outlinks.size()==1) 
		{
				OutLink outlink = (OutLink)outlinks.firstElement();
				return getRightX()<=outlink.getLeftX()
						  || (outlink.getLeftX()<getLeftX() && getLeftX() < outlink.getRightX() && outlink.getRightX() < getRightX());				
				/* (outlink instanceof Connector) {	
					return (outlink.getOutX()>(getX()+getWidth()/2));
				}
				else if (outlink instanceof EPICSLinkOut) {			// not cycling
						EPICSLinkOut obj = (EPICSLinkOut)outlink;
						return getMaxX()<obj.getMinX() ||
							(getMaxX()>obj.getMaxX() && getMinX()<obj.getMaxX());
				}
				else if (outlink instanceof VisibleObject) {			// do not cycle !!!
					VisibleObject obj = (VisibleObject)outlink;
					return ((obj.getX()+obj.getWidth()/2)>(getX()+getWidth()/2));
				}
				else 
					return super.isRight();*/
		}
		else 
			return super.isRight();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (5.2.2001 12:10:18)
	 * @return java.util.Vector
	 */
	public Vector getStartPoints() {
		OutLink out;
		Vector starts = new Vector();
		if (outlinks!=null)
		{
			Enumeration e = outlinks.elements();
			while (e.hasMoreElements()) {
				out = EPICSLinkOut.getStartPoint((Linkable)e.nextElement());
				if (out!=null) starts.addElement(out);
			}
		}
		return starts;
	}
	
	public int getLeftOffset() {
	    if (isRight()) return 0;
	    return Math.abs(2*r+labelLen);
	    
	}

	public int getRightOffset() {
	    if (!isRight()) return 0;
	    
	    return Math.abs(2*r + labelLen);
	}

}
