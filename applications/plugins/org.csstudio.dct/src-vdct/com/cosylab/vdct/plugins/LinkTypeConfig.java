package com.cosylab.vdct.plugins;

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

import java.util.Hashtable;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.cosylab.vdct.Constants;
import com.cosylab.vdct.plugin.PluginContext;
import com.cosylab.vdct.plugin.config.LinkTypeConfigPlugin;
import com.cosylab.vdct.xml.XMLManager;

/**
 * A default LinkTypeConfig plugin. 
 * It reads link type configuration from an XML file. The file name is defined by VDCT_LINK_CONFIG_FILE filename located
 * in user home directory (or in VDCT_CONFIG_DIR dir). Use VDCT_LINK_CONFIG_FILE_ENV environment variable to override this default setting.
 * An example of XML file:
 * <?xml version="1.0" encoding="UTF-8"?>
 * 
 * <links>
 *    <link type="CONSTANT" pattern=".*" default="" description="CONSTANT" />
 *    <link type="PV_LINK" pattern=".*" default="" description="PV_LINK" />
 *    <link type="VME_IO" pattern="#C\d+ S\d+ @.*" default="#C0 S0( )*( @.*)?" description="VME_IO - #Ccard Ssignal @parm"  />
 *    <link type="CAMAC_IO" pattern="#B\d+ C\d+ N\d+ A\d+ F\d+( )*( @.*)?" default="CAMAC_IO - #B0 C0 N0 A0 F0 @" description="#Bbranch Ccrate Nstation Asubaddress Ffunction @parm" />
 *    <link type="AB_IO" pattern="#L\d+ A\d+ C\d+ S\d+( )*( @.*)?" default="#L0 A0 C0 S0 @" description="AB_IO - #Llink Aadapter Ccard Ssignal @parm" />
 *    <link type="GPIB_IO" pattern="#L\d+ A\d+( )*( @.*)?" default="#L0 A0 @" description="GPIB_IO - #Llink Aaddr @parm"  />
 *    <link type="BITBUS_IO" pattern="#L\d+ N\d+ P\d+ S\d+( )*( @.*)?" default="BITBUS_IO - @L0 N0 P0 S0 @" description="#Llink Nnode Pport Ssignal @parm"  />
 *    <link type="INST_IO" pattern="@.*" default="@" description="INST_IO - @"  />
 *    <link type="BBGPIB_IO" pattern="#L\d+ B\d+ G\d+( )*( @.*)?" default="#L0 B0 G0 @" description="BBGPIB_IO - #Llink Bbbaddr Ggpibaddr @parm"  />
 *    <link type="RF_IO" pattern="#R\d+ M\d+ D\d+ E\d+( )*( @.*)?" default="#R0 M0 D0 E0 @" description="RF_IO - #Rcryo Mmicro Ddataset Eelement"  />
 *    <link type="VXI_IO" pattern="#V\d+ (C\d+)?+  S\d+( )*( @.*)?" default="#V0 C0 S0 @" description="VXI_IO - #Vframe Cslot Ssignal @parm"  />
 * </links>
 * 
 * @author Matej Sekoranja
 */
public class LinkTypeConfig implements LinkTypeConfigPlugin
{
	
	public static final String VDCT_LINK_CONFIG_FILE = ".vdctlinks.xml";
	public static final String VDCT_LINK_CONFIG_FILE_ENV = "VDCT_LINK_CONFIG";
	
	/**
	 * @see com.cosylab.vdct.plugin.config.LinkTypeConfigPlugin#getLinkTypeConfig()
	 */
	public Hashtable getLinkTypeConfig()
	{

		String fileName = null;
		Document doc = null;
		try
		{
		    fileName = Constants.getConfigFile(VDCT_LINK_CONFIG_FILE, VDCT_LINK_CONFIG_FILE_ENV);
		
			// is file does not exists, load default file
			if (!(new java.io.File(fileName).exists()))
			{
				com.cosylab.vdct.Console.getInstance().println("[LinkTypeConfigPlugin] No link type configuration file found.");
				//fileName = getClass().getResource("/"+Constants.CONFIG_DIR+VDCT_LINK_CONFIG_FILE).getFile();
				return null;
			}

			doc = XMLManager.readFileDocument(fileName);
		}
		catch (Exception e)
		{
			com.cosylab.vdct.Console.getInstance().println("[LinkTypeConfigPlugin] Failed to open link type configuration file '"+fileName+"'.");
			com.cosylab.vdct.Console.getInstance().println(e);
			return null;
		}
	
		if (doc==null)
		{
			com.cosylab.vdct.Console.getInstance().println("[LinkTypeConfigPlugin] Invalid link type configuration file '"+fileName+"'.");
			return null;
		}

		Hashtable table = new Hashtable();
						
		int count = 0;
		//Node node = doc.getElementsByTagName("links").item(0);
		NodeList nodes = doc.getElementsByTagName("link");
		for (int i=0; i<nodes.getLength(); i++)
		{
			Element e = (Element)nodes.item(i); 
			String type = e.getAttribute("type");
			String pattern = e.getAttribute("pattern");
			String def = e.getAttribute("default");
			String desc = e.getAttribute("description");
			if (type!=null && pattern!=null && def!=null && desc!=null)
			{
				try
				{
					table.put(type, new Object[] { Pattern.compile(pattern), def, desc });
					count++;
				}
				catch (PatternSyntaxException pse)
				{
					com.cosylab.vdct.Console.getInstance().println("[LinkTypeConfigPlugin] Invalid pattern '"+pattern+"' for link type '"+type+"'. Skipping...");
					com.cosylab.vdct.Console.getInstance().println(pse);
					com.cosylab.vdct.Console.getInstance().println();
				} 
			}
			else if (type!=null)
				com.cosylab.vdct.Console.getInstance().println("[LinkTypeConfigPlugin] Invalid configuration for link type '"+type+"'. Skipping...");
			else
				com.cosylab.vdct.Console.getInstance().println("[LinkTypeConfigPlugin] Invalid configuration found. Skipping....");
		}
		com.cosylab.vdct.Console.getInstance().println("[LinkTypeConfigPlugin] Loaded "+count+" link type configuration(s).");
		
		return table;
	}

	/**
	 * @see com.cosylab.vdct.plugin.Plugin#destroy()
	 */
	public void destroy()
	{
	}

	/**
	 * @see com.cosylab.vdct.plugin.Plugin#getAuthor()
	 */
	public String getAuthor()
	{
		return "matej.sekoranja@cosylab.com";
	}

	/**
	 * @see com.cosylab.vdct.plugin.Plugin#getDescription()
	 */
	public String getDescription()
	{
		return "LinkTypeConfig plugin which loads link type config from XML file.";
	}

	/**
	 * @see com.cosylab.vdct.plugin.Plugin#getName()
	 */
	public String getName()
	{
		return "Link Type Configurator";
	}

	/**
	 * @see com.cosylab.vdct.plugin.Plugin#getVersion()
	 */
	public String getVersion()
	{
		return "0.1";
	}

	/**
	 * @see com.cosylab.vdct.plugin.Plugin#init(Properties, PluginContext)
	 */
	public void init(Properties properties, PluginContext context)
	{
	}

	/**
	 * @see com.cosylab.vdct.plugin.Plugin#start()
	 */
	public void start()
	{
	}

	/**
	 * @see com.cosylab.vdct.plugin.Plugin#stop()
	 */
	public void stop()
	{
	}

}
