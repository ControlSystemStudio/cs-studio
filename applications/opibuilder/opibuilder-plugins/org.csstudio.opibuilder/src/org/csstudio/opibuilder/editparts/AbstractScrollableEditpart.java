/****************************************************************************
* Copyright (c) 2010-2017 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
****************************************************************************/
package org.csstudio.opibuilder.editparts;

import org.eclipse.draw2d.ScrollPane;

/**
 * This is an abstract class which represents all Container Editparts which can
 * be scrollable and have a {@link ScrollPane}
 *
 * @author mvitorovic
 */
public abstract class AbstractScrollableEditpart extends AbstractContainerEditpart {

    /**
     * @return The {@link ScrollPane} of this scrollable EditPart
     */
    public abstract ScrollPane getScrollPane();

}
