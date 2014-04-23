package org.csstudio.logbook.olog;

import static edu.msu.nscl.olog.api.LogBuilder.log;
import static edu.msu.nscl.olog.api.LogbookBuilder.logbook;
import static edu.msu.nscl.olog.api.PropertyBuilder.property;
import static edu.msu.nscl.olog.api.TagBuilder.tag;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.csstudio.logbook.Attachment;
import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.Logbook;
import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.Property;
import org.csstudio.logbook.Tag;
import org.csstudio.logbook.util.LogEntrySearchUtil;
import org.epics.util.time.TimeInterval;
import org.epics.util.time.TimeParser;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import edu.msu.nscl.olog.api.AttachmentBuilder;
import edu.msu.nscl.olog.api.Log;
import edu.msu.nscl.olog.api.LogBuilder;
import edu.msu.nscl.olog.api.OlogClient;
import edu.msu.nscl.olog.api.PropertyBuilder;

import static org.csstudio.logbook.util.LogEntrySearchUtil.*;

public class OlogLogbookClient implements LogbookClient {

    private final OlogClient reader;
    private final OlogClient writer;
    
    private final List<String> levels = Arrays.asList("Info", "Problem", "Request", "Suggestion", "Urgent");  

    public OlogLogbookClient(OlogClient ologClient) {
	this.reader = ologClient;
	this.writer = ologClient;
    }

    public OlogLogbookClient(OlogClient reader, OlogClient writer) {
	this.reader = reader;
	this.writer = writer;
    }

    @Override
    public Collection<Logbook> listLogbooks() throws Exception {
	return Collections.unmodifiableCollection(Collections2.transform(
		reader.listLogbooks(),
		new Function<edu.msu.nscl.olog.api.Logbook, Logbook>() {

		    @Override
		    public Logbook apply(edu.msu.nscl.olog.api.Logbook input) {
			return new OlogLogbook(input);
		    }
		}));
    }


    @Override
    public List<String> listLevels() throws Exception {
	return levels;
    }
    
    @Override
    public Collection<Tag> listTags() throws Exception {
	return Collections.unmodifiableCollection(Collections2.transform(
		reader.listTags(),
		new Function<edu.msu.nscl.olog.api.Tag, Tag>() {

		    @Override
		    public Tag apply(edu.msu.nscl.olog.api.Tag input) {
			return new OlogTag(input);
		    }
		}));
    }

    @Override
    public Collection<Property> listProperties() throws Exception {
	return Collections.unmodifiableCollection(Collections2.transform(
		reader.listProperties(),
		new Function<edu.msu.nscl.olog.api.Property, Property>() {

		    @Override
		    public Property apply(edu.msu.nscl.olog.api.Property input) {
			return new OlogProperty(input);
		    }
		}));
    }

    @Override
    public Collection<Attachment> listAttachments(final Object logId)
	    throws Exception {
	return Collections.unmodifiableCollection(Collections2.transform(
		reader.listAttachments((Long) logId),
		new Function<edu.msu.nscl.olog.api.Attachment, Attachment>() {

		    @Override
		    public Attachment apply(
			    edu.msu.nscl.olog.api.Attachment input) {
			// TODO (shroffk) n/w call
			try {
			    return new OlogAttachment(input, getAttachment(
				    logId, input.getFileName()));
			} catch (IOException e) {
			}
			return null;
		    }
		}));
    }

    @Override
    public InputStream getAttachment(Object logId, String attachmentFileName) {
	return reader.getAttachment((Long) logId, attachmentFileName);
    }

    @Override
    public LogEntry findLogEntry(Object logId) throws Exception {
	Log log = reader.getLog((Long) logId);
	if (log != null) {
	    LogBuilder logBuilder = LogBuilder.log(log);
	    for (edu.msu.nscl.olog.api.Attachment attachments : reader
		    .listAttachments(log.getId())) {
		logBuilder.attach(AttachmentBuilder.attachment(attachments));
	    }
	    return new OlogEntry(logBuilder.build());
	}
	return new OlogEntry(log);
    }

    @Override
    public Collection<LogEntry> findLogEntries(String search) throws Exception {
	Map<String, String> searchParameters = LogEntrySearchUtil
		.parseSearchString(search);
	// append text search with a leading and trailing *
	if (searchParameters.containsKey(SEARCH_KEYWORD_TEXT)) {
	    String textSearch = "*" + searchParameters.get(SEARCH_KEYWORD_TEXT) + "*";
	    searchParameters.put(SEARCH_KEYWORD_TEXT, textSearch);
	}
	if (searchParameters.containsKey(SEARCH_KEYWORD_START)) {
	    TimeInterval timeInterval;
	    // Check if both start and end are specified.
	    if (searchParameters.containsKey(SEARCH_KEYWORD_END)) {
		timeInterval = TimeParser.getTimeInterval(
			searchParameters.get(SEARCH_KEYWORD_START),
			searchParameters.get(SEARCH_KEYWORD_END));
		searchParameters.remove(SEARCH_KEYWORD_END);
	    } else {
		timeInterval = TimeParser.getTimeInterval(
			searchParameters.get(SEARCH_KEYWORD_START), "now");
	    }
	    searchParameters.remove(SEARCH_KEYWORD_START);
	    if (timeInterval != null && timeInterval.getStart() != null
				&& timeInterval.getEnd() != null) {
	    	searchParameters.put("start", String.valueOf(timeInterval.getStart().getSec()));
	    	searchParameters.put("end", String.valueOf(timeInterval.getEnd().getSec()));
	    }
	}
	Collection<LogEntry> logEntries = new ArrayList<LogEntry>();
	Collection<Log> logs = reader.findLogs(searchParameters);
	for (Log log : logs) {
	    logEntries.add(new OlogEntry(log));
	}
	return logEntries;
    }

    @Override
    public LogEntry createLogEntry(LogEntry logEntry) throws Exception {
	OlogEntry ologEntry = new OlogEntry(writer.set(LogBuilder(logEntry)));
	// creates the log entry and then adds all the attachments
	// TODO (shroffk) multiple network calls, one for each attachment, need
	// to improve
	for (Attachment attachment : logEntry.getAttachment()) {
	    if (attachment.getInputStream() != null) {
		addAttachment(ologEntry.getId(), attachment.getInputStream(),
			attachment.getFileName());
	    }
	}
	return ologEntry;
    }

    @Override
    public LogEntry updateLogEntry(LogEntry logEntry) throws Exception {
	OlogEntry ologEntry = new OlogEntry(writer.update(LogBuilder(logEntry)));
	// creates the log entry and then adds all the attachments
	// TODO (shroffk) multiple network calls, one for each attachment, need
	// to improve
	Collection<String> existingFiles = new ArrayList<String>();
	for (edu.msu.nscl.olog.api.Attachment attachment : reader.getLog((Long) ologEntry.getId()).getAttachments()) {
	    existingFiles.add(attachment.getFileName());
	}
	for (Attachment attachment : logEntry.getAttachment()) {
	    //Check the attachment doe snot already exist.
	    if (!existingFiles.contains(attachment.getFileName()) && attachment.getInputStream() != null) {
		addAttachment(ologEntry.getId(), attachment.getInputStream(),
			attachment.getFileName());
	    }
	}
	return ologEntry;
    }

    @Override
    public Attachment addAttachment(Object logId, InputStream attachment,
	    String name) throws Exception {
	try {
	    File file = new File(name);
	    OutputStream out = new FileOutputStream(file);
	    int read = 0;
	    byte[] bytes = new byte[1024];

	    while ((read = attachment.read(bytes)) != -1) {
		out.write(bytes, 0, read);
	    }
	    attachment.close();
	    out.flush();
	    out.close();
	    edu.msu.nscl.olog.api.Attachment response;
	    if (file != null) {
		response = writer.add(file, (Long) logId);
		file.delete();
		return new OlogAttachment(response, getAttachment(logId,
			response.getFileName()));
	    }
	} catch (IOException e) {
	    throw new Exception(e);
	}
	return null;
    }

    @Override
    public void updateLogEntries(Collection<LogEntry> logEntires)
	    throws Exception {
	Collection<LogBuilder> logbuilders = new ArrayList<LogBuilder>();
	for (LogEntry logEntry : logEntires) {
	    logbuilders.add(LogBuilder(logEntry));
	}
	writer.update(logbuilders);
    }

    /**
     * A utility method to create a edu.msu.nscl.olog.api.LogBuilder from
     * org.csstudio.logbook.LogEntry
     * 
     * @param logEntry
     * @return
     */
    private LogBuilder LogBuilder(LogEntry logEntry) {
	LogBuilder logBuilder = log().description(logEntry.getText()).level(logEntry.getLevel()).id((Long) logEntry.getId());
	for (Tag tag : logEntry.getTags())
	    logBuilder.appendTag(tag(tag.getName(), tag.getState()));
	for (Logbook logbook : logEntry.getLogbooks())
	    logBuilder.appendToLogbook(logbook(logbook.getName()).owner(
		    logbook.getOwner()));
	for (Property property : logEntry.getProperties()) {
	    PropertyBuilder propertyBuilder = property(property.getName());
	    for (Entry<String, String> attribute : property.getAttributes()) {
		propertyBuilder.attribute(attribute.getKey(),
			attribute.getValue());
	    }
	    logBuilder.appendProperty(propertyBuilder);
	}
	return logBuilder;
    }

    public static class OlogProperty implements Property {
	private final edu.msu.nscl.olog.api.Property property;

	public OlogProperty(edu.msu.nscl.olog.api.Property property) {
	    this.property = property;
	}

	@Override
	public String getName() {
	    return property.getName();
	}

	@Override
	public Collection<String> getAttributeNames() {
	    return property.getAttributes();
	}

	@Override
	public Set<Entry<String, String>> getAttributes() {
	    return property.getEntrySet();
	}

	@Override
	public Collection<String> getAttributeValues() {
	    return property.getAttributeValues();
	}

	@Override
	public String getAttributeValue(String attributeName) {
	    return property.getAttributeValue(attributeName);
	}

    }

    private class OlogTag implements Tag {
	private final edu.msu.nscl.olog.api.Tag tag;

	public OlogTag(edu.msu.nscl.olog.api.Tag tag) {
	    this.tag = tag;
	}

	@Override
	public String getName() {
	    return tag.getName();
	}

	@Override
	public String getState() {
	    return tag.getState();
	}

    }

    private class OlogLogbook implements Logbook {

	private final edu.msu.nscl.olog.api.Logbook logbook;

	public OlogLogbook(edu.msu.nscl.olog.api.Logbook logbook) {
	    this.logbook = logbook;
	}

	@Override
	public String getName() {
	    return logbook.getName();
	}

	@Override
	public String getOwner() {
	    return logbook.getOwner();
	}

    }

    private class OlogAttachment implements Attachment {

	private final edu.msu.nscl.olog.api.Attachment attachment;
	private byte[] byteArray = new byte[] {};

	public OlogAttachment(edu.msu.nscl.olog.api.Attachment attachment,
		InputStream inputStream) throws IOException {
	    this.attachment = attachment;
	    byte[] buffer = new byte[8192];
	    int bytesRead;
	    ByteArrayOutputStream output = new ByteArrayOutputStream();
	    while ((bytesRead = inputStream.read(buffer)) != -1) {
		output.write(buffer, 0, bytesRead);
	    }
	    byteArray = output.toByteArray();
	    inputStream.close();
	    output.close();
	}

	public OlogAttachment(edu.msu.nscl.olog.api.Attachment attachment) {
	    this.attachment = attachment;
	}

	@Override
	public String getFileName() {
	    return this.attachment.getFileName();
	}

	@Override
	public String getContentType() {
	    return this.attachment.getContentType();
	}

	@Override
	public Boolean getThumbnail() {
	    return this.attachment.getThumbnail();
	}

	@Override
	public Long getFileSize() {
	    return this.attachment.getFileSize();
	}

	@Override
	public InputStream getInputStream() {
	    return new ByteArrayInputStream(byteArray);
	}

    }

    private class OlogEntry implements LogEntry {

	private final edu.msu.nscl.olog.api.Log log;
	// private final Collection<String> attachmentURIs;
	private final Collection<Logbook> logbooks;
	private final Collection<Tag> tags;
	private final Collection<Property> properties;
	private final Collection<Attachment> attachments;

	public OlogEntry(edu.msu.nscl.olog.api.Log log) {
	    this.log = log;
	    this.logbooks = Collections2.transform(log.getLogbooks(),
		    new Function<edu.msu.nscl.olog.api.Logbook, Logbook>() {

			@Override
			public Logbook apply(edu.msu.nscl.olog.api.Logbook input) {
			    return new OlogLogbook(input);
			}

		    });
	    this.tags = Collections2.transform(log.getTags(),
		    new Function<edu.msu.nscl.olog.api.Tag, Tag>() {

			@Override
			public Tag apply(edu.msu.nscl.olog.api.Tag input) {
			    return new OlogTag(input);
			}
		    });
	    this.properties = Collections2.transform(log.getProperties(),
		    new Function<edu.msu.nscl.olog.api.Property, Property>() {

			@Override
			public Property apply(
				edu.msu.nscl.olog.api.Property input) {
			    return new OlogProperty(input);
			}
		    });

	    this.attachments = Collections2
		    .transform(
			    log.getAttachments(),
			    new Function<edu.msu.nscl.olog.api.Attachment, Attachment>() {

				@Override
				public Attachment apply(
					edu.msu.nscl.olog.api.Attachment input) {
				    return new OlogAttachment(input);
				}
			    });
	}


	@Override
	public String getLevel() {
	    return log.getLevel();
	}
	
	@Override
	public String getText() {
	    return log.getDescription();
	}

	@Override
	public String getOwner() {
	    return log.getOwner();
	}

	@Override
	public Date getCreateDate() {
	    return log.getCreatedDate();
	}

	@Override
	public Object getId() {
	    return log.getId();
	}

	@Override
	public Date getModifiedDate() {
	    return log.getModifiedDate();
	}

	@Override
	public Collection<Attachment> getAttachment() {
	    return this.attachments;
	}

	@Override
	public Collection<Tag> getTags() {
	    return this.tags;
	}

	@Override
	public Collection<Logbook> getLogbooks() {
	    return this.logbooks;
	}

	@Override
	public Collection<Property> getProperties() {
	    return this.properties;
	}

    }
    
}
