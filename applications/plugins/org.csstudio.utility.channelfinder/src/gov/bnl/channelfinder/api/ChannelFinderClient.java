package gov.bnl.channelfinder.api;

import static gov.bnl.channelfinder.api.Channel.Builder.*;
import static gov.bnl.channelfinder.api.Tag.Builder.*;

import gov.bnl.channelfinder.api.Channel.Builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * TODO: make this not a singleton. Add a constructor to programmatically pass
 * the configuration.
 * 
 * TODO: replace the usage of Xml* types with channel,tag,properties
 * 
 * @author shroffk
 * 
 */
/**
 * @author shroffk
 * 
 */
public class ChannelFinderClient {
	private static ChannelFinderClient instance = new ChannelFinderClient();
	private WebResource service;
	private static Preferences preferences;
	private static Properties defaultProperties;
	private static Properties userCFProperties;
	private static Properties userHomeCFProperties;
	private static Properties systemCFProperties;

	/**
	 * check java preferences for the requested key - then checks the various
	 * default properties files.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	private static String getPreferenceValue(String key, String defaultValue) {
		return preferences.get(key, getDefaultValue(key, defaultValue));
	}

	/**
	 * cycles through the default properties files and return the value for the
	 * key from the highest priority file
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	private static String getDefaultValue(String key, String defaultValue) {
		if (userCFProperties.containsKey(key))
			return userCFProperties.getProperty(key);
		else if (userHomeCFProperties.containsKey(key))
			return userHomeCFProperties.getProperty(key);
		else if (systemCFProperties.containsKey(key))
			return systemCFProperties.getProperty(key);
		else if (defaultProperties.containsKey(key))
			return defaultProperties.getProperty(key);
		else
			return defaultValue;
	}

	private void init() {
		System.out.println("Initializing channel finder client.");
		// log.info("Initializing channel finder client.");
		preferences = Preferences.userNodeForPackage(ChannelFinderClient.class);

		try {
			File userCFPropertiesFile = new File(System.getProperty(
					"channelfinder.properties", ""));
			File userHomeCFPropertiesFile = new File(System
					.getProperty("user.home")
					+ "/channelfinder.properties");
			File systemCFPropertiesFile = null;
			if (System.getProperty("os.name").startsWith("Windows")) {
				systemCFPropertiesFile = new File("/channelfinder.properties");
			} else if (System.getProperty("os.name").startsWith("Linux")) {
				systemCFPropertiesFile = new File(
						"/etc/channelfinder.properties");
			} else {
				systemCFPropertiesFile = new File(
						"/etc/channelfinder.properties");
			}

			// File defaultPropertiesFile = new
			// File(this.getClass().getResource(
			// "/config/channelfinder.properties").getPath());

			defaultProperties = new Properties();
			try {
				defaultProperties.load(this.getClass().getResourceAsStream(
						"/config/channelfinder.properties"));
			} catch (Exception e) {
				// The jar has been modified and the default packaged properties
				// file has been moved
				defaultProperties = null;
			}

			// Not using to new Properties(default Properties) constructor to
			// make the hierarchy clear.
			// TODO replace using constructor with default.
			systemCFProperties = new Properties(defaultProperties);
			if (systemCFPropertiesFile.exists()) {
				systemCFProperties.load(new FileInputStream(
						systemCFPropertiesFile));
			}
			userHomeCFProperties = new Properties(systemCFProperties);
			if (userHomeCFPropertiesFile.exists()) {
				userHomeCFProperties.load(new FileInputStream(
						userHomeCFPropertiesFile));
			}
			userCFProperties = new Properties(userHomeCFProperties);
			if (userCFPropertiesFile.exists()) {
				userCFProperties
						.load(new FileInputStream(userCFPropertiesFile));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create an instance of ChannelFinderClient
	 */
	private ChannelFinderClient() {
		init();

		// Authentication and Authorization configuration
		TrustManager mytm[] = null;
		SSLContext ctx = null;

		try {
			// System.out.println(this.getClass()
			// .getResource("/config/truststore.jks").getPath());
			// mytm = new TrustManager[] { new MyX509TrustManager(
			// getPreferenceValue("trustStore", this.getClass()
			// .getResource("/config/truststore.jks").getPath()),
			//					getPreferenceValue("trustPass", "default").toCharArray()) }; //$NON-NLS-1$
			mytm = new TrustManager[] { new DummyX509TrustManager() };
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			ctx = SSLContext.getInstance(getPreferenceValue("protocol", "SSL")); //$NON-NLS-1$
			ctx.init(null, mytm, null);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}

		ClientConfig config = new DefaultClientConfig();
		config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
				new HTTPSProperties(null, ctx));
		Client client = Client.create(config);
		client.addFilter(new HTTPBasicAuthFilter(getPreferenceValue("username",
				"username"), getPreferenceValue("password", "password"))); //$NON-NLS-1$ //$NON-NLS-2$

		// Logging filter - raw request and response printed to sys.o
		if (getPreferenceValue("raw_html_logging", "off").equals("on")) { //$NON-NLS-1$ //$NON-NLS-2$
			client.addFilter(new LoggingFilter());
		}
		service = client.resource(getBaseURI());
	}

	/**
	 * Get a list of all the properties currently existing
	 * 
	 * @return
	 */
	public Collection<String> getAllProperties() {
		Collection<String> allProperties = new HashSet<String>();
		try {
			XmlProperties allXmlProperties = service.path("properties").accept(
					MediaType.APPLICATION_XML).get(XmlProperties.class);
			for (XmlProperty xmlProperty : allXmlProperties.getProperties()) {
				allProperties.add(xmlProperty.getName());
			}
			return allProperties;
		} catch (UniformInterfaceException e) {
			throw new ChannelFinderException(e);
		}
	}

	/**
	 * Get a list of all the tags currently existing
	 * 
	 * @return
	 */
	public Collection<String> getAllTags() {
		Collection<String> allTags = new HashSet<String>();
		try {
			XmlTags allXmlTags = service.path("tags").accept(
					MediaType.APPLICATION_XML).get(XmlTags.class);
			for (XmlTag xmlTag : allXmlTags.getTags()) {
				allTags.add(xmlTag.getName());
			}
			return allTags;
		} catch (UniformInterfaceException e) {
			throw new ChannelFinderException(e);
		}
	}

	/**
	 * Returns the (singleton) instance of ChannelFinderClient
	 * 
	 * @return the instance of ChannelFinderClient
	 */
	public static ChannelFinderClient getInstance() {
		// System.out.println("requesting channel finder client object.");
		// log.info("requesting channel finder client object.");
		return instance;
	}

	private static URI getBaseURI() {
		return UriBuilder.fromUri(
				getPreferenceValue("channel_finder_url", null)).build(); //$NON-NLS-1$
	}

	@Deprecated
	public void resetPreferences() {
		try {
			Preferences.userNodeForPackage(this.getClass()).clear();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a channel that exactly matches the channelName
	 * <tt>channelName</tt>
	 * 
	 * @param channelName
	 * @return
	 * @throws ChannelFinderException
	 */
	public Channel getChannel(String channelName) throws ChannelFinderException {
		try {
			return new Channel(service
					.path("channel").path(channelName).accept( //$NON-NLS-1$
							MediaType.APPLICATION_XML).get(XmlChannel.class));
		} catch (UniformInterfaceException e) {
			throw new ChannelFinderException(e);
		}
	}

	/**
	 * Add a single channel <tt>channel</tt>
	 * 
	 * @param channel
	 *            the channel to be added
	 * @throws ChannelFinderException
	 */
	public void add(Channel.Builder channel) throws ChannelFinderException {
		try {
			service.path("channels").path(channel.toXml().getName()).type( //$NON-NLS-1$
					MediaType.APPLICATION_XML).put(channel.toXml());
		} catch (UniformInterfaceException e) {
			throw new ChannelFinderException(e);
		}
	}

	/**
	 * Add a set of channels
	 * 
	 * @param channels
	 *            set of channels to be added
	 * @throws ChannelFinderException
	 */
	public void add(Collection<Builder> channels) throws ChannelFinderException {
		try {
			XmlChannels xmlChannels = new XmlChannels();
			for (Channel.Builder channel : channels) {
				xmlChannels.addXmlChannel(channel.toXml());
			}
			service.path("channels").type(MediaType.APPLICATION_XML).post( //$NON-NLS-1$
					xmlChannels);
		} catch (UniformInterfaceException e) {
			throw new ChannelFinderException(e);
		}
	}

	/**
	 * Add a Tag <tt>tag</tt> with no associated channels to the database.
	 * 
	 * @param tag
	 */
	public void add(Tag.Builder tag) {
		try {
			XmlTag xmlTag = tag.toXml();
			service.path("tags").path(xmlTag.getName()).accept(
					MediaType.APPLICATION_XML).accept(
					MediaType.APPLICATION_JSON).put(xmlTag);
		} catch (UniformInterfaceException e) {
			throw new ChannelFinderException(e);
		}
	}

	/**
	 * Add Tag <tt>tag </tt> to Channel with name <tt>channelName</tt>
	 * 
	 * @param string
	 *            Name of the channel to which the tag is to be added
	 * @param tag
	 *            the tag to be added
	 */
	public void add(Tag.Builder tag, String channelName) {
		Set<String> channelNames = new HashSet<String>();
		channelNames.add(channelName);
		add(tag, channelNames);
	}

	/**
	 * Add the Tag <tt>tag</tt> to the set of the channels with names
	 * <tt>channelNames</tt>
	 * 
	 * @param channelNames
	 * @param tag
	 */
	public void add(Tag.Builder tag, Collection<String> channelNames) {
		try {
			XmlTag xmlTag = tag.toXml();
			XmlChannels channels = new XmlChannels();
			XmlChannel channel;
			for (String channelName : channelNames) {
				channel = new XmlChannel(channelName, "");
				channels.addXmlChannel(channel);
			}
			xmlTag.setXmlChannels(channels);
			service.path("tags").path(tag.toXml().getName()).type(
					MediaType.APPLICATION_XML).post(xmlTag);
		} catch (UniformInterfaceException e) {
			throw new ChannelFinderException(e);
		}
	}

	/**
	 * Add a new property <tt>property</tt>
	 * 
	 * @param prop
	 */
	public void add(Property.Builder prop) {
		try {
			XmlProperty property = prop.toXml();
			service.path("properties").path(property.getName()).accept(
					MediaType.APPLICATION_XML).accept(
					MediaType.APPLICATION_JSON).put(property);
		} catch (UniformInterfaceException e) {
			throw new ChannelFinderException(e);
		}
	}

	/**
	 * Add Property <tt>property</tt> to the channel <tt>channelName</tt>
	 * 
	 * @param string
	 * @param owner
	 */
	public void add(Property.Builder property, String channelName) {
		Channel channel = getChannel(channelName);
		if (channel != null) {
			updateChannel(channel(channel).with(property));
		}
	}

	/**
	 * @param channelNames
	 * @param property
	 */
	public void add(Property.Builder property, Collection<String> channelNames) {
		for (String channelName : channelNames) {
			add(property, channelName);
		}
	}

	/**
	 * 
	 * @param pattern
	 * @return
	 */
	public Collection<Channel> findChannelsByName(String pattern)
			throws ChannelFinderException {
		try {
			Collection<Channel> channels = new HashSet<Channel>();
			XmlChannels xmlChannels = service
					.path("channels").queryParam("~name", pattern).accept( //$NON-NLS-1$ //$NON-NLS-2$
							MediaType.APPLICATION_XML).accept(
							MediaType.APPLICATION_JSON).get(XmlChannels.class);
			for (XmlChannel xmlchannel : xmlChannels.getChannels()) {
				channels.add(new Channel(xmlchannel));
			}
			return Collections.unmodifiableCollection(channels);
		} catch (UniformInterfaceException e) {
			throw new ChannelFinderException(e);
		}
	}

	/**
	 * 
	 * @param pattern
	 * @return
	 */
	public Collection<Channel> findChannelsByTag(String pattern)
			throws ChannelFinderException {
		try {
			Collection<Channel> channels = new HashSet<Channel>();
			XmlChannels xmlChannels = service
					.path("channels").queryParam("~tag", pattern).accept( //$NON-NLS-1$ //$NON-NLS-2$
							MediaType.APPLICATION_XML).accept(
							MediaType.APPLICATION_JSON).get(XmlChannels.class);
			for (XmlChannel xmlchannel : xmlChannels.getChannels()) {
				channels.add(new Channel(xmlchannel));
			}
			return Collections.unmodifiableCollection(channels);

		} catch (UniformInterfaceException e) {
			throw new ChannelFinderException(e);
		}
	}

	/**
	 * This function is a subset of queryChannels - should it be removed??
	 * <p>
	 * TODO: add the usage of patterns and implement on top of the general query
	 * using the map
	 * 
	 * @param property
	 * @return
	 * @throws ChannelFinderException
	 */
	public Collection<Channel> findChannelsByProp(String property,
			String... patterns) throws ChannelFinderException {
		try {
			Collection<Channel> channels = new HashSet<Channel>();
			XmlChannels xmlChannels = service
					.path("channels").queryParam(property, "*").accept( //$NON-NLS-1$ //$NON-NLS-2$
							MediaType.APPLICATION_XML).accept(
							MediaType.APPLICATION_JSON).get(XmlChannels.class);
			for (XmlChannel xmlchannel : xmlChannels.getChannels()) {
				channels.add(new Channel(xmlchannel));
			}
			return Collections.unmodifiableCollection(channels);
		} catch (UniformInterfaceException e) {
			throw new ChannelFinderException(e);
		}
	}

	/**
	 * Query for channels based on the criteria specified in the map
	 * 
	 * @param map
	 * @return
	 */
	public Collection<Channel> findChannels(Map<String, String> map) {
		MultivaluedMapImpl mMap = new MultivaluedMapImpl();
		Iterator<Map.Entry<String, String>> itr = map.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<String, String> entry = itr.next();
			mMap
					.put(entry.getKey(), Arrays.asList(entry.getValue().split(
							",")));
		}
		return findChannels(mMap);
	}

	/**
	 * Multivalued map used to search for a key with multiple values. e.g.
	 * property a=1 or property a=2
	 * 
	 * @param map
	 *            Multivalue map for searching a key with multiple values
	 * @return
	 */
	public Collection<Channel> findChannels(MultivaluedMapImpl map) {
		Collection<Channel> channels = new HashSet<Channel>();
		XmlChannels xmlChannels = service.path("channels").queryParams(map)
				.accept(MediaType.APPLICATION_XML).accept(
						MediaType.APPLICATION_JSON).get(XmlChannels.class);
		for (XmlChannel xmlchannel : xmlChannels.getChannels()) {
			channels.add(new Channel(xmlchannel));
		}
		return Collections.unmodifiableCollection(channels);
	}

	/**
	 * Remove {tag} from all channels
	 * 
	 * @param tag
	 */
	public void deleteTag(String tag) {
		try {
			service.path("tags").path(tag).accept(MediaType.APPLICATION_XML) //$NON-NLS-1$
					.delete();
		} catch (UniformInterfaceException e) {
			throw new ChannelFinderException(e);
		}
	}

	/**
	 * 
	 * @param property
	 * @throws ChannelFinderException
	 */
	public void deleteProperty(String property) throws ChannelFinderException {
		try {
			service.path("properties").path(property).accept(
					MediaType.APPLICATION_XML).accept(
					MediaType.APPLICATION_JSON).delete();
		} catch (UniformInterfaceException e) {
			throw new ChannelFinderException(e);
		}
	}

	Collection<Channel> getAllChannels() {
		try {
			XmlChannels channels = service.path("channels").accept( //$NON-NLS-1$
					MediaType.APPLICATION_XML).get(XmlChannels.class);
			Collection<Channel> set = new HashSet<Channel>();
			for (XmlChannel channel : channels.getChannels()) {
				set.add(new Channel(channel));
			}
			return set;
		} catch (UniformInterfaceException e) {
			throw new ChannelFinderException(e);
		}
	}

	/**
	 * Remove the channel identified by <tt>channel</tt>
	 * 
	 * @param channel
	 *            channel to be removed
	 * @throws ChannelFinderException
	 */
	public void remove(Channel.Builder channel) throws ChannelFinderException {
		try {
			service.path("channels").path(channel.toXml().getName()).delete(); //$NON-NLS-1$
		} catch (UniformInterfaceException e) {
			throw new ChannelFinderException(e);
		}
	}

	/**
	 * Remove the set of channels identified by <tt>channels</tt>
	 * 
	 * @param channels
	 * @throws ChannelFinderException
	 */
	@Deprecated
	public void remove(Collection<Channel.Builder> channels)
			throws ChannelFinderException {
		for (Channel.Builder channel : channels) {
			remove(channel);
		}
	}

	/**
	 * Remove tag <tt>tag</tt> from the channel with the name
	 * <tt>channelName</tt>
	 * 
	 * @param tag
	 * @param channelName
	 */
	public void remove(Tag.Builder tag, String channelName)
			throws ChannelFinderException {
		try {
			service
					.path("tags").path(tag.toXml().getName()).path(channelName).accept( //$NON-NLS-1$
							MediaType.APPLICATION_XML).delete();
		} catch (UniformInterfaceException e) {
			throw new ChannelFinderException(e);
		}
	}

	/**
	 * Remove the tag <tt>tag </tt> from all the channels <tt>channelNames</tt>
	 * 
	 * @param tag
	 * @param channelNames
	 * @throws ChannelFinderException
	 */
	public void remove(Tag.Builder tag, Collection<String> channelNames)
			throws ChannelFinderException {
		// TODO optimize using the /tags/<name> payload with list of channels
		for (String channelName : channelNames) {
			remove(tag, channelName);
		}
	}

	/**
	 * Remove property <tt>property</tt> from the channel with name
	 * <tt>channelName</tt>
	 * 
	 * @param property
	 * @param name
	 * @throws ChannelFinderException
	 */
	public void remove(Property.Builder property, String channelName)
			throws ChannelFinderException {
		try {
			service.path("properties").path(property.toXml().getName()).path(
					channelName).accept(MediaType.APPLICATION_XML).accept(
					MediaType.APPLICATION_JSON).delete();
		} catch (UniformInterfaceException e) {
			throw new ChannelFinderException(e);
		}
	}

	/**
	 * Remove the property <tt>property</tt> from the set of channels
	 * <tt>channelNames</tt>
	 * 
	 * @param property
	 * @param channelNames
	 * @throws ChannelFinderException
	 */
	public void remove(Property.Builder property,
			Collection<String> channelNames) throws ChannelFinderException {
		for (String channel : channelNames) {
			remove(property, channel);
		}
	}

	/**
	 * Update properties and tags of existing channel <tt>channel</tt>
	 * 
	 * @param channel
	 * @throws ChannelFinderException
	 */
	public void updateChannel(Channel.Builder channel)
			throws ChannelFinderException {
		try {
			service.path("channels").path(channel.toXml().getName()).type( //$NON-NLS-1$
					MediaType.APPLICATION_XML).post(channel.toXml());
		} catch (UniformInterfaceException e) {
			throw new ChannelFinderException(e);
		}
	}

	/**
	 * Add tag <tt>tag</tt> to channel <tt>channelName</tt> and remove the tag
	 * from all other channels
	 * 
	 * @param tag
	 * @param channel
	 * @throws ChannelFinderException
	 */
	public void set(Tag.Builder tag, String channel)
			throws ChannelFinderException {
		try {
			// service.path("tags").path(tag.toXml().getName()).path(channel)
			// .type(MediaType.APPLICATION_XML).put(tag.toXml());
			Collection<String> channels = new ArrayList<String>();
			channels.add(channel);
			set(tag, channels);
		} catch (UniformInterfaceException e) {
			throw new ChannelFinderException(e);
		}

	}

	/**
	 * Set tag <tt>tag</tt> on the set of channels {channels} and remove it from
	 * all others
	 * 
	 * @param channels
	 * @param tag
	 */
	public void set(Tag.Builder tag, Collection<String> channelNames) {
		// Better than recursively calling set(tag, channel) for each channel
		try {
			XmlTag xmlTag = tag.toXml();
			XmlChannels channels = new XmlChannels();
			XmlChannel channel;
			for (String channelName : channelNames) {
				channel = new XmlChannel(channelName);
				channels.addXmlChannel(channel);
			}
			xmlTag.setXmlChannels(channels);
			service.path("tags").path(tag.toXml().getName()).accept(
					MediaType.APPLICATION_XML).put(xmlTag);
		} catch (UniformInterfaceException e) {
			throw new ChannelFinderException(e);
		}
	}

}
