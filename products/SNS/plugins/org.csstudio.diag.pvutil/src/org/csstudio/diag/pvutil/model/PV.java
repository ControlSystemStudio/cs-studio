package org.csstudio.diag.pvutil.model;

import org.csstudio.platform.model.IProcessVariable;
import org.eclipse.core.runtime.PlatformObject;

/** One device in the model: A PV with name, record type, FEC name, ...
 *  
 *  Also functions as a CSS IProcessVariable
 *  @see SNSRDBModelPV
 *  @author 9pj
 */
public class PV extends PlatformObject implements IProcessVariable
{
    final String pv, infoString;

    public PV( final String pv, final String infoString)
    {
        this.pv = pv;
        this.infoString = infoString;
    }
    
    /** @see IProcessVariable */
    public String getName()
    {
        return pv;
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

    public String getInfoString()
    {
        return infoString;
    }
}
