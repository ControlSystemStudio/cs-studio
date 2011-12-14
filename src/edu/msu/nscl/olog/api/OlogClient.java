package edu.msu.nscl.olog.api;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.jackrabbit.webdav.DavException;

/**
 * 
 * TODO: replace the usage of Xml* types with log,tag,logbooks
 * 
 * @author Eric Berryman taken from shroffk
 * 
 */
public interface OlogClient {

	// Logbook operations

	/**
	 * Get a list of all the logbooks currently existings
	 * 
	 * @return string collection of logbooks
	 * @throws OlogException
	 */
	public Collection<Logbook> listLogbooks() throws OlogException;

	/**
	 * Get a list of all the tags currently existing
	 * 
	 * @return string collection of tags
	 * @throws OlogException
	 */
	public Collection<Tag> listTags() throws OlogException;

	/**
	 * Get a list of all the levels currently existing
	 * 
	 * @return string collection of levels
	 * @throws OlogException
	 */
	public Collection<Level> listLevels() throws OlogException;

	/**
	 * Return all the logs. ***Warning can return a lot of data***
	 * 
	 * @return Collection of all log entires
	 * @throws OlogException
	 */
	public Collection<Log> listLogs();

	/**
	 * Returns a log that exactly matches the logId <tt>logId</tt>
	 * 
	 * @param logId
	 *            log id
	 * @return Log object
	 * @throws OlogException
	 */
	public Log getLog(Long logId) throws OlogException;

	/**
	 * Returns a collection of attachments that matches the logId <tt>logId</tt>
	 * 
	 * @param logId
	 *            log id
	 * @return attachments collection object
	 * @throws OlogException
	 */
	public Collection<String> getAttachments(Long logId) throws OlogException,
			DavException;

	/**
	 * Set a single log <tt>log</tt>, if the log already exists it is replaced.
	 * Destructive operation
	 * 
	 * TODO: (shroffk) should there be anything returned?
	 * XXX: creating logs with same subject allowed?
	 * @param log
	 *            the log to be added
	 * @throws OlogException
	 */
	public Log set(LogBuilder log) throws OlogException;

	/**
	 * Set a set of logs Destructive operation.
	 * 
	 * TODO: (shroffk) should anything be returned? and should be returned from the service?
	 * 
	 * @param logs
	 *            collection of logs to be added
	 * @throws OlogException
	 */
	public Collection<Log> set(Collection<LogBuilder> logs)
			throws OlogException;

	/**
	 * Set a Tag <tt>tag</tt> with no associated logs to the database.
	 * 
	 * @param tag
	 * @throws OlogException
	 */
	public Tag set(TagBuilder tag) throws OlogException;

	// /**
	// * Set a Collection of tags <tt>tags</tt> with no associated logs.
	// *
	// * @param tags
	// * @throws OlogException
	// */
	// public void set(Collection<TagBuilder> tags) throws OlogException;

	/**
	 * Add tag <tt>tag</tt> to log <tt>logId</tt> and remove the tag from all
	 * other logs
	 * 
	 * @param tag
	 * @param logId
	 * @throws OlogException
	 */
	public Tag set(TagBuilder tag, Long logId) throws OlogException;

	/**
	 * Set tag <tt>tag</tt> on the set of logs <tt>logIds</tt> and remove it
	 * from all others
	 * 
	 * @param tag
	 * @param logIds
	 * @throws OlogException
	 */
	public Tag set(TagBuilder tag, Collection<Long> logIds)
			throws OlogException;

	/**
	 * Set a new logbook <tt>logbook</tt> with no associated logs.
	 * 
	 * @param logbookBuilder
	 * @throws OlogException
	 */
	public Logbook set(LogbookBuilder logbookBuilder) throws OlogException;

	/**
	 * Set Logbook <tt>logbook</tt> to the log <tt>logId</tt> and remove it from
	 * all other logs
	 * 
	 * @param logbook
	 *            logbook builder
	 * @param logId
	 *            log id
	 * @throws OlogException
	 */
	public Logbook set(LogbookBuilder logbook, Long logId) throws OlogException;

	/**
	 * Set Logbook <tt>logbook</tt> to the logs <tt>logIds</tt> and remove it
	 * from all other logs
	 * 
	 * @param logbook
	 *            logbook builder
	 * @param logIds
	 *            log ids
	 * @throws OlogException
	 */
	public Logbook set(LogbookBuilder logbook, Collection<Long> logIds)
			throws OlogException;

	/**
	 * Set the logbook <tt>logbook</tt> to the logs identified by
	 * <tt>logIds</tt> and remove it from all other logs
	 * 
	 * @param logIds
	 * @param logbook
	 * 
	 *            public void set(LogbookBuilder logbook, Collection<Long>
	 *            logIds);
	 * 
	 *            /** Update a single log <tt>log</tt>
	 * 
	 * @param log
	 *            the log to be added
	 * @throws OlogException
	 */
	public Log update(LogBuilder log) throws OlogException;

	/**
	 * Update a set of logs
	 * 
	 * @param logs
	 *            set of logs to be added
	 * @throws OlogException
	 */
	public Collection<Log> update(Collection<LogBuilder> logs)
			throws OlogException;

	/**
	 * Update Tag <tt>tag </tt> by adding it to Log with name <tt>logName</tt>
	 * 
	 * @param tag
	 *            tag builder
	 * @param logId
	 *            log id the tag to be added
	 * @throws OlogException
	 */
	public Tag update(TagBuilder tag, Long logId) throws OlogException;

	/**
	 * Update the Tag <tt>tag</tt> by adding it to the set of the logs with ids
	 * <tt>logIds</tt>
	 * 
	 * @param tag
	 *            tag builder
	 * @param logIds
	 *            collection of log ids
	 * @throws OlogException
	 */
	public Tag update(TagBuilder tag, Collection<Long> logIds)
			throws OlogException;

	/**
	 * Add Logbook <tt>logbook</tt> to the log <tt>logId</tt>
	 * 
	 * @param logbook
	 *            logbook builder
	 * @param logId
	 *            log id
	 * @throws OlogException
	 */
	public Logbook update(LogbookBuilder logbook, Long logId) throws OlogException;

	/**
	 * @param logIds
	 * @param logbook
	 * @throws OlogException
	 */
	public Logbook update(LogbookBuilder logbook, Collection<Long> logIds)
			throws OlogException;

	/**
	 * Update Property <tt>property</tt> by adding it to Log with id
	 * <tt>logId</tt>
	 * 
	 * @param property
	 *            property builder
	 * @param logId
	 *            log id the property to be added
	 * @throws OlogException
	 */
	public Property update(PropertyBuilder property, Long logId)
			throws OlogException;

	/**
	 * Update the Property <tt>property</tt> by adding it to the set of the logs
	 * with ids <tt>logIds</tt>
	 * 
	 * @param property
	 *            property builder
	 * @param logIds
	 *            collection of log ids
	 * @throws OlogException
	 */
	public Property update(PropertyBuilder property, Collection<Long> logIds)
			throws OlogException;

	/**
	 * @param logId
	 * @param local
	 * @throws OlogException
	 */
	public void add(File local, Long logId) throws OlogException;

	/**
	 * 
	 * @param pattern
	 * @return collection of Log objects
	 * @throws OlogException
	 */
	public Collection<Log> findLogsBySearch(String pattern)
			throws OlogException;

	/**
	 * 
	 * @param pattern
	 * @return collection of Log objects
	 * @throws OlogException
	 */
	public Collection<Log> findLogsByTag(String pattern) throws OlogException;

	/**
	 * This function is a subset of queryLogs - should it be removed??
	 * <p>
	 * TODO: add the usage of patterns and implement on top of the general query
	 * using the map
	 * 
	 * @param logbook
	 *            logbook name
	 * @return collection of Log objects
	 * @throws OlogException
	 */
	public Collection<Log> findLogsByLogbook(String logbook)
			throws OlogException;

	/**
	 * Query for logs based on the criteria specified in the map
	 * 
	 * @param map
	 * @return collection of Log objects
	 * @throws OlogException
	 */
	public Collection<Log> findLogs(Map<String, String> map)
			throws OlogException;

	/**
	 * Multivalued map used to search for a key with multiple values. e.g.
	 * logbook a=1 or logbook a=2
	 * 
	 * @param map
	 *            Multivalue map for searching a key with multiple values
	 * @return collection of Log objects
	 * @throws OlogException
	 */
	public Collection<Log> findLogs(MultivaluedMap<String, String> map)
			throws OlogException;

	/**
	 * Remove {tag} from all logs
	 * 
	 * @param tag
	 * @throws OlogException
	 */
	public void deleteTag(String tag) throws OlogException;

	/**
	 * 
	 * @param logbook
	 * @throws LogFinderException
	 */
	public void deleteLogbook(String logbook) throws OlogException;

	/**
	 * Remove the log identified by <tt>log</tt>
	 * 
	 * @param log
	 *            log to be removed
	 * @throws OlogException
	 */
	public void delete(LogBuilder log) throws OlogException;

	/**
	 * Remove the log identified by <tt>log</tt>
	 * 
	 * @param logId
	 *            log id log id to be removed
	 * @throws OlogException
	 */
	public void delete(Long logId) throws OlogException;

	/**
	 * Remove the log collection identified by <tt>log</tt>
	 * 
	 * @param logs
	 *            logs to be removed
	 * @throws OlogException
	 */
	public void delete(Collection<Log> logs) throws OlogException;

	/**
	 * Remove tag <tt>tag</tt> from the log with the id <tt>logId</tt>
	 * 
	 * @param tag
	 * @param logId
	 */
	public void delete(TagBuilder tag, Long logId) throws OlogException;

	/**
	 * Remove the tag <tt>tag </tt> from all the logs <tt>logNames</tt>
	 * 
	 * @param tag
	 * @param logIds
	 * @throws OlogException
	 */
	public void delete(TagBuilder tag, Collection<Long> logIds)
			throws OlogException;

	/**
	 * Remove logbook <tt>logbook</tt> from the log with name <tt>logName</tt>
	 * 
	 * @param logbook
	 *            logbook builder
	 * @param logId
	 *            log id
	 * @throws OlogException
	 */
	public void delete(LogbookBuilder logbook, Long logId) throws OlogException;

	/**
	 * Remove the logbook <tt>logbook</tt> from the set of logs <tt>logIds</tt>
	 * 
	 * @param logbook
	 * @param logIds
	 * @throws OlogException
	 */
	public void delete(LogbookBuilder logbook, Collection<Long> logIds)
			throws OlogException;

	/**
	 * Remove property <tt>property</tt> from the log with id <tt>logId</tt>
	 * TODO: Should this be it's own service?
	 * 
	 * @param property
	 *            property builder
	 * @param logId
	 *            log id
	 * @throws OlogException
	 */
	public void delete(PropertyBuilder property, Long logId)
			throws OlogException;

	/**
	 * Remove the property <tt>property</tt> from the set of logs
	 * <tt>logIds</tt>
	 * 
	 * @param property
	 * @param logIds
	 * @throws OlogException
	 */
	public void delete(PropertyBuilder property, Collection<Long> logIds)
			throws OlogException;

	/**
	 * Remove file attachment from log <tt>logId<tt>
	 * 
	 * TODO: sardine delete hangs up, using jersey for delete
	 * 
	 * @param String
	 *            fileName
	 * @param Long
	 *            logId
	 * @throws OlogException
	 */
	public void delete(String fileName, Long logId) throws OlogException;

}
