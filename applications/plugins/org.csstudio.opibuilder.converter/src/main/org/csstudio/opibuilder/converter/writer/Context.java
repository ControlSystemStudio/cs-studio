/**
 * Copyright (c) 2009, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
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

package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.EdmDisplay;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class contains the context information that is passed when generating OPI
 * XML output.
 * 
 * @author ssah
 */
public class Context {
	private Document document;
	private Element element;
	private EdmDisplay rootDisplay;
	private int x;
	private int y;
	
	/**
	 * Creates a context with all information.
	 */
	public Context(Document document, Element element, EdmDisplay rootWidget,int x, int y) {
		super();
		this.document = document;
		this.element = element;
		this.rootDisplay = rootWidget;
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns current DOM document.
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 * Sets current DOM document.
	 */
	public void setDocument(Document document) {
		this.document = document;
	}
	
	/**
	 * Returns the current DOM element.
	 */
	public Element getElement() {
		return element;
	}

	/**
	 * @return the root display.
	 */
	public EdmDisplay getRootDisplay() {
		return rootDisplay;
	}
	
	/**Set the root display widget
	 * If it is not a widget, set this to null.
	 * @param rootDisplay 
	 */
	public void setRootDisplay(EdmDisplay rootDisplay) {
		this.rootDisplay = rootDisplay;
	}
	
	/**
	 * Sets current DOM element.
	 */
	public void setElement(Element element) {
		this.element = element;
	}

	/**
	 * Returns the element absolute x position.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets the element absolute x position.
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Returns the element absolute y position.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Returns the element absolute y position.
	 */
	public void setY(int y) {
		this.y = y;
	}
}
