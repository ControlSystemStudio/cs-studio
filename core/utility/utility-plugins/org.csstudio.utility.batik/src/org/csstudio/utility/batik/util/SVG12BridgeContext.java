/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.batik.util;

import org.apache.batik.anim.timing.TimedDocumentRoot;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.UserAgent;

/**
 * Extension of standard {@link org.apache.batik.bridge.svg12.SVG12BridgeContext} which uses the extended
 * {@link SVGAnimationEngine} in order to access {@link TimedDocumentRoot}.
 *
 * @author Fred Arnaud (Sopra Steria Group) - ITER
 */
public class SVG12BridgeContext extends org.apache.batik.bridge.svg12.SVG12BridgeContext {

    /**
     * Constructs a new bridge context.
     *
     * @param userAgent the user agent
     * @param loader document loader
     */
    public SVG12BridgeContext(UserAgent userAgent, DocumentLoader loader) {
        super(userAgent, loader);
    }

    /**
     * Returns the AnimationEngine for the document. Creates one if it doesn't exist.
     */
    @Override
    public org.apache.batik.bridge.SVGAnimationEngine getAnimationEngine() {
        if (animationEngine == null) {
            animationEngine = new SVGAnimationEngine(document, this);
            setAnimationLimitingMode();
        }
        return (org.apache.batik.bridge.SVGAnimationEngine) animationEngine;
    }

}
