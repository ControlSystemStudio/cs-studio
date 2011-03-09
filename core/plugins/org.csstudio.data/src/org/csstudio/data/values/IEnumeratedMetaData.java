/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.data.values;

/** The enumerated version of {@link IMetaData}.
 *  <p>
 *  Handles the mapping from enumeration integers to state strings.
 *
 *  @see IMetaData
 *  @see IEnumeratedValue
 *  @author Kay Kasemir
 */
public interface IEnumeratedMetaData extends IMetaData
{
    /** Obtain the state strings.
     *  <p>
     *  The array element <code>i</code> represends state number <code>i</code>.
     *
     *  @return The state string array, never <code>null</code>.
     */
    public String[] getStates();

    /** Convenience routine for getting a state string.
     *  <p>
     *  Also allows getting undefined states. Applications that want to
     *  specifically handle undefined states should use
     *  <code>getStates()</code> instead.
     *  @param state The state to get.
     *  @return A state string.
     */
    public String getState(int state);
}
