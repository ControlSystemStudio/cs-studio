/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.jaasauthentication.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.csstudio.jaasauthentication.ui.messages"; //$NON-NLS-1$
	public static String EditModuleOptionDialog_option;
	public static String EditModuleOptionDialog_value;
	public static String JAASPreferencePage_fileEntry;
	public static String JAASPreferencePage_modules;
	public static String JAASPreferencePage_restartNotice;
	public static String JAASPreferencePage_source;
	public static String JAASPreferencePage_title;
	public static String ModuleColumnLabelProvider_add;
	public static String ModuleTableEditor_deleteItems;
	public static String ModuleTableEditor_moduleFlag;
	public static String ModuleTableEditor_moduleName;
	public static String ModuleTableEditor_moveDown;
	public static String ModuleTableEditor_moveUp;
	public static String ModuleTableEditor_option;
	public static String ModuleTableEditor_options;
	public static String ModuleTableEditor_value;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	    // NOP
	}
}
