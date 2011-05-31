/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.data.values;

/** An enumerated value.
 *  <p>
 *  Enumerated types carry a limited number of integer values,
 *  where each possible value represents a state with a string representation.
 *  <p>
 *  {@link IEnumeratedValue} values go with {@link IEnumeratedMetaData}
 *  @see IValue
 *  @see EnumeratedMetaData
 *  @author Kay Kasemir
 */
public interface IEnumeratedValue extends IValue
{
    /** @return Returns the whole array of values. */
    public int[] getValues();

    /** @return Returns the first array element.
     *  <p>
     *  Since most values are probably scalars, this is a convenient
     *  way to get that one and only element.
     *  @see #getValues
     */
    public int getValue();

    /**
     * {@inheritDoc}
     */
    @Override
    public IEnumeratedMetaData getMetaData();
}
