package org.csstudio.trends.databrowser.model;

import org.csstudio.archive.MetaData;
import org.csstudio.archive.NumericMetaData;

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
}
