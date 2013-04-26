/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan;

import java.io.Serializable;
import java.util.Arrays;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.data.values.IValue;

/** Transfer object that can be used in object contributions
 *  and for drag-and-drop to interface the data browser to
 *  other CSS tools
 *
 *  @author Kay Kasemir
 */
public class ProcessVariableWithSamples implements Serializable
{
    private static final long serialVersionUID = 1L;

    final private ProcessVariable pv;
    final IValue[] samples;

    /** Initialize
     *  @param pv {@link ProcessVariable}
     *  @param samples Samples of type {@link IValue}
     */
    public ProcessVariableWithSamples(final ProcessVariable pv, final IValue[] samples)
    {
        this.pv = pv;
        this.samples = samples;
    }

    public ProcessVariable getProcessVariable()
    {
        return pv;
    }

    public IValue[] getSamples()
    {
        return samples;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pv == null) ? 0 : pv.hashCode());
        result = prime * result + Arrays.hashCode(samples);
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj) return true;
        if (! (obj instanceof ProcessVariableWithSamples))
            return false;
        final ProcessVariableWithSamples other = (ProcessVariableWithSamples) obj;
        return pv.equals(other.pv)  &&  Arrays.equals(samples, other.samples);
    }
}
