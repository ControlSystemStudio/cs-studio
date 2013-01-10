/**
 * 
 */
package org.csstudio.utility.olog;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.jackrabbit.webdav.DavException;
import org.csstudio.auth.security.SecureStorage;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

import edu.msu.nscl.olog.api.Level;
import edu.msu.nscl.olog.api.Log;
import edu.msu.nscl.olog.api.LogBuilder;
import edu.msu.nscl.olog.api.Logbook;
import edu.msu.nscl.olog.api.LogbookBuilder;
import edu.msu.nscl.olog.api.OlogClient;
import edu.msu.nscl.olog.api.OlogClientImpl.OlogClientBuilder;
import edu.msu.nscl.olog.api.OlogException;
import edu.msu.nscl.olog.api.Property;
import edu.msu.nscl.olog.api.PropertyBuilder;
import edu.msu.nscl.olog.api.Tag;
import edu.msu.nscl.olog.api.TagBuilder;

/**
 * Client to be regestered to the extension point.
 * 
 * @author shroffk
 * 
 */
@SuppressWarnings("deprecation")
public class OlogClientFromPreferences implements OlogClient {

	private static Logger log = Logger
			.getLogger(OlogClientFromPreferences.class.getName());
	private volatile OlogClient client;

	/**
	 * 
	 */
	public OlogClientFromPreferences() {
		final IPreferencesService prefs = Platform.getPreferencesService();
		OlogClientBuilder ologClientBuilder;
		String url = prefs.getString(Activator.PLUGIN_ID,
				PreferenceConstants.Olog_URL,
				"https://localhost:8181/Olog/resources", null);
		String jcr_url = prefs.getString(Activator.PLUGIN_ID,
				PreferenceConstants.Olog_jcr_URL,
				"http://localhost:8080/Olog/repository/olog", null);
		ologClientBuilder = OlogClientBuilder.serviceURL(url).jcrURI(jcr_url);
		if (prefs.getBoolean(Activator.PLUGIN_ID,
				PreferenceConstants.Use_authentication, false, null)) {
			ologClientBuilder
					.withHTTPAuthentication(true)
					.username(
							prefs.getString(Activator.PLUGIN_ID,
									PreferenceConstants.Username, "username",
									null))
					.password(
							SecureStorage.retrieveSecureStorage(
									Activator.PLUGIN_ID,
									PreferenceConstants.Password));
		} else {
			ologClientBuilder.withHTTPAuthentication(false);
		}
		log.info("Creating Olog client : " + url);
		try {
			// OlogClientManager.registerDefaultClient(ologClientBuilder.create());
			this.client = ologClientBuilder.create();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.msu.nscl.olog.api.OlogClient#listLogbooks()
	 */
	@Override
	public Collection<Logbook> listLogbooks() throws OlogException {
		return client.listLogbooks();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.msu.nscl.olog.api.OlogClient#listTags()
	 */
	@Override
	public Collection<Tag> listTags() throws OlogException {
		return client.listTags();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.msu.nscl.olog.api.OlogClient#listLevels()
	 */
	@Override
	public Collection<Level> listLevels() throws OlogException {
		return client.listLevels();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.msu.nscl.olog.api.OlogClient#listLogs()
	 */
	@Override
	public Collection<Log> listLogs() {
		return client.listLogs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.msu.nscl.olog.api.OlogClient#getLog(java.lang.Long)
	 */
	@Override
	public Log getLog(Long logId) throws OlogException {
		return client.getLog(logId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.msu.nscl.olog.api.OlogClient#set(edu.msu.nscl.olog.api.LogBuilder)
	 */
	@Override
	public Log set(LogBuilder log) throws OlogException {
		return client.set(log);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.msu.nscl.olog.api.OlogClient#set(java.util.Collection)
	 */
	@Override
	public Collection<Log> set(Collection<LogBuilder> logs)
			throws OlogException {
		return client.set(logs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.msu.nscl.olog.api.OlogClient#set(edu.msu.nscl.olog.api.TagBuilder)
	 */
	@Override
	public Tag set(TagBuilder tag) throws OlogException {
		return client.set(tag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.msu.nscl.olog.api.OlogClient#set(edu.msu.nscl.olog.api.TagBuilder,
	 * java.util.Collection)
	 */
	@Override
	public Tag set(TagBuilder tag, Collection<Long> logIds)
			throws OlogException {
		return client.set(tag, logIds);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.msu.nscl.olog.api.OlogClient#set(edu.msu.nscl.olog.api.LogbookBuilder
	 * )
	 */
	@Override
	public Logbook set(LogbookBuilder logbookBuilder) throws OlogException {
		return client.set(logbookBuilder);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.msu.nscl.olog.api.OlogClient#set(edu.msu.nscl.olog.api.LogbookBuilder
	 * , java.util.Collection)
	 */
	@Override
	public Logbook set(LogbookBuilder logbook, Collection<Long> logIds)
			throws OlogException {
		return client.set(logbook, logIds);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.msu.nscl.olog.api.OlogClient#update(edu.msu.nscl.olog.api.LogBuilder)
	 */
	@Override
	public Log update(LogBuilder log) throws OlogException {
		return client.update(log);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.msu.nscl.olog.api.OlogClient#update(java.util.Collection)
	 */
	@Override
	public Collection<Log> update(Collection<LogBuilder> logs)
			throws OlogException {
		return client.update(logs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.msu.nscl.olog.api.OlogClient#update(edu.msu.nscl.olog.api.TagBuilder,
	 * java.lang.Long)
	 */
	@Override
	public Tag update(TagBuilder tag, Long logId) throws OlogException {
		return client.update(tag, logId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.msu.nscl.olog.api.OlogClient#update(edu.msu.nscl.olog.api.TagBuilder,
	 * java.util.Collection)
	 */
	@Override
	public Tag update(TagBuilder tag, Collection<Long> logIds)
			throws OlogException {
		return client.update(tag, logIds);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.msu.nscl.olog.api.OlogClient#update(edu.msu.nscl.olog.api.LogbookBuilder
	 * , java.lang.Long)
	 */
	@Override
	public Logbook update(LogbookBuilder logbook, Long logId)
			throws OlogException {
		return client.update(logbook, logId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.msu.nscl.olog.api.OlogClient#update(edu.msu.nscl.olog.api.LogbookBuilder
	 * , java.util.Collection)
	 */
	@Override
	public Logbook update(LogbookBuilder logbook, Collection<Long> logIds)
			throws OlogException {
		return client.update(logbook, logIds);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.msu.nscl.olog.api.OlogClient#add(java.io.File, java.lang.Long)
	 */
	@Override
	public void add(File local, Long logId) throws OlogException {
		client.add(local, logId);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.msu.nscl.olog.api.OlogClient#findLogById(java.lang.Long)
	 */
	@Override
	public Log findLogById(Long logId) throws OlogException {
		return client.findLogById(logId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.msu.nscl.olog.api.OlogClient#findLogsBySearch(java.lang.String)
	 */
	@Override
	public Collection<Log> findLogsBySearch(String pattern)
			throws OlogException {
		return client.findLogsBySearch(pattern);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.msu.nscl.olog.api.OlogClient#findLogsByTag(java.lang.String)
	 */
	@Override
	public Collection<Log> findLogsByTag(String pattern) throws OlogException {
		return client.findLogsByTag(pattern);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.msu.nscl.olog.api.OlogClient#findLogsByLogbook(java.lang.String)
	 */
	@Override
	public Collection<Log> findLogsByLogbook(String logbook)
			throws OlogException {
		return client.findLogsByLogbook(logbook);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.msu.nscl.olog.api.OlogClient#findLogs(java.util.Map)
	 */
	@Override
	public Collection<Log> findLogs(Map<String, String> map)
			throws OlogException {
		return client.findLogs(map);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.msu.nscl.olog.api.OlogClient#deleteTag(java.lang.String)
	 */
	@Override
	public void deleteTag(String tag) throws OlogException {
		client.deleteTag(tag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.msu.nscl.olog.api.OlogClient#deleteLogbook(java.lang.String)
	 */
	@Override
	public void deleteLogbook(String logbook) throws OlogException {
		client.deleteLogbook(logbook);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.msu.nscl.olog.api.OlogClient#delete(edu.msu.nscl.olog.api.LogBuilder)
	 */
	@Override
	public void delete(LogBuilder log) throws OlogException {
		client.delete(log);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.msu.nscl.olog.api.OlogClient#delete(java.lang.Long)
	 */
	@Override
	public void delete(Long logId) throws OlogException {
		client.delete(logId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.msu.nscl.olog.api.OlogClient#delete(java.util.Collection)
	 */
	@Override
	public void delete(Collection<Log> logs) throws OlogException {
		client.delete(logs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.msu.nscl.olog.api.OlogClient#delete(edu.msu.nscl.olog.api.TagBuilder,
	 * java.lang.Long)
	 */
	@Override
	public void delete(TagBuilder tag, Long logId) throws OlogException {
		client.delete(tag, logId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.msu.nscl.olog.api.OlogClient#delete(edu.msu.nscl.olog.api.TagBuilder,
	 * java.util.Collection)
	 */
	@Override
	public void delete(TagBuilder tag, Collection<Long> logIds)
			throws OlogException {
		client.delete(tag, logIds);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.msu.nscl.olog.api.OlogClient#delete(edu.msu.nscl.olog.api.LogbookBuilder
	 * , java.lang.Long)
	 */
	@Override
	public void delete(LogbookBuilder logbook, Long logId) throws OlogException {
		client.delete(logbook, logId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.msu.nscl.olog.api.OlogClient#delete(edu.msu.nscl.olog.api.LogbookBuilder
	 * , java.util.Collection)
	 */
	@Override
	public void delete(LogbookBuilder logbook, Collection<Long> logIds)
			throws OlogException {
		client.delete(logbook, logIds);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.msu.nscl.olog.api.OlogClient#delete(edu.msu.nscl.olog.api.PropertyBuilder
	 * , java.lang.Long)
	 */
	@Override
	public void delete(PropertyBuilder property, Long logId)
			throws OlogException {
		client.delete(property, logId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.msu.nscl.olog.api.OlogClient#delete(edu.msu.nscl.olog.api.PropertyBuilder
	 * , java.util.Collection)
	 */
	@Override
	public void delete(PropertyBuilder property, Collection<Long> logIds)
			throws OlogException {
		client.delete(property, logIds);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.msu.nscl.olog.api.OlogClient#delete(java.lang.String,
	 * java.lang.Long)
	 */
	@Override
	public void delete(String fileName, Long logId) throws OlogException {
		client.delete(fileName, logId);
	}

	@Override
	public Collection<String> getAttachments(Long logId) throws OlogException,
			DavException {
		return client.getAttachments(logId);
	}

	@Override
	public Collection<Log> findLogs(MultivaluedMap<String, String> map)
			throws OlogException {
		return client.findLogs(map);
	}

	@Override
	public Collection<Property> listProperties() throws OlogException {
		return client.listProperties();
	}

	@Override
	public Collection<String> listAttributes(String propertyName)
			throws OlogException {
		return client.listAttributes(propertyName);
	}

	@Override
	public Property getProperty(String property) throws OlogException {
		return client.getProperty(property);
	}

	@Override
	public Property set(PropertyBuilder property) throws OlogException {
		return client.set(property);
	}

	@Override
	public Property update(PropertyBuilder property) {
		return client.update(property);
	}

	@Override
	public Collection<Log> findLogsByProperty(String propertyName,
			String attributeName, String attributeValue) throws OlogException {
		return client.findLogsByProperty(propertyName, attributeName,
				attributeValue);
	}

	@Override
	public Collection<Log> findLogsByProperty(String propertyName)
			throws OlogException {
		return client.findLogsByProperty(propertyName);
	}

	@Override
	public void deleteProperty(String property) throws OlogException {
		client.deleteProperty(property);
	}

	@Override
	public Log update(PropertyBuilder property, Long logId)
			throws OlogException {
		return client.update(property, logId);
	}

}
