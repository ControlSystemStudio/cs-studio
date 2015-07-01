/*******************************************************************************
 * Copyright (c) 2015 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.simplepv.vtypepv;

import java.util.concurrent.Executor;

import org.csstudio.simplepv.AbstractPVFactory;
import org.csstudio.simplepv.ExceptionHandler;
import org.csstudio.simplepv.IPV;

/** Factory for vtype.pv-based implementations
 *  of the opibuilder {@link IPV}
 *  @author Kay Kasemir
 */
public class VTypePVFactory extends AbstractPVFactory
{
    @Override
    public IPV createPV(final String name, final boolean readOnly,
            final long minUpdatePeriodInMs, final boolean bufferAllValues,
            final Executor notificationThread, final ExceptionHandler exceptionHandler)
            throws Exception
    {
        return new VTypePV(name, readOnly, notificationThread, exceptionHandler);
    }
}
