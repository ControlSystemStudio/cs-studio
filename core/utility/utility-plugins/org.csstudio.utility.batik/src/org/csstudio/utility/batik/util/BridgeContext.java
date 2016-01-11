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
import org.apache.batik.script.InterpreterPool;

/**
 * Extension of standard {@link org.apache.batik.bridge.BridgeContext} which uses the extended
 * {@link SVGAnimationEngine} in order to access {@link TimedDocumentRoot}.
 *
 * @author Fred Arnaud (Sopra Steria Group) - ITER
 */
public class BridgeContext extends org.apache.batik.bridge.BridgeContext {

    /**
     * By default we share a unique instance of InterpreterPool.
     */
    private static InterpreterPool sharedPool = new InterpreterPool();

    /**
     * Constructs a new bridge context.
     *
     * @param userAgent the user agent
     * @param loader document loader
     */
    public BridgeContext(UserAgent userAgent, DocumentLoader loader) {
        super(userAgent, sharedPool, loader);
    }

    /**
     * Returns the AnimationEngine for the document. Creates one if it doesn't exist.
     */
    public org.apache.batik.bridge.SVGAnimationEngine getAnimationEngine() {
        if (animationEngine == null) {
            animationEngine = new SVGAnimationEngine(document, this);
            setAnimationLimitingMode();
        }
        return (org.apache.batik.bridge.SVGAnimationEngine) animationEngine;
    }

}
