package org.csstudio.trends.databrowser.model;

import org.csstudio.platform.data.IValue;

/** Helper for displaying the 'Quality' of an IValue.
 *  @author Kay Kasemir
 */
public class QualityHelper
{
    public static String getString(IValue.Quality quality)
    {
        switch (quality)
        {
        case Original:
            return Messages.ModelSample_QualityOriginal;
        case Interpolated:
            return Messages.ModelSample_QualityInterpolated;
        }
        // There aren't any other, but w/o return we get compile error...
        return quality.name();
    }
}
