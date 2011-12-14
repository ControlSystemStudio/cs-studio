/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.msu.nscl.olog.api;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author berryman
 */
public class LogBuilder {
	// required
	private String subject;
	// optional
	private Long id;
	private String description;
	private String level;
	private Date createdDate;
	private Date modifiedDate;
	private int version;
	private Set<TagBuilder> tags = new HashSet<TagBuilder>();
	private Set<LogbookBuilder> logbooks = new HashSet<LogbookBuilder>();
	private Set<AttachmentBuilder> attachments = new HashSet<AttachmentBuilder>();
	private Set<PropertyBuilder> properties = new HashSet<PropertyBuilder>();

	public static LogBuilder log(Log log) {
		LogBuilder logBuilder = new LogBuilder();
		logBuilder.id = log.getId();
		logBuilder.subject = log.getSubject();
		logBuilder.description = log.getDescription();
		logBuilder.level = log.getLevel();
		logBuilder.createdDate = log.getCreatedDate();
		logBuilder.modifiedDate = log.getModifiedDate();
		logBuilder.version = log.getVersion();
		for (Tag tag : log.getTags()) {
			logBuilder.tags.add(TagBuilder.tag(tag));
		}
		for (Logbook logbook : log.getLogbooks()) {
			logBuilder.logbooks.add(LogbookBuilder.logbook(logbook));
		}
		for (Attachment attachment : log.getAttachments()) {
			logBuilder.attachments
					.add(AttachmentBuilder.attachment(attachment));
		}
		for (Property property : log.getProperties()) {
			logBuilder.properties.add(PropertyBuilder.property(property));
		}
		return logBuilder;
	}

	// if the subject is the only required field this constructor is wrong
	//
	// public static LogBuilder log(Long id) {
	// LogBuilder logBuilder = new LogBuilder();
	// logBuilder.id = id;
	// return logBuilder;
	// }

	public static LogBuilder log(String subject) {
		LogBuilder logBuilder = new LogBuilder();
		logBuilder.subject = subject;
		return logBuilder;
	}

	public LogBuilder id(Long id) {
		this.id = id;
		return this;
	}

	public LogBuilder description(String description) {
		this.description = description;
		return this;
	}

	public LogBuilder level(String level) {
		this.level = level;
		return this;
	}

	public LogBuilder with(TagBuilder tag) {
		tags.add(tag);
		return this;
	}

	public LogBuilder in(LogbookBuilder logbook) {
		logbooks.add(logbook);
		return this;
	}

	// @deprecated not really deprecated, but javadoc doesn't have unsupported
	@Deprecated
	public LogBuilder attach(AttachmentBuilder attachment) {
		attachments.add(attachment);
		return this;
	}

	public LogBuilder property(PropertyBuilder property) {
		properties.add(property);
		return this;
	}

	XmlLog toXml() {
		XmlLog xmlLog = new XmlLog(subject);
		xmlLog.setId(id);
		xmlLog.setSubject(subject);
		xmlLog.setDescription(description);
		xmlLog.setLevel(level);
		xmlLog.setCreatedDate(createdDate);
		xmlLog.setModifiedDate(modifiedDate);
		xmlLog.setVersion(version);
		for (TagBuilder tag : tags) {
			xmlLog.addXmlTag(tag.toXml());
		}
		for (LogbookBuilder logbook : logbooks) {
			xmlLog.addXmlLogbook(logbook.toXml());
		}
		for (AttachmentBuilder attachment : attachments) {
			xmlLog.addXmlAttachment(attachment.toXml());
		}
		for (PropertyBuilder property : properties) {
			xmlLog.addXmlProperty(property.toXml());
		}
		return xmlLog;

	}

	Log build() {
		return new Log(this.toXml());
	}

}
