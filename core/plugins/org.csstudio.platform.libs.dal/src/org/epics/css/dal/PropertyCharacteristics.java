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

package org.epics.css.dal;


/**
 *  Declares characteristics which are common to all properties
 * @author ikriznar
 *
 */
public interface PropertyCharacteristics
{
	/**
	 * Name of characteristic declaring position of this property
	 * within group of properties. This could be physical layout position of
	 * device in experimental installation or just indication of logical order
	 * in list of similar properties. By definition position must be of
	 * <code>double</code> type.<p>E.g. position my be used in profile
	 * chart to position this value point on horizontal axis.</p>
	 */
	public static String C_POSITION = CharacteristicInfo.C_POSITION.getName();

	/**
	 * Name of characteristic declaring short descriptive name of this
	 * property. May be shortend version of the unique name. This name is
	 * intended to be used in GUI widgets as title. Display name should be
	 * short, familiar to end users, but still unique inside context is
	 * appears.
	 */
	public static String C_DISPLAY_NAME = CharacteristicInfo.C_DISPLAY_NAME.getName();

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
	public static String C_PROPERTY_TYPE = CharacteristicInfo.C_PROPERTY_TYPE.getName();

	/**
	 * Name of characteristic declaring long description string for
	 * this property.
	 */
	public static String C_DESCRIPTION = CharacteristicInfo.C_DESCRIPTION.getName();
}

/* __oOo__ */
