package org.csstudio.utility.toolbox.framework.jpa;

import org.apache.commons.lang.builder.ToStringBuilder;

public class JpaQuery {

	private String query;

	public JpaQuery(String query) {
		this.query = query;
	}

	public String getQueryString() {
		return query;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
