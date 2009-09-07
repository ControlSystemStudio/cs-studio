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

import java.util.*;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * Insert the type's description here.
 * Creation date: (29.1.2001 21:26:39)
 * @author Matej Sekoranja
 */
public class EPICSFwdLink extends EPICSOutLink {

/**
 * EPICSFWDLink constructor comment.
 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
 * @param fieldData com.cosylab.vdct.vdb.VDBFieldData
 */
public EPICSFwdLink(ContainerObject parent, com.cosylab.vdct.vdb.VDBFieldData fieldData) {
	super(parent, fieldData);
	getLinkProperties().setProcess("PP");
}

/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 11:23:59)
 * @return java.util.Vector
 */
public java.util.Vector getItems() {
	Vector items = super.getItems();

	// FWD has only CA and PP
	Enumeration e = items.elements();
	while (e.hasMoreElements())
	{
		Object obj = e.nextElement();
		if (obj instanceof JMenu)
		{
			JMenu menu = (JMenu)obj;
			if (menu.getText().equals(processString))
			{
				for (int i=0; i<menu.getItemCount(); i++)
					{
						JMenuItem menuItem = menu.getItem(i);
						if (!menuItem.getText().equals(caString) &&
							!menuItem.getText().equals(ppString))
							{ menu.remove(i); i--; }
					}
				}
			break;
		}
	}
	

	return items;
}

}
