package edu.msu.nscl.olog.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import com.sun.jersey.multipart.impl.MultiPartWriter;

/**
 * 
 * 
 * @author Eric Berryman taken from shroffk
 * 
 */
public class OlogClientImpl implements OlogClient {
	private final WebResource service;
	private final ExecutorService executor;

	/**
	 * Builder Class to help create a olog client.
	 * 
	 * @author shroffk
	 * 
	 */
	public static class OlogClientBuilder {
		// required
		private URI ologURI = null;

		// optional
		private boolean withHTTPAuthentication = false;

		private ClientConfig clientConfig = null;
		private TrustManager[] trustManager = new TrustManager[] { new DummyX509TrustManager() };;
		@SuppressWarnings("unused")
		private SSLContext sslContext = null;

		private String protocol = null;
		private String username = null;
		private String password = null;

		private ExecutorService executor = Executors.newSingleThreadExecutor();

		private OlogProperties properties = new OlogProperties();

		private static final String DEFAULT_OLOG_URL = "http://localhost:8080/Olog/resources"; //$NON-NLS-1$

		private OlogClientBuilder() {
			this.ologURI = URI.create(this.properties.getPreferenceValue(
					"olog_url", DEFAULT_OLOG_URL));
			this.protocol = this.ologURI.getScheme();
		}

		private OlogClientBuilder(URI uri) {
			this.ologURI = uri;
			this.protocol = this.ologURI.getScheme();
		}

		/**
		 * Creates a {@link OlogClientBuilder} for a CF client to Default URL in
		 * the channelfinder.properties.
		 * 
		 * @return
		 */
		public static OlogClientBuilder serviceURL() {
			return new OlogClientBuilder();
		}

		/**
		 * Creates a {@link OlogClientBuilder} for a CF client to URI
		 * <tt>uri</tt>.
		 * 
		 * @param uri
		 * @return {@link OlogClientBuilder}
		 */
		public static OlogClientBuilder serviceURL(String uri) {
			return new OlogClientBuilder(URI.create(uri));
		}

		/**
		 * Creates a {@link OlogClientBuilder} for a CF client to {@link URI}
		 * <tt>uri</tt>.
		 * 
		 * @param uri
		 * @return {@link OlogClientBuilder}
		 */
		public static OlogClientBuilder serviceURL(URI uri) {
			return new OlogClientBuilder(uri);
		}

		/**
		 * Enable of Disable the HTTP authentication on the client connection.
		 * 
		 * @param withHTTPAuthentication
		 * @return {@link OlogClientBuilder}
		 */
		public OlogClientBuilder withHTTPAuthentication(
				boolean withHTTPAuthentication) {
			this.withHTTPAuthentication = withHTTPAuthentication;
			return this;
		}

		/**
		 * Set the username to be used for HTTP Authentication.
		 * 
		 * @param username
		 * @return {@link OlogClientBuilder}
		 */
		public OlogClientBuilder username(String username) {
			this.username = username;
			return this;
		}

		/**
		 * Set the password to be used for the HTTP Authentication.
		 * 
		 * @param password
		 * @return {@link OlogClientBuilder}
		 */
		public OlogClientBuilder password(String password) {
			this.password = password;
			return this;
		}

		/**
		 * set the {@link ClientConfig} to be used while creating the
		 * channelfinder client connection.
		 * 
		 * @param clientConfig
		 * @return {@link OlogClientBuilder}
		 */
		public OlogClientBuilder withClientConfig(ClientConfig clientConfig) {
			this.clientConfig = clientConfig;
			return this;
		}

		@SuppressWarnings("unused")
		private OlogClientBuilder withSSLContext(SSLContext sslContext) {
			this.sslContext = sslContext;
			return this;
		}

		/**
		 * Set the trustManager that should be used for authentication.
		 * 
		 * @param trustManager
		 * @return {@link OlogClientBuilder}
		 */
		public OlogClientBuilder withTrustManager(TrustManager[] trustManager) {
			this.trustManager = trustManager;
			return this;
		}

		/**
		 * Provide your own executor on which the queries are to be made. <br>
		 * By default a single threaded executor is used.
		 * 
		 * @param executor
		 * @return {@link OlogClientBuilder}
		 */
		public OlogClientBuilder withExecutor(ExecutorService executor) {
			this.executor = executor;
			return this;
		}

		public OlogClientImpl create() throws Exception {
			if (this.protocol.equalsIgnoreCase("http")) { //$NON-NLS-1$
				this.clientConfig = new DefaultClientConfig();
			} else if (this.protocol.equalsIgnoreCase("https")) { //$NON-NLS-1$
				if (this.clientConfig == null) {
					SSLContext sslContext = null;
					try {
						sslContext = SSLContext.getInstance("SSL"); //$NON-NLS-1$
						sslContext.init(null, this.trustManager, null);
					} catch (NoSuchAlgorithmException e) {
						throw new OlogException();
					} catch (KeyManagementException e) {
						throw new OlogException();
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
			this.username = ifNullReturnPreferenceValue(this.username,
					"username", "username");
			this.password = ifNullReturnPreferenceValue(this.password,
					"password", "password");
			return new OlogClientImpl(this.ologURI, this.clientConfig,
					this.withHTTPAuthentication, this.username, this.password,
					this.executor);
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

	private OlogClientImpl(URI ologURI, ClientConfig config,
			boolean withHTTPBasicAuthFilter, String username, String password,
			ExecutorService executor) {
		this.executor = executor;
		config.getClasses().add(MultiPartWriter.class);
		Client client = Client.create(config);
		if (withHTTPBasicAuthFilter) {
			client.addFilter(new HTTPBasicAuthFilter(username, password));
		}
		client.addFilter(new RawLoggingFilter(Logger
				.getLogger(OlogClientImpl.class.getName())));
		client.setFollowRedirects(true);
		service = client.resource(UriBuilder.fromUri(ologURI).build());
	}

	@Override
	public Collection<Logbook> listLogbooks() throws OlogException {
		return wrappedSubmit(new Callable<Collection<Logbook>>() {

			@Override
			public Collection<Logbook> call() throws Exception {

				Collection<Logbook> allLogbooks = new HashSet<Logbook>();
				XmlLogbooks allXmlLogbooks = service.path("logbooks")
						.accept(MediaType.APPLICATION_XML)
						.get(XmlLogbooks.class);
				for (XmlLogbook xmlLogbook : allXmlLogbooks.getLogbooks()) {
					allLogbooks.add(new Logbook(xmlLogbook));
				}
				return allLogbooks;
			}

		});
	}

	@Override
	public Collection<Tag> listTags() throws OlogException {
		return wrappedSubmit(new Callable<Collection<Tag>>() {

			@Override
			public Collection<Tag> call() throws Exception {
				Collection<Tag> allTags = new HashSet<Tag>();
				XmlTags allXmlTags = service.path("tags")
						.accept(MediaType.APPLICATION_XML).get(XmlTags.class);
				for (XmlTag xmlTag : allXmlTags.getTags()) {
					allTags.add(new Tag(xmlTag));
				}
				return allTags;
			}

		});
	}

	@Override
	public Collection<Property> listProperties() throws OlogException {
		return wrappedSubmit(new Callable<Collection<Property>>() {
			@Override
			public Collection<Property> call() throws Exception {
				Collection<Property> allProperties = new HashSet<Property>();
				XmlProperties xmlProperties = service.path("properties")
						.accept(MediaType.APPLICATION_XML)
						.accept(MediaType.APPLICATION_JSON)
						.get(XmlProperties.class);
				for (XmlProperty xmlProperty : xmlProperties.getProperties()) {
					allProperties.add(new Property(xmlProperty));
				}
				return allProperties;
			}
		});
	}

	@Override
	public Collection<String> listAttributes(String propertyName)
			throws OlogException {
		return getProperty(propertyName).getAttributes();
	}

	@SuppressWarnings("deprecation")
	@Override
	public Collection<Level> listLevels() throws OlogException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Log> listLogs() {
		return wrappedSubmit(new Callable<Collection<Log>>() {
			@Override
			public Collection<Log> call() throws Exception {
				XmlLogs xmlLogs = service.path("logs")
						.accept(MediaType.APPLICATION_XML)
						.accept(MediaType.APPLICATION_JSON).get(XmlLogs.class);
				return LogUtil.toLogs(xmlLogs);
			}
		});
	}

	@Override
	public Log getLog(Long logId) throws OlogException {
		return findLogById(logId);
	}

	@Override
	public Collection<Attachment> listAttachments(final Long logId)
			throws OlogException {
		return wrappedSubmit(new Callable<Collection<Attachment>>() {

			@Override
			public Collection<Attachment> call() throws Exception {
				Collection<Attachment> allAttachments = new HashSet<Attachment>();
				XmlAttachments allXmlAttachments = service.path("attachments")
						.path(logId.toString())
						.accept(MediaType.APPLICATION_XML)
						.get(XmlAttachments.class);
				for (XmlAttachment xmlAttachment : allXmlAttachments
						.getAttachments()) {
					allAttachments.add(new Attachment(xmlAttachment));
				}
				return allAttachments;
			}

		});
	}

	@Override
	public InputStream getAttachment(final Long logId, String attachmentFileName) {
		try {
			ClientResponse response = service.path("attachments")
					.path(logId.toString()).path(attachmentFileName)
					.get(ClientResponse.class);
			return response.getEntity(InputStream.class);
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public Property getProperty(String property) throws OlogException {
		final String propertyName = property;
		return wrappedSubmit(new Callable<Property>() {

			@Override
			public Property call() throws Exception {
				return new Property(service.path("properties")
						.path(propertyName).accept(MediaType.APPLICATION_XML)
						.accept(MediaType.APPLICATION_JSON)
						.get(XmlProperty.class));
			}
		});
	}

	@Override
	public Log set(LogBuilder log) throws OlogException {
		Collection<Log> result = wrappedSubmit(new SetLogs(log));
		if (result.size() == 1) {
			return result.iterator().next();
		} else {
			throw new OlogException();
		}
	}

	@Override
	public Collection<Log> set(Collection<LogBuilder> logs)
			throws OlogException {
		return wrappedSubmit(new SetLogs(logs));
	}

	private class SetLogs implements Callable<Collection<Log>> {
		private Collection<LogBuilder> logs;

		public SetLogs(LogBuilder log) {
			this.logs = new ArrayList<LogBuilder>();
			this.logs.add(log);
		}

		public SetLogs(Collection<LogBuilder> logs) {
			this.logs = new ArrayList<LogBuilder>(logs);
		}

		@Override
		public Collection<Log> call() {
			XmlLogs xmlLogs = new XmlLogs();
			for (LogBuilder log : logs) {
				xmlLogs.getLogs().add(log.toXml());
			}
			ClientResponse clientResponse = service.path("logs")
					.accept(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class, xmlLogs);
			if (clientResponse.getStatus() < 300) {
				XmlLogs responseLogs = clientResponse.getEntity(XmlLogs.class);
				Collection<Log> returnLogs = new HashSet<Log>();
				for (XmlLog xmllog : responseLogs.getLogs()) {
					returnLogs.add(new Log(xmllog));
				}
				return Collections.unmodifiableCollection(returnLogs);
			} else
				throw new UniformInterfaceException(clientResponse);

		}
	}

	@Override
	public Tag set(TagBuilder tag) throws OlogException {
		return wrappedSubmit(new SetTag(tag));

	}

	@Override
	public Tag set(TagBuilder tag, Collection<Long> logIds)
			throws OlogException {
		return wrappedSubmit(new SetTag(tag, logIds));
	}

	private class SetTag implements Callable<Tag> {

		private TagBuilder tag;
		private Collection<Long> logIds;

		public SetTag(TagBuilder tag) {
			this.tag = tag;
			this.logIds = null;
		}

		public SetTag(TagBuilder tag, Long LogId) {
			this.tag = tag;
			this.logIds = new ArrayList<Long>();
			this.logIds.add(LogId);
		}

		public SetTag(TagBuilder tag, Collection<Long> logIds) {
			this.tag = tag;
			this.logIds = logIds;
		}

		@Override
		public Tag call() {
			XmlTag xmlTag = tag.toXml();
			if (logIds != null && logIds.size() > 0) {
				XmlLogs xmlLogs = new XmlLogs();
				for (Long logId : logIds) {
					xmlLogs.addXmlLog(new XmlLog(logId));
				}
				xmlTag.setXmlLogs(xmlLogs);
			}
			ClientResponse clientResponse = service.path("tags")
					.path(tag.toXml().getName())
					.accept(MediaType.APPLICATION_XML)
					.put(ClientResponse.class, xmlTag);
			if (clientResponse.getStatus() < 300)
				return new Tag(clientResponse.getEntity(XmlTag.class));
			else
				throw new UniformInterfaceException(clientResponse);
		}
	}

	@Override
	public Logbook set(LogbookBuilder logbook) throws OlogException {
		return wrappedSubmit(new SetLogbook(logbook));
	}

	@Override
	public Logbook set(LogbookBuilder logbook, Collection<Long> logIds)
			throws OlogException {
		return wrappedSubmit(new SetLogbook(logbook, logIds));

	}

	private class SetLogbook implements Callable<Logbook> {
		private final LogbookBuilder logbook;
		private final Collection<Long> logIds;

		SetLogbook(LogbookBuilder logbook) {
			this.logbook = logbook;
			this.logIds = null;
		}

		public SetLogbook(LogbookBuilder logbook, Collection<Long> logIds) {
			this.logbook = logbook;
			this.logIds = logIds;
		}

		@Override
		public Logbook call() {
			XmlLogbook xmlLogbook = logbook.toXml();
			if (logIds != null) {
				XmlLogs xmlLogs = new XmlLogs();
				for (Long logId : logIds) {
					xmlLogs.addXmlLog(new XmlLog(logId));
				}
				xmlLogbook.setXmlLogs(xmlLogs);
			}
			ClientResponse clientResponse = service.path("logbooks")
					.path(xmlLogbook.getName())
					.accept(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_JSON)
					.put(ClientResponse.class, xmlLogbook);
			if (clientResponse.getStatus() < 300)
				return new Logbook(clientResponse.getEntity(XmlLogbook.class));
			else
				throw new UniformInterfaceException(clientResponse);
		}

	}

	@Override
	public Property set(PropertyBuilder property) {
		return wrappedSubmit(new SetProperty(property));
	}

	private class SetProperty implements Callable<Property> {
		private final PropertyBuilder property;

		SetProperty(PropertyBuilder property) {
			this.property = property;
		}

		@Override
		public Property call() throws Exception {
			XmlProperty xmlProperty = property.toXml();
			ClientResponse clientResponse = service.path("properties")
					.path(xmlProperty.getName())
					.accept(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_JSON)
					.put(ClientResponse.class, xmlProperty);
			if (clientResponse.getStatus() < 300)
				return new Property(clientResponse.getEntity(XmlProperty.class));
			else
				throw new UniformInterfaceException(clientResponse);

		}

	}

	@Override
	public Log update(LogBuilder log) throws OlogException {
		return wrappedSubmit(new UpdateLog(log));
	}

	private class UpdateLog implements Callable<Log> {
		private final XmlLog log;

		public UpdateLog(LogBuilder log) {
			this.log = log.toXml();
		}

		@Override
		public Log call() throws Exception {
			ClientResponse clientResponse = service.path("logs")
					.path(String.valueOf(log.getId()))
					.accept(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class, log);
			if (clientResponse.getStatus() < 300)
				return new Log(clientResponse.getEntity(XmlLog.class));
			else
				throw new UniformInterfaceException(clientResponse);
		}
	}

	@Override
	public Collection<Log> update(Collection<LogBuilder> logs)
			throws OlogException {
	    	return wrappedSubmit(new UpdateLogs(logs));
	}
	
	private class UpdateLogs implements Callable<Collection<Log>> {
		private final XmlLogs logs;

		public UpdateLogs(Collection<LogBuilder> logs) {
			this.logs = new XmlLogs();
			Collection<XmlLog> xmlLogs = new ArrayList<XmlLog>();
			for (LogBuilder log : logs){
			    xmlLogs.add(log.toXml());
			}
			this.logs.setLogs(xmlLogs);
		}

		@Override
		public Collection<Log> call() throws Exception {
			ClientResponse clientResponse = service.path("logs")
					.accept(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class, logs);
			if (clientResponse.getStatus() < 300) {
			    // return new Log(clientResponse.getEntity(XmlLog.class));
			    Collection<Log> logs = new HashSet<Log>();
			    for (XmlLog xmlLog : clientResponse.getEntity(XmlLogs.class).getLogs()) {
				logs.add(new Log(xmlLog));
			    };
			    return Collections.unmodifiableCollection(logs);
			} else {
			    throw new UniformInterfaceException(clientResponse);
			}			
		}
	}

	@Override
	public Property update(PropertyBuilder property) throws OlogException {
		final XmlProperty xmlProperty = property.toXml();
		return wrappedSubmit(new Callable<Property>() {
			@Override
			public Property call() throws Exception {
				ClientResponse clientResponse = service.path("properties")
						.path(xmlProperty.getName())
						.accept(MediaType.APPLICATION_XML)
						.accept(MediaType.APPLICATION_JSON)
						.post(ClientResponse.class, xmlProperty);
				if (clientResponse.getStatus() < 300)
					return new Property(
							clientResponse.getEntity(XmlProperty.class));
				else
					throw new UniformInterfaceException(clientResponse);

			}
		});
	}

	@Override
	public Tag update(TagBuilder tag, Long logId) throws OlogException {
		final XmlTag xmlTag = tag.toXml();
		final Long appendLogId = logId;
		return wrappedSubmit(new Callable<Tag>() {

			@Override
			public Tag call() {
				ClientResponse clientResponse = service.path("tags")
						.path(xmlTag.getName())
						.path(String.valueOf(appendLogId))
						.accept(MediaType.APPLICATION_XML)
						.accept(MediaType.APPLICATION_JSON)
						.put(ClientResponse.class);
				if (clientResponse.getStatus() < 300)
					return new Tag(clientResponse.getEntity(XmlTag.class));
				else
					throw new UniformInterfaceException(clientResponse);
			}

		});
	}

	@Override
	public Tag update(TagBuilder tag, Collection<Long> logIds)
			throws OlogException {
		final TagBuilder updateTag = tag;
		final Collection<Long> updateIds = logIds;
		return wrappedSubmit(new Callable<Tag>() {
			@Override
			public Tag call() {
				XmlTag xmlTag = updateTag.toXml();
				XmlLogs logs = new XmlLogs();
				for (Long logId : updateIds) {
					logs.addXmlLog(new XmlLog(logId));
				}
				xmlTag.setXmlLogs(logs);
				ClientResponse clientResponse = service.path("tags")
						.path(xmlTag.getName())
						.accept(MediaType.APPLICATION_XML)
						.accept(MediaType.APPLICATION_JSON)
						.post(ClientResponse.class, xmlTag);
				if (clientResponse.getStatus() < 300)
					return new Tag(clientResponse.getEntity(XmlTag.class));
				else
					throw new UniformInterfaceException(clientResponse);

			}
		});
	}

	@Override
	public Logbook update(LogbookBuilder logbook, final Long logId)
			throws OlogException {
		final XmlLogbook xmlLogbook = logbook.toXml();
		return wrappedSubmit(new Callable<Logbook>() {
			@Override
			public Logbook call() {
				ClientResponse clientResponse = service.path("logbooks")
						.path(xmlLogbook.getName()).path(logId.toString())
						.accept(MediaType.APPLICATION_XML)
						.accept(MediaType.APPLICATION_JSON)
						.put(ClientResponse.class);
				if (clientResponse.getStatus() < 300)
					return new Logbook(
							clientResponse.getEntity(XmlLogbook.class));
				else
					throw new UniformInterfaceException(clientResponse);
			}
		});
	}

	@Override
	public Logbook update(LogbookBuilder logbook, Collection<Long> logIds)
			throws OlogException {
		return wrappedSubmit(new UpdateLogBook(logbook, logIds));
	}

	private class UpdateLogBook implements Callable<Logbook> {
		private final LogbookBuilder logBook;
		private final Collection<Long> logIds;

		public UpdateLogBook(LogbookBuilder logBook) {
			this.logBook = logBook;
			this.logIds = null;
		}

		public UpdateLogBook(LogbookBuilder logBook, Collection<Long> logIds) {
			this.logBook = logBook;
			this.logIds = logIds;
		}

		@Override
		public Logbook call() {
			XmlLogbook xmlLogBook = this.logBook.toXml();
			if (this.logIds != null && this.logIds.size() > 0) {
				XmlLogs xmlLogs = new XmlLogs();
				for (Long logId : this.logIds) {
					xmlLogs.addXmlLog(new XmlLog(logId));
				}
				xmlLogBook.setXmlLogs(xmlLogs);
			}
			ClientResponse clientResponse = service.path("logbooks")
					.path(xmlLogBook.getName())
					.accept(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class, xmlLogBook);
			if (clientResponse.getStatus() < 300)
				return new Logbook(clientResponse.getEntity(XmlLogbook.class));
			else
				throw new UniformInterfaceException(clientResponse);
		}
	}

	@Override
	public Log update(PropertyBuilder property, Long logId)
			throws OlogException {
		final XmlProperty xmlProperty = property.toXml();
		final String updateLogId = logId.toString();
		return wrappedSubmit(new Callable<Log>() {
			@Override
			public Log call() throws Exception {
				ClientResponse clientResponse = service.path("properties")
						.path(xmlProperty.getName()).path(updateLogId)
						.accept(MediaType.APPLICATION_XML)
						.accept(MediaType.APPLICATION_JSON)
						.put(ClientResponse.class, xmlProperty);
				if (clientResponse.getStatus() < 300)
					return new Log(clientResponse.getEntity(XmlLog.class));
				else
					throw new UniformInterfaceException(clientResponse);
			}
		});

	}

	@Override
	public Attachment add(File local, Long logId) throws OlogException {
		FormDataMultiPart form = new FormDataMultiPart();
		form.bodyPart(new FileDataBodyPart("file", local));
		XmlAttachment xmlAttachment = service.path("attachments")
				.path(logId.toString()).type(MediaType.MULTIPART_FORM_DATA)
				.accept(MediaType.APPLICATION_XML)
				.post(XmlAttachment.class, form);

		return new Attachment(xmlAttachment);
	}

	@Override
	public Log findLogById(final Long logId) {
		return wrappedSubmit(new Callable<Log>() {

			@Override
			public Log call() throws Exception {
				XmlLog xmlLog = service.path("logs").path(logId.toString())
						.accept(MediaType.APPLICATION_XML)
						.accept(MediaType.APPLICATION_JSON).get(XmlLog.class);
				return new Log(xmlLog);
			}

		});
	}

	@Override
	public Collection<Log> findLogsBySearch(String pattern)
			throws OlogException {
		return wrappedSubmit(new FindLogs("search", pattern));
	}

	@Override
	public Collection<Log> findLogsByTag(String pattern) throws OlogException {
		return wrappedSubmit(new FindLogs("tag", pattern));
	}

	@Override
	public Collection<Log> findLogsByLogbook(String logbook)
			throws OlogException {
		return wrappedSubmit(new FindLogs("logbook", logbook));
	}

	@Override
	public Collection<Log> findLogsByProperty(String propertyName)
			throws OlogException {
		return wrappedSubmit(new FindLogs("property", propertyName));
	}

	@Override
	public Collection<Log> findLogsByProperty(String propertyName,
			String attributeName, String attributeValue) throws OlogException {
		MultivaluedMap<String, String> mMap = new MultivaluedMapImpl();
		mMap.putSingle(propertyName + "." + attributeName, attributeValue);
		return wrappedSubmit(new FindLogs(mMap));
	}

	@Override
	public Collection<Log> findLogs(Map<String, String> map)
			throws OlogException {
		return wrappedSubmit(new FindLogs(map));
	}

	@Override
	public Collection<Log> findLogs(MultivaluedMap<String, String> map)
			throws OlogException {
		return wrappedSubmit(new FindLogs(map));
	}

	private class FindLogs implements Callable<Collection<Log>> {

		private final MultivaluedMap<String, String> map;

		public FindLogs(String queryParameter, String pattern) {
			MultivaluedMap<String, String> mMap = new MultivaluedMapImpl();
			mMap.putSingle(queryParameter, pattern);
			this.map = mMap;
		}

		public FindLogs(MultivaluedMap<String, String> map) {
			this.map = map;
		}

		public FindLogs(Map<String, String> map) {
			MultivaluedMap<String, String> mMap = new MultivaluedMapImpl();
			Iterator<Map.Entry<String, String>> itr = map.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry<String, String> entry = itr.next();
				mMap.put(entry.getKey(),
						Arrays.asList(entry.getValue().split(",")));
			}
			this.map = mMap;
		}

		@Override
		public Collection<Log> call() throws Exception {
			Collection<Log> logs = new HashSet<Log>();
			XmlLogs xmlLogs = service.path("logs").queryParams(map)
					.accept(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_JSON).get(XmlLogs.class);
			for (XmlLog xmllog : xmlLogs.getLogs()) {
				logs.add(new Log(xmllog));
			}
			return Collections.unmodifiableCollection(logs);
		}

	}

	@Override
	public void deleteTag(String tag) throws OlogException {
		final String deleteTag = tag;
		wrappedSubmit(new Runnable() {

			@Override
			public void run() {
				service.path("tags").path(deleteTag)
						.accept(MediaType.APPLICATION_XML)
						.accept(MediaType.APPLICATION_JSON).delete();
			}
		});

	}

	@Override
	public void deleteLogbook(String logbook) throws OlogException {
		final String logbookName = logbook;
		wrappedSubmit(new Runnable() {

			@Override
			public void run() {
				service.path("logbooks").path(logbookName)
						.accept(MediaType.APPLICATION_XML)
						.accept(MediaType.APPLICATION_JSON).delete();
			}

		});
	}

	@Override
	public void deleteProperty(String property) throws OlogException {
		final String propertyName = property;
		wrappedSubmit(new Runnable() {

			@Override
			public void run() {
				service.path("properties").path(propertyName)
						.accept(MediaType.TEXT_XML)
						.accept(MediaType.APPLICATION_JSON)
						.delete(new XmlProperty(propertyName));
			}
		});
	}

	@Override
	public void delete(LogBuilder log) throws OlogException {
		delete(log.build().getId());
	}

	@Override
	public void delete(Long logId) throws OlogException {
		final Long deleteLogId = logId;
		wrappedSubmit(new Runnable() {
			@Override
			public void run() {
				service.path("logs").path(String.valueOf(deleteLogId))
						.accept(MediaType.APPLICATION_XML)
						.accept(MediaType.APPLICATION_JSON).delete();
			}
		});
	}

	@Deprecated
	@Override
	public void delete(Collection<Log> logs) throws OlogException {
		final Collection<Long> logIds = LogUtil.getLogIds(logs);
		wrappedSubmit(new Runnable() {
			@Override
			public void run() {
				for (Long logId : logIds) {
					service.path("logs").path(logId.toString())
							.accept(MediaType.APPLICATION_XML)
							.accept(MediaType.APPLICATION_JSON).delete();
				}
			}
		});

	}

	@Override
	public void delete(final TagBuilder tag, final Long logId)
			throws OlogException {
		wrappedSubmit(new Runnable() {
			@Override
			public void run() {
				service.path("tags").path(tag.build().getName())
						.path(logId.toString()).accept(MediaType.TEXT_XML)
						.accept(MediaType.APPLICATION_JSON).delete();
			}
		});
	}

	@Override
	public void delete(TagBuilder tag, Collection<Long> logIds)
			throws OlogException {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(final LogbookBuilder logbook, final Long logId)
			throws OlogException {
		wrappedSubmit(new Runnable() {
			@Override
			public void run() {
				service.path("logbooks").path(logbook.build().getName())
						.path(logId.toString()).accept(MediaType.TEXT_XML)
						.accept(MediaType.APPLICATION_JSON).delete();
			}
		});
	}

	@Override
	public void delete(LogbookBuilder logbook, Collection<Long> logIds)
			throws OlogException {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(final PropertyBuilder property, final Long logId)
			throws OlogException {
		wrappedSubmit(new Runnable() {
			@Override
			public void run() {
				service.path("properties").path(property.build().getName())
						.path(logId.toString()).accept(MediaType.TEXT_XML)
						.accept(MediaType.APPLICATION_JSON)
						.delete(property.toXml());
			}
		});
	}

	@Override
	public void delete(PropertyBuilder property, Collection<Long> logIds)
			throws OlogException {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(final String fileName, final Long logId) {
		wrappedSubmit(new Runnable() {
			@Override
			public void run() {
				service.path("attachments").path(logId.toString())
						.path(fileName).accept(MediaType.TEXT_XML)
						.accept(MediaType.APPLICATION_JSON).delete();
			}
		});
	}

	private <T> T wrappedSubmit(Callable<T> callable) {
		try {
			return this.executor.submit(callable).get();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			if (e.getCause() != null
					&& e.getCause() instanceof UniformInterfaceException) {
				throw new OlogException(
						(UniformInterfaceException) e.getCause());
			}
			throw new RuntimeException(e);
		}
	}

	private void wrappedSubmit(Runnable runnable) {
		try {
			this.executor.submit(runnable).get(60, TimeUnit.SECONDS);
		} catch (ExecutionException e) {
			if (e.getCause() != null
					&& e.getCause() instanceof UniformInterfaceException) {
				throw new OlogException(
						(UniformInterfaceException) e.getCause());
			}
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
