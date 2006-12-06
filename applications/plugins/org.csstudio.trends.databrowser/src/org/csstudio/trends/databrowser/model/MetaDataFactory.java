package org.csstudio.trends.databrowser.model;

import org.csstudio.value.NumericMetaData;

/** Helper for creating some generic MetaData instances.
 *  @author Kay Kasemir
 */
public class MetaDataFactory
{
    private static NumericMetaData numeric = null;
    
    /** Don't instantiate. */
    private MetaDataFactory() {}
    
    /** @return A bogus instance of NumericMetaData. */
    public static NumericMetaData getNumeric()
    {
        if (numeric == null)
            numeric = new NumericMetaData(10, 0, 0, 0, 0, 0, 0, ""); //$NON-NLS-1$
        return numeric;
    }
}
