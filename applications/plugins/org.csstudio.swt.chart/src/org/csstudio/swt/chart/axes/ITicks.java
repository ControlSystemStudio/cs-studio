/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart.axes;

/** Tick marks of an X or Y Axis.
 *  @author Kay Kasemir
 */
public interface ITicks
{
    /** @return Returns the value of the start tick. */
    public double getStart();

    /** @return Returns the next tick, following a given tick mark. */
    public double getNext(double tick);

    /** @return Returns the number formatted according to the tick precision. */
    public String format(double num);
    
    /** @return Returns the number formatted with some extra precision. */
    public String format(double num, int extra_precision);
}
