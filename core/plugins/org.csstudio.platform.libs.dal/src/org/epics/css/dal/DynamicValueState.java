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
 * Enumeration <code>DynamicValue</code> describes avaliable states in which
 * dynamic value property migth find itself. This state does not described
 * connection management status since dynamic value property is by definition
 * stateless in terms of connection management. By definition DynamicValueState
 * objects define data property from point when property is connected to remote
 * object till the point this connection is destroyed on local system. Dynamic
 * value property might be described at given moment with set of different
 * states. Interpretation of states is left to the particular implementation.
 * Also implementation migth find some states compatible and some not with
 * eachother.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public enum DynamicValueState {
	/**
	 * Normal state, no errors or alarms, connection to remote object functions
	 * without problems.
	 */
	NORMAL, 
	/**
	 * General warning, dynamic value my not be reliable. Corresponds to MINOR_ALARM severity in EPICS.
	 */
	WARNING, 
	/**
	 * More serious than warning, dynamic value my not be reliable. Corresponds to MAJOR_ALARM severity in EPICS.
	 */
	ALARM, 
	/**
	 * Error occured on remote object, dynamic value my not be reliable. Corresponds to INVALID_ALARM severity in EPICS.
	 */
	ERROR, 
	/**
	 * Dynamic value updated are not ariving for longer than timeout period.
	 */
	TIMEOUT, 
	/**
	 * Dynamic value updates are arriving but with delay larger than timelag
	 * period.
	 */
	TIMELAG, 
	/**
	 * Remote object is not available, value is not reliable, timeout my occur as
	 * well.
	 */
	LINK_NOT_AVAILABLE;
}
/* __oOo__ */
