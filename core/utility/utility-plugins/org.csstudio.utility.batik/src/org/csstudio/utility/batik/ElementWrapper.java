/*******************************************************************************
 * Copyright (c) 2010-2015 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.batik;

import org.w3c.dom.Element;

/**
 * @author Fred Arnaud (Sopra Steria Group) - ITER
 */
public class ElementWrapper extends AbstractNodeWrapper {

	private final Element element;
	private final String originalStyle;

	public ElementWrapper(Element element) {
		this.element = element;
		this.originalStyle = element.getAttribute("style");
	}

	@Override
	protected String getOriginalData() {
		return originalStyle;
	}

	@Override
	protected String getData() {
		return element.getAttribute("style");
	}

	@Override
	protected void setData(String data) {
		element.setAttribute("style", data);
	}

	@Override
	protected void reset() {
		element.setAttribute("style", originalStyle);
	}

}
