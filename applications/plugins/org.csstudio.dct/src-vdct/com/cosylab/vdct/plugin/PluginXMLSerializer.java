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

import java.io.*;
import java.net.*;
import java.util.*;

import com.cosylab.vdct.xml.*;

import org.w3c.dom.*;

/**
 * Insert the class' description here.
 * Creation date: (7.12.2001 14:48:33)
 * @author Matej Sekoranja
 */

public class PluginXMLSerializer implements PluginSerializer
{
    private static final String DTD_SYMBOL = "com.cosylab.vdct:plugins";
    private static final String DTD_URL = com.cosylab.vdct.Constants.DTD_DIR+"plugins.dtd";

/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 15:04:28)
 * @param
 */

public void exportPlugins(String fileName, PluginManager pluginManager) throws Exception
{
    Document doc = XMLManager.newDocument();
    Element root = (Element)doc.createElement("plugins");

    doc.appendChild(root);

    // save all plugins
    Iterator plugins = pluginManager.getPlugins();

    while (plugins.hasNext())
    {

        Element node = (Element)doc.createElement("plugin");
        ((PluginObject)plugins.next()).saveConfig(doc, node);
        root.appendChild(node);

    }

    root.normalize();

    XMLManager.writeDocument(fileName, doc, null, DTD_SYMBOL, null);
}

/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 14:52:16)
 * @param
 */
public void importPlugins(String fileName, PluginManager pluginManager) throws Exception
{

    // read from resource
    URL dtdURL = getClass().getResource("/"+DTD_URL);
    if (dtdURL==null)
        throw new Exception("Failed to locate DTD file: /"+DTD_URL);

    Document doc = null;
    try
    {
        // shp: importPlugins is now capable of loading xml from jar files
        //doc = XMLManager.readFileDocument(fileName, DTD_SYMBOL, dtdURL);
        doc = XMLManager.readResourceDocument(fileName, DTD_SYMBOL, dtdURL);
    }
    catch (FileNotFoundException e)
    {
        com.cosylab.vdct.Console.getInstance().println("Plugins configuration file '"+fileName+"' not found. Using defaults.");
        return;
    }

    if (doc==null)
    {
        com.cosylab.vdct.Console.getInstance().println("Failed to read plugins configuration file '"+fileName+"'.");
        return;
    }

    Node node = XMLManager.findNode(doc, "plugins").getNextSibling().getFirstChild();

    while (node!=null)
    {
        if (node instanceof Element)
        {
            if (node.getNodeName().equals("plugin"))
            {
                try
                {
                    PluginObject plugin = new PluginObject((Element)node);
                    pluginManager.addPlugin(plugin);
                }
                catch (Throwable t)
                {
                    com.cosylab.vdct.Console.getInstance().println("Failed to load/initialize plugin: " + t.getMessage());
                    System.err.println("Failed to load/initialize plugin: " + t.getMessage());
                }
            }
        }
        node = node.getNextSibling();
    }
}
}
