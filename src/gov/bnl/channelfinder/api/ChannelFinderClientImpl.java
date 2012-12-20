/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package gov.bnl.channelfinder.api;

import gov.bnl.channelfinder.api.Channel.Builder;

import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import com.google.common.base.Joiner;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * A Client object to query the channelfinder service for channels based on
 * channel names and/or properties and tags associated with channels.
 * 
 * @author shroffk
 * 
 */
public class ChannelFinderClientImpl implements ChannelFinderClient {
	private final WebResource service;
	private final ExecutorService executor;

	private static final String resourceChannels = "resources/channels";
	private static final String resourceProperties = "resources/properties";
	private static final String resourceTags = "resources/tags";

	/**
	 * A Builder class to help create the client to the Channelfinder Service
	 * 
	 * @author shroffk
	 * 
	 */
	public static class CFCBuilder {

		// required
		private URI uri = null;

		// optional
		private boolean withHTTPAuthentication = false;
		private HTTPBasicAuthFilter httpBasicAuthFilter = null;

		private ClientConfig clientConfig = null;
		private TrustManager[] trustManager = new TrustManager[] { new DummyX509TrustManager() };;
		@SuppressWarnings("unused")
		private SSLContext sslContext = null;

		private String protocol = null;
		private String username = null;
		private String password = null;

		private ExecutorService executor = Executors.newSingleThreadExecutor();

		private CFProperties properties = new CFProperties();

		private static final String serviceURL = "http://localhost:8080/ChannelFinder"; //$NON-NLS-1$

		private CFCBuilder() {
			this.uri = URI.create(this.properties.getPreferenceValue(
					"channelfinder.serviceURL", serviceURL)); //$NON-NLS-1$
			this.protocol = this.uri.getScheme();
		}

		private CFCBuilder(URI uri) {
			this.uri = uri;
			this.protocol = this.uri.getScheme();
		}

		/**
		 * Creates a {@link CFCBuilder} for a CF client to Default URL in the
		 * channelfinder.properties.
		 * 
		 * @return
		 */
		public static CFCBuilder serviceURL() {
			return new CFCBuilder();
		}

		/**
		 * Creates a {@link CFCBuilder} for a CF client to URI <tt>uri</tt>.
		 * 
		 * @param uri
		 * @return {@link CFCBuilder}
		 */
		public static CFCBuilder serviceURL(String uri) {
			return new CFCBuilder(URI.create(uri));
		}

		/**
		 * Creates a {@link CFCBuilder} for a CF client to {@link URI}
		 * <tt>uri</tt>.
		 * 
		 * @param uri
		 * @return {@link CFCBuilder}
		 */
		public static CFCBuilder serviceURL(URI uri) {
			return new CFCBuilder(uri);
		}

		/**
		 * Enable of Disable the HTTP authentication on the client connection.
		 * 
		 * @param withHTTPAuthentication
		 * @return {@link CFCBuilder}
		 */
		public CFCBuilder withHTTPAuthentication(boolean withHTTPAuthentication) {
			this.withHTTPAuthentication = withHTTPAuthentication;
			return this;
		}

		/**
		 * Set the username to be used for HTTP Authentication.
		 * 
		 * @param username
		 * @return {@link CFCBuilder}
		 */
		public CFCBuilder username(String username) {
			this.username = username;
			return this;
		}

		/**
		 * Set the password to be used for the HTTP Authentication.
		 * 
		 * @param password
		 * @return {@link CFCBuilder}
		 */
		public CFCBuilder password(String password) {
			this.password = password;
			return this;
		}

		/**
		 * set the {@link ClientConfig} to be used while creating the
		 * channelfinder client connection.
		 * 
		 * @param clientConfig
		 * @return {@link CFCBuilder}
		 */
		public CFCBuilder withClientConfig(ClientConfig clientConfig) {
			this.clientConfig = clientConfig;
			return this;
		}

		@SuppressWarnings("unused")
		private CFCBuilder withSSLContext(SSLContext sslContext) {
			this.sslContext = sslContext;
			return this;
		}

		/**
		 * Set the trustManager that should be used for authentication.
		 * 
		 * @param trustManager
		 * @return {@link CFCBuilder}
		 */
		public CFCBuilder withTrustManager(TrustManager[] trustManager) {
			this.trustManager = trustManager;
			return this;
		}

		/**
		 * Provide your own executor on which the queries are to be made. <br>
		 * By default a single threaded executor is used.
		 * 
		 * @param executor
		 * @return {@link CFCBuilder}
		 */
		public CFCBuilder withExecutor(ExecutorService executor) {
			this.executor = executor;
			return this;
		}

		/**
		 * Will actually create a {@link ChannelFinderClientImpl} object using
		 * the configuration informoation in this builder.
		 * 
		 * @return {@link ChannelFinderClientImpl}
		 */
		public ChannelFinderClient create() throws ChannelFinderException {
			if (this.protocol.equalsIgnoreCase("http")) { //$NON-NLS-1$
				this.clientConfig = new DefaultClientConfig();
			} else if (this.protocol.equalsIgnoreCase("https")) { //$NON-NLS-1$
				if (this.clientConfig == null) {
					SSLContext sslContext = null;
					try {
						sslContext = SSLContext.getInstance("SSL"); //$NON-NLS-1$
						sslContext.init(null, this.trustManager, null);
					} catch (NoSuchAlgorithmException e) {
						throw new ChannelFinderException(e.getMessage());
					} catch (KeyManagementException e) {
						throw new ChannelFinderException(e.getMessage());
					}
					this.clientConfig = new DefaultClientConfig();
					this.clientConfig.getProperties().put(
							HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
							new HTTPSProperties(new HostnameVerifier() {

								@Override
								public boolean verify(String hostname,
										SSLSession session) {
									return true;
								}
							}, sslContext));
				}
			}
			if (this.withHTTPAuthentication) {
				this.httpBasicAuthFilter = new HTTPBasicAuthFilter(
						ifNullReturnPreferenceValue(this.username,
								"channelfinder.username", "username"),
						ifNullReturnPreferenceValue(this.password,
								"channelfinder.password", "password"));
			}
			return new ChannelFinderClientImpl(this.uri, this.clientConfig,
					this.httpBasicAuthFilter, this.executor);
		}

		private String ifNullReturnPreferenceValue(String value, String key,
				String Default) {
			if (value == null) {
				return this.properties.getPreferenceValue(key, Default);
			} else {
				return value;
			}
		}
	}

	ChannelFinderClientImpl(URI uri, ClientConfig config,
			HTTPBasicAuthFilter httpBasicAuthFilter, ExecutorService executor) {
		Client client = Client.create(config);
		if (httpBasicAuthFilter != null) {
			client.addFilter(httpBasicAuthFilter);
		}
		client.addFilter(new RawLoggingFilter(Logger
				.getLogger(RawLoggingFilter.class.getName())));
		service = client.resource(UriBuilder.fromUri(uri).build());
		this.executor = executor;
	}

	/**
	 * Get a list of names of all the properties currently present on the
	 * channelfinder service.
	 * 
	 * @return list of names of all existing {@link Property}s.
	 */
	public Collection<String> getAllProperties() {

		return wrappedSubmit(new Callable<Collection<String>>() {

			@Override
			public Collection<String> call() throws Exception {

				Collection<String> allProperties = new HashSet<String>();
				XmlProperties allXmlProperties = service
						.path(resourceProperties)
						.accept(MediaType.APPLICATION_XML)
						.get(XmlProperties.class);
				for (XmlProperty xmlProperty : allXmlProperties.getProperties()) {
					allProperties.add(xmlProperty.getName());
				}
				return allProperties;
			}

		});
	}

	/**
	 * Get a list of names of all the tags currently present on the
	 * channelfinder service.
	 * 
	 * @return a list of names of all the existing {@link Tag}s.
	 */
	public Collection<String> getAllTags() {
		return wrappedSubmit(new Callable<Collection<String>>() {

			@Override
			public Collection<String> call() throws Exception {
				Collection<String> allTags = new HashSet<String>();
				XmlTags allXmlTags = service.path(resourceTags) //$NON-NLS-1$
						.accept(MediaType.APPLICATION_XML).get(XmlTags.class);
				for (XmlTag xmlTag : allXmlTags.getTags()) {
					allTags.add(xmlTag.getName());
				}
				return allTags;
			}
		});
	}

	@Deprecated
	public static void resetPreferences() {
		try {
			Preferences.userNodeForPackage(ChannelFinderClientImpl.class)
					.clear();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a channel that exactly matches the channelName
	 * <tt>channelName</tt>.
	 * 
	 * @param channelName
	 *            - name of the required channel.
	 * @return {@link Channel} with name <tt>channelName</tt> or null
	 * @throws ChannelFinderException
	 */
	public Channel getChannel(String channelName) throws ChannelFinderException {
		try {
			return wrappedSubmit(new FindByChannelName(channelName));
		} catch (ChannelFinderException e) {
			if (e.getStatus().equals(ClientResponse.Status.NOT_FOUND)) {
				return null;
			} else {
				throw e;
			}
		}

	}

	private class FindByChannelName implements Callable<Channel> {

		private final String channelName;

		FindByChannelName(String channelName) {
			super();
			this.channelName = channelName;
		}

		@Override
		public Channel call() throws UniformInterfaceException {
			return new Channel(service.path(resourceChannels).path(channelName)
					.accept( //$NON-NLS-1$
					MediaType.APPLICATION_XML).get(XmlChannel.class));
		}

	}

	/**
	 * Destructively set a single channel <tt>channel</tt>, if the channel
	 * already exists it will be replaced with the given channel.
	 * 
	 * @param channel
	 *            the channel to be added
	 * @throws ChannelFinderException
	 */
	public void set(Channel.Builder channel) throws ChannelFinderException {
		wrappedSubmit(new SetChannels(new XmlChannels(channel.toXml())));
	}

	/**
	 * Destructively set a set of channels, if any channels already exists it is
	 * replaced.
	 * 
	 * @param channels
	 *            set of channels to be added
	 * @throws ChannelFinderException
	 */
	public void set(Collection<Builder> channels) throws ChannelFinderException {
		wrappedSubmit(new SetChannels(ChannelUtil.toXmlChannels(channels)));
	}

	private class SetChannels implements Runnable {

		private final XmlChannels xmlChannels;

		SetChannels(XmlChannels xmlChannels) {
			super();
			this.xmlChannels = xmlChannels;
		}

		@Override
		public void run() {
			service.path(resourceChannels).type(MediaType.APPLICATION_XML)
					.post(this.xmlChannels);
		}

	}

	/**
	 * Destructively set a Tag <tt>tag</tt> with no associated channels to the
	 * database.
	 * 
	 * @param tag
	 *            - the tag to be set.
	 */
	public void set(Tag.Builder tag) {
		wrappedSubmit(new SetTag(tag.toXml()));
	}

	/**
	 * Destructively set tag <tt>tag</tt> to channel <tt>channelName</tt> and
	 * remove the tag from all other channels.
	 * 
	 * @param tag
	 *            - the tag to be set.
	 * @param channelName
	 *            - the channel to which the tag should be set on.
	 * @throws ChannelFinderException
	 */
	public void set(Tag.Builder tag, String channelName)
			throws ChannelFinderException {
		Collection<String> channelNames = new ArrayList<String>();
		channelNames.add(channelName);
		wrappedSubmit(new SetTag(tag.toXml(), channelNames));

	}

	/**
	 * Set tag <tt>tag</tt> on the set of channels {channels} and remove it from
	 * all others.
	 * 
	 * @param tag
	 *            - the tag to be set.
	 * @param channelNames
	 *            - the list of channels to which this tag will be added and
	 *            removed from all others.
	 * @throws ChannelFinderException
	 */
	public void set(Tag.Builder tag, Collection<String> channelNames)
			throws ChannelFinderException {
		wrappedSubmit(new SetTag(tag.toXml(), channelNames));
	}

	private class SetTag implements Runnable {
		private final XmlTag xmlTag;

		SetTag(XmlTag xmlTag) {
			super();
			this.xmlTag = xmlTag;
		}

		SetTag(XmlTag xmlTag, Collection<String> channelNames) {
			super();
			this.xmlTag = xmlTag;
			try {
				XmlChannels channels = new XmlChannels();
				XmlChannel channel;
				for (String channelName : channelNames) {
					channel = new XmlChannel(channelName);
					channels.addXmlChannel(channel);
				}
				xmlTag.setXmlChannels(channels);
				service.path(resourceTags)
						//$NON-NLS-1$
						.path(this.xmlTag.getName())
						.accept(MediaType.APPLICATION_XML).put(this.xmlTag);
			} catch (UniformInterfaceException e) {
				throw new ChannelFinderException(e);
			}
		}

		@Override
		public void run() {
			service.path(resourceTags)
					//$NON-NLS-1$
					.path(xmlTag.getName()).accept(MediaType.APPLICATION_XML)
					.put(xmlTag);
		}

	}

	/**
	 * Destructively set a new property <tt>property</tt>.
	 * 
	 * @param prop
	 *            - the property to be set.
	 */
	public void set(Property.Builder prop) throws ChannelFinderException {
		wrappedSubmit(new SetProperty(prop.toXml()));
	}

	/**
	 * Destructively set property <tt>prop</tt> and add it to the channel
	 * <tt>channelName</tt> and remove it from all others.
	 * 
	 * @param prop
	 *            - property to be set.
	 * @param channelName
	 *            - the channel to which this property must be added.
	 */
	public void set(Property.Builder prop, String channelName) {
		Collection<String> ch = new ArrayList<String>();
		ch.add(channelName);
		wrappedSubmit(new SetProperty(prop.toXml(), ch));
	}

	/**
	 * Destructively set property <tt>prop</tt> and add it to the channels
	 * <tt>channelNames</tt> removing it from all other channels. By default all
	 * channels will contain the property with the same value specified in the
	 * <tt>prop</tt>.<br>
	 * to individually set the value for each channel use channelPropertyMap.
	 * 
	 * @param prop
	 *            - the property to be set.
	 * @param channelNames
	 *            - the channels to which this property should be added and
	 *            removed from all others.
	 */
	public void set(Property.Builder prop, Collection<String> channelNames) {
		wrappedSubmit(new SetProperty(prop.toXml(), channelNames));
	}

	/**
	 * Destructively set the property <tt>prop</tt> and add it to the channels
	 * specified in the <tt>channelPropertyMap</tt>, where the map key is the
	 * channel name and the associated value is the property value to be used
	 * for that channel.
	 * 
	 * @param prop
	 *            - the property to be set.
	 * @param channelPropertyMap
	 *            - map with channel names and property values
	 */
	public void set(Property.Builder prop,
			Map<String, String> channelPropertyMap) {
		wrappedSubmit(new SetProperty(prop.toXml(), channelPropertyMap));
	}

	private class SetProperty implements Runnable {
		private final XmlProperty xmlProperty;

		SetProperty(XmlProperty prop) {
			this.xmlProperty = prop;
		}

		SetProperty(XmlProperty prop, Map<String, String> channelPropertyMap) {
			super();
			this.xmlProperty = prop;
			XmlChannels channels = new XmlChannels();
			for (Entry<String, String> e : channelPropertyMap.entrySet()) {
				XmlChannel xmlChannel = new XmlChannel(e.getKey());
				// need a copy to avoid a cycle
				xmlChannel.addXmlProperty(new XmlProperty(this.xmlProperty
						.getName(), this.xmlProperty.getOwner(), e.getValue()));
				channels.addXmlChannel(xmlChannel);
			}
			this.xmlProperty.setXmlChannels(channels);
		}

		SetProperty(XmlProperty prop, Collection<String> channelNames) {
			super();
			this.xmlProperty = prop;
			XmlChannels channels = new XmlChannels();
			for (String channelName : channelNames) {
				XmlChannel xmlChannel = new XmlChannel(channelName);
				// need a copy to avoid a linking cycle
				xmlChannel.addXmlProperty(new XmlProperty(this.xmlProperty
						.getName(), this.xmlProperty.getOwner(),
						this.xmlProperty.getValue()));
				channels.addXmlChannel(xmlChannel);
			}
			this.xmlProperty.setXmlChannels(channels);
		}

		@Override
		public void run() {
			service.path(resourceProperties).path(xmlProperty.getName())
					.accept(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_JSON).put(xmlProperty);
		}
	}

	/**
	 * Update existing channel with <tt>channel</tt>.
	 * 
	 * @param channel
	 * @throws ChannelFinderException
	 */
	public void update(Channel.Builder channel) throws ChannelFinderException {
		wrappedSubmit(new UpdateChannel(channel.toXml()));
	}

	private class UpdateChannel implements Runnable {
		private final XmlChannel channel;

		UpdateChannel(XmlChannel channel) {
			super();
			this.channel = channel;
		}

		@Override
		public void run() {
			service.path(resourceChannels).path(channel.getName())
					.type(MediaType.APPLICATION_XML).post(channel);
		}

	}

	/**
	 * Update Tag <tt>tag </tt> by adding it to Channel with name
	 * <tt>channelName</tt>, without affecting the other instances of this tag.
	 * 
	 * @param tag
	 *            the tag to be added
	 * @param channelName
	 *            Name of the channel to which the tag is to be added
	 * @throws ChannelFinderException
	 */
	public void update(Tag.Builder tag, String channelName)
			throws ChannelFinderException {
		wrappedSubmit(new UpdateTag(tag.toXml(), channelName));
	}

	/**
	 * 
	 * Update the Tag <tt>tag</tt> by adding it to the set of the channels with
	 * names <tt>channelNames</tt>, without affecting the other instances of
	 * this tag.
	 * 
	 * @param tag
	 *            - the tag that needs to be updated.
	 * @param channelNames
	 *            - list of channels to which this tag should be added.
	 * @throws ChannelFinderException
	 */
	public void update(Tag.Builder tag, Collection<String> channelNames)
			throws ChannelFinderException {
		wrappedSubmit(new UpdateTag(tag.toXml(), channelNames));
	}

	private class UpdateTag implements Runnable {
		private final XmlTag xmlTag;

		@SuppressWarnings("unused")
		UpdateTag(XmlTag xmlTag) {
			super();
			this.xmlTag = xmlTag;
		}

		UpdateTag(XmlTag xmlTag, String ChannelName) {
			super();
			this.xmlTag = xmlTag;
			this.xmlTag.setXmlChannels(new XmlChannels(new XmlChannel(
					ChannelName)));
		}

		UpdateTag(XmlTag xmlTag, Collection<String> channelNames) {
			super();
			this.xmlTag = xmlTag;
			XmlChannels channels = new XmlChannels();
			for (String channelName : channelNames) {
				channels.addXmlChannel(new XmlChannel(channelName, "")); //$NON-NLS-1$
			}
			xmlTag.setXmlChannels(channels);
		}

		@Override
		public void run() {
			service.path(resourceTags).path(xmlTag.getName())
					.type(MediaType.APPLICATION_XML).post(xmlTag);
		}

	}

	/**
	 * Update Property <tt>property</tt> by adding it to the channel
	 * <tt>channelName</tt>, without affecting the other channels.
	 * 
	 * @param property
	 *            - the property to be updated
	 * @param channelName
	 *            - the channel to which this property should be added or
	 *            updated.
	 * @throws ChannelFinderException
	 */
	public void update(Property.Builder property, String channelName)
			throws ChannelFinderException {
		wrappedSubmit(new UpdateChannelProperty(property.toXml(), channelName));
	}

	private class UpdateChannelProperty implements Runnable {
		private final String channelName;
		private final XmlProperty xmlProperty;

		UpdateChannelProperty(XmlProperty xmlProperty, String channelName) {
			super();
			this.xmlProperty = xmlProperty;
			this.channelName = channelName;
			XmlChannel xmlChannel = new XmlChannel(this.channelName);
			// need a defensive copy to avoid A cycle
			xmlChannel.addXmlProperty(new XmlProperty(xmlProperty.getName(),
					xmlProperty.getOwner(), xmlProperty.getValue()));
			xmlProperty.setXmlChannels(new XmlChannels(xmlChannel));
		}

		@Override
		public void run() {
			service.path(resourceProperties).path(this.xmlProperty.getName())
					.path(this.channelName).accept(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_JSON).put(this.xmlProperty);
		}

	}

	/**
	 * 
	 * 
	 * @param property
	 * @param channelNames
	 * @throws ChannelFinderException
	 */
	public void update(Property.Builder property,
			Collection<String> channelNames) throws ChannelFinderException {
		wrappedSubmit(new UpdateProperty(property.toXml(), channelNames));
	}

	/**
	 * 
	 * 
	 * @param property
	 * @param channelPropValueMap
	 * @throws ChannelFinderException
	 */
	public void update(Property.Builder property,
			Map<String, String> channelPropValueMap)
			throws ChannelFinderException {
		wrappedSubmit(new UpdateProperty(property.toXml(), channelPropValueMap));
	}

	private class UpdateProperty implements Runnable {
		private final XmlProperty xmlProperty;

		@SuppressWarnings("unused")
		UpdateProperty(XmlProperty xmlProperty) {
			super();
			this.xmlProperty = xmlProperty;
		}

		UpdateProperty(XmlProperty xmlProperty, Collection<String> channelNames) {
			super();
			this.xmlProperty = xmlProperty;
			XmlChannels channels = new XmlChannels();
			for (String channelName : channelNames) {
				XmlChannel xmlChannel = new XmlChannel(channelName);
				// need a defensive copy to avoid A cycle
				xmlChannel.addXmlProperty(new XmlProperty(
						xmlProperty.getName(), xmlProperty.getOwner(),
						xmlProperty.getValue()));
				channels.addXmlChannel(xmlChannel);
			}
			xmlProperty.setXmlChannels(channels);
		}

		UpdateProperty(XmlProperty xmlProperty,
				Map<String, String> channelPropValueMap) {
			super();
			this.xmlProperty = xmlProperty;
			XmlChannels channels = new XmlChannels();
			for (Entry<String, String> e : channelPropValueMap.entrySet()) {
				XmlChannel xmlChannel = new XmlChannel(e.getKey());
				// need a defensive copy to avoid A cycle
				xmlChannel.addXmlProperty(new XmlProperty(
						xmlProperty.getName(), xmlProperty.getOwner(), e
								.getValue()));
				channels.addXmlChannel(xmlChannel);
			}
			xmlProperty.setXmlChannels(channels);
		}

		@Override
		public void run() {
			service.path(resourceProperties).path(xmlProperty.getName())
					.accept(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_JSON).post(xmlProperty);
		}

	}

	/**
	 * Search for channels who's name match the pattern <tt>pattern</tt>.<br>
	 * The pattern can contain wildcard char * or ?.<br>
	 * 
	 * @param pattern
	 *            - the search pattern for the channel names
	 * @return A Collection of channels who's name match the pattern
	 *         <tt>pattern</tt>
	 * @throws ChannelFinderException
	 */
	public Collection<Channel> findByName(String pattern)
			throws ChannelFinderException {
		// return wrappedSubmit(new FindByParam("~name", pattern));
		Map<String, String> searchMap = new HashMap<String, String>();
		searchMap.put("~name", pattern);
		return wrappedSubmit(new FindByMap(searchMap));
	}

	/**
	 * Search for channels with tags who's name match the pattern
	 * <tt>pattern</tt>.<br>
	 * The pattern can contain wildcard char * or ?.<br>
	 * 
	 * @param pattern
	 *            - the search pattern for the tag names
	 * @return A Collection of channels which contain tags who's name match the
	 *         pattern <tt>pattern</tt>
	 * @throws ChannelFinderException
	 */
	public Collection<Channel> findByTag(String pattern)
			throws ChannelFinderException {
		// return wrappedSubmit(new FindByParam("~tag", pattern));
		Map<String, String> searchMap = new HashMap<String, String>();
		searchMap.put("~tag", pattern);
		return wrappedSubmit(new FindByMap(searchMap));
	}

	/**
	 * Search for channels with properties who's Value match the pattern
	 * <tt>pattern</tt>.<br>
	 * The pattern can contain wildcard char * or ?.<br>
	 * 
	 * @param property
	 *            - the name of the property.
	 * @param pattern
	 *            - the seatch pattern for the property value.
	 * @return A collection of channels containing the property with name
	 *         <tt>propertyName</tt> who's value matches the pattern
	 *         <tt> pattern</tt>.
	 * @throws ChannelFinderException
	 */
	public Collection<Channel> findByProperty(String property,
			String... pattern) throws ChannelFinderException {
		Map<String, String> propertyPatterns = new HashMap<String, String>();
		if (pattern.length > 0) {
			propertyPatterns.put(property, Joiner.on(",").join(pattern)); //$NON-NLS-1$
		} else {
			propertyPatterns.put(property, "*"); //$NON-NLS-1$
		}
		return wrappedSubmit(new FindByMap(propertyPatterns));

	}

	/**
	 * Query for channels based on the Query string <tt>query</tt> example:
	 * find("SR* Cell=1,2 Tags=GolderOrbit,myTag)<br>
	 * 
	 * this will return all channels with names starting with SR AND have
	 * property Cell=1 OR 2 AND have tags goldenOrbit AND myTag.<br>
	 * 
	 * IMP: each criteria is logically AND'ed while multiple values for
	 * Properties are OR'ed.<br>
	 * 
	 * @param query
	 * @return Collection of channels which satisfy the search criteria.
	 * @throws ChannelFinderException
	 */
	public Collection<Channel> find(String query) throws ChannelFinderException {
		return wrappedSubmit(new FindByMap(buildSearchMap(query)));
	}

	/**
	 * Query for channels based on the multiple criteria specified in the map.
	 * Map.put("~name", "*")<br>
	 * Map.put("~tag", "tag1")<br>
	 * Map.put("Cell", "1,2,3")
	 * 
	 * this will return all channels with name=any name AND tag=tag1 AND
	 * property Cell = 1 OR 2 OR 3.
	 * 
	 * @param map
	 * @return Collection of channels which satisfy the search map.
	 * @throws ChannelFinderException
	 */
	public Collection<Channel> find(Map<String, String> map)
			throws ChannelFinderException {
		return wrappedSubmit(new FindByMap(map));
	}

	/**
	 * uery for channels based on the multiple criteria specified in the map.
	 * Map.put("~name", "*")<br>
	 * Map.put("~tag", "tag1")<br>
	 * Map.put("Cell", "1")<br>
	 * Map.put("Cell", "2")<br>
	 * Map.put("Cell", "3")<br>
	 * 
	 * this will return all channels with name=any name AND tag=tag1 AND
	 * property Cell = 1 OR 2 OR 3.
	 * 
	 * @param map
	 *            - multivalued map of all search criteria
	 * @return Collection of channels which satisfy the search map.
	 * @throws ChannelFinderException
	 */
	public Collection<Channel> find(MultivaluedMap<String, String> map)
			throws ChannelFinderException {
		return wrappedSubmit(new FindByMap(map));
	}

	private class FindByMap implements Callable<Collection<Channel>> {

		private MultivaluedMapImpl map;

		FindByMap(Map<String, String> map) {
			MultivaluedMapImpl mMap = new MultivaluedMapImpl();
			for (Entry<String, String> entry : map.entrySet()) {
				String key = entry.getKey();
				for (String value : Arrays.asList(entry.getValue().split(","))) {
					mMap.add(key, value.trim());
				}
			}
			this.map = mMap;
		}

		FindByMap(MultivaluedMap<String, String> map) {
			this.map = new MultivaluedMapImpl();
			this.map.putAll(map);
		}

		@Override
		public Collection<Channel> call() throws Exception {
			Collection<Channel> channels = new HashSet<Channel>();
			XmlChannels xmlChannels = service.path(resourceChannels)
					//$NON-NLS-1$
					.queryParams(this.map).accept(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_JSON).get(XmlChannels.class);
			for (XmlChannel xmlchannel : xmlChannels.getChannels()) {
				channels.add(new Channel(xmlchannel));
			}
			return Collections.unmodifiableCollection(channels);
		}

	}

	static Map<String, String> buildSearchMap(String searchPattern) {
		Hashtable<String, String> map = new Hashtable<String, String>();
		searchPattern = searchPattern.replaceAll(", ", ",");
		String[] words = searchPattern.split("\\s");
		if (words.length <= 0) {
			throw new IllegalArgumentException();
		} else {
			for (int index = 0; index < words.length; index++) {
				if (!words[index].contains("=")) {
					// this is a name value
					if (words[index] != null)
						map.put("~name", words[index]);
				} else {
					// this is a property or tag
					String[] keyValue = words[index].split("=");
					String key = null;
					String valuePattern;
					try {
						key = keyValue[0];
						valuePattern = keyValue[1];
						if (key.equalsIgnoreCase("Tags")) {
							map.put("~tag", valuePattern.replace("||", ","));
							// for (int i = 0; i < values.length; i++)
							// map.put("~tag", values[i]);
						} else if(!key.isEmpty()) {
							map.put(key, valuePattern.replace("||", ","));
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						if(e.getMessage().equals(String.valueOf(0))){
							throw new IllegalArgumentException("= must be preceeded by a propertyName or 'Tags'.");
						} else if (e.getMessage().equals(String.valueOf(1)))
							throw new IllegalArgumentException("No pattern specified for property '" + key + "'.");
					}

				}
			}
		}
		return map;
	}

	/**
	 * Completely Delete {tag} with name = tagName from all channels and the
	 * channelfinder service.
	 * 
	 * @param tagName
	 *            - name of tag to be deleted.
	 * @throws ChannelFinderException
	 */
	public void deleteTag(String tagName) throws ChannelFinderException {
		wrappedSubmit(new DeleteElement(resourceTags, tagName));
	}

	/**
	 * Completely Delete property with name = propertyName from all channels and
	 * the channelfinder service.
	 * 
	 * @param propertyName
	 *            - name of property to be deleted.
	 * @throws ChannelFinderException
	 */
	public void deleteProperty(String propertyName)
			throws ChannelFinderException {
		wrappedSubmit(new DeleteElement(resourceProperties, propertyName));
	}

	/**
	 * Delete the channel identified by <tt>channel</tt>
	 * 
	 * @param channel
	 *            channel to be removed
	 * @throws ChannelFinderException
	 */
	public void deleteChannel(String channelName) throws ChannelFinderException {
		wrappedSubmit(new DeleteElement(resourceChannels, //$NON-NLS-1$
				channelName));
	}

	private class DeleteElement implements Runnable {
		private final String elementType;
		private final String elementName;

		DeleteElement(String elementType, String elementName) {
			super();
			this.elementType = elementType;
			this.elementName = elementName;
		}

		@Override
		public void run() {
			service.path(elementType).path(elementName).delete();
		}

	}

	/**
	 * Delete the set of channels identified by <tt>channels</tt>
	 * 
	 * @param channels
	 * @throws ChannelFinderException
	 */
	@Deprecated
	public void delete(Collection<Channel.Builder> channels)
			throws ChannelFinderException {
		for (Channel.Builder channel : channels) {
			deleteChannel(channel.build().getName());
		}
	}

	/**
	 * Delete tag <tt>tag</tt> from the channel with the name
	 * <tt>channelName</tt>
	 * 
	 * @param tag
	 *            - the tag to be deleted.
	 * @param channelName
	 *            - the channel from which to delete the tag <tt>tag</tt>
	 * @throws ChannelFinderException
	 */
	public void delete(Tag.Builder tag, String channelName)
			throws ChannelFinderException {
		wrappedSubmit(new DeleteElementfromChannel(resourceTags, tag //$NON-NLS-1$
				.toXml().getName(), channelName));
	}

	/**
	 * Remove the tag <tt>tag </tt> from all the channels <tt>channelNames</tt>
	 * 
	 * @param tag
	 *            - the tag to be deleted.
	 * @param channelNames
	 *            - the channels from which to delete the tag <tt>tag</tt>
	 * @throws ChannelFinderException
	 */
	public void delete(Tag.Builder tag, Collection<String> channelNames)
			throws ChannelFinderException {
		// TODO optimize using the /tags/<name> payload with list of channels
		for (String channelName : channelNames) {
			delete(tag, channelName);
		}
	}

	/**
	 * Remove property <tt>property</tt> from the channel with name
	 * <tt>channelName</tt>
	 * 
	 * @param property
	 *            - the property to be deleted.
	 * @param channelName
	 *            - the channel from which to delete the property
	 *            <tt>property</tt>
	 * @throws ChannelFinderException
	 */
	public void delete(Property.Builder property, String channelName)
			throws ChannelFinderException {
		wrappedSubmit(new DeleteElementfromChannel(resourceProperties, property
				.build().getName(), channelName));
	}

	/**
	 * Remove the property <tt>property</tt> from the set of channels
	 * <tt>channelNames</tt>
	 * 
	 * @param property
	 *            - the property to be deleted.
	 * @param channelNames
	 *            - the channels from which to delete the property
	 *            <tt>property</tt>
	 * @throws ChannelFinderException
	 */
	public void delete(Property.Builder property,
			Collection<String> channelNames) throws ChannelFinderException {
		for (String channel : channelNames) {
			delete(property, channel);
		}
	}

	private class DeleteElementfromChannel implements Runnable {
		private final String elementType;
		private final String elementName;
		private final String channelName;

		DeleteElementfromChannel(String elementType, String elementName,
				String channelName) {
			super();
			this.elementType = elementType;
			this.elementName = elementName;
			this.channelName = channelName;
		}

		@Override
		public void run() {
			service.path(this.elementType).path(this.elementName)
					.path(this.channelName).accept(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_JSON).delete();
		}

	}

	/**
	 * close
	 */
	public void close() {
		this.executor.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!this.executor.awaitTermination(60, TimeUnit.SECONDS)) {
				this.executor.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!this.executor.awaitTermination(60, TimeUnit.SECONDS))
					System.err.println("Pool did not terminate"); //$NON-NLS-1$
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			this.executor.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}

	private <T> T wrappedSubmit(Callable<T> callable) {
		try {
			return this.executor.submit(callable).get();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			if (e.getCause() != null
					&& e.getCause() instanceof UniformInterfaceException) {
				throw new ChannelFinderException(
						(UniformInterfaceException) e.getCause());
			}
			throw new RuntimeException(e);
		}
	}

	private void wrappedSubmit(Runnable runnable) {
		try {
			this.executor.submit(runnable).get();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			if (e.getCause() != null
					&& e.getCause() instanceof UniformInterfaceException) {
				throw new ChannelFinderException(
						(UniformInterfaceException) e.getCause());
			}
			throw new RuntimeException(e);
		}
	}

	Collection<Channel> getAllChannels() {
		try {
			XmlChannels channels = service.path(resourceChannels) //$NON-NLS-1$
					.accept(MediaType.APPLICATION_XML).get(XmlChannels.class);
			Collection<Channel> set = new HashSet<Channel>();
			for (XmlChannel channel : channels.getChannels()) {
				set.add(new Channel(channel));
			}
			return set;
		} catch (UniformInterfaceException e) {
			throw new ChannelFinderException(e);
		}
	}

}
