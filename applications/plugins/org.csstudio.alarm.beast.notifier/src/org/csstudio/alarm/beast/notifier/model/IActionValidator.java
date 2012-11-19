package org.csstudio.alarm.beast.notifier.model;

import org.csstudio.alarm.beast.client.AADataStructure;

/**
 * Automated action command validator interface.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public interface IActionValidator 
{
	/**
	 * Initialize the validator with automated action details
	 * @param details from {@link AADataStructure}
	 */
	public void init(String details);
	
	/** @return <code>true</code> if the command is valid.
	 * @throws Exception
	 */
	public boolean validate() throws Exception;
}
