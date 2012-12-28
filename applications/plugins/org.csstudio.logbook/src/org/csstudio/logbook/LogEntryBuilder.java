/**
 * 
 */
package org.csstudio.logbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A builder for a default implementation on the LogEntry interface
 * 
 * @author shroffk
 * 
 */
public class LogEntryBuilder {

    private Object id;
    private String text;
    private String owner;
    private Date createdDate;
    private Date modifiedDate;

    private Collection<TagBuilder> tags = new ArrayList<TagBuilder>();
    private Collection<LogbookBuilder> logbooks = new ArrayList<LogbookBuilder>();
    private Collection<PropertyBuilder> properties = new ArrayList<PropertyBuilder>();
    private Collection<AttachmentBuilder> attachments = new ArrayList<AttachmentBuilder>();

    private LogEntryBuilder(String text) {
	this.text = text;
    }

    public static LogEntryBuilder withText(String text) {
	return new LogEntryBuilder(text);
    }

    public LogEntryBuilder addText(String text) {
	this.text = this.text.concat(text);
	return this;
    }

    public LogEntryBuilder setText(String text) {
	this.text = text;
	return this;
    }

    public LogEntryBuilder owner(String owner) {
	this.owner = owner;
	return this;
    }

    public LogEntryBuilder addTag(TagBuilder tagBuilder) {
	this.tags.add(tagBuilder);
	return this;
    }

    public LogEntryBuilder setTags(Collection<TagBuilder> tags) {
	this.tags = new ArrayList<TagBuilder>(tags);
	return this;
    }

    public LogEntryBuilder addProperty(PropertyBuilder propertyBuilder) {
	this.properties.add(propertyBuilder);
	return this;
    }

    public LogEntryBuilder addLogbook(LogbookBuilder logbookBuilder) {
	this.logbooks.add(logbookBuilder);
	return this;
    }

    public LogEntryBuilder setLogbooks(Collection<LogbookBuilder> logbooks) {
	this.logbooks = new ArrayList<LogbookBuilder>(logbooks);
	return this;
    }

    public LogEntryBuilder attach(AttachmentBuilder attachment) {
	this.attachments.add(attachment);
	return this;
    }

    public LogEntryBuilder setAttachments(
	    Collection<AttachmentBuilder> attachments) {
	this.attachments = attachments;
	return this;
    }

    public static LogEntryBuilder logEntry(LogEntry logEntry) {
	LogEntryBuilder logEntryBuilder = new LogEntryBuilder(
		logEntry.getText());
	if (logEntry.getId() != null) {
	    logEntryBuilder.id = logEntry.getId();
	}
	logEntryBuilder.owner = logEntry.getOwner();
	logEntryBuilder.createdDate = logEntry.getCreateDate();
	if (logEntry.getModifiedDate() == null) {
	    logEntryBuilder.modifiedDate = logEntry.getModifiedDate();
	}

	for (Tag tag : logEntry.getTags()) {
	    logEntryBuilder.tags.add(TagBuilder.tag(tag));
	}
	for (Logbook logbook : logEntry.getLogbooks()) {
	    logEntryBuilder.logbooks.add(LogbookBuilder.logbook(logbook));
	}
	for (Property property : logEntry.getProperties()) {
	    logEntryBuilder.properties.add(PropertyBuilder.property(property));
	}
	for (Attachment attachment : logEntry.getAttachment()) {
	    logEntryBuilder.attachments.add(AttachmentBuilder
		    .attachment(attachment));
	}
	return logEntryBuilder;
    }

    public LogEntry build() {
	return new LogEntryImpl(id, text, owner, createdDate, modifiedDate,
		tags, logbooks, properties, attachments);
    }

    /**
     * A Default implementation of the LogEntry interface
     * 
     * @author shroffk
     * 
     */
    private class LogEntryImpl implements LogEntry {

	private final Object id;
	private final String text;
	private final String owner;
	private final Date createdDate;
	private final Date modifiedDate;

	private final Map<String, Tag> tags;
	private final Map<String, Logbook> logbooks;
	private final Map<String, Property> properties;
	private final Collection<Attachment> attachments;

	/**
	 * @param id
	 * @param text
	 * @param owner
	 * @param createdDate
	 * @param modifiedDate
	 * @param tags
	 * @param logbooks
	 * @param properties
	 */
	public LogEntryImpl(Object id, String text, String owner,
		Date createdDate, Date modifiedDate,
		Collection<TagBuilder> tags,
		Collection<LogbookBuilder> logbooks,
		Collection<PropertyBuilder> properties,
		Collection<AttachmentBuilder> attachments) {
	    super();
	    this.id = id;
	    this.text = text;
	    this.owner = owner;
	    this.createdDate = createdDate;
	    this.modifiedDate = modifiedDate;

	    Map<String, Tag> newTags = new HashMap<String, Tag>();
	    for (TagBuilder tagBuilder : tags) {
		Tag tag = tagBuilder.build();
		newTags.put(tag.getName(), tag);
	    }
	    this.tags = Collections.unmodifiableMap(newTags);

	    Map<String, Logbook> newLogbooks = new HashMap<String, Logbook>();
	    for (LogbookBuilder logbookBuilder : logbooks) {
		Logbook logbook = logbookBuilder.build();
		newLogbooks.put(logbook.getName(), logbook);
	    }
	    this.logbooks = Collections.unmodifiableMap(newLogbooks);

	    Map<String, Property> newProperties = new HashMap<String, Property>();
	    for (PropertyBuilder propertyBuilder : properties) {
		Property property = propertyBuilder.build();
		newProperties.put(property.getName(), property);
	    }
	    this.properties = Collections.unmodifiableMap(newProperties);

	    Collection<Attachment> newAttachments = new ArrayList<Attachment>();
	    for (AttachmentBuilder attachmentBuilder : attachments) {
		try {
		    newAttachments.add(attachmentBuilder.build());
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	    this.attachments = Collections
		    .unmodifiableCollection(newAttachments);
	}

	@Override
	public String getText() {
	    return text;
	}

	@Override
	public String getOwner() {
	    return owner;
	}

	@Override
	public Date getCreateDate() {
	    return createdDate;
	}

	@Override
	public Collection<Tag> getTags() {
	    return tags.values();
	}

	@Override
	public Collection<Logbook> getLogbooks() {
	    return logbooks.values();
	}

	@Override
	public Collection<Property> getProperties() {
	    return properties.values();
	}

	@Override
	public Object getId() {
	    return id;
	}

	@Override
	public Date getModifiedDate() {
	    return modifiedDate;
	}

	@Override
	public Collection<Attachment> getAttachment() {
	    return attachments;
	}

    }

}
