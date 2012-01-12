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

import org.csstudio.dal.AccessType;
import org.csstudio.dal.CommonDataTypes;

/** Base interface for a sample's meta data.
 *  @see AnyData
 *  @see INumericMetaData
 *  @see IEnumeratedMetaData
 *  @author Kay Kasemir
 */
public interface MetaData
{
	/** @return Suggested lower display limit. */
    public double getDisplayLow();

    /** @return Suggested upper display limit. */
    public double getDisplayHigh();

    /** @return Low warning limit. */
    public double getWarnLow();

    /** @return High warning limit. */
    public double getWarnHigh();

    /** @return Low alarm limit. */
    public double getAlarmLow();

    /** @return High alarm limit. */
    public double getAlarmHigh();

    /** @return Suggested display precision (fractional digits). */
    public int getPrecision();

    /** @return The engineering units string. */
    public String getUnits();
    
    /** 
     *  Obtains the states.
     *  <p>
     *  The array element <code>i</code> represents enum number <code>i</code>.
     *  
     *  @return The state string array, never <code>null</code>.
     */
    public String[] getStates();

    /** 
     *  Convenience routine for getting the state.
     *  
     *  @param index the index for which the enum description is returned
     *  
     *  @return the enum description.
     */
    public String getState(int index);
    
    /**
     *  Obtains the enumeration values.
     *  <p>
     *  The array element <code>i</code> represents enum number <code>i</code>.
     *  
     *  @return The state value object array, never <code>null</code>.
     */
    public Object[] getStateValues();

    /** 
     *  Convenience routine for getting a state value.
     *    
     *  @param index the index for which the value is returned
     *  
     *  @return the state value
     */
    public Object getStateValue(int index);
    
    /**
     * Return the display format for the numerical values.
     * 
     * @return the display format 
     */
    public String getFormat();
    
    /**
     * Returns the access type.
     * 
     * @return the access type
     */
    public AccessType getAccessType();
    
    /**
     * Returns the host name of the channel that this meta data belongs to.
     * 
     * @return the host name
     */
    public String getHostname();
    
    /**
     * Returns the datatype of the channel. This method returns one of the
     * string in {@link CommonDataTypes}.
     * 
     * @return the datatype
     */
    public String getDataType();
    
    /**
     * Returns the description of the channel.
     * 
     * @return the description
     */
    public String getDescription();
    
    /**
     * Returns the name of the channel.
     * 
     * @return the name of the channel
     */
    public String getName();
 
    /**
     * Returns the sequence length
     * 
     * @return the sequence length
     */
    public int getSequenceLength();
}
