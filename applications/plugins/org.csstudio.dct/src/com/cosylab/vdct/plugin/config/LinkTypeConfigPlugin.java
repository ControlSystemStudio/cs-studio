package com.cosylab.vdct.plugin.config;

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

import com.cosylab.vdct.plugin.Plugin;

/**
 * Insert the type's description here.
 * Creation date: (8.12.2001 12:43:54)
 * @author Matej Sekoranja 
 */
public interface LinkTypeConfigPlugin extends Plugin {

/**
 * This method should return a table of different link type configurations.
 * Each element of the list should have a key identifiying the link type (e.g. VME_IO) and
 * have the value of type Object[3], where array consists of
 * { java.util.regex.Pattern, java.lang.String, java.lang.String }.
 * The array represents { link validation schema, default schema, description }.
 * An example of default implementation:
 * <pre>
 * 
 * 	Hashtable linkTypeConfigTable = new Hashtable();
 * 
 *	linkTypeConfigTable.put("CONSTANT", new Object[] { Pattern.compile(".*"), "", "CONSTANT" });
 *	linkTypeConfigTable.put("PV_LINK", new Object[] { Pattern.compile(".*"), "", "PV_LINK" } );
 *	linkTypeConfigTable.put("VME_IO", new Object[] { Pattern.compile("#C\\d+ S\\d+ @.*"), "#C0 S0 @", "VME_IO - #Ccard Ssignal @parm" });
 *	linkTypeConfigTable.put("CAMAC_IO", new Object[] { Pattern.compile("#B\\d+ C\\d+ N\\d+ A\\d+ F\\d+ @.*"), "CAMAC_IO - #B0 C0 N0 A0 F0 @", "#Bbranch Ccrate Nstation Asubaddress Ffunction @parm" });
 *	linkTypeConfigTable.put("AB_IO", new Object[] { Pattern.compile("#L\\d+ A\\d+ C\\d+ S\\d+ @.*"), "#L0 A0 C0 S0 @", "AB_IO - #Llink Aadapter Ccard Ssignal @parm" });
 *	linkTypeConfigTable.put("GPIB_IO", new Object[] { Pattern.compile("#L\\d+ A\\d+ @.*"), "#L0 A0 @", "GPIB_IO - #Llink Aaddr @parm" });
 *	linkTypeConfigTable.put("BITBUS_IO", new Object[] { Pattern.compile("#L\\d+ N\\d+ P\\d+ S\\d+ @.*"), "BITBUS_IO - @L0 N0 P0 S0 @", "#Llink Nnode Pport Ssignal @parm" });
 *	linkTypeConfigTable.put("INST_IO", new Object[] { Pattern.compile("@.*"), "@", "INST_IO - @" });
 *	linkTypeConfigTable.put("RF_IO", new Object[] { Pattern.compile("#R\\d+ M\\d+ D\\d+ E\\d+ @.*"), "#R0 M0 D0 E0 @", "RF_IO - #Rcryo Mmicro Ddataset Eelement" });
 *	linkTypeConfigTable.put("VXI_IO", new Object[] { Pattern.compile("#V\\d+ (C\\d+)?+  S\\d+ @.*"), "#V0 C0 S0 @", "VXI_IO - #Vframe Cslot Ssignal @parm" });
 * </pre> 
 * If <code>null</code> is returned, VisualDCT interprets this as 'failed to load' / 'no configuration found'. 
 * If all <code>LinkTypeConfigPlugin</code> plugins return <code>null</code>, VisualDCT loads default configuration.
 * Plugins are loaded as defined in VisualDCT plugin configuration. Configurations are additive, i.e. if there are two configurations for the same link type, the last overrides the first.   
 * Creation date: (8.12.2001 12:45:31)
 * @return java.util.ArrayList
 */
public Hashtable getLinkTypeConfig();
}
