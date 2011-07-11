
package org.csstudio.nams.configurator.beans;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.FilterActionType;

public interface FilterAction extends Cloneable {
	public Object clone() throws CloneNotSupportedException;

	public String getEmpfaengerName();

	public FilterActionType getFilterActionType();

	public FilterActionType[] getFilterActionTypeValues();

	public String getMessage();

	public IReceiverBean getReceiver();

	public void setMessage(String value);

	public void setReceiver(IReceiverBean receiver);

	public void setType(FilterActionType type);
}
