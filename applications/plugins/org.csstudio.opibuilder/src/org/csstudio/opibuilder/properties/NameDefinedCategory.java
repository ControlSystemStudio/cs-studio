/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.properties;

/** A property category whose name is specified from the constructor input.
 * @author Xihui Chen
 *
 */
public class NameDefinedCategory implements WidgetPropertyCategory{
		private String name;
		
		public NameDefinedCategory(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
		
	}
