
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

package org.csstudio.ams.filter;

import java.sql.Connection;
import javax.jms.MapMessage;
import org.csstudio.ams.AMSException;

/**
 * FIXME introduce destroy method to make disconnect possible.
 */
public interface IFilterCondition {
	/**
	 * Initialize the filter. Will be called before any call to match.
	 * 
	 * @param conDb
	 *            Connection to configuration database.
	 * @param iFilterConditionID
	 *            ID of this filter condition instance.
	 * @param filterID
	 *            ID of filter this filter condition is currently used in.
	 * @throws AMSException
	 *             Throws an AMS-Exception if initialization fails;
	 *             cause-exception should be nested.
	 * @require conDb != null
	 * @require iFilterConditionID > -1
	 * @require filterID > -1
	 * 
	 * XXX Note: Using a DB-connection instead of a set of configuration loaded
	 * by a central system makes it to complicated to test this component;
	 * interface design should be revised. Additionally not the filterId should
	 * given, better would be the filter-instance itself.
	 */
	void init(Connection conDb, int iFilterConditionID, int filterID)
			throws AMSException;

	/**
	 * Determines if given message matches this condition. It must not modify
	 * the message!
	 * 
	 * @param map
	 *            Message to be checked against this condition, not {@code null}.
	 * @return {@code true} if given messages matches this condition,
	 *         {@code false} otherwise.
	 * @require message != null
	 */
	boolean match(MapMessage message);
}