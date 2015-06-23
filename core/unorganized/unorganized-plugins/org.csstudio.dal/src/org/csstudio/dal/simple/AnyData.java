/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.dal.simple;

import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.Timestamp;

/**
 * Simple access to control system data.
 * Remote value and all accompanying values are gathered when this object is
 * created and remained fixed for the created object.
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
 *  @author Igor Kriznar
 */
public interface AnyData
{
    /** Get the time stamp.
     *  @return The time stamp.
     */
    public Timestamp getTimestamp();

    /** Get the <code>Severity</code> object.
     *  @see Severity
     *  @see #getStatus()
     *  @return The <code>Severity</code> object.
     */
    public Severity getSeverity();

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
        Interpolated,

        /**
         * Value is not valid. For example because of error or timeout.
         */
        Invalid
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
    public MetaData getMetaData();

    /**
     * Returns data value as string.
     * @return value of this data object, null if not possible.
     */
    public String stringValue();

    /**
     * Returns data value as double.
     * @return value of this data object.
     */
    public double doubleValue();

    /**
     * Returns data value as long.
     * @return value of this data object.
     */
    public long longValue();
    /**
     * Returns data value as string.
     * @return value of this data object.
     */
    public String[] stringSeqValue();

    /**
     * Returns data value as double.
     * @return value of this data object.
     */
    public double[] doubleSeqValue();

    /**
     * Returns data value as long.
     * @return value of this data object.
     */
    public long[] longSeqValue();

    /**
     * Returns data value as Number.
     * @return value of this data object.
     */
    public Number numberValue();

    /**
     * Returns data value as Number.
     * @return value of this data object.
     */
    public Number[] numberSeqValue();

    /**
     * Returns value of AnyData as Java Object, as it is without any data type conversion.
     * @return value of this data as Java Object, as it is without any data type conversion.
     */
    public Object anyValue();

    /**
     * Returns value of AnyData as array of Java Objects, as it is without any data type conversion.
     * @return value of this data as array of Java Objects, as it is without any data type conversion.
     */
    public Object[] anySeqValue();

    /**
     * @return Returns <code>true</code> only if this data object contains valid value.
     */
    public boolean isValid();

    /**
     * @return Returns parent data provider if exists, <code>null</code> otherwise. Reference to parent may be weak reference.
     */
    public AnyDataChannel getParentChannel();

    /**
     * @return Returns parent data provider as property, or <code>null</code>
     * if provider is not a property. Reference to parent may be weak reference.
     */
    public DynamicValueProperty<?> getParentProperty();

    /**
     * @return any data value presented in human-readable string
     */
    @Override
    public String toString();

    public long getBeamID();

}