package org.csstudio.alarm.table.readDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;


/**
 * Class holds the answer from the db. Notifies the
 * Observers if a new answer is set.
 * 
 * @author jhatje
 *
 */
public class DBAnswer extends Observable {

	ArrayList<HashMap<String, String>> dbAnswer;
	
	public ArrayList<HashMap<String, String>> getDBAnswer() {
		return dbAnswer;
	}
	
	public void setDBAnswer(ArrayList<HashMap<String, String>> answer) {
		dbAnswer = answer;
		setChanged();
		notifyObservers();
	}
	
}
