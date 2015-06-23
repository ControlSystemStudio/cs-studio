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
 * This interface describes the property which has a primary data access mode
 * of type <code>long</code>. This is also a numeric property, therefore it
 * has a range, described by minimum and maximum statically declared
 * characteristics.
 */
public interface LongPropertyCharacteristics
    extends NumericPropertyCharacteristics
{
    /**
     * Optional characteristic for those long properties, for which long value
     * can be interpreted as state with own arbitrary value. Property migth
     * define sequence of characteristics from "stateValue&lt;minimum&gt;" up
     * to "stateValue&lt;maximum&gt;" where &lt;minimum&gt; and
     * &lt;maximum&gt; are boudaries for long value as state.
     */
    public final static String C_STATE_VALUE = "stateValue";

    /**
     * Optional characteristic for those long properties, for which long value
     * can be interpreted as state with own arbitrary description. Property
     * migth define sequence of characteristics from
     * "stateDescription&lt;minimum&gt;" up to
     * "stateDescription&lt;maximum&gt;" where &lt;minimum&gt; and
     * &lt;maximum&gt; are boudaries for long value as state.
     */
    public final static String C_STATE_DESCRIPTION = "stateDescription";
}

/* __oOo__ */
