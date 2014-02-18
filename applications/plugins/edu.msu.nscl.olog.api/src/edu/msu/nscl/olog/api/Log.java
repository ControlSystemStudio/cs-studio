package edu.msu.nscl.olog.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.sun.jersey.core.util.MultivaluedMapImpl;

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
	private final Multimap<String, Property> properties;

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
			newAttachments.put(attachment.getFileName(), new Attachment(attachment));
		}
		this.attachments = Collections.unmodifiableMap(newAttachments);
		Multimap<String, Property> newProperties = HashMultimap.create();
		for (XmlProperty property : log.getXmlProperties()) {
			newProperties.put(property.getName(), new Property(property));
		}
		this.properties = newProperties;

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

	/**
	 * Get all the attachments associated with this log.
	 * 	
	 * @return
	 */
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
	public Collection<Property> getProperty(String propertyName) {
		return properties.get(propertyName);
	}

	@Override
	public int compareTo(Log obj) throws ClassCastException {
		if (!(obj instanceof Log))
			throw new ClassCastException("A Log object expected.");
		return (int) (this.id - ((Log) obj).getId());
	}
	
	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((id == null) ? 0 : id.hashCode());
	    result = prime * result + version;
	    return result;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
		return true;
	    if (obj == null)
		return false;
	    if (getClass() != obj.getClass())
		return false;
	    Log other = (Log) obj;
	    if (id == null) {
		if (other.id != null)
		    return false;
	    } else if (!id.equals(other.id))
		return false;
	    if (version != other.version)
		return false;
	    return true;
	}

	@Override
	public String toString() {
		return "Log#" + id + ":v." + version + " [ description=" + description + "]";
	}

}
