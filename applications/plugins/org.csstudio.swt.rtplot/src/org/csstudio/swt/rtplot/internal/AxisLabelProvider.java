/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal;

import org.eclipse.swt.graphics.RGB;

/** Used by the {@link YAxisImpl} to obtain labels.
 *
 *  <p>The number of traces on a Y Axis and thus
 *  the number of labels can change dynamically.
 *  This provides a snapshot of the labels and
 *  their color that is updated whenever calling
 *  <code>start</code>, then treating it like
 *  an iterator:
 *
 *  <pre>
 *  start();
 *  while (hasNext())
 *  {
 *      .. use getLabel(), getColor()
 *  }
 *  </pre>
 *
 *  @author Kay Kasemir
 */
public interface AxisLabelProvider
{
    /** Start another iteration of current labels */
    public void start();

    /** @return <code>true</code> if there is one more label */
    public boolean hasNext();

    /** @return Text of the label */
    public String getLabel();

    /** @return Color of the label */
    public RGB getColor();
}
