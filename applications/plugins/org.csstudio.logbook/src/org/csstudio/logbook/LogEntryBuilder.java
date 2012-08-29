/**
 * 
 */
package org.csstudio.logbook;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
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
	private Collection<Attachment> attachements = new ArrayList<Attachment>();

	private LogEntryBuilder(String text) {
		this.text = text;
	}

	public static LogEntryBuilder withText(String text) {
		return new LogEntryBuilder(text);
	}
	
	public LogEntryBuilder addTag(TagBuilder tagBuilder){
		this.tags.add(tagBuilder);
		return this;
	}
	
	public LogEntryBuilder addProperty(PropertyBuilder propertyBuilder){
		this.properties.add(propertyBuilder);
		return this;
	}
	
	public LogEntryBuilder addLogbook(LogbookBuilder logbookBuilder){
		this.logbooks.add(logbookBuilder);
		return this;
	}
	
	public LogEntryBuilder attach(Attachment attachment){
		this.attachements.add(attachment);
		return this;
	}

	public static LogEntryBuilder logEntry(LogEntry logEntry) {
		LogEntryBuilder logEntryBuilder = new LogEntryBuilder(
				logEntry.getText());
		// logEntryBuilder.id = logEntry.getId();
		logEntryBuilder.owner = logEntry.getOwner();
		logEntryBuilder.createdDate = logEntry.getCreateDate();
		// logEntryBuilder.modifiedDate = logEntry.getModifiedDate();

		for (Tag tag : logEntry.getTags()) {
			logEntryBuilder.tags.add(TagBuilder.tag(tag));
		}
		for (Logbook logbook : logEntry.getLogbooks()) {
			logEntryBuilder.logbooks.add(LogbookBuilder.logbook(logbook));
		}
		for (Property property : logEntry.getProperties()) {
			logEntryBuilder.properties.add(PropertyBuilder.property(property));
		}
		logEntryBuilder.attachements = logEntry.getAttachment();
		return logEntryBuilder;
	}

	public LogEntry build() {
		return new LogEntryImpl(id, text, owner, createdDate, modifiedDate,
				tags, logbooks, properties, attachements);
	}

	/**
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
				Collection<Attachment> attachments) {
			super();
			this.id = id;
			this.text = text;
			this.owner = owner;
			this.createdDate = createdDate;
			this.modifiedDate = modifiedDate;
			
			this.attachments = attachments;
			
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
			return attachements;
		}

	}
}
