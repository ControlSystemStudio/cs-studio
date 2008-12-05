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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.cosylab.vdct.Console;
import com.cosylab.vdct.plugin.menu.*;

/**
 * Application runner plugin, runs external applications from the VDCT. <br/>
 * A menu Tools->Applications is added.<br/>Applications are configured via plugins configuration file.<br/> 
 * Add the following lines to the
 * ${user.home}/.vdctplugins.xml or /etc/vdct/.vdctplugins.xml (system config)
 * file:
 * <pre>
 * &lt;plugin class="com.cosylab.vdct.plugins.ApplicationRunnerPlugin" autostart="true"&gt;
 *   &lt;param name="Calculator" value="calc" /&gt;
 *   &lt;param name="Minesweeper" value="winmine" /&gt;
 *   &lt;param name="Notepad" value="notepad" /&gt;
 * &lt;/plugin&gt;
 * </pre>
 * 
 * @author Matej Sekoranja
 */
public class ApplicationRunnerPlugin implements MenuPlugin {

    class MenuItemHandler implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            String action = e.getActionCommand();
            //Console.getInstance().println("ApplicationRunnerPlugin running: " + action);
            try  {
                Runtime.getRuntime().exec(action);
            } catch (Throwable th)
            {
                Console.getInstance().println("Failed to execute: " + action);
                Console.getInstance().println(th);
                th.printStackTrace();
            }
        }
    }

    protected ApplicationRunnerPlugin.MenuItemHandler menuitemHandler = null;

    protected JMenu menu = null;

    /**
     * Insert the method's description here. Creation date: (2.2.2001 23:00:51)
     * 
     * @return com.cosylab.vdct.graphics.objects.Connector.PopupMenuHandler
     */
    private ApplicationRunnerPlugin.MenuItemHandler getMenuHandler()
    {
        if (menuitemHandler == null)
            menuitemHandler = new MenuItemHandler();
        return menuitemHandler;
    }

    /**
     * Insert the method's description here. Creation date: (8.12.2001 13:29:26)
     * 
     * @param
     * @return
     */
    public void destroy()
    {
    }

    /**
     * Insert the method's description here. Creation date: (8.12.2001 13:29:26)
     * 
     * @return java.lang.String
     */
    public String getAuthor()
    {
        return "matej.sekoranja@cosylab.com";
    }

    /**
     * Insert the method's description here. Creation date: (8.12.2001 13:29:26)
     * 
     * @return java.lang.String
     */
    public String getDescription()
    {
        return "Plugin that runs external applications from the VDCT.";
    }

    /**
     * Insert the method's description here. Creation date: (8.12.2001 13:29:26)
     * 
     * @param
     * @return
     */
    public String getName()
    {
        return "Application Runner Plugin";
    }

    /**
     * Insert the method's description here. Creation date: (8.12.2001 13:29:26)
     * 
     * @return java.lang.String
     */
    public String getVersion()
    {
        return "1.0";
    }

    /**
     * Insert the method's description here. Creation date: (8.12.2001 13:29:26)
     * 
     * @param
     * @return
     */
    public void init(java.util.Properties properties,
            com.cosylab.vdct.plugin.PluginContext context)
    {
        ActionListener handler = getMenuHandler();

        menu = new JMenu("Applications");
        
        Iterator iter = properties.keySet().iterator();
        while (iter.hasNext())
        {
            String name = iter.next().toString();
            String exec = properties.get(name).toString();
            if (exec == null)
                continue;
            
	        JMenuItem item = new JMenuItem(name);
	        item.addActionListener(handler);
	        item.setActionCommand(exec);
	        menu.add(item);
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (8.12.2001 13:29:26)
     * @param 
     * @return
     */
    public void start()
    {
    }

    /**
     * Insert the method's description here.
     * Creation date: (8.12.2001 13:29:26)
     * @param 
     * @return
     */
    public void stop()
    {
    }

    /**
     * @see com.cosylab.vdct.plugin.menu.MenuPlugin#getMenu()
     */
    public JMenu getMenu()
    {
        return menu;
    }

}