package com.cosylab.vdct.plugin.popup;

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

import java.util.Vector;

import com.cosylab.vdct.plugin.Plugin;

/**
 * Insert the type's description here.
 * Creation date: (8.12.2001 12:43:54)
 * @author Matej Sekoranja 
 */
public interface ContextPopupPlugin extends Plugin {

/**
 * 
 * This metod is called each time request of popup menu is gived to VisualDCT.
 * PluginPopupManager then queries all popup plugins, asking them to return list of menus
 * or menu items to be added to the default list of popup items.
 * Plugin can also return null value, if there is no action available to the
 * given list of selected objects.
 * Creation date: (8.12.2001 12:45:31)
 * @param selectedObjects List of selected elements. All elements inhereit from
 * 						   com.cosylab.vdct.graphics.objects.Selectable interface. Typical there are objects:
 * <ul>
 * 	<li><b>com.cosylab.vdct.graphics.objects.Group</b> - group
 * 	<li><b>com.cosylab.vdct.graphics.objects.Record</b> - record
 * 	<li><b>com.cosylab.vdct.graphics.objects.Template</b> - template instance
 * 	<li><b>com.cosylab.vdct.graphics.objects.Connector</b> - connector
 * 	<li><b>...</b>
 * </ul>
 * If list is <code>null</code>, then popup over empty workspace is issued.
 * @return java.util.Vector list of <code>javax.swing.JMenuItems</code> and/or <code>javax.swing.JMenu</code> and/or <code>javax.swing.JSeparator</code> objects
 * 							 to be added to the default list of popup items. Can also be null.
 */
public Vector getItems(Vector selectedObjects);

}
