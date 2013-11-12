package org.csstudio.shift;

import java.util.Date;

public interface Shift {
	
	Object getId();
	
	String getOwner();
	
	Date getStartDate();
	
	Date getEndDate();
	
	String getType();
	
	String getDescription();
	
	String getCloseShiftUser();
	
	String getOnShiftPersonal();
	
	String getLeadOperator();
	
	String getReport();
	

}
