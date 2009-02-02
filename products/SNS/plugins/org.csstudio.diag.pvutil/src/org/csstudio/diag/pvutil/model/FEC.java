package org.csstudio.diag.pvutil.model;

import org.csstudio.platform.model.IFrontEndControllerName;

/** One FEC in the PVUtilDataAPI.
 *  @see SNSRDBModelPV
 *  @author 9pj
 */
public class FEC  implements IFrontEndControllerName
{
    final String fec_nm;

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

	public String getTypeId() {
		return IFrontEndControllerName.TYPE_ID;
	}

	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}
}
