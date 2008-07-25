package org.csstudio.nams.configurator.beans;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.FilterActionType;

public abstract class AbstractFilterAction<Type extends FilterActionType>
		implements FilterAction {

	protected String message = "";
	protected Type type;
	protected IReceiverBean receiver;
	protected final Class<Type> clazz;

	protected AbstractFilterAction(Class<Type> clazz) {
		this.clazz = clazz;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public FilterActionType getFilterActionType() {
		return type;
	}

	@SuppressWarnings("unchecked")
	public void setType(FilterActionType type) {
		if (clazz.isInstance(type)) {
			this.type = (Type) type;
		} else {
			throw new IllegalArgumentException(
					"FilterAction does not support FilterActionType "
							+ type.getClass().getSimpleName());
		}
	}

	public IReceiverBean getReceiver() {
		return receiver;
	}

	public void setReceiver(IReceiverBean receiver) {
		this.receiver = receiver;
	}

	public String getEmpfaengerName() {
		return receiver.getDisplayName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result
				+ ((receiver == null) ? 0 : receiver.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AbstractFilterAction<Type> other = (AbstractFilterAction<Type>) obj;
		if (clazz == null) {
			if (other.clazz != null)
				return false;
		} else if (!clazz.equals(other.clazz))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (receiver == null) {
			if (other.receiver != null)
				return false;
		} else if (!receiver.equals(other.receiver))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
	@Override
	public abstract Object clone() throws CloneNotSupportedException;
}
