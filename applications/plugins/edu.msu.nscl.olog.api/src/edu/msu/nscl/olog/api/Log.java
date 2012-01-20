package edu.msu.nscl.olog.api;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Eric Berryman taken from shroffk
 * 
 */
public class Log implements Comparable<Log> {
	private final Long id;
	private final String owner;
	private final String description;
	private final String level;
	private final Date createdDate;
	private final Date modifiedDate;
	private final int version;

	private final Map<String, Tag> tags;
	private final Map<String, Logbook> logbooks;
	private final Map<String, Attachment> attachments;
	private final Map<String, Property> properties;

	Log(XmlLog log) {
		this.id = log.getId();
		this.owner = log.getOwner();
		this.description = log.getDescription();
		this.level = log.getLevel();
		this.createdDate = log.getCreatedDate();
		this.modifiedDate = log.getModifiedDate();
		this.version = log.getVersion();
		Map<String, Tag> newTags = new HashMap<String, Tag>();
		for (XmlTag tag : log.getXmlTags()) {
			newTags.put(tag.getName(), new Tag(tag));
		}
		this.tags = Collections.unmodifiableMap(newTags);
		Map<String, Logbook> newLogbooks = new HashMap<String, Logbook>();
		for (XmlLogbook logbook : log.getXmlLogbooks()) {
			newLogbooks.put(logbook.getName(), new Logbook(logbook));
		}
		this.logbooks = Collections.unmodifiableMap(newLogbooks);
		Map<String, Attachment> newAttachments = new HashMap<String, Attachment>();
		for (XmlAttachment attachment : log.getXmlAttachments()
				.getAttachments()) {
			newAttachments.put(attachment.getUri(), new Attachment(attachment));
		}
		this.attachments = Collections.unmodifiableMap(newAttachments);
		Map<String, Property> newProperties = new HashMap<String, Property>();
		for (XmlProperty property : log.getXmlProperties()) {
			newProperties.put(property.getName(), new Property(property));
		}
		this.properties = Collections.unmodifiableMap(newProperties);

	}

	public Long getId() {
		return id;
	}

	public String getOwner() {
		return owner;
	}

	public String getDescription() {
		return description;
	}

	public String getLevel() {
		return level;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public int getVersion() {
		return version;
	}

	/**
	 * Get a Collection of all the Tags associated with this log.
	 * 
	 * @return
	 */
	public Collection<Tag> getTags() {
		return tags.values();
	}

	/**
	 * Get a set of Names of all the tags associated with this log.
	 * 
	 * @return Set of all tag Names
	 */
	public Collection<String> getTagNames() {
		return tags.keySet();
	}

	/**
	 * Returns a Tag with the name tagName if it exists on this log else returns
	 * null.
	 * 
	 * @param tagName
	 * @return {@link Tag} with name tagName else null if no such tag attached
	 *         to this log
	 */
	public Tag getTag(String tagName) {
		return tags.get(tagName);
	}

	/**
	 * Get all the logbooks associated with this log.
	 * 
	 * @return a Collection of all {@link Logbook}
	 */
	public Collection<Logbook> getLogbooks() {
		return logbooks.values();
	}

	/**
	 * Get a set of all the logbook names.
	 * 
	 * @return
	 */
	public Collection<String> getLogbookNames() {
		return logbooks.keySet();
	}

	// @deprecated not really deprecated, but javadoc doesn't have future
	@Deprecated
	public Collection<Attachment> getAttachments() {
		return attachments.values();
	}

	/**
	 * Get all the {@link Property}s associated with this log.
	 * 
	 * @return
	 */
	public Collection<Property> getProperties() {
		return properties.values();
	}

	/**
	 * Get a set of names for all the properties associated with this log.
	 * 
	 * @return a set of all property names.
	 */
	public Collection<String> getPropertyNames() {
		return properties.keySet();
	}

	/**
	 * return the {@link Property} with name <tt>propertyName</tt> if it exists
	 * on this log else return null.
	 * 
	 * @param propertyName
	 * @return {@link Property} with name propertyName else null if no such
	 *         property exists on this log.
	 */
	public Property getProperty(String propertyName) {
		return properties.get(propertyName);
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Log obj) throws ClassCastException {
		if (!(obj instanceof Log))
			throw new ClassCastException("A Log object expected.");
		return (int) (this.id - ((Log) obj).getId());
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
		if (!(obj instanceof Log))
			return false;
		Log other = (Log) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id)) {
			return false;
		} // else if (version != other.version)
			// return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Log#" + id + ":v." + version + " [ description=" + description + "]";
	}

}
