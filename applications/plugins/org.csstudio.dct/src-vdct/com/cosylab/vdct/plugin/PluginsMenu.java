package com.cosylab.vdct.plugin;

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

import java.util.*;
import java.beans.*;
import java.awt.event.*;

/**
 * Insert the class' description here.
 * Creation date: (7.12.2001 17:15:12)
 * @author Matej Sekoranja
 */
public class PluginsMenu extends JMenu implements PluginListener
{
	private class PluginMenu extends JMenu implements PropertyChangeListener
	{
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 17:06:52)
 * @param
 */
public PluginMenu(PluginObject plugin)
{
	uninstallPluginAction = new JMenuItem("Uninstall");
	uninstallPluginAction.setMnemonic('U');
	uninstallPluginAction.addActionListener(new UninstallPluginAction());
	
	startPluginAction = new JMenuItem("Start");
	startPluginAction.setMnemonic('S');
	startPluginAction.addActionListener(new StartPluginAction());

	stopPluginAction = new JMenuItem("Stop");
	stopPluginAction.setMnemonic('t');
	stopPluginAction.addActionListener(new StopPluginAction());

	pluginInfoAction = new JMenuItem("Info");
	pluginInfoAction.setMnemonic('I');
	pluginInfoAction.addActionListener(new PluginInfoAction());

	displayPluginDocumentationAction = new JMenuItem("Documentation");
	displayPluginDocumentationAction.setMnemonic('D');
	displayPluginDocumentationAction.addActionListener(new DisplayPluginDocumentationAction());

    add( startPluginAction );
    add( stopPluginAction );
    addSeparator();
    add( pluginInfoAction );
    add( displayPluginDocumentationAction );
    addSeparator();
    add( uninstallPluginAction );

    setPlugin(plugin);
}

/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 17:05:28)
 * @param
 * @return
 */
public void setPlugin(PluginObject newPlugin)
{
	if (plugin!=null)
		plugin.removePropertyChangeListener(this);

	plugin = newPlugin;

	if (plugin!=null)
	{
		plugin.addPropertyChangeListener(this);
		updateStatus();
	}
}

/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 17:05:56)
 * @param
 */
public void propertyChange(PropertyChangeEvent evt)
{
	if (evt.getPropertyName().equals("Status"))
		updateStatus();
}

/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 17:05:42)
 * @param
 * @return
 */
private void updateStatus()
{
	if (plugin!=null)
	{
		startPluginAction.setEnabled(plugin.getStatus()==PluginObject.PLUGIN_INITIALIZED || plugin.getStatus()==PluginObject.PLUGIN_STOPPED );
		stopPluginAction.setEnabled(plugin.getStatus()==PluginObject.PLUGIN_STARTED );
		//pluginInfoAction.setEnabled(plugin.getStatus()!=PluginObject.PLUGIN_INVALID && plugin.getStatus()!=PluginObject.PLUGIN_NOT_LOADED );
		//displayPluginDocumentationAction.setEnabled(plugin.getStatus()!=PluginObject.PLUGIN_INVALID && plugin.getStatus()!=PluginObject.PLUGIN_NOT_LOADED && plugin.getDocumentationURL()!=null);
		pluginInfoAction.setEnabled(false);
		displayPluginDocumentationAction.setEnabled(false);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 17:06:11)
 * @param
 * @return
 */	
public String getText()
{
	if( plugin!=null )
		return plugin.getName();
	else
		return "";
}

/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 17:06:38)
 * @param
 * @return
 */
public Icon getIcon()
{
	/*if( plugin!=null )
		return plugin.getIcon();
	else*/
		return null;
}

		private class UninstallPluginAction implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				if (plugin!=null)
					PluginManager.getInstance().removePlugin(plugin);
			}
		}

		private class StartPluginAction implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				if (plugin!=null)
					plugin.start();
			}
		}

		private class StopPluginAction implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				if (plugin!=null)
					plugin.stop();
			}
		}

		private class PluginInfoAction implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
//				if (plugin!=null)
			}
		}

		private class DisplayPluginDocumentationAction implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{

				/*if(plugin!=null)
				{
					URL doc_url = plugin.getDocumentationURL();

					if( doc_url!=null )
						XBrowser.getBrowser().showInNewDocument( doc_url.toString() );
				}
				*/
			}
		}

		private PluginObject plugin = null;

		private JMenuItem uninstallPluginAction = null;
		private JMenuItem startPluginAction = null;
		private JMenuItem stopPluginAction = null;
		private JMenuItem pluginInfoAction = null;
		private JMenuItem displayPluginDocumentationAction = null;
	}

	private Map pluginMenu = new HashMap();
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 17:08:53)
 * @param
 */
public PluginsMenu()
{
}
/**
 * Insert the method's description here.
 * Needed to add plugins after "Plugin Manager..." menu item
 * Creation date: (7.12.2001 17:55:14)
 */
public void init()
{
	PluginManager.getInstance().addPluginListener(this);
}
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 17:09:23)
 * @param
 */
public void pluginAdded(PluginObject plugin)
{
	if( plugin.getStatus()==PluginObject.PLUGIN_NOT_LOADED ||
	    plugin.getStatus()==PluginObject.PLUGIN_INVALID )
		    return;

	PluginMenu menu = new PluginMenu(plugin);

	add(menu);
	pluginMenu.put(plugin, menu);
}
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 17:10:37)
 * @param
 */
public void pluginRemoved(PluginObject plugin)
{
	PluginMenu menu = (PluginMenu)pluginMenu.remove(plugin);

	if (menu!=null)
	{
		remove(menu);
		menu.setPlugin(null);
	}
}
}
