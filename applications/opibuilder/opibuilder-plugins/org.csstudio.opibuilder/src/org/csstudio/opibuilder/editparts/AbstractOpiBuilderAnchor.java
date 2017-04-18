/****************************************************************************
* Copyright (c) 2010-2017 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
****************************************************************************/
package org.csstudio.opibuilder.editparts;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;

/**
 * We have multiple implementations of the Anchor, and the
 * {@link FixedPointsConnectionRouter} needs to know orientation in
 * which the connector connects to the widget in order to correctly
 * calculate the route.
 * This becomes especially important once the widgets change positions
 * (are animated) through some action.
 *
 * @author mvitorovic
 */
abstract public class AbstractOpiBuilderAnchor extends AbstractConnectionAnchor {

    /**
     * This enum tells in which direction a connector is connected to the widget
     *
     * @author mvitorovic
     */
    public static enum ConnectorOrientation { HORIZONTAL, VERTICAL }

    public AbstractOpiBuilderAnchor(IFigure owner) {
        super(owner);
    }

    /**
     * @return The direction in which the connection is oriented at this anchor, if the Manhattan router is being selected
     */
    abstract public ConnectorOrientation getOrientation();
}
