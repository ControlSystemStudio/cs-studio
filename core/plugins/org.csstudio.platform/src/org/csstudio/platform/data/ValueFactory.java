package org.csstudio.platform.data;

import org.csstudio.platform.data.IValue.Quality;
import org.csstudio.platform.internal.data.DoubleValue;
import org.csstudio.platform.internal.data.EnumeratedValue;
import org.csstudio.platform.internal.data.EnumeratedMetaData;
import org.csstudio.platform.internal.data.IntegerValue;
import org.csstudio.platform.internal.data.NumericMetaData;
import org.csstudio.platform.internal.data.SeverityInstances;
import org.csstudio.platform.internal.data.StringValue;

/** Factory for IValue-based types.
 *  @author Kay Kasemir
 */
public class ValueFactory
{
    /** Prohibit instance creation. */
    private ValueFactory()
    { /* NOP */ }
    
    /** Create an 'OK' ISeverity.
     *  @return ISeverity
     */
    public static final ISeverity createOKSeverity()
    {
        return SeverityInstances.ok;
    }
    
    /** Create a 'minor' ISeverity.
     *  @return ISeverity
     */
    public static final ISeverity createMinorSeverity()
    {
        return SeverityInstances.minor;
    }

    /** Create a 'major' ISeverity.
     *  @return ISeverity
     */
    public static final ISeverity createMajorSeverity()
    {
        return SeverityInstances.major;
    }

    /** Create an 'invalid' ISeverity.
     *  @return ISeverity
     */
    public static final ISeverity createInvalidSeverity()
    {
        return SeverityInstances.invalid;
    }

    /** Create instance of {@link INumericMetaData}.
     *  @param disp_low Low end of suggested display range.
     *  @param disp_high High end of suggested display range.
     *  @param warn_low Lower warning limit.
     *  @param warn_high Upper warning limit.
     *  @param alarm_low Lower alarm limit.
     *  @param alarm_high Upper alarm limit.
     *  @param prec Suggested display precision.
     *  @param units Engineering units string.
     *  @return Instance of INumericMetaData.
     */
    public static final INumericMetaData
        createNumericMetaData(double disp_low, double disp_high,
                        double warn_low, double warn_high,
                        double alarm_low, double alarm_high,
                        int prec, String units)
    {
        return new NumericMetaData(disp_low, disp_high, warn_low, warn_high,
                        alarm_low, alarm_high, prec, units);
    }
    
    /** Create instance of {@link IEnumeratedMetaData}.
     *  @param states State strings
     *  @return Instance of IEnumeratedMetaData.
     */
    public static final IEnumeratedMetaData
        createEnumeratedMetaData(String states[])
    {
        return new EnumeratedMetaData(states);
    }

    /** Create instance of {@link IDoubleValue}.
     *  @param time Time stamp
     *  @param severity Severity descriptor
     *  @param status Status string.
     *  @param meta_data Numeric meta data.
     *  @param quality Data quality descriptor.
     *  @param values The actual values.
     *  @return Instance of IDoubleValue.
     */
    public static final IDoubleValue
        createDoubleValue(final ITimestamp time, final ISeverity severity,
                    final String status, final INumericMetaData meta_data,
                    final Quality quality,
                    final double values[])
    {
        return new DoubleValue(time, severity, status, meta_data,
                        quality, values);
    }

    /** Create instance of {@link IIntegerValue}.
     *  @param time Time stamp
     *  @param severity Severity descriptor
     *  @param status Status string.
     *  @param meta_data Numeric meta data.
     *  @param quality Data quality descriptor.
     *  @param values The actual values.
     *  @return Instance of IIntegerValue.
     */
    public static final IIntegerValue
        createIntegerValue(final ITimestamp time, final ISeverity severity,
                    final String status, final INumericMetaData meta_data,
                    final Quality quality,
                    final int values[])
    {
        return new IntegerValue(time, severity, status, meta_data,
                        quality, values);
    }

    /** Create instance of {@link IEnumeratedValue}.
     *  @param time Time stamp
     *  @param severity Severity descriptor
     *  @param status Status string.
     *  @param meta_data Enumerated meta data.
     *  @param quality Data quality descriptor.
     *  @param values The actual values.
     *  @return Instance of IEnumValue.
     */
    public static final IEnumeratedValue
        createEnumeratedValue(final ITimestamp time, final ISeverity severity,
                    final String status, final IEnumeratedMetaData meta_data,
                    final Quality quality,
                    final int values[])
    {
        return new EnumeratedValue(time, severity, status, meta_data,
                             quality, values);
    }

    /** Create instance of {@link IStringValue}.
     *  @param time Time stamp
     *  @param severity Severity descriptor
     *  @param status Status string.
     *  @param quality Data quality descriptor.
     *  @param values The actual values.
     *  @return Instance of IStringValue.
     */
    public static final IStringValue
        createStringValue(final ITimestamp time, final ISeverity severity,
                    final String status, final Quality quality,
                    final String value)
    {
        return new StringValue(time, severity, status, quality, value);
    }
}
