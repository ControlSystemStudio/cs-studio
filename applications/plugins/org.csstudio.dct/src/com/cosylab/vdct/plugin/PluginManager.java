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

import java.util.*;

import com.cosylab.vdct.Constants;

/**
 * Insert the class' description here.
 * Creation date: (6.12.2001 22:14:18)
 * @author Matej Sekoranja
 */
public final class PluginManager
{
	private static PluginManager instance = null;
    private LinkedList pluginListeners = null;
    private LinkedList plugins = null;

    private PluginSerializer pluginSerializer = null;
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:25:50)
 */
protected PluginManager() 
{
    pluginListeners = new LinkedList();
    plugins = new LinkedList();

    pluginSerializer = new PluginXMLSerializer();

	// load
	load();
}

/**
 * @return
 */
public PluginSerializer getPluginSerializer()
{
	return pluginSerializer;
}

/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:17:37)
 * @param
 * @return
 */
public void addPlugin(PluginObject plugin)
{
	if(plugins.contains(plugin))
		return;

	// moved to here to have plugin serialized
	plugins.add(plugin);
	plugin.init();
	//plugins.add(plugin);

	for(int i=0; i<pluginListeners.size(); i++)
		((PluginListener)pluginListeners.get(i)).pluginAdded(plugin);
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:17:37)
 * @param
 * @return
 */
public void addPluginListener(PluginListener listener)
{
	if(!pluginListeners.contains(listener))
	{
		pluginListeners.add(listener);
		updateListener(listener);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:17:37)
 * @param
 * @return
 */
public void checkAutoStartPlugins()
{
	Iterator it = plugins.iterator();
	PluginObject plugin;

	while(it.hasNext())
	{
		plugin = (PluginObject)it.next();
		if(plugin.isAutoStart())
			plugin.start();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:17:37)
 * @param
 * @return
 */
public void destroyAllPlugins()
{
	Iterator it = plugins.iterator();
	PluginObject plugin;

	while(it.hasNext())
	{
		plugin = (PluginObject)it.next();
		plugin.stop();
		plugin.destroy();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:26:27)
 * @return com.cosylab.vdct.plugin.PluginManager
 */
public static PluginManager getInstance() {
	if (instance==null)
	{
		instance = new PluginManager();

		// !!! find a better place
		// create plugin managers
		PluginUIManager.getInstance();
		com.cosylab.vdct.plugin.config.PluginLinkTypeConfigManager.getInstance();
		com.cosylab.vdct.plugin.debug.PluginDebugManager.getInstance();
		com.cosylab.vdct.plugin.export.PluginExportManager.getInstance();
		com.cosylab.vdct.plugin.popup.PluginPopupManager.getInstance();
		com.cosylab.vdct.plugin.menu.PluginMenuManager.getInstance();
	}
		
	return instance;
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:17:37)
 * @param
 * @return
 */
public Iterator getPlugins()
{
	return plugins.iterator();
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:17:37)
 * @param
 * @return
 */
private void load()
{
	try
	{
	    String fileName = Constants.getConfigFile(Constants.PLUGINS_FILE_NAME, Constants.VDCT_PLUGINS_FILE);
	
		// is file does not exists, load default file
		if (!(new java.io.File(fileName).exists()))
		{
			com.cosylab.vdct.Console.getInstance().println("o) No plugins configuration file found. Using defaults...");
			fileName = getClass().getResource("/" + Constants.CONFIG_DIR + Constants.PLUGINS_FILE_NAME).getFile();
		}
		pluginSerializer.importPlugins(fileName, this);
	}
	catch (Exception e)
	{
		com.cosylab.vdct.Console.getInstance().println("An error occured while loading the plugins list!");
		com.cosylab.vdct.Console.getInstance().println(e);

		plugins.clear();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:17:37)
 * @param
 * @return
 */
public void removePlugin(PluginObject plugin)
{
	if(!plugins.contains(plugin))
		return;

	plugins.remove(plugin);
	plugin.stop();
	plugin.destroy();

	for( int i=0; i<pluginListeners.size(); i++ )
		((PluginListener)pluginListeners.get(i)).pluginRemoved(plugin);
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:17:37)
 * @param
 * @return
 */
public void removePluginListener(PluginListener listener)
{
	pluginListeners.remove(listener);
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:17:37)
 * @param
 * @return
 */
public void save()
{
	try
	{
		//String fileName = com.cosylab.vdct.Settings.getInstance().getDefaultDir()+com.cosylab.vdct.Constants.CONFIG_DIR+PLUGINS_FILE;
		String fileName = System.getProperty(Constants.VDCT_PLUGINS_FILE);
		if (fileName == null)
			fileName = System.getProperty("user.home") + "/" + Constants.PLUGINS_FILE_NAME;
		pluginSerializer.exportPlugins(fileName, this);
	}
	catch (Exception e)
	{
		com.cosylab.vdct.Console.getInstance().println("An error occured while saving the plugins list!");
		com.cosylab.vdct.Console.getInstance().println(e);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:22:20)
 * @param
 * @return
 */
private void updateListener(PluginListener listener)
{
	Iterator it = plugins.iterator();
	while( it.hasNext() )
		listener.pluginAdded( (PluginObject)it.next() );
}
}
