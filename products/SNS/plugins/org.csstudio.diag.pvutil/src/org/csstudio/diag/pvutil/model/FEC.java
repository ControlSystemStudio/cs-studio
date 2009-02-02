package org.csstudio.diag.pvutil.model;

import org.csstudio.platform.model.IFrontEndControllerName;
import org.eclipse.core.runtime.PlatformObject;

/** One Front-End-Controller (FEC) in the PVUtilDataAPI.
 *  @see PVUtilDataAPI
 *  @author Dave Purcell
 */
public class FEC extends PlatformObject implements IFrontEndControllerName
{
    final private String fec_nm;

    public FEC(final String fec_nm)
    {
        this.fec_nm = fec_nm;
    }
    
    @Override
    public String toString()
    {
        return "Name: " + fec_nm; //$NON-NLS-1$
    }

    public String getName()
    {
    	return fec_nm;
    }

    // IFrontEndControllerName
	public String getTypeId() {
		return IFrontEndControllerName.TYPE_ID;
	}
}
