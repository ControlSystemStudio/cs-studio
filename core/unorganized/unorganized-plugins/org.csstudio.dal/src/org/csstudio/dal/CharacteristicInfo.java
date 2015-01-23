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
package org.csstudio.dal;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.dal.simple.MetaData;
import org.csstudio.dal.simple.Severity;

import com.cosylab.util.BitCondition;

/**
 * This is info class, which provides some meta information about a characteristic.
 * This class also provides access to set of characteristics, which
 * DAL API forces to be supported on all DAL plugs.
 *
 * <p>Info clasu is unmodifiable, data can be provided only trough constructor.
 *
 * @author ikriznar
 *
 */
public class CharacteristicInfo {

	/*
	 * Static declaration of default DAL supported characteristics.
	 */

	/**
	 * Name of characteristic declaring position of this property
	 * within group of properties. This could be physical layout position of
	 * device in experimental installation or just indication of logical order
	 * in list of similar properties. By definition position must be of
	 * <code>double</code> type.<p>E.g. position my be used in profile
	 * chart to position this value point on horizontal axis.</p>
	 */
	public static final CharacteristicInfo C_POSITION = new CharacteristicInfo("position",Double.class, new Class[]{SimpleProperty.class},"Position of this property within group of properties, this could be physical layout position of device in experimental installation or just indication of logical order.");
	/**
	 * Name of characteristic declaring long description string for
	 * this property.
	 */
	public static final CharacteristicInfo C_DESCRIPTION = new CharacteristicInfo("description",String.class, new Class[]{SimpleProperty.class},"Description of the property.");
	/**
	 * Name of characteristic declaring short descriptive name of this
	 * property. May be shortend version of the unique name. This name is
	 * intended to be used in GUI widgets as title. Display name should be
	 * short, familiar to end users, but still unique inside context is
	 * appears.
	 */
	public static final CharacteristicInfo C_DISPLAY_NAME = new CharacteristicInfo("displayName",String.class, new Class[]{SimpleProperty.class},"A short descriptive name of this property, intended to be used in GUI widgets as title.");
	/**
	 * Short descriptive name of the "type" of the property. The
	 * properties with same propertyType can be grouped together in GUI
	 * widgets: same types go to same row in table, go to same series in
	 * profile chart, etc. Same property types must be of same access type or
	 * property class.<p>Example: Device of type "PowerSupply" has
	 * property "current". Property type for such property would be "current"
	 * or even "PowerSupply/current", whatever distinguish better this
	 * property inside context or set of properties it is used.</p>
	 */
	public static final CharacteristicInfo C_PROPERTY_TYPE = new CharacteristicInfo("propertyType",String.class, new Class[]{SimpleProperty.class},"Descriptive name for type of the property. Example: Device of type 'PowerSupply' has property 'current', property type would be 'current' or even 'PowerSupply/current'.");
	/**
	 * Name of the resolution characteristic which gives in number of bits used for ADC conversion of analog value when sampled.
	 */
	public static final CharacteristicInfo C_RESOLUTION = new CharacteristicInfo("resolution",Integer.class, new Class[]{NumericProperty.class},"Number of bits used for ADC conversion of analog value when sampled.");

	/**
	 * Name of the precision characteristic. Such characteristic represents the number of decimal places.
	 */
	public static final CharacteristicInfo C_PRECISION = new CharacteristicInfo("precision",Integer.class, new Class[]{NumericProperty.class},"Number of decimal places.");

	/**
	 * Name of the minimum characteristic. Such characteristic represents the
	 * value that should be taken as a minimum allowed value of the dynamic
	 * value.
	 */
	public static final CharacteristicInfo C_MINIMUM = new CharacteristicInfo("minimum",Number.class, new Class[]{NumericProperty.class},"A minimum allowed value of the property.");

	/**
	 * The name of the maximum characteristic. Such characteristic represents
	 * the value that should be taken as a maximum allowed of the dynamic
	 * value.
	 * If dynamic value type is array or sequence, then this value is scalar value and
	 * represents limit for all positions in array or seuence.
	 */
	public static final CharacteristicInfo C_MAXIMUM = new CharacteristicInfo("maximum",Number.class, new Class[]{NumericProperty.class},"A maximum allowed value of the property.");

	/**
	 * Name of the graphMin characteristic. Such characteristic represents the
	 * value that should be taken as a display minimum if the dynamic value of
	 * the property is being charted.
	 * If dynamic value type is array or sequence, then this value is scalar value and
	 * represents limit for all positions in array or sequence.
	 */
	public static final CharacteristicInfo C_GRAPH_MIN = new CharacteristicInfo("graphMin",Number.class, new Class[]{NumericProperty.class},"A minimum display value of the property.");

	/**
	 * The name of the graphMax characteristic. Such characteristic represents
	 * the value that should be taken as a display maximum if the dynamic
	 * value of the property is being charted.
	 * If dynamic value type is array or sequence, then this value is scalar value and
	 * represents limit for all positions in array or sequence.
	 */
	public static final CharacteristicInfo C_GRAPH_MAX = new CharacteristicInfo("graphMax",Number.class, new Class[]{NumericProperty.class},"A maximum display value of the property.");

	/**
	 * The name of the format characteristic. Such characteristic represents
	 * the C printf-style format specifier that is used to render the dynamic
	 * value of the given property into a string.
	 */
	public static final CharacteristicInfo C_FORMAT = new CharacteristicInfo("format",String.class, new Class[]{NumericProperty.class},"A C printf-style format specifier that is used to convert a property value to a string.");

	/**
	 * The name of the units characteristic. Such characteristic represents the
	 * units of the dynamic value.
	 */
	public static final CharacteristicInfo C_UNITS = new CharacteristicInfo("units",String.class, new Class[]{NumericProperty.class},"A physical units of property value.");

	/**
	 * The name of the scaleType characteristic. Such characteristic represents
	 * the scale type used to plot the property. It can have values "linear"
	 * or "logarithmic"; case is significant.
	 */
	public static final CharacteristicInfo C_SCALE_TYPE = new CharacteristicInfo("scaleType",String.class, new Class[]{NumericProperty.class},"Can specify 'linear' or 'logarithmic' scale.");

	/**
	 * Optional characteristic.
	 *
	 * The name of the warning upper limit characteristic. Such characteristic
	 * represents the value that should be taken as a maximum value which
	 * is displayed without a warning. Any value higher that this maximum, should
	 * have a warning label attached to it.
	 * If dynamic value type is array or sequence, then this value is scalar value and
	 * represents limit for all positions in array or sequence.
	 */
	public static final CharacteristicInfo C_WARNING_MAX = new CharacteristicInfo("warningMax",Number.class, new Class[]{NumericProperty.class},"A maximum warning value of the property.");

	/**
	 * Optional characteristic.
	 *
	 * The name of the warning lower limit characteristic. Such characteristic
	 * represents the value that should be taken as a minimum value which
	 * is displayed without a warning. Any value lower that this minimum, should
	 * have a warning label attached to it.
	 * If dynamic value type is array or sequence, then this value is scalar value and
	 * represents limit for all positions in array or sequence.
	 */
	public static final CharacteristicInfo C_WARNING_MIN = new CharacteristicInfo("warningMin",Number.class, new Class[]{NumericProperty.class},"A minimum warning value of the property.");

	/**
	 * Optional characteristic.
	 *
	 * The name of the alarm upper limit characteristic. Such characteristic
	 * represents the value that should be taken as a maximum value which
	 * is displayed without an alarm. Any value higher that this maximum, should
	 * have a major alarm label attached to it.
	 * If dynamic value type is array or sequence, then this value is scalar value and
	 * represents limit for all positions in array or sequence.
	 */
	public static final CharacteristicInfo C_ALARM_MAX = new CharacteristicInfo("alarmMax",Number.class, new Class[]{NumericProperty.class},"A maximum alarm value of the property.");

	/**
	 * Optional characteristic.
	 *
	 * The name of the alarm lower limit characteristic. Such characteristic
	 * represents the value that should be taken as a minimum value which
	 * is displayed without an alarm. Any value lower that this minimum, should
	 * have a major alarm label attached to it.
	 * If dynamic value type is array or sequence, then this value is scalar value and
	 * represents limit for all positions in array or sequence.
	 */
	public static final CharacteristicInfo C_ALARM_MIN = new CharacteristicInfo("alarmMin",Number.class, new Class[]{NumericProperty.class},"A minimum alarm value of the property.");
	/**
	 * The name of the characteristic denoting sequence length. The returned value type is <code>Integer</code>.
	 */
	public static final CharacteristicInfo C_SEQUENCE_LENGTH = new CharacteristicInfo("sequenceLength", Integer.class, new Class[]{DoubleSeqSimpleProperty.class,LongSeqSimpleProperty.class,ObjectSeqSimpleProperty.class,StringSeqSimpleProperty.class},"The length of value array if property is a sequence kind.");
	/**
	 * Characteristic name for array of enumerated objects in same order as enum indexes returned by the property. Characteristic type
	 * is <code>Object[]</code>.
	 */
	public final static CharacteristicInfo C_ENUM_VALUES = new CharacteristicInfo("enumValues", Object[].class, new Class[]{EnumSimpleProperty.class}, "An array of all enumerated values for property in same order as enum indexes returned by the property.");

	/**
	 * Characteristic name for array of descriptions for enumerated objects in same order as enum indexes returned by the property.
	 * Characteristic type is <code>String[]</code>.
	 */
	public final static CharacteristicInfo C_ENUM_DESCRIPTIONS = new CharacteristicInfo("enumDescriptions", String[].class, new Class[]{EnumSimpleProperty.class}, "An array of short descriptions for all enumerated values for property in same order as enum indexes returned by the property.");
	/**
	 * Name of the characteristic describing bits. Returned value type
	 * is <code>String[]</code>.
	 */
	public static final CharacteristicInfo C_BIT_DESCRIPTIONS = new CharacteristicInfo("bitDescriptions", String[].class, new Class[]{PatternSimpleProperty.class},"An array with descriptions for masked bits.");

	/**
	 * Name of the characteristic defining active bit significance.
	 * Returned value type is <code>BitCondition[]</code>.
	 */
	public static final CharacteristicInfo C_CONDITION_WHEN_SET = new CharacteristicInfo("conditionWhenSet", BitCondition[].class, new Class[]{PatternSimpleProperty.class},"An array defining masked bit significance when active (bit value 1).");

	/**
	 * Name of the characteristic defining inactive bit significance.
	 * Returned value type is <code>BitCondition[]</code>.
	 */
	public static final CharacteristicInfo C_CONDITION_WHEN_CLEARED = new CharacteristicInfo("conditionWhenCleared", BitCondition[].class, new Class[]{PatternSimpleProperty.class},"An array defining masked bit significance when inactive (bit value 0).");

	/**
	 * Name of the characteristic defining bits relevance. Returned
	 * value type is <code>BitSet</code>.
	 */
	public static final CharacteristicInfo C_BIT_MASK = new CharacteristicInfo("bitMask", BitSet.class, new Class[]{PatternSimpleProperty.class},"A mask which tells which bits in value are relevant and has defined conditions and descriptions.");


	/**
	 * Meta characteristic for last received EPICS style timestamp.
	 * Returned value type is <code>Timestamp</code>.
	 */
	public static final CharacteristicInfo C_TIMESTAMP = new CharacteristicInfo("timestampInfo", Timestamp.class, new Class[] { DynamicValueProperty.class }, "Meta characteristic for last received EPICS style timestamp.", null, true);
	/**
	 * Meta characteristic for last received severity.
	 * Returned value type is <code>Severity</code>.
	 */
	public static final CharacteristicInfo C_SEVERITY = new CharacteristicInfo("severity", Severity.class, new Class[] { DynamicValueProperty.class }, "Meta characteristic for last received severity.", null, true);
	/**
	 * Meta characteristic for last received EPICS style status.
	 * Returned value type is <code>String</code>.
	 */
	public static final CharacteristicInfo C_STATUS = new CharacteristicInfo("statusInfo", String.class, new Class[] { DynamicValueProperty.class }, "Meta characteristic for last received EPICS style status.", null, true);
	/**
	 * MetaData characteristic similar to that used by simple DAL.
	 * Returned value type is <code>MetaData</code>.
	 */
	public static final CharacteristicInfo C_META_DATA = new CharacteristicInfo("metaData", MetaData.class, new Class[] { DynamicValueProperty.class }, "MetaData characteristic similar to that used by simple DAL.", null, true);


	private static CharacteristicInfo[] defaultCharacterictics;
	private static Map<String,List<CharacteristicInfo>> plugSpecific= new HashMap<String, List<CharacteristicInfo>>();

	/**
	 * Returns an array with all default characteristics declared in this class.
	 *
	 * @param plug if plug name non-null also plug specific characteristic are included in returned array
	 *
	 * @return an array with all default characteristics
	 */
	public synchronized static final CharacteristicInfo[] getDefaultCharacteristics(final String plug) {
		if (defaultCharacterictics==null) {

			final List<CharacteristicInfo> l= new ArrayList<CharacteristicInfo>(32);

			final Field[] f= CharacteristicInfo.class.getDeclaredFields();
			for (final Field element : f) {
				if (Modifier.isStatic(element.getModifiers()) && element.getType()==CharacteristicInfo.class) {
					try {
						final CharacteristicInfo ci= (CharacteristicInfo)element.get(null);
						if (!ci.isMeta()) {
							l.add(ci);
						}
					} catch (final IllegalArgumentException e) {
						e.printStackTrace();
					} catch (final IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}

			defaultCharacterictics= l.toArray(new CharacteristicInfo[l.size()]);

		}

		final List<CharacteristicInfo> infos= plugSpecific.get(plug);
		if (infos != null) {
			final CharacteristicInfo[] ci= new CharacteristicInfo[defaultCharacterictics.length+infos.size()];
			infos.toArray(ci);
			System.arraycopy(defaultCharacterictics, 0, ci, infos.size(), defaultCharacterictics.length);
			return ci;
		}

		return defaultCharacterictics;
	}

	private static final CharacteristicInfo[] getDeclaredCharacteristics() {
		final List<CharacteristicInfo> l= new ArrayList<CharacteristicInfo>(32);

		final Field[] f= CharacteristicInfo.class.getDeclaredFields();
		for (final Field element : f) {
			if (Modifier.isStatic(element.getModifiers()) && element.getType()==CharacteristicInfo.class) {
				try {
					final CharacteristicInfo ci= (CharacteristicInfo)element.get(null);
					l.add(ci);
				} catch (final IllegalArgumentException e) {
					e.printStackTrace();
				} catch (final IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}

		return l.toArray(new CharacteristicInfo[l.size()]);
	}

	/**
	 *
	 * @param property the property type
	 * @param plug name if non-null also plug specific characteristic are included in returned array
	 * @return array of characteristics info supported by provided property type and plug
	 */
	public static final CharacteristicInfo[] getDefaultCharacteristics(final Class<?> property, final String plug) {

		final CharacteristicInfo[] inf= getDefaultCharacteristics(plug);
		final List<CharacteristicInfo> l= new ArrayList<CharacteristicInfo>(inf.length);

		for (final CharacteristicInfo element : inf) {
			final Class[] c= element.getProperties();
			for (final Class element2 : c) {
				if (element2.isAssignableFrom(property)) {
					l.add(element);
					break;
				}
			}
		}

		return l.toArray(new CharacteristicInfo[l.size()]);
	}

	/**
	 * Dynamically registers a characteristic info.
	 * @param info a new characteristic info to register
	 */
	public static final void registerCharacteristicInfo(final CharacteristicInfo info) {
		List<CharacteristicInfo> infos= plugSpecific.get(info.getPlug());
		if (infos == null) {
			infos= new ArrayList<CharacteristicInfo>(8);
			plugSpecific.put(info.getPlug(), infos);
		}

		infos.add(info);
	}


	public static void main(final String[] args) {

		final CharacteristicInfo[] infos= getDeclaredCharacteristics();

		for (final CharacteristicInfo info : infos) {
			System.out.println(info);
		}

	}

	private final String name;
	private final String description;
	private String plug;
	private final Class<?> type;
	private final Class<? extends SimpleProperty<?>>[] properties;
	private boolean meta= false;
	private int hash=0;



	/**
	 * Constructor with parameters.
	 *
	 * @param name characteristic name as it it used in DAL properties
	 * @param type Java class of value as returned by DAL property.
	 * 			If more types are possible, first common superclass should be used.
	 * @param description a short description
	 * @param properties an array of DAL property interfaces, which support characteristic
	 */
	public CharacteristicInfo(final String name, final Class<?> type,
			final Class<? extends SimpleProperty<?>>[] properties, final String description) {

		this.description = description;
		this.name = name;
		this.properties = properties;
		this.type = type;
	}

	/**
	 * Constructor with parameters.
	 *
	 * @param name characteristic name as it it used in DAL properties
	 * @param type Java class of value as returned by DAL property.
	 * 			If more types are possible, first common superclass should be used.
	 * @param description a short description
	 * @param properties an array of DAL property interfaces, which support characteristic
	 * @param plug name of plug if this characteristic is specific to certain plug.
	 * 			If null then characteristic applies to any plug.
	 * @param meta <code>true</code> if this is "meta" characteristic generated by DAL and not directly
	 * connected to a remote entity.
	 */
	public CharacteristicInfo(final String name, final Class<?> type,
			final Class<? extends SimpleProperty<?>>[] properties, final String description, final String plug, final boolean meta) {

		this.description = description;
		this.name = name;
		this.properties = properties;
		this.type = type;
		this.plug= plug;
		this.meta= meta;
	}

	/**
	 * The name of the characteristic.
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * The Java type of returned characteristic value by DAL property.
	 * Charateristic value should be possible to cast to this type.
	 * @return the type
	 */
	public Class<?> getType() {
		return type;
	}

	/**
	 * The array of properties in which this characteristic is supported.
	 * Instead of multiple property interfaces a common super interface should be used.
	 * For example: NumericProperrty instead of DoubleProperty and LongProperty.
	 * @return the properties
	 */
	public Class<? extends SimpleProperty<?>>[] getProperties() {
		final Class<? extends SimpleProperty<?>>[] r= new Class[properties.length];
		System.arraycopy(properties, 0, r, 0, r.length);
		return r;
	}

	/**
	 * Short description of the characcteristic.
	 * @return short description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns plug name, if this characteristic is plug specific. If <code>null</code> then characteristic applies to all plugs.
	 * @return plug name or <code>null</code>
	 */
	public String getPlug() {
		return plug;
	}

	@Override
	public String toString() {
		final StringBuilder sb= new StringBuilder(256);
		sb.append(name);
		sb.append(":{");
		sb.append(type.getSimpleName());

		if (properties!=null) {
			for (final Class<? extends SimpleProperty<?>> propertie : properties) {
				sb.append(',');
				sb.append(propertie.getSimpleName());
			}
		}
		sb.append("}");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		if (hash==0) {
			hash= toString().hashCode();
		}
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof CharacteristicInfo) {
			return hashCode()==obj.hashCode();
		}
		return false;
	}

	/**
	 * Returns <code>true</code> if this is "meta" characteristic generated by DAL and not directly
	 * connected to a remote entity. It might not be supported in all DAL implementations.
	 * @return <code>true</code> if this is "meta" characteristic, could be optional
	 */
	public boolean isMeta() {
		return meta;
	}

}
