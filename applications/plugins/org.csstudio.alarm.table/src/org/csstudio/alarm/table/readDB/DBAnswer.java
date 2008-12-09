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
package org.csstudio.alarm.table.readDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

/**
 * Class holds the answer from the db. Notifies the Observers if a new answer is
 * set.
 * 
 * There are three types of DB answers:
 * 
 * 1: List of all log messages for the DB query.
 * 2: number of log messages that will be deleted (for the warning-message dialog)
 * 3: result (ok, error) of the delete operation.
 * 
 * @author jhatje
 * 
 */
public class DBAnswer extends Observable {

	/**
	 * result of DB query for log messages
	 */
	ArrayList<HashMap<String, String>> _logMessages;

	/** 
	 *  maxSize is true if maxrow in the SQL statement has cut off more messages.
	 */
	boolean _maxSize = false;

	/**
	 * number of messages to delete from DB.
	 */
	int _msgNumberToDelete = -1;
	
	/**
	 * message of the DB delete operation (success, error, ...)
	 */
	String _deleteResult = null;
	
	ResultType _dbqueryType;
	
	/**
	 * Types of answer from DB
	 */
	public enum ResultType {
		LOG_MESSAGES,
		MSG_NUMBER_TO_DELETE,
		DELETE_RESULT
	}

	public ResultType getDbqueryType() {
		return _dbqueryType;
	}
	
	public void setDbqueryType(ResultType dbqueryType) {
		this._dbqueryType = dbqueryType;
	}
	
	public boolean is_maxSize() {
		return _maxSize;
	}
	
	public void setLogMssages(ArrayList<HashMap<String, String>> answer, boolean maxSize) {
		_dbqueryType = ResultType.LOG_MESSAGES;
		_maxSize = maxSize;
		_logMessages = answer;
		
		//set properties for other operations to invalid states.
		_deleteResult = null;
		_msgNumberToDelete = -1;

		setChanged();
		notifyObservers();
	}
	
	public ArrayList<HashMap<String, String>> getLogMessages() {
		return _logMessages;
	}
	
	public int get_msgNumberToDelete() {
		return _msgNumberToDelete;
	}
	
	public void set_msgNumberToDelete(int numberToDelete) {
		_dbqueryType = ResultType.MSG_NUMBER_TO_DELETE;
		_msgNumberToDelete = numberToDelete;
		
		//set properties for other operations to invalid states.
		_deleteResult = null;

		setChanged();
		notifyObservers();
	}
	
	public String getDeleteResult() {
		return _deleteResult;
	}
	
	public void setDeleteResult(String deleteResult) {
		_dbqueryType = ResultType.DELETE_RESULT;
		this._deleteResult = deleteResult;

		//set properties for other operations to invalid states.
		_msgNumberToDelete = -1;
		_maxSize = false;
		_logMessages = null;

		setChanged();
		notifyObservers();
	}
	
	public void set_maxSize(boolean size) {
		_maxSize = size;
	}
}