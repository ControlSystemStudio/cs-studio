package org.csstudio.diag.pvutil.model;

import org.csstudio.platform.model.IProcessVariable;
import org.eclipse.core.runtime.PlatformObject;

/** One PV in the model: PV name and additional info text
 *  
 *  Also functions as a CSS IProcessVariable
 *  @see PVUtilDataAPI
 *  @author Dave Purcell
 */
public class PV extends PlatformObject implements IProcessVariable
{
    final private String pv, infoString;

    public PV(final String pv, final String infoString)
    {
        this.pv = pv;
        this.infoString = infoString;
    }
    
    /** @see IProcessVariable */
    public String getName()
    {
        return pv;
    }

    public String getInfoString()
    {
        return infoString;
    }

    /** @see IProcessVariable */
    public String getTypeId()
    {
        return IProcessVariable.TYPE_ID;
    }

    @Override
    public String toString()
    {
        return "PV: " + pv; //$NON-NLS-1$
    }
}
