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
 * A builder for a default implementation on the {@link LogEntry} interface
 * 
 * @author shroffk
 * 
 */
public class LogEntryBuilder {

    private Object id;
    private String level;
    private String text;
    private String owner;
    private Date createdDate;
    private Date modifiedDate;

    private Map<String, TagBuilder> tags = new HashMap<String, TagBuilder>();
    private Map<String, LogbookBuilder> logbooks = new HashMap<String, LogbookBuilder>();
    private Map<String, PropertyBuilder> properties = new HashMap<String, PropertyBuilder>();
    private Map<String, AttachmentBuilder> attachments = new HashMap<String, AttachmentBuilder>();

    private LogEntryBuilder(String text) {
	this.text = text;
    }

    /**
     * Create a constructor with the text _text_
     * 
     * @param text - the initial text to create the builder with.
     * @return LogEntryBuilder
     */
    public static LogEntryBuilder withText(String text) {
	return new LogEntryBuilder(text);
    }

    /**
     * Set the level of the log entry
     * 
     * @param Level
     * @return LogEntryBuilder
     */
    public LogEntryBuilder setLevel(String level){
	this.level = level;
	return this;	
    }
    
    /**
     * Append the _text_ to the existing text in the builder
     * 
     * @param text - the text to the appended
     * @return LogEntryBuilder
     */
    public LogEntryBuilder addText(String text) {
	this.text = this.text.concat(text);
	return this;
    }

    /**
     * Replace the existing text in the builder with _text_
     * 
     * @param text
     * @return LogEntryBuilder
     */
    public LogEntryBuilder setText(String text) {
	this.text = text;
	return this;
    }

    /**
     * set the owner of this logEntry
     * 
     * @param owner
     *            - name of the owner
     * @return LogEntryBuilder
     */
    public LogEntryBuilder owner(String owner) {
	this.owner = owner;
	return this;
    }

    /**
     * Append the tag described by _tagBuilder_ to the existing tagBuilders
     * 
     * @param tagBuilder
     * @return LogEntryBuilder
     */
    public LogEntryBuilder addTag(TagBuilder tagBuilder) {
	this.tags.put(tagBuilder.build().getName(), tagBuilder);
	return this;
    }

    /**
     * Remove the tag with the name _tagName_
     * 
     * @param tagName
     * @return LogEntryBuilder
     */
    public LogEntryBuilder removeTag(String tagName) {
	this.tags.remove(tagName);
	return this;
    }
    

    public void removeProperty(String propertyname) {
	this.properties.remove(propertyname);
    }

    /**
     * Set the list of tags to _tags_
     * 
     * @param tags
     * @return LogEntryBuilder
     */
    public LogEntryBuilder setTags(Collection<TagBuilder> tags) {
	this.tags = new HashMap<String, TagBuilder>(tags.size());
	for (TagBuilder tagBuilder : tags) {
	    this.tags.put(tagBuilder.build().getName(), tagBuilder);
	}
	return this;
    }

    /**
     * Append the property described by the _propertyBuilder_ to the existing
     * properties
     * 
     * @param propertyBuilder
     * @return LogEntryBuilder
     */
    public LogEntryBuilder addProperty(PropertyBuilder propertyBuilder) {
	this.properties.put(propertyBuilder.build().getName(), propertyBuilder);
	return this;
    }

    /**
     * Append the logbook described by _logbookBuilder_ to the existing logbooks
     * 
     * @param logbookBuilder
     * @return LogEntryBuilder
     */
    public LogEntryBuilder addLogbook(LogbookBuilder logbookBuilder) {
	this.logbooks.put(logbookBuilder.build().getName(), logbookBuilder);
	return this;
    }

    /**
     * Reomve the logbook identified by _logbookName_
     * 
     * @param logbookName
     * @return
     */
    public LogEntryBuilder removeLogbook(String logbookName) {
	this.logbooks.remove(logbookName);
	return this;
    }

    /**
     * Set the list of logbooks to _logbooks_
     * 
     * @param logbooks
     * @return
     */
    public LogEntryBuilder setLogbooks(Collection<LogbookBuilder> logbooks) {
	this.logbooks = new HashMap<String, LogbookBuilder>(logbooks.size());
	for (LogbookBuilder logbookBuilder : logbooks) {
	    this.logbooks.put(logbookBuilder.build().getName(), logbookBuilder);
	}
	return this;
    }

    /**
     * Append _attachment_ to the existing attachments
     * 
     * @param attachment
     * @return LogEntryBuilder
     * @throws IOException
     */
    public LogEntryBuilder attach(AttachmentBuilder attachment)
	    throws IOException {
	this.attachments.put(attachment.build().getFileName(), attachment);
	return this;
    }

    /**
     * Remove the attachment identified by the name _name_
     * 
     * @param name
     * @return
     */
    public LogEntryBuilder removeAttachment(String name) {
	this.attachments.remove(name);
	return this;
    }

    /**
     * Set the attachments to _attachments_
     * 
     * @param attachments
     * @return LogEntryBuilder
     * @throws IOException
     */
    public LogEntryBuilder setAttachments(
	    Collection<AttachmentBuilder> attachments) throws IOException {
	this.attachments = new HashMap<String, AttachmentBuilder>(
		attachments.size());
	for (AttachmentBuilder attachmentBuilder : attachments) {
	    this.attachments.put(attachmentBuilder.build().getFileName(),
		    attachmentBuilder);
	}
	return this;
    }

    /**
     * Create a logEntryBuilder initialized using the _logEntry_
     * 
     * @param logEntry
     * @return LogEntryBuilder
     * @throws IOException
     */
    public static LogEntryBuilder logEntry(LogEntry logEntry)
	    throws IOException {
	LogEntryBuilder logEntryBuilder = new LogEntryBuilder(
		logEntry.getText());
	if (logEntry.getId() != null) {
	    logEntryBuilder.id = logEntry.getId();
	}
	logEntryBuilder.level = logEntry.getLevel();
	logEntryBuilder.owner = logEntry.getOwner();
	logEntryBuilder.createdDate = logEntry.getCreateDate();
	if (logEntry.getModifiedDate() == null) {
	    logEntryBuilder.modifiedDate = logEntry.getModifiedDate();
	}

	for (Tag tag : logEntry.getTags()) {
	    logEntryBuilder.tags.put(tag.getName(), TagBuilder.tag(tag));
	}
	for (Logbook logbook : logEntry.getLogbooks()) {
	    logEntryBuilder.logbooks.put(logbook.getName(),
		    LogbookBuilder.logbook(logbook));
	}
	for (Property property : logEntry.getProperties()) {
	    logEntryBuilder.properties.put(property.getName(),
		    PropertyBuilder.property(property));
	}
	for (Attachment attachment : logEntry.getAttachment()) {
	    logEntryBuilder.attachments.put(attachment.getFileName(),
		    AttachmentBuilder.attachment(attachment));
	}
	return logEntryBuilder;
    }

    /**
     * Build LogEntry object using the parameters set in the builder
     * 
     * @return LogEntry - a immutable instance of the {@link LogEntry}
     * @throws IOException
     */
    public LogEntry build() throws IOException {
	return new LogEntryImpl(id, level, text, owner, createdDate, modifiedDate,
		tags.values(), logbooks.values(), properties.values(),
		attachments.values());
    }

    /**
     * A Default implementation of the {@link LogEntry}
     * 
     * @author shroffk
     * 
     */
    private class LogEntryImpl implements LogEntry {

	private final Object id;
	private final String level;
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
	 * @throws IOException
	 */
	public LogEntryImpl(Object id, String level, String text, String owner,
		Date createdDate, Date modifiedDate,
		Collection<TagBuilder> tags,
		Collection<LogbookBuilder> logbooks,
		Collection<PropertyBuilder> properties,
		Collection<AttachmentBuilder> attachments) throws IOException {
	    super();
	    this.id = id;
	    this.level = level;
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
		newAttachments.add(attachmentBuilder.build());
	    }
	    this.attachments = Collections
		    .unmodifiableCollection(newAttachments);
	}


	@Override
	public String getLevel() {
	    return level;
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
