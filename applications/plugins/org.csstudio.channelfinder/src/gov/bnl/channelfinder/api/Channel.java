package gov.bnl.channelfinder.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author shroffk
 * 
 */
public class Channel {

	private final String name;
	private final String owner;
	private final Set<Tag> tags;
	private final Set<Property> properties;

	public static class Builder {
		// required
		private String name;
		// optional
		private String owner;
		private Set<Tag.Builder> tags = new HashSet<Tag.Builder>();
		private Set<Property.Builder> properties = new HashSet<Property.Builder>();

		public static Builder channel(Channel channel) {
			Builder channelBuilder = new Builder();
			channelBuilder.name = channel.getName();
			channelBuilder.owner = channel.getOwner();
			for (Tag tag : channel.getTags()) {
				channelBuilder.tags.add(Tag.Builder.tag(tag));
			}
			for (Property property : channel.getProperties()) {
				channelBuilder.properties.add(Property.Builder
						.property(property));
			}
			return channelBuilder;
		}

		public static Builder channel(String name) {
			Builder channelBuilder = new Builder();
			channelBuilder.name = name;
			return channelBuilder;
		}

		public Builder owner(String owner) {
			this.owner = owner;
			return this;
		}

		public Builder with(Tag.Builder tag) {
			tags.add(tag);
			return this;
		}

		public Builder with(Property.Builder property) {
			properties.add(property);
			return this;
		}

		XmlChannel toXml() {
			XmlChannel xmlChannel = new XmlChannel(name, owner);
			for (Tag.Builder tag : tags) {
				xmlChannel.addXmlTag(tag.toXml());
			}
			for (Property.Builder property : properties) {
				xmlChannel.addXmlProperty(property.toXml());
			}
			return xmlChannel;

		}

		Channel build() {
			return new Channel(this);
		}
	}

	Channel(XmlChannel channel) {
		this.name = channel.getName();
		this.owner = channel.getOwner();
		Set<Tag> newTags = new HashSet<Tag>();
		for (XmlTag tag : channel.getXmlTags().getTags()) {
			newTags.add(new Tag(tag));
		}
		this.tags = Collections.unmodifiableSet(newTags);
		Set<Property> newProperties = new HashSet<Property>();
		for (XmlProperty property : channel.getXmlProperties().getProperties()) {
			newProperties.add(new Property(property));
		}
		this.properties = Collections.unmodifiableSet(newProperties);

	}

	private Channel(Builder builder) {
		this.name = builder.name;
		this.owner = builder.owner;
		Set<Tag> newTags = new HashSet<Tag>();
		for (Tag.Builder tag : builder.tags) {
			newTags.add(tag.build());
		}
		this.tags = Collections.unmodifiableSet(newTags);
		Set<Property> newProperties = new HashSet<Property>();
		for (Property.Builder property : builder.properties) {
			newProperties.add(property.build());
		}
		this.properties = Collections.unmodifiableSet(newProperties);
	}

	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public String getOwner() {
		return owner;
	}

	public Collection<Tag> getTags() {
		return tags;
	}

	public Collection<Property> getProperties() {
		return properties;
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
		if (!(obj instanceof Channel))
			return false;
		Channel other = (Channel) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Channel [name=" + name + ", owner=" + owner + "]";
	}

}
