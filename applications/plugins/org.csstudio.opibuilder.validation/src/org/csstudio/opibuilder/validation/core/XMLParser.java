/*******************************************************************************
 * Copyright (c) 2010-2015 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.validation.core;

import org.jdom.DefaultJDOMFactory;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.input.SAXHandler;

/**
 * 
 * <code>XMLParser</code> provides a set of classes that parse an XML file and set the line number on each Element that
 * was parsed.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class XMLParser {

    /**
     * 
     * <code>LinedElement</code> is an element that also holds the line number at which it is located.
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     */
    public static class LinedElement extends Element {

        private static final long serialVersionUID = 1L;
        private final int lineNumber;

        public LinedElement(String name, Namespace namespace, int lineNumber) {
            super(name, namespace);
            this.lineNumber = lineNumber;
        }

        public int getLineNumber() {
            return lineNumber;
        }
    }

    private static class XMLLineBuilder extends SAXBuilder {

        private LineFactory factory = new LineFactory();

        public XMLLineBuilder() {
            super();
            setFactory(factory);
        }

        @Override
        protected void configureContentHandler(SAXHandler contentHandler) {
            super.configureContentHandler(contentHandler);
            factory.setSAXHandler(contentHandler);
        }

    }

    private static class LineFactory extends DefaultJDOMFactory {
        private SAXHandler saxHandler;

        public void setSAXHandler(SAXHandler sh) {
            this.saxHandler = sh;
        }

        public Element element(String name) {
            return this.element(name, (Namespace) null);
        }

        public Element element(String name, Namespace namespace) {
            return new LinedElement(name, namespace, saxHandler.getDocumentLocator().getLineNumber());
        }
    }

    /**
     * @return a new builder that parses an XML and sets the line numbers on the elements
     */
    public static SAXBuilder createBuilder() {
        return new XMLLineBuilder();
    }
}
