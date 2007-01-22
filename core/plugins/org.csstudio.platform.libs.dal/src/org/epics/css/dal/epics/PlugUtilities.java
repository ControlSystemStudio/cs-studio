/*
 * Copyright (c) 2006 by Cosylab d.o.o.
 *
 * The full license specifying the redistribution, modification, usage and other
 * rights and obligations is included with the distribution of this project in
 * the file license.html. If the license is not included you may find a copy at
 * http://www.cosylab.com/legal/abeans_license.htm or may write to Cosylab, d.o.o.
 *
 * THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND, NOT EVEN THE
 * IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR OF THIS SOFTWARE, ASSUMES
 * _NO_ RESPONSIBILITY FOR ANY CONSEQUENCE RESULTING FROM THE USE, MODIFICATION,
 * OR REDISTRIBUTION OF THIS SOFTWARE.
 */

package org.epics.css.dal.epics;

import gov.aps.jca.CAException;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.TimeStamp;

import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.DoubleSeqProperty;
import org.epics.css.dal.DoubleSeqSimpleProperty;
import org.epics.css.dal.DoubleSimpleProperty;
import org.epics.css.dal.EnumProperty;
import org.epics.css.dal.EnumSimpleProperty;
import org.epics.css.dal.LongProperty;
import org.epics.css.dal.LongSeqProperty;
import org.epics.css.dal.LongSeqSimpleProperty;
import org.epics.css.dal.LongSimpleProperty;
import org.epics.css.dal.PatternProperty;
import org.epics.css.dal.PatternSimpleProperty;
import org.epics.css.dal.SimpleProperty;
import org.epics.css.dal.StringProperty;
import org.epics.css.dal.StringSeqProperty;
import org.epics.css.dal.StringSeqSimpleProperty;
import org.epics.css.dal.StringSimpleProperty;
import org.epics.css.dal.Timestamp;
import org.epics.css.dal.impl.DoublePropertyImpl;
import org.epics.css.dal.impl.DoubleSeqPropertyImpl;
import org.epics.css.dal.impl.EnumPropertyImpl;
import org.epics.css.dal.impl.LongPropertyImpl;
import org.epics.css.dal.impl.LongSeqPropertyImpl;
import org.epics.css.dal.impl.PatternPropertyImpl;
import org.epics.css.dal.impl.StringPropertyImpl;
import org.epics.css.dal.impl.StringSeqPropertyImpl;
import org.epics.css.dal.proxy.PropertyProxy;

import org.epics.css.dal.spi.Plugs;

import java.lang.reflect.Array;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

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
	 * @param type	DBR type.
	 * @param elementCount element count.
	 * @return property implementation type.
	 */
	public static Class<? extends SimpleProperty> getPropertyImplForDBRType(DBRType type, int elementCount) {
	
		if (elementCount == 1)
		{
			if (type.isDOUBLE())
				return DoublePropertyImpl.class;
			else if (type.isFLOAT())
				return DoublePropertyImpl.class;
			else if (type.isBYTE())
				return LongPropertyImpl.class;
			else if (type.isSHORT())
				return LongPropertyImpl.class;
			else if (type.isINT())
				return LongPropertyImpl.class;
			else if (type.isENUM())
				return EnumPropertyImpl.class;
			else if (type.isSTRING())
				return StringPropertyImpl.class;
		}	
		else
		{
			if (type.isDOUBLE())
				return DoubleSeqPropertyImpl.class;
			else if (type.isFLOAT())
				return DoubleSeqPropertyImpl.class;
			else if (type.isBYTE())
				return LongSeqPropertyImpl.class;
			else if (type.isSHORT())
				return LongSeqPropertyImpl.class;
			else if (type.isINT())
				return LongSeqPropertyImpl.class;
			/*
			else if (type.isENUM())
				return EnumSeqPropertyImpl.class;
			*/
			else if (type.isSTRING())
				return StringSeqPropertyImpl.class;
		}

		throw new RuntimeException("Unsupported channel type.");
	}

	/**
	 * Initialize supported proxy implementations.
	 * @param plug plug to be initialized.
	 */
	public static void initializeSupportedProxyImplementations(EPICSPlug plug) {
		
		plug.putPropertyProxyImplementationClass(DoublePropertyImpl.class, DoublePropertyProxyImpl.class);

		plug.putPropertyProxyImplementationClass(LongPropertyImpl.class, LongPropertyProxyImpl.class);

		plug.putPropertyProxyImplementationClass(PatternPropertyImpl.class, PatternPropertyProxyImpl.class);

		plug.putPropertyProxyImplementationClass(EnumPropertyImpl.class, EnumPropertyProxyImpl.class);

		plug.putPropertyProxyImplementationClass(StringPropertyImpl.class, StringPropertyProxyImpl.class);

		plug.putPropertyProxyImplementationClass(DoubleSeqPropertyImpl.class, DoubleSeqPropertyProxyImpl.class);

		plug.putPropertyProxyImplementationClass(LongSeqPropertyImpl.class, LongSeqPropertyProxyImpl.class);

		plug.putPropertyProxyImplementationClass(StringSeqPropertyImpl.class, StringSeqPropertyProxyImpl.class);
	
	}
	
	/**
	 * Get proxy implementation class for property type.
	 * @param propertyType property type.
	 * @return proxy implementation class.
	 */
	public static Class<? extends PropertyProxy> getPropertyProxyImplementationClass(Class<? extends SimpleProperty> propertyType){

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
	 * @return converted java object.
	 */
	public static Object toJavaValue(DBR dbr, Class javaType)
	{
		if (javaType == null) {
			throw new NullPointerException("javaType");
		}

		if (dbr.getValue() == null) {
			return null;
		}

		if (javaType.equals(Double.class)) {
			if (dbr.isDOUBLE()) {
				return new Double(((double[])dbr.getValue())[0]);
			}

			if (dbr.isFLOAT()) {
				return new Double(((float[])dbr.getValue())[0]);
			}
		}

		if (javaType.equals(double[].class)) {
			if (dbr.isDOUBLE()) {
				return (double[])dbr.getValue();
			}

			if (dbr.isFLOAT()) {
				float[] f = (float[])dbr.getValue();
				double[] d = new double[f.length];

				for (int i = 0; i < d.length; i++) {
					d[i] = f[i];
				}

				return d;
			}
		}

		if (javaType.equals(Long.class)) {
			if (dbr.isINT()) {
				return new Long(((int[])dbr.getValue())[0]);
			}

			if (dbr.isBYTE()) {
				return new Long(((byte[])dbr.getValue())[0]);
			}

			if (dbr.isSHORT()) {
				return new Long(((short[])dbr.getValue())[0]);
			}

			if (dbr.isENUM()) {
				return new Long(((short[])dbr.getValue())[0]);
			}
		}

		if (javaType.equals(long[].class)) {
			if (dbr.isINT()) {
				int[] f = (int[])dbr.getValue();
				long[] d = new long[f.length];

				for (int i = 0; i < d.length; i++) {
					d[i] = f[i];
				}

				return d;
			}

			if (dbr.isBYTE()) {
				byte[] f = (byte[])dbr.getValue();
				long[] d = new long[f.length];

				for (int i = 0; i < d.length; i++) {
					d[i] = f[i];
				}

				return d;
			}

			if (dbr.isSHORT()) {
				short[] f = (short[])dbr.getValue();
				long[] d = new long[f.length];

				for (int i = 0; i < d.length; i++) {
					d[i] = f[i];
				}

				return d;
			}

			if (dbr.isENUM()) {
				short[] f = (short[])dbr.getValue();
				long[] d = new long[f.length];

				for (int i = 0; i < d.length; i++) {
					d[i] = f[i];
				}

				return d;
			}
		}

		if (javaType.equals(String.class)) {
			if (dbr.isSTRING()) {
				return ((String[])dbr.getValue())[0];
			}
		}

		if (javaType.equals(String[].class)) {
			if (dbr.isSTRING()) {
				return (String[])dbr.getValue();
			}
		}

		if (javaType.equals(BitSet.class)) {
			if (dbr.isENUM()) {
				return fromLong(((short[])dbr.getValue())[0]);
			}
			if (dbr.isBYTE()) {
				return fromLong(((byte[])dbr.getValue())[0]);
			}
			if (dbr.isSHORT()) {
				return fromLong(((short[])dbr.getValue())[0]);
			}
			if (dbr.isINT()) {
				return fromLong(((int[])dbr.getValue())[0]);
			}
		}
		
		if (javaType.equals(Object.class)) {
			return Array.get(dbr.getValue(), 0);
		}

		if (javaType.equals(Object[].class)) {
			return dbr.getValue();
		}

		return dbr.getValue();
	}

	/**
	 * Get DBR type from java object. 
	 * @param javaType java object to be inspected.
	 * @return DBR type.
	 * @throws CAException 
	 * @throws NullPointerException 
	 */
	public static DBRType toDBRType(Class javaType) throws CAException
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
	 * @return DBR value.
	 * @throws CAException
	 * @throws NullPointerException
	 */
	public static Object toDBRValue(Object value) throws CAException
	{
		if (value == null) {
			throw new NullPointerException("value");
		}

		if (value.getClass().equals(Double.class)) {
			return new double[]{ (Double)value };
		}

		if (value.getClass().equals(Long.class)) {
			return new int[]{ ((Long)value).intValue() };
		}

		if (value.getClass().equals(String.class)) {
			return new String[]{ (String)value };
		}

		if (value.getClass().equals(BitSet.class)) {
			return new int[]{ ( (int)toLong((BitSet)value)) };
		}

		if (value.getClass().equals(double[].class)
		    || value.getClass().equals(String[].class)) {
			return value;
		}

		if (value.getClass().equals(long[].class)) {
			long[] l = (long[])value;
			int[] a = new int[l.length];

			for (int i = 0; i < a.length; i++) {
				a[i] = (int)l[i];
			}

			return a;
		}

		if (value.getClass().equals(Object[].class)) {
			Object[] o = (Object[])value;
			String[] s = new String[o.length];

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
	public static DBRType toTimeDBRType(DBRType type) throws CAException
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
	public static void configureEPICSPlug(Properties p)
	{
		String[] s = Plugs.getPlugNames(p);
		Set<String> set = new HashSet<String>(Arrays.asList(s));

		if (!set.contains(EPICSPlug.PLUG_TYPE)) {
			set.add(EPICSPlug.PLUG_TYPE);

			StringBuffer sb = new StringBuffer();

			for (Iterator iter = set.iterator(); iter.hasNext();) {
				if (sb.length() > 0) {
					sb.append(',');
				}

				sb.append(iter.next());
			}

			p.put(Plugs.PLUGS, sb.toString());
		}

		p.put(Plugs.PLUGS_DEFAULT, EPICSPlug.PLUG_TYPE);
		p.put(Plugs.PLUG_PROPERTY_FACTORY_CLASS + EPICSPlug.PLUG_TYPE,
		    PropertyFactoryImpl.class.getName());
	}

	/**
	 * Converts CA timestamp to UTC Java time.
	 *
	 * @param ts CA timestamp
	 *
	 * @return Java UTC
	 */
	public static long toUTC(TimeStamp ts)
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
	public static Timestamp convertTimestamp(TimeStamp ts)
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
	public static Date toDate(TimeStamp ts)
	{
		return new Date(toUTC(ts));
	}
	
	/**
	 * Converts <code>BitSet</code> to <code>long</code> value if possible.
	 * @param value the <code>BitSet</code> object
	 * @return long representatnion of the bit set
	 */
	public static final long toLong(BitSet value)
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
		BitSet bs = new BitSet();

		int i = 0;

		while (value > 0) {
			bs.set(i++, (value & 1) > 0);
			value = value >> 1;
		}

		return bs;
	}
	
}

/* __oOo__ */
