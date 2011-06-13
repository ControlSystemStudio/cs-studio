/**
 * 
 */
package gov.bnl.channelfinder.api;

/**
 * @author shroffk
 * 
 */
public class Property {
	private final String name;
	private final String owner;
	private final String value;

	public static class Builder {
		// required
		private String name;
		// optional
		private String owner;
		private String value;

		public static Builder property(String name){
			Builder propertyBuilder = new Builder();
			propertyBuilder.name = name;
			return propertyBuilder;
		}
		
		/**
		 * @param name
		 * @param value
		 * @return
		 */
		public static Builder property(String name, String value) {
			Builder propertyBuilder = new Builder();
			propertyBuilder.name = name;
			propertyBuilder.value = value;
			return propertyBuilder;
		}

		public static Builder property(Property property) {
			Builder propertyBuilder = new Builder();
			propertyBuilder.name = property.getName();
			propertyBuilder.value = property.getValue();
			propertyBuilder.owner = property.getOwner();
			return propertyBuilder;
		}

		public Builder owner(String owner) {
			this.owner = owner;
			return this;
		}
		
		public Builder value(String value) {
			this.value = value;
			return this;
		}
		
		XmlProperty toXml() {
			return new XmlProperty(name, owner, value);
		}

		public Property build() {
			return new Property(this);
		}
	}

	/**
	 * @param xmlProperty
	 */
	Property(XmlProperty xmlProperty) {
		this.name = xmlProperty.getName();
		this.owner = xmlProperty.getOwner();
		this.value = xmlProperty.getValue();
	}

	private Property(Builder builder) {
		this.name = builder.name;
		this.value = builder.value;
		this.owner = builder.owner;
	}

	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner;
	}

	public String getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Property))
			return false;
		Property other = (Property) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
