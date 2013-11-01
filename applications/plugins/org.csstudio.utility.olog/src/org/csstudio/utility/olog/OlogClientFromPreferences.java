/**
 * 
 */
package org.csstudio.utility.olog;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.core.MultivaluedMap;

import org.csstudio.security.preferences.SecurePreferences;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

import edu.msu.nscl.olog.api.Attachment;
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
 * Client to be registered to the extension point.
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
		ologClientBuilder = OlogClientBuilder.serviceURL(url);
		if (prefs.getBoolean(Activator.PLUGIN_ID,
				PreferenceConstants.Use_authentication, false, null)) {
			ologClientBuilder
					.withHTTPAuthentication(true)
					.username(
							prefs.getString(Activator.PLUGIN_ID,
									PreferenceConstants.Username, "username",
									null))
					.password(
							SecurePreferences.get(
									Activator.PLUGIN_ID,
									PreferenceConstants.Password,
									null));
		}  else {
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

	
	@Override
	public Collection<Logbook> listLogbooks() throws OlogException {
		return client.listLogbooks();
	}

	
	@Override
	public Collection<Tag> listTags() throws OlogException {
		return client.listTags();
	}

	@Override
	public Collection<Level> listLevels() throws OlogException {
		return client.listLevels();
	}

	@Override
	public Collection<Log> listLogs() {
		return client.listLogs();
	}

	@Override
	public Log getLog(Long logId) throws OlogException {
		return client.getLog(logId);
	}

	@Override
	public Log set(LogBuilder log) throws OlogException {
		return client.set(log);
	}

	@Override
	public Collection<Log> set(Collection<LogBuilder> logs)
			throws OlogException {
		return client.set(logs);
	}

	@Override
	public Tag set(TagBuilder tag) throws OlogException {
		return client.set(tag);
	}

	@Override
	public Tag set(TagBuilder tag, Collection<Long> logIds)
			throws OlogException {
		return client.set(tag, logIds);
	}

	@Override
	public Logbook set(LogbookBuilder logbookBuilder) throws OlogException {
		return client.set(logbookBuilder);
	}

	@Override
	public Logbook set(LogbookBuilder logbook, Collection<Long> logIds)
			throws OlogException {
		return client.set(logbook, logIds);
	}

	@Override
	public Log update(LogBuilder log) throws OlogException {
		return client.update(log);
	}

	@Override
	public Collection<Log> update(Collection<LogBuilder> logs)
			throws OlogException {
		return client.update(logs);
	}

	@Override
	public Tag update(TagBuilder tag, Long logId) throws OlogException {
		return client.update(tag, logId);
	}

	@Override
	public Tag update(TagBuilder tag, Collection<Long> logIds)
			throws OlogException {
		return client.update(tag, logIds);
	}

	@Override
	public Logbook update(LogbookBuilder logbook, Long logId)
			throws OlogException {
		return client.update(logbook, logId);
	}

	@Override
	public Logbook update(LogbookBuilder logbook, Collection<Long> logIds)
			throws OlogException {
		return client.update(logbook, logIds);
	}

	@Override
	public Attachment add(File local, Long logId) throws OlogException {
		return client.add(local, logId);

	}

	@Override
	public Log findLogById(Long logId) throws OlogException {
		return client.findLogById(logId);
	}

	@Override
	public Collection<Log> findLogsBySearch(String pattern)
			throws OlogException {
		return client.findLogsBySearch(pattern);
	}

	@Override
	public Collection<Log> findLogsByTag(String pattern) throws OlogException {
		return client.findLogsByTag(pattern);
	}

	@Override
	public Collection<Log> findLogsByLogbook(String logbook)
			throws OlogException {
		return client.findLogsByLogbook(logbook);
	}

	@Override
	public Collection<Log> findLogs(Map<String, String> map)
			throws OlogException {
		return client.findLogs(map);
	}

	@Override
	public void deleteTag(String tag) throws OlogException {
		client.deleteTag(tag);
	}

	@Override
	public void deleteLogbook(String logbook) throws OlogException {
		client.deleteLogbook(logbook);
	}

	@Override
	public void delete(LogBuilder log) throws OlogException {
		client.delete(log);
	}

	@Override
	public void delete(Long logId) throws OlogException {
		client.delete(logId);
	}

	@Override
	public void delete(Collection<Log> logs) throws OlogException {
		client.delete(logs);
	}

	@Override
	public void delete(TagBuilder tag, Long logId) throws OlogException {
		client.delete(tag, logId);
	}

	@Override
	public void delete(TagBuilder tag, Collection<Long> logIds)
			throws OlogException {
		client.delete(tag, logIds);
	}

	@Override
	public void delete(LogbookBuilder logbook, Long logId) throws OlogException {
		client.delete(logbook, logId);
	}

	@Override
	public void delete(LogbookBuilder logbook, Collection<Long> logIds)
			throws OlogException {
		client.delete(logbook, logIds);
	}

	@Override
	public void delete(PropertyBuilder property, Long logId)
			throws OlogException {
		client.delete(property, logId);
	}

	@Override
	public void delete(PropertyBuilder property, Collection<Long> logIds)
			throws OlogException {
		client.delete(property, logIds);
	}

	@Override
	public void delete(String fileName, Long logId) throws OlogException {
		client.delete(fileName, logId);
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

	@Override
	public Collection<Attachment> listAttachments(Long logId)
			throws OlogException {
		return client.listAttachments(logId);
	}

	@Override
	public InputStream getAttachment(Long logId, String attachmentFileName) {
	    return client.getAttachment(logId, attachmentFileName);
	}

}
