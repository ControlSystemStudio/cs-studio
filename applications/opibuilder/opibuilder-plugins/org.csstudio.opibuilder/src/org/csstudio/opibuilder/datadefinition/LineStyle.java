/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.datadefinition;

import org.csstudio.ui.util.SWTConstants;

/**The line sytle:
	 * 0: Solid,
	 * 1: Dash,
	 * 2: Dot,
	 * 3: DashDot,
	 * 4: DashDotDot.
	 * @author Xihui Chen
	 *
	 */
	public enum LineStyle {
		SOLID("Solid", SWTConstants.LINE_SOLID),
		DASH("Dash", SWTConstants.LINE_DASH),
		DOT("Dot", SWTConstants.LINE_DOT),
		DASH_DOT("DashDot", SWTConstants.LINE_DASHDOT),
		Dash_DOTDOT("DashDotDot", SWTConstants.LINE_DASHDOTDOT);
				
		String description;
		int style;
		LineStyle(String description, int style){
			this.description = description;
			this.style = style;
		}
		
		/**
		 * @return SWT line style {SWT.LINE_SOLID,
			SWT.LINE_DASH, SWT.LINE_DOT, SWT.LINE_DASHDOT, SWT.LINE_DASHDOTDOT }
		 */
		public int getStyle() {
			return style;
		}
		
		@Override
		public String toString() {
			return description;
		}
		
		public static String[] stringValues(){
			String[] sv = new String[values().length];
			int i=0;
			for(LineStyle p : values())
				sv[i++] = p.toString();
			return sv;
		}
	}