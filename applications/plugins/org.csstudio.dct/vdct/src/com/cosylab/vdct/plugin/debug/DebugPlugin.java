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

import java.util.Map;

import com.cosylab.vdct.plugin.Plugin;
import com.cosylab.vdct.graphics.objects.Debuggable;

/**
 * Narrow interface to be added
 * Creation date: (7.12.2001 19:09:18)
 * @author Matej Sekoranja
 */
public interface DebugPlugin extends Plugin {

	/**
	 * Insert the method's description here.
	 * Creation date: (8.12.2001 17:44:51)
	 */
	public void deregisterAll();

	/**
	 * Insert the method's description here.
	 * Creation date: (7.12.2001 19:20:55)
	 * @param field com.cosylab.vdct.graphics.objects.Debuggable
	 */
	void deregisterMonitor(Debuggable field);

	/**
	 * Insert the method's description here.
	 * Creation date: (7.12.2001 19:09:51)
	 * @return java.lang.String
	 * @param field java.lang.String
	 */
	public String getValue(String field);

	/**
	 * Insert the method's description here.
	 * Creation date: (7.12.2001 19:20:37)
	 * @param field com.cosylab.vdct.graphics.objects.Debuggable
	 */
	public void registerMonitor(Debuggable field);

	/**
	 * Insert the method's description here.
	 * Creation date: (8.12.2001 17:59:52)
	 */
	public void startDebugging();

	/**
	 * Insert the method's description here.
	 * Creation date: (8.12.2001 18:00:07)
	 */
	public void stopDebugging();

	/**
	 * Get currently active macro set.
	 * @return currently active macro set.
	 */
	public Map getMacroSet();
}

