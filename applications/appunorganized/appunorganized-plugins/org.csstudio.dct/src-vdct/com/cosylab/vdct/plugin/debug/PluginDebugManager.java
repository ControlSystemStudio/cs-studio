package com.cosylab.vdct.plugin.debug;

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

import com.cosylab.vdct.Console;
import com.cosylab.vdct.VisualDCT;
import com.cosylab.vdct.graphics.DrawingSurface;
import com.cosylab.vdct.graphics.objects.Group;
import com.cosylab.vdct.plugin.*;

/**
 * Creation date: (7.12.2001 13:57:49)
 * @author Matej Sekoranja
 */
public final class PluginDebugManager implements PluginListener, PropertyChangeListener
{
    private static PluginDebugManager instance = null;

    private LinkedList list = null;

    private static DebugPlugin debugPlugin = null;
    private static boolean debugState = false;

    private static final String DEBUG_MODE = "Debug Mode";
    private static final String EDIT_MODE = "Edit Mode";

/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 14:00:41)
 */
protected PluginDebugManager()
{
    list = new LinkedList();

    PluginManager.getInstance().addPluginListener(this);
}
/**
 * Insert the method's description here.
 * Creation date: (8.12.2001 17:47:11)
 * @return com.cosylab.vdct.plugin.debug.DebugPlugin
 */
public static DebugPlugin getDebugPlugin() {
    return debugPlugin;
}
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 14:01:03)
 * @return com.cosylab.vdct.plugin.PluginDebugManager
 */
public static PluginDebugManager getInstance() {
    if (instance==null) instance = new PluginDebugManager();
    return instance;
}
/**
 * Insert the method's description here.
 * Creation date: (8.12.2001 18:47:14)
 * @return boolean
 */
public static boolean isDebugState() {
    return debugState;
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:23:57)
 * @param
 * @return
 */
public void pluginAdded(PluginObject plugin)
{
    if (plugin.getPlugin() instanceof DebugPlugin)
    {
        if (!list.contains(plugin))
        {
            list.add(plugin);
            plugin.addPropertyChangeListener(this);
            com.cosylab.vdct.Console.getInstance().println(plugin.getName()+" is registered as debug plugin.");
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
    if (plugin.getPlugin() instanceof DebugPlugin)
    {
        if (plugin.getPlugin() == getDebugPlugin())
            stopDebugging();

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
 * Insert the method's description here.
 * Creation date: (8.12.2001 17:47:11)
 * @param newDebugPlugin com.cosylab.vdct.plugin.debug.DebugPlugin
 */
public static void setDebugPlugin(DebugPlugin newDebugPlugin) {
    if (newDebugPlugin != null)
        VisualDCT.getInstance().setMode(DEBUG_MODE);
    else
        VisualDCT.getInstance().setMode(EDIT_MODE);

    debugPlugin = newDebugPlugin;
}
/**
 * Insert the method's description here.
 * Creation date: (8.12.2001 18:47:14)
 * @param newDebugState boolean
 */
public static void setDebugState(boolean newDebugState) {
    debugState = newDebugState;
}

/**
 * Insert the method's description here.
 * Creation date: (8.12.2001 18:57:13)
 */
public static void stopDebugging()
{
    DebugPlugin debugPlugin = PluginDebugManager.getDebugPlugin();
    if (debugPlugin!=null)
    {
        Console.getInstance().println("Stopping debugging with '" + debugPlugin.getName() + "'...");

        debugPlugin.deregisterAll();
        debugPlugin.stopDebugging();
        PluginDebugManager.setDebugState(false);
        PluginDebugManager.setDebugPlugin(null);



        // update all fields
        Group group = DrawingSurface.getInstance().getViewGroup();
        Enumeration e = group.getSubObjectsV().elements();
        while (e.hasMoreElements())
        {
            Object obj = e.nextElement();
            if (obj instanceof com.cosylab.vdct.graphics.objects.Record)
            {
                com.cosylab.vdct.vdb.VDBRecordData rec = ((com.cosylab.vdct.graphics.objects.Record)obj).getRecordData();
                Enumeration e2 = rec.getFieldsV().elements();
                while (e2.hasMoreElements())
                    rec.fieldValueChanged((com.cosylab.vdct.vdb.VDBFieldData)e2.nextElement());

            }
        }

        Group.getRoot().unconditionalValidateSubObjects(false);
        DrawingSurface.getInstance().repaint();

    }
}

}
