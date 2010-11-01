/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv;

/** Interface that implementors of the PVFactory extension point
 *  must provide.
 *  @author Kay Kasemir
 */
public interface IPVFactory
{
    /** Create a PV for the given channel name.
     *  @param name Name of the Process Variable
     *  @return PV
     *  @throws Exception on error
     */
    public PV createPV(String name) throws Exception;
}
