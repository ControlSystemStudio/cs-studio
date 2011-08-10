
package org.csstudio.nams.configurator.beans;

public class MessageTemplateBean {
    
	private final String name;
	private final String message;

	public MessageTemplateBean(final String n, final String m) {
		this.name = n;
		this.message = m;
	}

	public String getMessage() {
		return this.message;
	}

	public String getName() {
		return this.name;
	}
}
