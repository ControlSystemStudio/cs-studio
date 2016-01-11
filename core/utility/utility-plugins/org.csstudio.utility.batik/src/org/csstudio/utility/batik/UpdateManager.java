/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.batik;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Document;

/**
 * Extension of standard {@link UpdateManager} which allows to update the root
 * node and filters repaints.
 * @author Fred Arnaud (Sopra Steria Group) - ITER
 */
public class UpdateManager extends org.apache.batik.bridge.UpdateManager {

    public UpdateManager(BridgeContext ctx, GraphicsNode gn, Document doc) {
        super(ctx, gn, doc);
    }

    // FIXME: the SVG display freeze when this part is enabled
    // private long lastRepaintTime = 0;
    // private long maxRepaintTime = 200; // milliseconds
    //
    // /**
    // * Repaints the dirty areas, if needed.
    // */
    // protected void repaint() {
    // long cTime = System.currentTimeMillis();
    // if (cTime - lastRepaintTime > maxRepaintTime) {
    // lastRepaintTime = cTime;
    // super.repaint();
    // }
    // }
    //
    // public void setMaxRepaintTime(long maxRepaintTime) {
    // this.maxRepaintTime = maxRepaintTime;
    // }

    public void setGVTRoot(GraphicsNode newRoot) {
        this.graphicsNode = newRoot;
    }

}
