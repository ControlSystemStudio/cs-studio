/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.data.values;

import java.io.Serializable;

/** Base interface for all control system values.
 *  <p>
 *  The <code>Value</code> handles all the other 'stuff' that comes with
 *  a control system value except for the actual value itself:
 *  <ul>
 *  <li>Time stamp
 *  <li>A severity code that indicates if the data reflects some sort of warning
 *      or error state.
 *  <li>A status string that explains the severity.
 *  <li>Meta data that might be useful for displaying the data.
 *  </ul>
 *
 *  It also offers convenience routines for displaying values.
 *  <p>
 *  In some cases, that's already all that there might be, because
 *  a sample can have a severity that indicates that there was no value,
 *  and the status describes why.
 *  <p>
 *  In most cases, however, access to the actual data requires the specific
 *  subtypes <code>DoubleValue</code>, <code>StringValue</code> etc.
 *
 *  @see IDoubleValue
 *  @see ILongValue
 *  @see IStringValue
 *  @see IEnumeratedValue
 *  @see IMinMaxDoubleValue
 *  @author Kay Kasemir
 */
public interface IValue extends Serializable
{
    /** Get the time stamp.
     *  @return The time stamp.
     */
    public ITimestamp getTime();

    /** Get the severity info.
     *  @see ISeverity
     *  @see #getStatus()
     *  @return The severity info.
     */
    public ISeverity getSeverity();

    /** Get the status text that might describe the severity.
     *  @see #getSeverity()
     *  @return The status string.
     */
    public String getStatus();

    /** Describe the data quality.
     *  <p>
     *  Control system data can originate directly from a front-end controller,
     *  or from a data history archive that stored such front-end controller
     *  values.
     *  We consider this the 'original' data.
     *  <p>
     *  Mid-level data servers or history data servers might also offer
     *  processed data, which reduces several 'original' samples to for example
     *  an 'averaged' sample. For those processed values, the time stamp
     *  actually no longer matches one specific instance in time when the
     *  front-end controller obtained a sample.
     *  <p>
     *  While the quality code does not fully describe what happened to the
     *  data, it provides a hint to for example a plotting tool, so that
     *  processed samples can be shown in a different way from original
     *  samples.
     */
    public enum Quality
    {
        /** This is a raw, original sample. */
        Original,

        /** This value is the result of interpolating 'original' samples.
         *  <p>
         *  There are several possible examples:
         *  <ul>
         *  <li>The data type was changed from 'double' to 'integer',
         *      with a possible loss off precision.
         *  <li>This sample results from linear interpolation between two
         *      original samples.
         *  <li>This sample results from averaging over several original
         *      values.
         *  </ul>
         */
        Interpolated
    }

    /** Get the quality of this value.
     *  @see Quality
     *  @return The quality.
     */
    public Quality getQuality();

    /** Meta Data that helps with using the value, mostly for formatting.
     *  <p>
     *  It might be OK for some value types to only have <code>null</code>
     *  MetaData, while others might require a specific one like
     *  <code>NumericMetaData</code>.
     *  @return The Meta Data.
     */
    public IMetaData getMetaData();

    /** @see #format(Format, int) */
    public enum Format
    {
        /** Use all the MetaData information. */
        Default,

        /** If possible, use decimal representation. */
        Decimal,

        /** If possible, use exponential notation. */
        Exponential,

        /** If possible, convert to String */
        String
    }

    /** Format the value as a string.
     *  <p>
     *  This means only the numeric or string value.
     *  Not the timestamp, not the severity and status.
     *  <p>
     *  @param how Detail on how to format.
     *  @param precision Might be used by some format types to select for example
     *                   the number of digits after the decimal point.
     *                   A precision of '-1' might select the default-precision
     *                   obtained from the MetaData.
     *  @return This Value's value as a string.
     *  @see #toString()
     */
    public String format(Format how, int precision);

    /** Format the value via the Default format.
     *  Typically this means: using the meta data.
     *  <p>
     *  @return This Value's value as a string.
     *  @see #format(Format, int)
     *  @see #toString()
     */
    public String format();
}
