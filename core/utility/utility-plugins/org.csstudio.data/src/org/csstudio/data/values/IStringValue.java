/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.data.values;

/** An string value.
 *  <p>
 *  <p>
 *  {@link IStringValue} values have no meta data, i.e.
 *  <code>getMetaData()</code> will return <code>null</code>!
 *  @see IValue
 *  @author Kay Kasemir
 */
public interface IStringValue extends IValue
{
    /** @return Returns the whole array of values. */
    public String[] getValues();

    /** @return Returns the first array element.
     *  <p>
     *  Since most samples are probably scalars, this is a convenient
     *  way to get that one and only element.
     *  @see #getValues
     */
    public String getValue();
}
