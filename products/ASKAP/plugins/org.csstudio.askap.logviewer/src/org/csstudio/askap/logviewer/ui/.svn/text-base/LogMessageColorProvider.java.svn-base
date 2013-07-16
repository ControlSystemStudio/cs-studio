/*
 * Copyright (c) 2009 CSIRO Australia Telescope National Facility (ATNF) Commonwealth
 * Scientific and Industrial Research Organisation (CSIRO) PO Box 76, Epping NSW 1710,
 * Australia atnf-enquiries@csiro.au
 *
 * This file is part of the ASKAP software distribution.
 *
 * The ASKAP software distribution is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite
 * 330, Boston, MA 02111-1307 USA
 */

package org.csstudio.askap.logviewer.ui;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.askap.logviewer.util.LogObject;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @author wu049
 * @created Apr 28, 2011
 *
 */
public class LogMessageColorProvider extends ColumnLabelProvider implements
		IColorProvider {
	
	public static Map<String, Integer> LOG_LEVEL_COLOR_MAP = new HashMap<String, Integer>();
	static {
//		"TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL"
		LOG_LEVEL_COLOR_MAP.put("FATAL", SWT.COLOR_RED);
		LOG_LEVEL_COLOR_MAP.put("WARN", SWT.COLOR_YELLOW);
		LOG_LEVEL_COLOR_MAP.put("ERROR", SWT.COLOR_RED);
	}
	

	
	@Override
	public Color getForeground(Object element) {
		return null;
	}

	@Override
	public Color getBackground(Object element) {
		LogObject log = (LogObject) element;
		Integer color = LOG_LEVEL_COLOR_MAP.get(log.getLogLevel());
		if (color!=null)
			return Display.getCurrent().getSystemColor(color);					
		
		return null;
	}
}
