package gov.bnl.channelfinder.api;

public class Tag {
	private final String name;
	private final String owner;

	/**
	 * @author shroffk
	 *
	 */
	public static class Builder {
		// Required
		private String name;
		// Optional
		private String owner = null;

		public static Builder tag(Tag tag) {
			Builder builder = new Builder();
			builder.name = tag.getName();
			builder.owner = tag.getOwner();
			return builder;
		}

		public static Builder tag(String name) {
			Builder builder = new Builder();
			builder.name = name;
			return builder;
		}

		public static Builder tag(String name, String owner) {
			Builder builder = new Builder();
			builder.name = name;
			builder.owner = owner;
			return builder;
		}

		public Builder owner(String owner) {
			this.owner = owner;
			return this;
		}

		XmlTag toXml() {
			XmlTag xml = new XmlTag();
			xml.setName(name);
			xml.setOwner(owner);
			return xml;
		}
		
		public Tag build(){
			return new Tag(this);
		}
	}

	Tag(XmlTag xml) {
		this.name = xml.getName();
		this.owner = xml.getOwner();
	}

	private Tag(Builder builder) {
		this.name = builder.name;
		this.owner = builder.owner;
	}

	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Tag))
			return false;
		Tag other = (Tag) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	
}
