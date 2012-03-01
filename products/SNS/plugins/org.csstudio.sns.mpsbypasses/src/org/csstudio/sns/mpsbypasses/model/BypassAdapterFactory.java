package org.csstudio.sns.mpsbypasses.model;

import org.csstudio.csdata.ProcessVariable;
import org.eclipse.core.runtime.IAdapterFactory;

/** Factory for adapter from {@link Bypass} to {@link ProcessVariable}
 *
 *  <p>Allows bypass table's context menu to link
 *  to Probe, Data Browser etc. to inspect the PVs
 *
 *  @author Kay Kasemir
 */
public class BypassAdapterFactory implements IAdapterFactory
{
    final private static Class<?>[] targets = new Class<?>[]
    {
        ProcessVariable[].class,
        ProcessVariable.class,
    };

    /** {@inheritDoc} */
    @Override
    public Class<?>[] getAdapterList()
    {
        return targets;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(final Object adaptableObject, final Class adapterType)
    {
        if (! (adaptableObject instanceof Bypass))
            return null;

        final Bypass bypass = (Bypass) adaptableObject;
        final ProcessVariable[] pvs = new ProcessVariable[]
        {
            new ProcessVariable(bypass.getJumperPVName()),
            new ProcessVariable(bypass.getMaskPVName())
        };

        if (adapterType == ProcessVariable[].class)
            return pvs;
        if (adapterType == ProcessVariable.class)
            return pvs[0];
        return null;
    }
}
