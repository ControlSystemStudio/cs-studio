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


/**
 * Decleares Characteristics which may be contained  in <code>SimpleNumericProperty</code>
 *
 * @author <a href="mailto:igor.kriznar@cosylab.com">Igor Kriznar</a>
 * @version $id$
 */
public interface NumericPropertyCharacteristics extends PropertyCharacteristics
{
	/**
	 * Name of the resolution characteristic. If such characteristic is
	 * sensible for a given property and the data source can provide
	 * resolution characteristic, it must provide it under this name as a
	 * static characteristic.
	 */
	public static final String C_RESOLUTION = CharacteristicInfo.C_RESOLUTION.getName();

	/**
	 * Name of the minimum characteristic. Such characteristic represents the
	 * value that should be taken as a minimum allowed value of the dynamic
	 * value.
	 */
	public static final String C_MINIMUM = CharacteristicInfo.C_MINIMUM.getName();

	/**
	 * The name of the maximum characteristic. Such characteristic represents
	 * the value that should be taken as a maximum allowed of the dynamic
	 * value.
	 * If dynamic value type is array or sequence, then this value is scalar value and
	 * represents limit for all positions in array or sequence.
	 */
	public static final String C_MAXIMUM = CharacteristicInfo.C_MAXIMUM.getName();

	/**
	 * Name of the graphMin characteristic. Such characteristic represents the
	 * value that should be taken as a display minimum if the dynamic value of
	 * the property is being charted.
	 * If dynamic value type is array or sequence, then this value is scalar value and
	 * represents limit for all positions in array or sequence.
	 */
	public static final String C_GRAPH_MIN = CharacteristicInfo.C_GRAPH_MIN.getName();

	/**
	 * The name of the graphMax characteristic. Such characteristic represents
	 * the value that should be taken as a display maximum if the dynamic
	 * value of the property is being charted.
	 * If dynamic value type is array or sequence, then this value is scalar value and
	 * represents limit for all positions in array or sequence.
	 */
	public static final String C_GRAPH_MAX = CharacteristicInfo.C_GRAPH_MAX.getName();

	/**
	 * The name of the format characteristic. Such characteristic represents
	 * the C printf-style format specifier that is used to render the dynamic
	 * value of the given property into a string.
	 */
	public static final String C_FORMAT = CharacteristicInfo.C_FORMAT.getName();

	/**
	 * The name of the units characteristic. Such characteristic represents the
	 * units of the dynamic value.
	 */
	public static final String C_UNITS = CharacteristicInfo.C_UNITS.getName();

	/**
	 * The name of the scaleType characteristic. Such characteristic represents
	 * the scale type used to plot the property. It can have values "linear"
	 * or "logarithmic"; case is significant.
	 */
	public static final String C_SCALE_TYPE = CharacteristicInfo.C_SCALE_TYPE.getName();

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
	public static final String C_WARNING_MAX = CharacteristicInfo.C_WARNING_MAX.getName();

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
	public static final String C_WARNING_MIN = CharacteristicInfo.C_WARNING_MIN.getName();

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
	public static final String C_ALARM_MAX = CharacteristicInfo.C_ALARM_MAX.getName();

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
	public static final String C_ALARM_MIN = CharacteristicInfo.C_ALARM_MIN.getName();

	/**
	 * Optional characteristic.
	 *
	 * The name of the number of decimal places. Such characteristic
	 */
	public static final String C_PRECISION = CharacteristicInfo.C_PRECISION.getName();
}

/* __oOo__ */
