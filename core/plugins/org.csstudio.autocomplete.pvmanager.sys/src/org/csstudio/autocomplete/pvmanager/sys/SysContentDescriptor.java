/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.pvmanager.sys;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.csstudio.autocomplete.parser.ContentDescriptor;

/**
 * @author Fred Arnaud (Sopra Group) - ITER
 * 
 */
public class SysContentDescriptor extends ContentDescriptor {

	private static Map<String, String> functions = new TreeMap<String, String>();
	static {
		functions.put("time", "Local date and time");
		functions.put("free_mb", "Free Java VM memory in MB");
		functions.put("used_mb", "Used Java VM memory in MB");
		functions.put("max_mb", "Maximum available Java VM memory in MB");
		functions.put("user", "User Name");
		functions.put("host_name", "Host name");
		functions.put("qualified_host_name", "Full Host Name");
		functions.put("system", "Any system property, e.g. \"sys://system.user.name\"");
		functions = Collections.unmodifiableMap(functions);
	}

	public static Collection<String> listFunctions() {
		return functions.keySet();
	}

	public static String getDescription(String function) {
		return functions.get(function);
	}

}
