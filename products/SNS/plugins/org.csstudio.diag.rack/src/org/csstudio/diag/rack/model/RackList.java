package org.csstudio.diag.rack.model;

import org.eclipse.core.runtime.PlatformObject;

/** Devices within a rack;
 *  
 *  String dvc_id is the device in the rack.
 *  int bgn is the lower "U" position
 *  int end is the upper "U" position 
 *  String dvc_type_id is the type of device 
 *  String bl_dvc_ind  indication of beam-line device. (Probably not needed.)
 *  
 *  
 *  @see SNSRDBModelRack
 *  @author 9pj
 */
public class RackList extends PlatformObject 
{
    final String dvc_id, dvc_type_id, bl_dvc_ind;
    final int bgn, end;

    public RackList( final String dvc_id, final int bgn, final int end, final String dvc_type_id, final String bl_dvc_ind)
    {
        this.dvc_id = dvc_id;
        this.bgn = bgn;
        this.end = end;
        this.dvc_type_id = dvc_type_id;
        this.bl_dvc_ind = bl_dvc_ind;
    }
    
    public String getDvcId()
    {
        return dvc_id;
    }

    public int getBGN()
    {
        return bgn;
    }

    public int getEND()
    {
        return end;
    }

    public String getDvcTypeId()
    {
        return dvc_type_id;
    }
    public String getBlDvcInd()
    {
        return bl_dvc_ind;
    }
}
