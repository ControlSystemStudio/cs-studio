package org.csstudio.nams.configurator.beans;

public abstract class AbstractFilterAction<Type extends FilterActionType> implements FilterAction {

	protected String message = "";
	protected Type type;
	protected IReceiverBean receiver;
	private final Class<Type> clazz;
	
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
			throw new IllegalArgumentException("FilterAction does not support FilterActionType " + type.getClass().getSimpleName());
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
}
