/**
 * 
 */
package org.csstudio.autocomplete.shift;

import gov.bnl.shiftClient.Shift;
import gov.bnl.shiftClient.ShiftClient;
import gov.bnl.shiftClient.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.shift.ShiftClientManager;
import org.csstudio.shift.util.ShiftSearchUtil;


public class ShiftAutoCompleteSearchProvider extends AbstractAutoCompleteSearchProvider {

    @Override
    Map<String, List<String>> initializeKeyValueMap() {
		try {
		    Map<String, List<String>> keyValueMap = new HashMap<String, List<String>>();
		    ShiftClient shiftClient = ShiftClientManager.getShiftClientFactory().getClient();
		    List<String> shifts = new ArrayList<String>();
		    for (Shift shift : shiftClient.listShifts()) {
		    	shifts.add(shift.getId().toString());
		    }
		    List<String> types = new ArrayList<String>();
		    for(Type type : shiftClient.listTypes()) {
		    	types.add(type.getName());
		    }
		    List<String> timeOptions = new ArrayList<String>(Arrays.asList(
			    "lastMin", "1minAgo", "lastHour", "1hourAgo", "lastDay",
			    "1dayAgo", "lastWeek", "1weekAgo"));
		    List<String> status = new ArrayList<String>(Arrays.asList("active", "end", "signed"));
		    keyValueMap.put(ShiftSearchUtil.SEARCH_KEYWORD_SHIFTS, shifts);
		    keyValueMap.put(ShiftSearchUtil.SEARCH_KEYWORD_TYPE, types);	   
		    keyValueMap.put(ShiftSearchUtil.SEARCH_KEYWORD_START, timeOptions);
		    keyValueMap.put(ShiftSearchUtil.SEARCH_KEYWORD_END, timeOptions);
		    keyValueMap.put(ShiftSearchUtil.SEARCH_KEYWORD_STATUS, status);
		    keyValueMap.put(ShiftSearchUtil.SEARCH_KEYWORD_OWNER, new ArrayList<String>());
		    keyValueMap.put(ShiftSearchUtil.SEARCH_KEYWORD_CLOSEUSER, new ArrayList<String>());
		    keyValueMap.put(ShiftSearchUtil.SEARCH_KEYWORD_LEADOPERATOR, new ArrayList<String>());
	
		    return keyValueMap;
		} catch (Exception e1) {
		    return Collections.emptyMap();
		}
    }
}
