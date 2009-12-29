package org.csstudio.opibuilder.script;

/**The data structure which include the pvName and trigger flag 
 * @author Xihui Chen
 *
 */
public class PVTuple{
	public String pvName;
	public boolean trigger;
	
	public PVTuple(String pvName, boolean trigger) {
		this.pvName = pvName;
		this.trigger = trigger;
	}
	
}