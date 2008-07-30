package org.csstudio.nams.configurator.beans;

public class MessageTemplateBean {
	private final String name;
	private final String message;

	public MessageTemplateBean(final String name, final String message) {
		this.name = name;
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

	public String getName() {
		return this.name;
	}
}
