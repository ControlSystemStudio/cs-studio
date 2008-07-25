package org.csstudio.nams.configurator.beans;

public class MessageTemplateBean {
	private final String name;
	private final String message;
	
	public MessageTemplateBean(String name, String message){
		this.name = name;
		this.message = message;
	}
	
	public String getName() {
		return name;
	}
	public String getMessage() {
		return message;
	}
}
