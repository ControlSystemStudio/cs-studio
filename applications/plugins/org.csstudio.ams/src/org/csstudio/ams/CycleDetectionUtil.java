
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

package org.csstudio.ams;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.csstudio.ams.dbAccess.configdb.CommonConjunctionFilterConditionDAO;
import org.csstudio.ams.dbAccess.configdb.CommonConjunctionFilterConditionTObject;
import org.csstudio.ams.dbAccess.configdb.FilterConditionDAO;
import org.csstudio.ams.dbAccess.configdb.FilterConditionTObject;
import org.csstudio.ams.filter.IFilterCondition;

/**
 * This class is for detecting a cycle within a conjuncted {@link IFilterCondition} (e.g. 'OR'-FilterCondition).
 * @author C1 WPS / KM, MZ
 *
 */
public class CycleDetectionUtil {
	
	/**
	 * Searches for leaves in the filter condition tree. Does not check anything (returns true) if filter condition with given id is not a conjunction.
	 * 
	 * This method should only be called by {@link #isChildConditionValid(Connection, List, int)}.
	 * 
	 * @param refList The list of previously checked conditions in this branch.
	 * @param conditionRef This condition has been check to not be a part of a cycle.
	 * @return True if the {@link IFilterCondition} is not a part of a cycle (always when it is a leaf), false otherwise
	 */
	private static boolean areChildConditionsValid(final Connection conDb,
			final List<Integer> refList, final int conditionRef, final List<Integer> cyclesFound) throws SQLException {
		boolean result = false;
		CommonConjunctionFilterConditionTObject conditionTObject = CommonConjunctionFilterConditionDAO
				.select(conDb, conditionRef);
		if (conditionTObject != null) {
			int firstFilterConditionReference = conditionTObject
					.getFirstFilterConditionReference();
			result = isChildConditionValid(conDb, refList,
					firstFilterConditionReference, cyclesFound);

			if (result) {
				int secondFilterConditionReference = conditionTObject
						.getSecondFilterConditionReference();
				result = isChildConditionValid(conDb, refList,
						secondFilterConditionReference, cyclesFound);
			}
		} else {
			result = true;
		}
		return result;
	}

	/**
	 * Checks if the {@link IFilterCondition} specified by the given <i>currentConditionRef</i>
	 * is NOT a part of a cycle
	 * 
	 * @param refList The list of previously checked conditions in this branch.
	 * @param currentConditionRef filter condition to be checked.
	 * @return True if the {@link IFilterCondition} is not a part of a cycle, false otherwise
	 */
	public static boolean isChildConditionValid(final Connection conDb,
			final List<Integer> refList, final int currentConditionRef, final List<Integer> cyclesFound) throws SQLException {
		// cycle detection
		if (refList.contains(currentConditionRef)) {
			cyclesFound.addAll(refList);
			cyclesFound.add(currentConditionRef);
			return false;
		}
		List<Integer> newRefList = new LinkedList<Integer>(refList);
		newRefList.add(currentConditionRef);
		return areChildConditionsValid(conDb, newRefList,
				currentConditionRef, cyclesFound);
	}
	
	/**
	 * Creates a human readable text of a cycle specified by the given list of {@link IFilterCondition} references.
	 * The text could seem like this: X -> Y -> X
	 * @param conDb The database connection
	 * @param cycleList The list of {@link IFilterCondition} references, which build a cycle
	 * @return The textual representation of a cycle
	 */
	public static String createCycleDetectionMessage(final Connection conDb,
			final List<Integer> cycleList) {
		String result = "";
		try {
			if (cycleList != null && cycleList.size() > 0) {
				int conditionRef = cycleList.get(0);
				FilterConditionTObject condition = FilterConditionDAO.select(
						conDb, conditionRef);
				result = result + condition.getName();
				for (int i = 1; i < cycleList.size(); i++) {
					condition = FilterConditionDAO.select(conDb, cycleList
							.get(i));
					result = result + " -> " + condition.getName();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

}
