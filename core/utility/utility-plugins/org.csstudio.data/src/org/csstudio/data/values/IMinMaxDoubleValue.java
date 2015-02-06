/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.data.values;

/** A double-typed value that also has a minimum and maximum,
 *  usually as the result of averaging or otherwise interpolating
 *  over raw samples.
 *  @see IDoubleValue
 *  @author Kay Kasemir
 */
public interface IMinMaxDoubleValue extends IDoubleValue
{
    /** @return Minimum of the original values. */
    public double getMinimum();

    /** @return Maximum of the original values. */
    public double getMaximum();
}
