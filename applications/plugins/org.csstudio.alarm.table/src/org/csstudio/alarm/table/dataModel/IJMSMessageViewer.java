package org.csstudio.alarm.table.dataModel;

public interface IJMSMessageViewer {

	/**
	 * Update the view to reflect the fact that a JMSMessage was added 
	 * to the JMSMessage list
	 * 
	 * @param jmsm
	 */
	public void addJMSMessage(JMSMessage jmsm);
	
	/**
	 * Update the view to reflect the fact that a JMSMessage was removed 
	 * from the JMSMessage list
	 * 
	 * @param jmsm
	 */
	public void removeJMSMessage(JMSMessage jmsm);
	
}
