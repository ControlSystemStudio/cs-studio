package org.csstudio.nams.configurator.beans;


public interface FilterAction {
	public String getEmpfaengerName();

	public FilterActionType getFilterActionType();
	
	public void setType(FilterActionType type);

	public FilterActionType[] getFilterActionTypeValues();

	public String getMessage();

	public void setMessage(String value);

	public IReceiverBean getReceiver();
	
	public void setReceiver(IReceiverBean receiver);
}
