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
import java.util.Iterator;
import java.util.Map;

/**
 * A builder for a default implementation on the {@link LogEntry} interface
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

    /**
     * Create a constructor with the text _text_
     * 
     * @param text
     *            - the initial text to create the builder with.
     * @return LogEntryBuilder
     */
    public static LogEntryBuilder withText(String text) {
	return new LogEntryBuilder(text);
    }

    /**
     * Append the _text_ to the existing text in the builder
     * 
     * @param text
     *            - the text to the appended
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
	this.tags.add(tagBuilder);
	return this;
    }

    /**
     * Set the list of tags to _tags_
     * 
     * @param tags
     * @return LogEntryBuilder
     */
    public LogEntryBuilder setTags(Collection<TagBuilder> tags) {
	this.tags = new ArrayList<TagBuilder>(tags);
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
	this.properties.add(propertyBuilder);
	return this;
    }

    /**
     * Append the logbook described by _logbookBuilder_ to the existing logbooks
     * 
     * @param logbookBuilder
     * @return LogEntryBuilder
     */
    public LogEntryBuilder addLogbook(LogbookBuilder logbookBuilder) {
	this.logbooks.add(logbookBuilder);
	return this;
    }

    /**
     * Set the list of logbooks to _logbooks_
     * 
     * @param logbooks
     * @return
     */
    public LogEntryBuilder setLogbooks(Collection<LogbookBuilder> logbooks) {
	this.logbooks = new ArrayList<LogbookBuilder>(logbooks);
	return this;
    }

    /**
     * Append _attachment_ to the existing attachments
     * 
     * @param attachment
     * @return LogEntryBuilder
     */
    public LogEntryBuilder attach(AttachmentBuilder attachment) {
	this.attachments.add(attachment);
	return this;
    }

    /**
     * Append _attachment_ to the existing attachments
     * 
     * @param attachment
     * @return LogEntryBuilder
     * @throws IOException 
     */
    public LogEntryBuilder removeAttach(String attachmentName) throws IOException {
    	if(attachmentName==null) {
    		return this;
    	}
    	Iterator<AttachmentBuilder> it = this.attachments.iterator();
    	while(it.hasNext()) {
    		AttachmentBuilder attachment = it.next();
			if(attachmentName.equals(attachment.build().getFileName())) {
				it.remove();
			}
    	}
    	return this;
    }

    /**
     * Set the attachments to _attachments_
     * 
     * @param attachments
     * @return LogEntryBuilder
     */
    public LogEntryBuilder setAttachments(
	    Collection<AttachmentBuilder> attachments) {
	this.attachments = attachments;
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

    /**
     * Build LogEntry object using the parameters set in the builder
     * 
     * @return LogEntry - a immutable instance of the {@link LogEntry}
     * @throws IOException 
     */
    public LogEntry build() throws IOException {
	return new LogEntryImpl(id, text, owner, createdDate, modifiedDate,
		tags, logbooks, properties, attachments);
    }

    /**
     * A Default implementation of the {@link LogEntry}
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
	 * @throws IOException 
	 */
	public LogEntryImpl(Object id, String text, String owner,
		Date createdDate, Date modifiedDate,
		Collection<TagBuilder> tags,
		Collection<LogbookBuilder> logbooks,
		Collection<PropertyBuilder> properties,
		Collection<AttachmentBuilder> attachments) throws IOException {
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
		newAttachments.add(attachmentBuilder.build());
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
