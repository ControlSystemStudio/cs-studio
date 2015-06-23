/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

package org.csstudio.dal.epics;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.TimeStamp;
import gov.aps.jca.event.PutListener;
import gov.aps.jca.jni.JNIException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.csstudio.dal.CommonDataTypes;
import org.csstudio.dal.DoubleProperty;
import org.csstudio.dal.DoubleSeqProperty;
import org.csstudio.dal.DoubleSeqSimpleProperty;
import org.csstudio.dal.DoubleSimpleProperty;
import org.csstudio.dal.EnumProperty;
import org.csstudio.dal.EnumSimpleProperty;
import org.csstudio.dal.LongProperty;
import org.csstudio.dal.LongSeqProperty;
import org.csstudio.dal.LongSeqSimpleProperty;
import org.csstudio.dal.LongSimpleProperty;
import org.csstudio.dal.PatternProperty;
import org.csstudio.dal.PatternSimpleProperty;
import org.csstudio.dal.SimpleProperty;
import org.csstudio.dal.StringProperty;
import org.csstudio.dal.StringSeqProperty;
import org.csstudio.dal.StringSeqSimpleProperty;
import org.csstudio.dal.StringSimpleProperty;
import org.csstudio.dal.Timestamp;
import org.csstudio.dal.impl.DoublePropertyImpl;
import org.csstudio.dal.impl.DoubleSeqPropertyImpl;
import org.csstudio.dal.impl.EnumPropertyImpl;
import org.csstudio.dal.impl.LongPropertyImpl;
import org.csstudio.dal.impl.LongSeqPropertyImpl;
import org.csstudio.dal.impl.PatternPropertyImpl;
import org.csstudio.dal.impl.StringPropertyImpl;
import org.csstudio.dal.impl.StringSeqPropertyImpl;
import org.csstudio.dal.proxy.PropertyProxy;
import org.csstudio.dal.spi.AbstractFactory;
import org.csstudio.dal.spi.Plugs;

/**
 * Convenience method for work with JCA and CAJ.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public final class PlugUtilities
{
    /** Seconds of epoch start since UTC time start. */
    public static long TS_EPOCH_SEC_PAST_1970 = 7305 * 86400;


    /**
     * Get property implementation type of property DBR type.
     * @param type    DBR type.
     * @param elementCount element count.
     * @return property implementation type.
     */
    public static Class<? extends SimpleProperty<?>> getPropertyImplForDBRType(final DBRType type, final int elementCount) {

        if (elementCount == 1)
        {
            if (type.isDOUBLE()) {
                return DoublePropertyImpl.class;
            } else if (type.isFLOAT()) {
                return DoublePropertyImpl.class;
            } else if (type.isBYTE()) {
                return LongPropertyImpl.class;
            } else if (type.isSHORT()) {
                return LongPropertyImpl.class;
            } else if (type.isINT()) {
                return LongPropertyImpl.class;
            } else if (type.isENUM()) {
                return EnumPropertyImpl.class;
            } else if (type.isSTRING()) {
                return StringPropertyImpl.class;
            }
        }
        else
        {
            if (type.isDOUBLE()) {
                return DoubleSeqPropertyImpl.class;
            } else if (type.isFLOAT()) {
                return DoubleSeqPropertyImpl.class;
            } else if (type.isBYTE()) {
                return StringPropertyImpl.class;
            } else if (type.isSHORT()) {
                return LongSeqPropertyImpl.class;
            } else if (type.isINT()) {
                return LongSeqPropertyImpl.class;
            } else if (type.isSTRING()) {
                return StringSeqPropertyImpl.class;
            }
        }

        throw new RuntimeException("Unsupported channel type.");
    }

    /**
     * Initialize supported proxy implementations.
     * @param plug plug to be initialized.
     */
    public static void initializeSupportedProxyImplementations(final EPICSPlug plug) {

        plug.registerPropertyProxyImplementationClass(DoublePropertyImpl.class, DoublePropertyProxyImpl.class);

        plug.registerPropertyProxyImplementationClass(LongPropertyImpl.class, LongPropertyProxyImpl.class);

        plug.registerPropertyProxyImplementationClass(PatternPropertyImpl.class, PatternPropertyProxyImpl.class);

        plug.registerPropertyProxyImplementationClass(EnumPropertyImpl.class, EnumPropertyProxyImpl.class);

        plug.registerPropertyProxyImplementationClass(StringPropertyImpl.class, StringPropertyProxyImpl.class);

        plug.registerPropertyProxyImplementationClass(DoubleSeqPropertyImpl.class, DoubleSeqPropertyProxyImpl.class);

        plug.registerPropertyProxyImplementationClass(LongSeqPropertyImpl.class, LongSeqPropertyProxyImpl.class);

        plug.registerPropertyProxyImplementationClass(StringSeqPropertyImpl.class, StringSeqPropertyProxyImpl.class);

    }

    /**
     * Get proxy implementation class for property type.
     * @param propertyType property type.
     * @return proxy implementation class.
     */
    public static Class<? extends PropertyProxy> getPropertyProxyImplementationClass(final Class<? extends SimpleProperty> propertyType){

        if (propertyType.equals(DoubleProperty.class) || propertyType.equals(DoubleSimpleProperty.class) ) {
            return DoublePropertyProxyImpl.class;
        }
        if (propertyType.equals(LongProperty.class) || propertyType.equals(LongSimpleProperty.class) ) {
            return LongPropertyProxyImpl.class;
        }
        if (propertyType.equals(PatternProperty.class) || propertyType.equals(PatternSimpleProperty.class) ) {
            return PatternPropertyProxyImpl.class;
        }
        if (propertyType.equals(EnumProperty.class) || propertyType.equals(EnumSimpleProperty.class) ) {
            return EnumPropertyProxyImpl.class;
        }
        if (propertyType.equals(StringProperty.class) || propertyType.equals(StringSimpleProperty.class) ) {
            return StringPropertyProxyImpl.class;
        }


        if (propertyType.equals(DoubleSeqProperty.class) || propertyType.equals(DoubleSeqSimpleProperty.class) ) {
            return DoubleSeqPropertyProxyImpl.class;
        }
        if (propertyType.equals(LongSeqProperty.class) || propertyType.equals(LongSeqSimpleProperty.class) ) {
            return LongSeqPropertyProxyImpl.class;
        } /*
        if (propertyType.equals(PatternSeqProperty.class) || propertyType.equals(PatternSeqSimpleProperty.class) ) {
            return PatternSeqPropertyProxyImpl.class;
        }
        if (propertyType.equals(EnumSeqProperty.class) || propertyType.equals(EnumSeqSimpleProperty.class) ) {
            return EnumSeqPropertyProxyImpl.class;
        } */
        if (propertyType.equals(StringSeqProperty.class) || propertyType.equals(StringSeqSimpleProperty.class) ) {
            return StringSeqPropertyProxyImpl.class;
        }

        throw new RuntimeException(propertyType + " not supported by EPICS plug.");
    }

    /**
     * Convert DBR to Java object.
     * @param dbr DBR to convet.
     * @param javaType type to convert to.
     * @param originalType the original channel field type (used in case the channel type
     *                 is different than the type presented in DAL)
     * @return converted java object.
     */
    public static <T> T toJavaValue(final DBR dbr, final Class<T> javaType, final DBRType originalType)
    {
        if (javaType == null) {
            throw new NullPointerException("javaType");
        }

        if (dbr == null || dbr.getValue() == null) {
            throw new NullPointerException("dbr");
        }

        if (javaType.equals(Double.class)) {
            if (dbr.isDOUBLE()) {
                return javaType.cast(new Double(((double[])dbr.getValue())[0]));
            }

            if (dbr.isFLOAT()) {
                return javaType.cast(new Double(((float[])dbr.getValue())[0]));
            }
        }

        if (javaType.equals(double[].class)) {
            if (dbr.isDOUBLE()) {
                return javaType.cast(dbr.getValue());
            }

            if (dbr.isFLOAT()) {
                final float[] f = (float[])dbr.getValue();
                final double[] d = new double[f.length];

                for (int i = 0; i < d.length; i++) {
                    d[i] = f[i];
                }

                return javaType.cast(d);
            }
        }

        if (javaType.equals(Long.class)) {
            if (dbr.isINT()) {
                return javaType.cast(new Long(((int[])dbr.getValue())[0]));
            }

            if (dbr.isBYTE()) {
                return javaType.cast(new Long(((byte[])dbr.getValue())[0]));
            }

            if (dbr.isSHORT()) {
                return javaType.cast(new Long(((short[])dbr.getValue())[0]));
            }

            if (dbr.isENUM()) {
                return javaType.cast( new Long(((short[])dbr.getValue())[0]));
            }
        }

        if (javaType.equals(long[].class)) {
            if (dbr.isINT()) {
                final int[] f = (int[])dbr.getValue();
                final long[] d = new long[f.length];

                for (int i = 0; i < d.length; i++) {
                    d[i] = f[i];
                }

                return javaType.cast(d);
            }

            if (dbr.isBYTE()) {
                final byte[] f = (byte[])dbr.getValue();
                final long[] d = new long[f.length];

                for (int i = 0; i < d.length; i++) {
                    d[i] = f[i];
                }

                return javaType.cast(d);
            }

            if (dbr.isSHORT()) {
                final short[] f = (short[])dbr.getValue();
                final long[] d = new long[f.length];

                for (int i = 0; i < d.length; i++) {
                    d[i] = f[i];
                }

                return javaType.cast(d);
            }

            if (dbr.isENUM()) {
                final short[] f = (short[])dbr.getValue();
                final long[] d = new long[f.length];

                for (int i = 0; i < d.length; i++) {
                    d[i] = f[i];
                }

                return javaType.cast(d);
            }
        }

        if (javaType.equals(String.class)) {
            if (dbr.isSTRING()) {
                //if type is char, return string composed of chars else return first element

                if (originalType.isBYTE()) {
                    final String[] val = (String[])dbr.getValue();
                    int ascii;
                    final StringBuilder sb = new StringBuilder();
                    for (final String element : val) {
                        // convert string into integer (represents the ASCII value of the character)
                        ascii = Integer.valueOf(element).intValue();
                        if (ascii != 0 ) {
                            // create 'real' char from ASCII number
                            // char singleChar = (char) ascii;

                            // create new string from list of chars
                            sb.append( (char) ascii);
                        } else {
                            break;
                        }
                    }
                    return javaType.cast(sb.toString());
                } else {
                    return javaType.cast(((String[])dbr.getValue())[0]);
                }
            }
        }

        if (javaType.equals(String[].class)) {
            if (dbr.isSTRING()) {
                return javaType.cast(dbr.getValue());
            }
        }

        if (javaType.equals(BitSet.class)) {
            if (dbr.isENUM()) {
                return javaType.cast(fromLong(((short[])dbr.getValue())[0]));
            }
            if (dbr.isBYTE()) {
                return javaType.cast(fromLong(((byte[])dbr.getValue())[0]));
            }
            if (dbr.isSHORT()) {
                return javaType.cast(fromLong(((short[])dbr.getValue())[0]));
            }
            if (dbr.isINT()) {
                return javaType.cast(fromLong(((int[])dbr.getValue())[0]));
            }
        }

        if (javaType.equals(Object.class)) {
            return javaType.cast(Array.get(dbr.getValue(), 0));
        }

        if (javaType.equals(Object[].class)) {
            return javaType.cast(dbr.getValue());
        }

        return javaType.cast(dbr.getValue());
    }

    /**
     * Get DBR type from java object.
     * @param javaType java object to be inspected.
     * @return DBR type.
     * @throws CAException
     * @throws NullPointerException
     */
    public static DBRType toDBRType(final Class javaType) throws CAException
    {
        if (javaType == null) {
            throw new NullPointerException("javaType");
        }

        if (javaType.equals(Double.class) || javaType.equals(double[].class)) {
            return DBRType.DOUBLE;
        }

        if (javaType.equals(Long.class) || javaType.equals(long[].class)) {
            return DBRType.INT;
        }

        if (javaType.equals(String.class) || javaType.equals(String[].class)) {
            return DBRType.STRING;
        }

        if (javaType.equals(BitSet.class)) {
            return DBRType.INT;
        }

        if (javaType.equals(Object.class)) {
            return DBRType.STRING;
        }

        throw new CAException("Class " + javaType.getName() + " is not supported by CA.");
    }

    /**
     * Convert java object to DBR value.
     * @param value java object to convert.
     * @param originalType the original channel field type (used when
     *             the channel type is different than the type used in DAL.
     *             e.g. char[] is treated as String)
     * @return DBR value.
     * @throws CAException
     * @throws NullPointerException
     */
    public static Object toDBRValue(final Object value, final DBRType originalType) throws CAException
    {
        if (value == null) {
            throw new NullPointerException("value");
        }

        if (value.getClass().equals(Double.class)) {
            return new double[]{ (Double)value };
        }

        if (value.getClass().equals(Float.class)) {
            return new double[]{ (Float)value };
        }

        if (value.getClass().equals(Long.class)) {
            return new int[]{ ((Long)value).intValue() };
        }

        if (value.getClass().equals(Integer.class)) {
            return new int[]{ ((Integer)value).intValue() };
        }

        if (value.getClass().equals(String.class)) {
            if (originalType.isBYTE()) {
                final String sVal = (String)value;
                final String[] retVal = new String[sVal.length()];
                for (int i = 0; i < retVal.length; i++) {
                    retVal[i] = String.valueOf(sVal.charAt(i));
                }
                return retVal;
            } else {
                return new String[]{ (String)value };
            }
        }

        if (value.getClass().equals(BitSet.class)) {
            return new int[]{ ( (int)toLong((BitSet)value)) };
        }

        if (value.getClass().equals(double[].class)
            || value.getClass().equals(String[].class)
            || value.getClass().equals(int[].class)) {
            return value;
        }

        if (value.getClass().equals(long[].class)) {
            final long[] l = (long[])value;
            final int[] a = new int[l.length];

            for (int i = 0; i < a.length; i++) {
                a[i] = (int)l[i];
            }

            return a;
        }
        if (value.getClass().equals(float[].class)) {
            final float[] l = (float[])value;
            final double[] a = new double[l.length];

            for (int i = 0; i < a.length; i++) {
                a[i] = l[i];
            }

            return a;
        }

        if (value.getClass().equals(Object[].class)) {
            final Object[] o = (Object[])value;
            final String[] s = new String[o.length];

            for (int i = 0; i < s.length; i++) {
                s[i] = o[i].toString();
            }

            return s;
        }

        if (value.getClass().equals(Object.class)) {
            return new String[]{ value.toString() };
        }

        throw new CAException("Class " + value.getClass().getName() + " is not supported by CA.");
    }

    /**
     * Get TIME DBR type.
     * @param type DBR type.
     * @return TIME DBR type
     * @throws CAException
     * @throws NullPointerException
     */
    public static DBRType toTimeDBRType(final DBRType type) throws CAException
    {
        if (type == null) {
            throw new NullPointerException("type");
        }

        if (type.isTIME()) {
            return type;
        }

        if (type.isBYTE()) {
            return DBRType.TIME_BYTE;
        }

        if (type.isDOUBLE()) {
            return DBRType.TIME_DOUBLE;
        }

        if (type.isENUM()) {
            return DBRType.TIME_ENUM;
        }

        if (type.isFLOAT()) {
            return DBRType.TIME_FLOAT;
        }

        if (type.isINT()) {
            return DBRType.TIME_INT;
        }

        if (type.isSHORT()) {
            return DBRType.TIME_SHORT;
        }

        if (type.isSTRING()) {
            return DBRType.TIME_STRING;
        }

        throw new CAException("Type " + type
            + " can not be converted to Time DBR.");
    }

    /**
     * Loads to properties configuration, which enables EPICS plug.
     *
     * @param p configuration
     */
    public static void configureEPICSPlug(final Properties p)
    {
        final String[] s = Plugs.getPlugNames(p);
        final Set<String> set = new HashSet<String>(Arrays.asList(s));

        if (!set.contains(EPICSPlug.PLUG_TYPE)) {
            set.add(EPICSPlug.PLUG_TYPE);

            final StringBuffer sb = new StringBuffer();

            for (final Object element : set) {
                if (sb.length() > 0) {
                    sb.append(',');
                }

                sb.append(element);
            }

            p.put(Plugs.PLUGS, sb.toString());
        }

        p.put(Plugs.PLUGS_DEFAULT, EPICSPlug.PLUG_TYPE);
        p.put(Plugs.PLUG_PROPERTY_FACTORY_CLASS + EPICSPlug.PLUG_TYPE,
            PropertyFactoryImpl.class.getName());
        p.put(AbstractFactory.SYNCHRONIZE_FAMILY,"true");
    }

    /**
     * Converts CA timestamp to UTC Java time.
     *
     * @param ts CA timestamp
     *
     * @return Java UTC
     */
    public static long toUTC(final TimeStamp ts)
    {
        return (ts.secPastEpoch() + TS_EPOCH_SEC_PAST_1970) * 1000
        + ts.nsec() / 1000000;
    }

    /**
     * Converts CA timestamp to DAL timestamp.
     *
     * @param ts CA timestamp
     *
     * @return DAL timestamp
     */
    public static Timestamp convertTimestamp(final TimeStamp ts)
    {
        return new Timestamp((ts.secPastEpoch() + TS_EPOCH_SEC_PAST_1970) * 1000, ts.nsec());
    }

    /**
     * Converts CA timestamp to UTC Java time.
     *
     * @param ts CA timestamp
     *
     * @return Java UTC
     */
    public static Date toDate(final TimeStamp ts)
    {
        return new Date(toUTC(ts));
    }

    /**
     * Converts <code>BitSet</code> to <code>long</code> value if possible.
     * @param value the <code>BitSet</code> object
     * @return long representatnion of the bit set
     */
    public static final long toLong(final BitSet value)
    {
        long longValue = 0;

        for (int i = Math.min(value.length() - 1, 63); i >= 0; i--) {
            longValue <<= 1;

            if (value.get(i)) {
                longValue++;
            }
        }

        return longValue;
    }

    /**
     * Converts <code>long</code> value to <code>BitSet</code>.
     * @param value the long value
     * @return the <code>BitSet</code> corresponding to the value
     */
    public static final BitSet fromLong(long value)
    {
        final BitSet bs = new BitSet();

        int i = 0;

        while (value > 0) {
            bs.set(i++, (value & 1) > 0);
            value = value >> 1;
        }

        return bs;
    }

    /**
     * Puts value of </code>Object</code> parameter to the </code>Channel</code>.
     * @param channel the </code>Channel</code> to put value to.
     * @param value the </code>Object</code> parameter to put to the </code>Channel</code>.
     * @throws CAException
     * @throws NullPointerException
     */
    public static void put(final Channel channel, final Object value) throws CAException {
        put(channel, value, null);
    }

    /**
     * Puts value of </code>Object</code> parameter to the </code>Channel</code>.
     * @param channel the </code>Channel</code> to put value to.
     * @param value the </code>Object</code> parameter to put to the </code>Channel</code>.
     * @param listener the </code>PutListener</code> to use (if </code>null</code> no listener is used)
     * @throws CAException
     * @throws NullPointerException
     */
    public static void put(final Channel channel, final Object value, final PutListener listener) throws CAException
    {
        if (value == null) {
            throw new NullPointerException("value");
        }

        if (listener == null) {
            if (value.getClass().equals(Double.class)) {
                channel.put((Double) value);
            } else if (value.getClass().equals(double[].class)) {
                channel.put((double[]) value);
            } else if (value.getClass().equals(Integer.class)) {
                channel.put((Integer) value);
            } else if (value.getClass().equals(int[].class)) {
                channel.put((int[]) value);
            } else if (value.getClass().equals(String.class)) {
                channel.put((String) value);
            } else if (value.getClass().equals(String[].class)) {
                channel.put((String[]) value);
            } else if (value.getClass().equals(Float.class)) {
                channel.put((Float) value);
            } else if (value.getClass().equals(float[].class)) {
                channel.put((float[]) value);
            } else if (value.getClass().equals(Byte.class)) {
                channel.put((Byte) value);
            } else if (value.getClass().equals(byte[].class)) {
                channel.put((byte[]) value);
            } else if (value.getClass().equals(Short.class)) {
                channel.put((Short) value);
            } else if (value.getClass().equals(short[].class)) {
                channel.put((short[]) value);
            } else {
                throw new CAException("Class " + value.getClass().getName() + " is not supported by CA.");
            }
        }
        else {
            if (value.getClass().equals(Double.class)) {
                channel.put((Double) value, listener);
            } else if (value.getClass().equals(double[].class)) {
                channel.put((double[]) value, listener);
            } else if (value.getClass().equals(Integer.class)) {
                channel.put((Integer) value, listener);
            } else if (value.getClass().equals(int[].class)) {
                channel.put((int[]) value, listener);
            } else if (value.getClass().equals(String.class)) {
                channel.put((String) value, listener);
            } else if (value.getClass().equals(String[].class)) {
                channel.put((String[]) value, listener);
            } else if (value.getClass().equals(Float.class)) {
                channel.put((Float) value, listener);
            } else if (value.getClass().equals(float[].class)) {
                channel.put((float[]) value, listener);
            } else if (value.getClass().equals(Byte.class)) {
                channel.put((Byte) value, listener);
            } else if (value.getClass().equals(byte[].class)) {
                channel.put((byte[]) value, listener);
            } else if (value.getClass().equals(Short.class)) {
                channel.put((Short) value, listener);
            } else if (value.getClass().equals(short[].class)) {
                channel.put((short[]) value, listener);
            } else {
                throw new CAException("Class " + value.getClass().getName() + " is not supported by CA.");
            }
        }

    }

    /**
     * Checks the given type and constructs the data type name,
     * that the given type is associated with.
     *
     * @param type the epics record type
     * @return the datatype
     */
    public static String getDataType(final DBRType type) {
        if (type != null) {
            if (type.isBYTE()) {
                return CommonDataTypes.BYTE;
            }
            if (type.isDOUBLE()) {
                return CommonDataTypes.DOUBLE;
            }
            if (type.isFLOAT()) {
                return CommonDataTypes.FLOAT;
            }
            if (type.isINT()) {
                return CommonDataTypes.INT;
            }
            if (type.isSHORT()) {
                return CommonDataTypes.SHORT;
            }
            if (type.isSTRING()) {
                return CommonDataTypes.STRING;
            }
            if (type.isENUM()) {
                return CommonDataTypes.ENUM;
            }
        }
        return CommonDataTypes.UNKNOWN;
    }

    /**
     * Returns a default value for a given java type.
     * @param javaType type to convert to.
     * @return default value for java type.
     */
    public static <T> T defaultValue(final Class<T> javaType)
    {
        if (javaType == null) {
            throw new NullPointerException("javaType");
        }

        if (javaType.equals(Double.class)) {
            return javaType.cast(Double.NaN);
        }

        if (javaType.equals(double[].class)) {
            return javaType.cast(new double[] {Double.NaN});
        }

        if (javaType.equals(Long.class)) {
            return javaType.cast(Long.MIN_VALUE);
        }

        if (javaType.equals(long[].class)) {
            return javaType.cast(new long[] {Long.MIN_VALUE});
        }

        if (javaType.equals(String.class)) {
            return javaType.cast("NaN");
        }

        if (javaType.equals(String[].class)) {
            return javaType.cast(new String[] {"NaN"});
        }

        if (javaType.equals(BitSet.class)) {
            return javaType.cast(fromLong(Long.MIN_VALUE));
        }

        if (javaType.equals(Object.class)) {
            return javaType.cast(Double.NaN);
        }

        if (javaType.equals(Object[].class)) {
            return javaType.cast(new Object[] {Double.NaN});
        }

        return null;
    }


    public static final String toShortErrorReport(Throwable t) {
        final StringBuilder sb= new StringBuilder(128);

        try {
            appendShortErrorReport(t, sb);

            while (t.getCause()!=null) {
                sb.append(", caused by ");
                appendShortErrorReport(t.getCause(), sb);
                t= t.getCause();
            }
        } catch (final IOException e) {
            Logger.getLogger(PlugUtilities.class).warn("Unhandled exception.", e);
        }


        return sb.toString();
    }

    public static final void appendShortErrorReport(final Throwable t, final Appendable buffer) throws IOException {
        //if (t instanceof CAException) {
        if (t instanceof CAStatusException) {
            final CAStatusException e= (CAStatusException)t;
            buffer.append("CA status error:'");
            buffer.append(e.getStatus().toString());
            buffer.append("'");
            if (e.getMessage()!=null) {
                buffer.append(", message'");
                buffer.append(e.getMessage());
                buffer.append("'");
            }
        //} else if (t instanceof TimeoutException) {
        //} else if (t instanceof ConfigurationException) {
        } else if (t instanceof JNIException){
            final JNIException e= (JNIException)t;
            buffer.append("JNI error:'");
            buffer.append(e.getStatus().toString());
            buffer.append("'");
            if (e.getMessage()!=null) {
                buffer.append(", message'");
                buffer.append(e.getMessage());
                buffer.append("'");
            }
        } else {
            buffer.append("error:'");
            buffer.append(t.toString());
            buffer.append("'");
        }
    }
}

/* __oOo__ */
