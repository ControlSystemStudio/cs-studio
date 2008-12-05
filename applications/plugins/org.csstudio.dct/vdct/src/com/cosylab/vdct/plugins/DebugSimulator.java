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

import com.cosylab.vdct.plugin.*;
import com.cosylab.vdct.plugin.debug.*;
import com.cosylab.vdct.graphics.objects.Debuggable;
import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (7.12.2001 16:40:05)
 * @author Matej Sekoranja
 */
public class DebugSimulator implements DebugPlugin, Runnable {
	private Thread thread = null;
	private boolean destroyed = false;

	private int minVal = 0;
	private int maxVal = 100;
	private int interval = 1000;	// 1s
	
	private Vector list = null;

	private String source = "none";
/**
 * DebugSimulator constructor comment.
 */
public DebugSimulator()
{
	list = new Vector();
}
/**
 * Insert the method's description here.
 * Creation date: (8.12.2001 17:45:13)
 */
public void deregisterAll()
{
	list.removeAllElements();
}
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 19:40:04)
 * @param field com.cosylab.vdct.graphics.objects.Debuggable
 */
public void deregisterMonitor(Debuggable field)
{
	if (!list.contains(field))
		com.cosylab.vdct.Console.getInstance().println("BUG: Invalid deregistration of the field!");
	else
	{
		list.remove(field);
		field.setConnected(true);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 16:40:05)
 * @param 
 * @return
 */
public void destroy()
{
	stop();
}
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 16:40:05)
 * @return java.lang.String
 */
public String getAuthor() {
	return "matej.sekoranja@cosylab.com";
}
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 16:40:05)
 * @return java.lang.String
 */
public String getDescription() {
	return "A plug simulating debug plugin generating random values.";
}
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 16:40:05)
 * @param 
 * @return
 */
public String getName() {
	return "Debug Simulator ("+source+")";
}
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 19:40:04)
 * @return java.lang.String
 * @param field java.lang.String
 */
public java.lang.String getValue(java.lang.String field) {
	return "0";
}
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 16:40:05)
 * @return java.lang.String
 */
public String getVersion() {
	return "0.1";
}
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 16:40:05)
 * @param 
 * @return
 */
public void init(java.util.Properties properties, PluginContext context)
{
	// read properties
	String tmp = properties.get("max").toString();
	if (tmp!=null)
		maxVal = Integer.parseInt(tmp);

	tmp = properties.get("min").toString();
	if (tmp!=null)
		minVal = Integer.parseInt(tmp);

	tmp = properties.get("interval").toString();
	if (tmp!=null)
		interval = Integer.parseInt(tmp);

	tmp = properties.get("source").toString();
	if (tmp!=null)
		source = tmp;

		// test of saving additional properties
	properties.put("lastrun", new Date().toString());
}
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 19:40:04)
 * @param field com.cosylab.vdct.graphics.objects.Debuggable
 */
public void registerMonitor(Debuggable field)
{
	if (list.contains(field))
		com.cosylab.vdct.Console.getInstance().println("BUG: Multiple registration of the field!");
	else
	{
		list.add(field);
		field.setConnected(true);
	}
}
/**
 * When an object implementing interface <code>Runnable</code> is used 
 * to create a thread, starting the thread causes the object's 
 * <code>run</code> method to be called in that separately executing 
 * thread. 
 * <p>
 * The general contract of the method <code>run</code> is that it may 
 * take any action whatsoever.
 *
 * @see     java.lang.Thread#run()
 */
public void run()
{
	java.util.Random rand = new java.util.Random();
	while (!destroyed)
	{
		Date timeStamp = new Date();
		
		Enumeration enumer = list.elements();
		while (enumer.hasMoreElements())
		{
			int num = minVal + rand.nextInt(maxVal-minVal+1);
			((Debuggable)enumer.nextElement()).setDebugValue(String.valueOf(num), timeStamp, (short)0);
		}

		try
		{
			Thread.sleep(interval);
		}
		catch (InterruptedException e)
		{
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 16:40:05)
 * @param 
 * @return
 */
public void start()
{
}
/**
 * Insert the method's description here.
 * Creation date: (8.12.2001 18:00:20)
 */
public void startDebugging()
{
	destroyed = false;
	thread = new Thread(this);
	thread.start();
}
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 16:40:05)
 * @param 
 * @return
 */
public void stop()
{
	stopDebugging();
}
/**
 * Insert the method's description here.
 * Creation date: (8.12.2001 18:00:20)
 */
public void stopDebugging()
{
	destroyed = true;
}

/**
 * @see com.cosylab.vdct.plugin.debug.DebugPlugin#getMacroSet()
 */
public Map getMacroSet() {
	return null;
}

}
