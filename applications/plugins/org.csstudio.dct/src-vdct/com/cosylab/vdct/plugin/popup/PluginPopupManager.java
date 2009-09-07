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

import java.util.*;
import java.beans.*;

import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import com.cosylab.vdct.Console;
import com.cosylab.vdct.plugin.*;

/**
 * Creation date: (7.12.2001 13:57:49)
 * @author Matej Sekoranja
 */
public final class PluginPopupManager implements PluginListener, PropertyChangeListener
{
    private static PluginPopupManager instance = null;

    private LinkedList list = null;
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 14:00:41)
 */
protected PluginPopupManager()
{
	list = new LinkedList();
		
	PluginManager.getInstance().addPluginListener(this);
}
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 14:01:03)
 * @return com.cosylab.vdct.plugin.PluginExportManager
 */
public static PluginPopupManager getInstance() {
	if (instance==null) instance = new PluginPopupManager();
	return instance;
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:23:57)
 * @param
 * @return
 */
public void pluginAdded(PluginObject plugin)
{
	if (plugin.getPlugin() instanceof ContextPopupPlugin)
	{
		if (!list.contains(plugin))
		{
			list.add(plugin);
			plugin.addPropertyChangeListener(this);
			com.cosylab.vdct.Console.getInstance().println(plugin.getName()+" is registered as popup menu plugin.");
		}

	}
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:23:57)
 * @param
 * @return
 */
public void pluginRemoved(PluginObject plugin)
{
	if (plugin.getPlugin() instanceof ContextPopupPlugin)
	{
		list.remove(plugin);
		plugin.removePropertyChangeListener(this);
	}
}
/**
 * Not implemented
 * Creation date: (6.12.2001 22:23:57)
 * @param
 * @return
 */
public void propertyChange(PropertyChangeEvent evt)
{
	PluginObject plugin = (PluginObject)evt.getSource();
	String propertyName = evt.getPropertyName();

	if (propertyName.equals("Status"))
	{
		if (plugin.getStatus() == PluginObject.PLUGIN_STARTED)
		{
		}
		else if (plugin.getStatus() == PluginObject.PLUGIN_STOPPED)
		{
		}
	}
}

/**
 * This metod is called each time request of popup menu is gived to VisualDCT.
 * PluginPopupManager then queries all popup plugins, asking them to return list of menus
 * or menu items to be added to the default list of popup items.
 * Creation date: (8.12.2001 12:45:31)
 * @return java.util.Vector list of <code>javax.swing.JMenuItems</code>. This method never returns null.
 */
public Vector getAllPluginItems(Vector selectedObjects)
{
	ContextPopupPlugin plugin = null;
	Vector items = new Vector();
	
	Iterator iter = list.iterator();
	while (iter.hasNext())
	{
		PluginObject pluginObject = (PluginObject)iter.next();
		plugin = (ContextPopupPlugin)(pluginObject).getPlugin();
		
		if (pluginObject.getStatus() == PluginObject.PLUGIN_STARTED)
		{		
			// protect yourself from buggy plugins
			try
			{
				Vector pluginItems = plugin.getItems(selectedObjects);
				if (pluginItems!=null)
				{
					Enumeration e = pluginItems.elements();
					while (e.hasMoreElements())
					{
						Object obj = e.nextElement();
						if (obj instanceof JMenuItem ||
							obj instanceof JSeparator)
							items.addElement(obj);
					}
				}
			} catch (Exception e)
			{
				Console.getInstance().println("Exception caught when calling popup plugin '"+plugin.getName()+"'.getItems(Vector).");
				Console.getInstance().println(e);
			}
		}
	}

	return items;
}

}
