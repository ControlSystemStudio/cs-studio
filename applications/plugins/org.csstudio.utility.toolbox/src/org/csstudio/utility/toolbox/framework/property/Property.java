package org.csstudio.utility.toolbox.framework.property;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Property {

	private String name;
	
	private PropertyNameHint hint;
	
	private SearchTermType type = SearchTermType.STRING;

	public static enum PropertyNameHint {
		SubQueryOnly
	};

	public static Property P(String name) {
		return new Property(name);
	}
	
	public String getName() {
		return name;
	}

	public Property(String name) {
		this.name = name;
	}

	public Boolean isSubQuery() {
		if (hint == null) {
			return false;
		}
		return hint == PropertyNameHint.SubQueryOnly;
	}

	public SearchTermType getType() {
		return type;
	}

	public void setType(SearchTermType type) {
		this.type = type;
	}
	
	public PropertyNameHint getHint() {
		return hint;
	}

	public void setHint(PropertyNameHint hint) {
		this.hint = hint;
	}


	public static List<Property> createList(String... names) {
		List<Property> properties = new ArrayList<Property>();
		for (String name : names) {
			properties.add(new Property(name));
		}
		return properties;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(name).toHashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}

		Property rhs = (Property) obj;

		return new EqualsBuilder().append(name, rhs.name).isEquals();
	}

}
