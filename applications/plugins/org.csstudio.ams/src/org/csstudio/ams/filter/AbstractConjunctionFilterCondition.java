
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
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.jms.MapMessage;
import org.csstudio.ams.AMSException;
import org.csstudio.ams.CycleDetectionUtil;
import org.csstudio.ams.Log;
import org.csstudio.ams.dbAccess.configdb.AggrFilterConditionDAO;
import org.csstudio.ams.dbAccess.configdb.CommonConjunctionFilterConditionDAO;
import org.csstudio.ams.dbAccess.configdb.CommonConjunctionFilterConditionTObject;
import org.csstudio.ams.dbAccess.configdb.FilterConditionDAO;
import org.csstudio.ams.dbAccess.configdb.FilterConditionTObject;

/**
 * This class can be used as superclass for a conjuncted {@link IFilterCondition}.
 * A conjuncted FilterCondition can be a 'OR'- or a 'AND'-FilterCondition
 * @author C1 WPS / KM, MZ
 *
 */
public abstract class AbstractConjunctionFilterCondition implements IFilterCondition{

	private IFilterCondition _firstOperand;
	private IFilterCondition _secondOperand;

	/**
	 * Constructor.
	 */
	public AbstractConjunctionFilterCondition() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public final void init(Connection conDb, int filterConditionID, int filterID)
			throws AMSException {
				try {
					CommonConjunctionFilterConditionTObject configuration = CommonConjunctionFilterConditionDAO.select(conDb, filterConditionID);
					List<Integer> refList = new LinkedList<Integer>();
					//refList.add(configuration.getOwnFilterConditionReference());
					List<Integer> cyclesFound = new LinkedList<Integer>();
					boolean valid = CycleDetectionUtil.isChildConditionValid(conDb, refList, configuration.getOwnFilterConditionReference(), cyclesFound);
					if (valid) {
						// load...
						Map<Integer, String> filterConditionTypeList = AggrFilterConditionDAO.selectFCTList(conDb);
						int firstFilterConditionReference = configuration.getFirstFilterConditionReference();
						int secondFilterConditionReference = configuration.getSecondFilterConditionReference();
						
						IFilterCondition firstOperand = loadAndInitFilterCondition(conDb,
								filterID, filterConditionTypeList,
								firstFilterConditionReference);
						
						IFilterCondition secondOperand = loadAndInitFilterCondition(conDb,
								filterID, filterConditionTypeList,
								secondFilterConditionReference);
						
						doInit(firstOperand, secondOperand);	
					} else {
					    Log.log(this, Log.ERROR,
								"FilterCondition (ID: "+configuration.getOwnFilterConditionReference()+") contains at least one cycle!\n" +
										CycleDetectionUtil.createCycleDetectionMessage(conDb, cyclesFound)+"\nThe FilterCondition could not be loaded.");
					}
				} catch (Exception e) {
				    Log.log(this, Log.ERROR,
							"Initialization of filter condition failed", e);
					throw new AMSException("Initialization of filter condition failed",
							e);
				}
			}

	/**
	 * Loads and initializes the {@link IFilterCondition} specified by the given <i>filterConditionReference</i> 
	 * @param conDb The database connection
	 * @param filterID The id of the corresponding filter
	 * @param fCTList All known FilterConditon types
	 * @param filterConditionReference The current {@link IFilterCondition} reference
	 * @return The corresponding {@link IFilterCondition}
	 * @throws SQLException Thrown if a SQL-exception occurs
	 * @throws InstantiationException Thrown if the {@link IFilterCondition} could not be instantiated
	 * @throws IllegalAccessException Thrown if the {@link IFilterCondition} could not be accessed
	 * @throws ClassNotFoundException Thrown if the {@link IFilterCondition} class could not be found
	 * @throws AMSException
	 */
	private IFilterCondition loadAndInitFilterCondition(Connection conDb,
			int filterID, Map<Integer, String> fCTList,
			int filterConditionReference) throws SQLException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, AMSException {
		FilterConditionTObject conditionTObject = FilterConditionDAO.select(conDb, filterConditionReference);
		int fCTypeRef = conditionTObject.getFilterConditionTypeRef();
		String className = fCTList.get(fCTypeRef);
		
		Object newObj = Class.forName(className).newInstance();
		
		IFilterCondition filterCondition = (IFilterCondition)newObj;
		filterCondition.init(conDb, filterConditionReference, filterID);
		return filterCondition;
	}

	/**
	 * @require firstOperand != null
	 * @require secondOperand != null
	 */
	public final void doInit(IFilterCondition firstOperand, IFilterCondition secondOperand) {
		assert firstOperand != null : "Precondition violated: firstOperand != null";
		assert secondOperand != null : "Precondition violated: secondOperand != null";
	
		_firstOperand = firstOperand;
		_secondOperand = secondOperand;
	}

	/**
	 * Returns the conjuncted {@link IFilterCondition} as array
	 * @return The corresponding array (length = 2)
	 */
	public final IFilterCondition[] getCunjunctedOperands() {
		return new IFilterCondition[] {_firstOperand, _secondOperand};
	}
	
	/**
	 * {@inheritDoc}
	 */
	public final boolean match(MapMessage message) {
		boolean result = true;
		try {
			if (_firstOperand!=null && _secondOperand!=null) {
				result = doMatch(message, _firstOperand, _secondOperand);
			}
		} catch (Throwable e) {
			Log.log(this, Log.ERROR, "An error occurred during evaluation of conducted filters.", e);
			result = true;
		}

		return result;
	}
	
	/**
	 * Evaluates the given operands and returns their result combined
	 * by the logical function of the concrete class.
	 * This method should only be called by {@link #match(MapMessage)}!
	 * @param message The received message 
	 * @param firstOperand The first {@link IFilterCondition} (must not be null)
	 * @param secondOperand The second {@link IFilterCondition} (must not be null)
	 * @return The result of the evaluation
	 * @throws Throwable Thrown if an error occurred during evaluation
	 */
	protected abstract boolean doMatch(MapMessage message, IFilterCondition firstOperand, IFilterCondition secondOperand) throws Throwable;

}