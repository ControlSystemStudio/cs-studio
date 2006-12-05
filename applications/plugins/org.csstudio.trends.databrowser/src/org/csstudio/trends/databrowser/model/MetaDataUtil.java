package org.csstudio.trends.databrowser.model;

import org.csstudio.archive.EnumeratedMetaData;
import org.csstudio.archive.MetaData;
import org.csstudio.archive.NumericMetaData;
import org.csstudio.utility.pv.EnumValue;
import org.csstudio.utility.pv.NumericValue;
import org.csstudio.utility.pv.StringValue;
import org.csstudio.utility.pv.Value;

/** Helper for creating some generic MetaData instances.
 *  @author Kay Kasemir
 */
public class MetaDataUtil
{
    private static MetaData numeric = null;
    
    /** Don't instanciate. */
    private MetaDataUtil() {}
    
    /** @return Instance of 'invalid' Severity. */
    public static MetaData getNumeric()
    {
        if (numeric == null)
            numeric = new NumericMetaData(10, 0, 0, 0, 0, 0, 0, ""); //$NON-NLS-1$
        return numeric;
    }
    
    /** This is a slight mess:
     *  In principle, both the archiver and the PV lib should
     *  use the same MetaData,
     *  but for now they don't, so that they can be used independently.
     *  <p>
     *  So this basically turns the PV Meta Data of the Value
     *  into Archive MetaData.
     *  
     *  @return (archive) MetaData for a (PV) Value. */
    public static MetaData forValue(Value value)
    {
        if (value == null  ||  value instanceof StringValue)
            return null;
        if (value instanceof EnumValue)
        {
            org.csstudio.utility.pv.EnumeratedMetaData pv_meta =
                (org.csstudio.utility.pv.EnumeratedMetaData)
                                        ((EnumValue) value).getMeta();
            return new EnumeratedMetaData(pv_meta.getStates());
        }
        org.csstudio.utility.pv.NumericMetaData pv_meta =
            (org.csstudio.utility.pv.NumericMetaData)
                                    ((NumericValue) value).getMeta();
                            
        return new NumericMetaData(10, 0, 0, 0, 0, 0,
                        pv_meta.getPrecision(), pv_meta.getUnits());
    }
}
