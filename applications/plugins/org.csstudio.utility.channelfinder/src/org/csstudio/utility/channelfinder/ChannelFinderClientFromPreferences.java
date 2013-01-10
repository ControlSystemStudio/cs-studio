package org.csstudio.utility.channelfinder;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.Channel.Builder;
import gov.bnl.channelfinder.api.ChannelFinderClient;
import gov.bnl.channelfinder.api.ChannelFinderClientComp;
import gov.bnl.channelfinder.api.ChannelFinderClientImpl.CFCBuilder;
import gov.bnl.channelfinder.api.ChannelFinderException;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.core.MultivaluedMap;

import org.csstudio.auth.security.SecureStorage;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * ChannelFinderClient that takes the configuration from the CSS preferences.
 * 
 * @author carcassi
 *
 */
public class ChannelFinderClientFromPreferences implements ChannelFinderClient {
	
	private static final Logger log = Logger.getLogger(ChannelFinderClientFromPreferences.class.getName());
	private volatile ChannelFinderClient client;
	
	public ChannelFinderClientFromPreferences() {
		reloadConfiguration();
	}
	
	public void reloadConfiguration() {
		final IPreferencesService prefs = Platform.getPreferencesService();
		String url = prefs.getString(Activator.PLUGIN_ID,
				PreferenceConstants.ChannelFinder_URL, "http://localhost/ChannelFinder", null);
		String username = prefs.getString(Activator.PLUGIN_ID,
				PreferenceConstants.Username, null, null);
		String password = SecureStorage.retrieveSecureStorage(
				Activator.PLUGIN_ID, PreferenceConstants.Password);
		log.info("Creating Channelfinder client : "+ username + "@"+url);
		ChannelFinderClientComp compositeClient = ChannelFinderClientComp
				.getInstance();
		compositeClient.setReader(CFCBuilder.serviceURL(url).create());
		compositeClient.setWriter(CFCBuilder.serviceURL(url)
				.withHTTPAuthentication(true).username(username)
				.password(password).create());
		client = compositeClient;
	}

	@Override
	public Collection<String> getAllProperties() {
		return client.getAllProperties();
	}

	@Override
	public Collection<String> getAllTags() {
		return client.getAllTags();
	}

	@Override
	public Channel getChannel(String channelName) throws ChannelFinderException {
		return client.getChannel(channelName);
	}

	@Override
	public void set(Builder channel) throws ChannelFinderException {
		client.set(channel);
	}

	@Override
	public void set(gov.bnl.channelfinder.api.Tag.Builder channel) {
		client.set(channel);
	}

	@Override
	public void set(gov.bnl.channelfinder.api.Property.Builder channel)
			throws ChannelFinderException {
		client.set(channel);
	}

	@Override
	public void set(Collection<Builder> channels) throws ChannelFinderException {
		client.set(channels);
	}

	@Override
	public void set(gov.bnl.channelfinder.api.Tag.Builder tag,
			String channelName) throws ChannelFinderException {
		client.set(tag, channelName);
	}

	@Override
	public void set(gov.bnl.channelfinder.api.Property.Builder tag,
			String channelName) {
		client.set(tag, channelName);
	}

	@Override
	public void set(gov.bnl.channelfinder.api.Tag.Builder tag,
			Collection<String> channelNames) throws ChannelFinderException {
		client.set(tag, channelNames);
	}

	@Override
	public void set(gov.bnl.channelfinder.api.Property.Builder tag,
			Collection<String> channelNames) {
		client.set(tag, channelNames);
	}

	@Override
	public void set(gov.bnl.channelfinder.api.Property.Builder prop,
			Map<String, String> channelPropertyMap) {
		client.set(prop, channelPropertyMap);
	}

	@Override
	public void update(Builder channel) throws ChannelFinderException {
		client.update(channel);
	}

	@Override
	public void update(gov.bnl.channelfinder.api.Tag.Builder tag,
			String channelName) throws ChannelFinderException {
		client.update(tag, channelName);
	}

	@Override
	public void update(gov.bnl.channelfinder.api.Property.Builder tag,
			String channelName) throws ChannelFinderException {
		client.update(tag, channelName);
	}

	@Override
	public void update(gov.bnl.channelfinder.api.Tag.Builder tag,
			Collection<String> channelNames) throws ChannelFinderException {
		client.update(tag, channelNames);
	}

	@Override
	public void update(gov.bnl.channelfinder.api.Property.Builder tag,
			Collection<String> channelNames) throws ChannelFinderException {
		client.update(tag, channelNames);
	}

	@Override
	public void update(gov.bnl.channelfinder.api.Property.Builder property,
			Map<String, String> channelPropValueMap)
			throws ChannelFinderException {
		client.update(property, channelPropValueMap);
	}

	@Override
	public Collection<Channel> findByName(String pattern)
			throws ChannelFinderException {
		return client.findByName(pattern);
	}

	@Override
	public Collection<Channel> findByTag(String pattern)
			throws ChannelFinderException {
		return client.findByTag(pattern);
	}

	@Override
	public Collection<Channel> findByProperty(String property,
			String... pattern) throws ChannelFinderException {
		return client.findByProperty(property, pattern);
	}

	@Override
	public Collection<Channel> find(String query) throws ChannelFinderException {
		return client.find(query);
	}

	@Override
	public Collection<Channel> find(Map<String, String> map)
			throws ChannelFinderException {
		return client.find(map);
	}

	@Override
	public Collection<Channel> find(MultivaluedMap<String, String> map)
			throws ChannelFinderException {
		return client.find(map);
	}

	@Override
	public void deleteTag(String tagName) throws ChannelFinderException {
		client.deleteTag(tagName);
	}

	@Override
	public void deleteProperty(String propertyName)
			throws ChannelFinderException {
		client.deleteProperty(propertyName);
	}

	@Override
	public void deleteChannel(String channelName) throws ChannelFinderException {
		client.deleteChannel(channelName);
	}

	@Override
	public void delete(Collection<Builder> channels)
			throws ChannelFinderException {
		client.delete(channels);
	}

	@Override
	public void delete(gov.bnl.channelfinder.api.Tag.Builder tag,
			String channelName) throws ChannelFinderException {
		client.delete(tag, channelName);
	}

	@Override
	public void delete(gov.bnl.channelfinder.api.Property.Builder tag,
			String channelName) throws ChannelFinderException {
		client.delete(tag, channelName);
	}

	@Override
	public void delete(gov.bnl.channelfinder.api.Tag.Builder tag,
			Collection<String> channelNames) throws ChannelFinderException {
		client.delete(tag, channelNames);
	}

	@Override
	public void delete(gov.bnl.channelfinder.api.Property.Builder tag,
			Collection<String> channelNames) throws ChannelFinderException {
		client.delete(tag, channelNames);
	}

	@Override
	public void close() {
		client.close();
	}

}
