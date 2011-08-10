
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
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.jms.MapMessage;

import org.csstudio.ams.AMSException;
import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.Log;
import org.csstudio.ams.dbAccess.configdb.FilterConditionTimeBasedDAO;
import org.csstudio.ams.dbAccess.configdb.FilterConditionTimeBasedItemsDAO;
import org.csstudio.ams.dbAccess.configdb.FilterConditionTimeBasedItemsTObject;
import org.csstudio.ams.dbAccess.configdb.FilterConditionTimeBasedTObject;
import org.csstudio.ams.dbAccess.configdb.HistoryDAO;
import org.csstudio.ams.dbAccess.configdb.HistoryTObject;
import org.csstudio.ams.dbAccess.configdb.MessageDAO;

public class FilterConditionTimeBased implements IFilterCondition {
    
    private static final DateFormat df = DateFormat.getDateTimeInstance();

    public static final short OPERATOR_TEXT_EQUAL = 1;
	public static final short OPERATOR_TEXT_NOT_EQUAL = 2;
	
	public static final short OPERATOR_NUMERIC_LT = 3;
	public static final short OPERATOR_NUMERIC_LT_EQUAL = 4;
	public static final short OPERATOR_NUMERIC_EQUAL = 5;
	public static final short OPERATOR_NUMERIC_GT_EQUAL = 6;
	public static final short OPERATOR_NUMERIC_GT = 7;
	public static final short OPERATOR_NUMERIC_NOT_EQUAL = 8;

	public static final short OPERATOR_TIME_BEFORE = 9;
	public static final short OPERATOR_TIME_BEFORE_EQUAL = 10;
	public static final short OPERATOR_TIME_EQUAL = 11;
	public static final short OPERATOR_TIME_AFTER_EQUAL = 12;
	public static final short OPERATOR_TIME_AFTER = 13;
	public static final short OPERATOR_TIME_NOT_EQUAL = 14;

	private FilterConditionTimeBasedTObject fcObj = null;
	private int filterRef = -1;
	private Connection conDb = null;
	
	@Override
    public void init(Connection c, int filterConditionID, int fr) throws AMSException
	{
		this.filterRef = fr;
		this.conDb = c;
		
		try
		{
			fcObj = FilterConditionTimeBasedDAO.select(conDb, filterConditionID);
			if (fcObj == null) 
				throw new AMSException("FilterConditionString.FilterConditionID=" + filterConditionID + " not found.");
			
		}
		catch (SQLException ex)
		{
			throw new AMSException(ex);
		}
	}
	
	private int numericCompare(String value, String compValue, int operator) throws Exception
	{
		double dVal = 0.0;
		try
		{
			dVal = Double.parseDouble(value);
		}
		catch(Exception e)
		{
			Log.log(Log.WARN, "fcs(id/operator) = " 
					+ fcObj.getFilterConditionRef() + "/" + operator 
					+ " ! Input value '" + value + "' is not a number!");
			return -1;
		}
		double dCompVal = Double.parseDouble(compValue);

		return Double.compare(dVal, dCompVal);
	}
	
	private int timeCompare(String value, String compValue) throws Exception
	{
		Date dateValue = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US).parse(value); 
		Date dateCompValue = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US).parse(compValue); 

		return dateValue.compareTo(dateCompValue);
	}
	
	@Override
    public boolean match(MapMessage map)
	{
		String value = null;
		String identifier = null;
		
		try
		{			
			value = map.getString(fcObj.getConfirmKeyValue());
			identifier = map.getString("NAME");
		}
		catch(Exception ex)
		{
			Log.log(this, Log.WARN, ex);
			return false;
		}
		
		//check first for confirming
		if(match(value,  fcObj.getConfirmCompValue(), fcObj.getConfirmOperator()))
		{
			Log.log(Log.INFO, "Message confirms time based condition in filter=" + filterRef + " filterCondition=" + fcObj.getFilterConditionRef());
			try
			{
				FilterConditionTimeBasedItemsTObject item = 
					FilterConditionTimeBasedItemsDAO.selectWaitingByIdentifier(conDb, fcObj.getFilterConditionRef(), filterRef, identifier);
				if(item != null)
				{
					// HistoryLog Confirmation/Cancellation) TimeBased-Filter
					HistoryTObject history = new HistoryTObject();
					history.setTimeNew(new Date(System.currentTimeMillis()));
					history.setType("TimeBased");
					String str = "?Unknown?";
					if (item.getTimeOutAction() == AmsConstants.TIMEBEHAVIOR_CONFIRMED_THEN_ALARM)
						str = "Confirmation";
					else if (item.getTimeOutAction() == AmsConstants.TIMEBEHAVIOR_TIMEOUT_THEN_ALARM)
						str = "Cancellation";
					history.setDescription(str + " for Msg " + item.getMessageRef()
							+ " (FC=" + item.getFilterConditionRef()
							+ "/F=" + item.getFilterRef() + ")");
					HistoryDAO.insert(conDb, history);
					
					FilterConditionTimeBasedItemsDAO.updateState(conDb, item.getItemID(), AmsConstants.STATE_WAITING, AmsConstants.STATE_CONFIRMED);
				}
			}
			catch(Exception ex)
			{
				Log.log(this, Log.ERROR, ex);
			}
			return false;
		}
		
		//check for activating condition
		if(match(value, fcObj.getStartCompValue(), fcObj.getStartOperator()))
		{
			Log.log(Log.INFO, "Message actives time based condition in filter=" + filterRef + " filterCondition=" + fcObj.getFilterConditionRef());

			try
			{
				FilterConditionTimeBasedItemsTObject item = FilterConditionTimeBasedItemsDAO.selectWaitingByIdentifier(conDb, fcObj.getFilterConditionRef(), filterRef, identifier);
				if(item != null)
				{
					Log.log(this, Log.INFO,"There already is an waiting alarm for identifier '" + identifier + "'");
					return false;
				}
				
				int messageID = MessageDAO.insert(conDb, map, true);
				
				Date startTime = new Date();
				Date endTime = new Date(startTime.getTime() + (fcObj.getTimePeriod() * 1000));

				// HistoryLog Activation TimeBased-Filter
				String timeBehav = "unknown.";
				if (fcObj.getTimeBehavior() == AmsConstants.TIMEBEHAVIOR_CONFIRMED_THEN_ALARM)
					timeBehav = "Alarm at Confirmation.";
				else if (fcObj.getTimeBehavior() == AmsConstants.TIMEBEHAVIOR_TIMEOUT_THEN_ALARM)
					timeBehav = "Alarm at Timeout.";
				HistoryTObject history = new HistoryTObject();
				history.setTimeNew(new Date(System.currentTimeMillis()));
				history.setType("TimeBased");
				history.setDescription("New by Msg " + messageID
						+ " (FC=" + fcObj.getFilterConditionRef()
						+ "/F=" + filterRef	+ ") EndTime=" + df.format(endTime)
						+ "  " + timeBehav);
				HistoryDAO.insert(conDb, history);
				
				
				FilterConditionTimeBasedItemsDAO.insert(
						conDb, 
						new FilterConditionTimeBasedItemsTObject(
								-1, 
								fcObj.getFilterConditionRef(), 
								filterRef, 
								identifier, 
								(short)0, 
								startTime, 
								endTime,
								fcObj.getTimeBehavior(),
								messageID
								)
						);
			}
			catch(Exception ex)
			{
				Log.log(this, Log.ERROR, ex);
			}
		}
		return false;
	}
	
	private boolean match(String value, String compValue, int operator)
	{		
		try
		{
			switch (operator)
			{
				// text compare
				case OPERATOR_TEXT_EQUAL:
					return WildcardStringCompare.compare(value, compValue);
				case OPERATOR_TEXT_NOT_EQUAL:
					return !WildcardStringCompare.compare(value, compValue);
				
				// numeric compare
				case OPERATOR_NUMERIC_LT: 
					return numericCompare(value, compValue, operator) < 0;
				case OPERATOR_NUMERIC_LT_EQUAL: 
					return numericCompare(value, compValue, operator) <= 0;
				case OPERATOR_NUMERIC_EQUAL: 
					return numericCompare(value, compValue, operator) == 0;
				case OPERATOR_NUMERIC_GT_EQUAL: 
					return numericCompare(value, compValue, operator) >= 0;
				case OPERATOR_NUMERIC_GT: 
					return numericCompare(value, compValue, operator) > 0;
				case OPERATOR_NUMERIC_NOT_EQUAL: 
					return numericCompare(value, compValue, operator) != 0;
				
				// time compare
				case OPERATOR_TIME_BEFORE: 
					return timeCompare(value, compValue) < 0;
				case OPERATOR_TIME_BEFORE_EQUAL: 
					return timeCompare(value, compValue) <= 0;
				case OPERATOR_TIME_EQUAL: 
					return timeCompare(value, compValue) == 0;
				case OPERATOR_TIME_AFTER_EQUAL: 
					return timeCompare(value, compValue) >= 0;
				case OPERATOR_TIME_AFTER: 
					return timeCompare(value, compValue) > 0;
				case OPERATOR_TIME_NOT_EQUAL: 
					return timeCompare(value, compValue) != 0;
				default: 
					return false;
			}
		}
		catch (Exception ex)
		{
    		Log.log(this, Log.WARN, ex);
    		return false;
		}
	}
}