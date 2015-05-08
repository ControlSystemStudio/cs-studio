/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.xygraph.figures;

/**
 * A listener on an annotation when annotation position was changed.
 * @author Xihui Chen
 *
 */
public interface IAnnotationListener {

    /**
     * This event indicates a change in the axis' value range
     */
    public void annotationMoved(double oldX, double oldY, double newX, double newY);


}
