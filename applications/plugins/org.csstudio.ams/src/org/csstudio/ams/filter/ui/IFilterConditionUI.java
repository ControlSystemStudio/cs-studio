
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

package org.csstudio.ams.filter.ui;

import java.sql.Connection;
import org.csstudio.ams.AMSException;
import org.eclipse.swt.widgets.Composite;

public interface IFilterConditionUI {
	
    /**
	 * Returns the name of this filter condition to be shown in the filter
	 * condition combo box.
	 * 
	 * @ensure result != null
	 */
	String getDisplayName();

	void load(Connection conDb, int iFilterConditionID) throws AMSException;

	void delete(Connection conDb, int iFilterConditionID) throws AMSException;

	void save(Connection conDb) throws AMSException;

	/**
	 * Creates the concrete FilterCondition and stores it into corresponding
	 * database using given id (Parameter: iFilterConditionID).
	 * 
	 * @param conDb
	 *            The connection to configuration database.
	 * @param iFilterConditionID
	 *            The Id for the new FilterCondition.
	 * @throws AMSException
	 *             An {@link AMSException} if an error occours.
	 */
	void create(Connection conDb, int iFilterConditionID) throws AMSException;

	/**
	 * Checks the users UI input to be valid to filter conditions configuration
	 * possibilities.
	 */
	boolean check();

	boolean isChanged();

	/**
	 * Resets the UI. Clears all input widgets and prepares widgets to show
	 * another filter condition.
	 */
	void reset();

	/**
	 * Creates the UI widget control specific for the filter condition
	 * parameters as child of param parent.
	 */
	void createUI(Composite parent);

	/**
	 * Disposes the filter condition UI specific widget/composites (do not dispose
	 * the parent given in {@link #createUI(Composite)}.
	 */
	void dispose();
}