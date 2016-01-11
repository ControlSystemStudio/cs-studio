/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.batik.util;

import org.apache.batik.anim.timing.TimedDocumentRoot;
import org.apache.batik.bridge.BridgeContext;
import org.w3c.dom.Document;

/**
 * Extension of standard {@link org.apache.batik.bridge.SVGAnimationEngine} which allows to access the
 * {@link TimedDocumentRoot}.
 *
 * @author Fred Arnaud (Sopra Steria Group) - ITER
 */
public class SVGAnimationEngine extends org.apache.batik.bridge.SVGAnimationEngine {

    public SVGAnimationEngine(Document doc, BridgeContext ctx) {
        super(doc, ctx);
    }

    public TimedDocumentRoot getTimedDocumentRoot() {
        return timedDocumentRoot;
    }

}
