package org.csstudio.nams.configurator.beans;


public interface FilterAction extends Cloneable {
	public String getEmpfaengerName();

	public FilterActionType getFilterActionType();
	
	public void setType(FilterActionType type);

	public FilterActionType[] getFilterActionTypeValues();

	public String getMessage();

	public void setMessage(String value);

	public IReceiverBean getReceiver();
	
	public void setReceiver(IReceiverBean receiver);
	
	public Object clone() throws CloneNotSupportedException;
}
