package gov.bnl.shiftClient;

import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.impl.MultiPartWriter;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;


/**
*@author: eschuhmacher
 */
public class ShiftClientImpl implements ShiftClient {
    private final WebResource service;
    private final ExecutorService executor;

    public static class ShiftClientBuilder {
        // required
        private URI shiftURI = null;

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

        private ShiftProperties properties = new ShiftProperties();

        private static final String DEFAULT_SHIFT_URL = "http://localhost:8080/Shift/resources"; //$NON-NLS-1$8

        private ShiftClientBuilder() {
            this.shiftURI = URI.create(this.properties.getPreferenceValue(
                    "shift_url", DEFAULT_SHIFT_URL));
            this.protocol = this.shiftURI.getScheme();
        }

        private ShiftClientBuilder(URI uri) {
            this.shiftURI = uri;
            this.protocol = this.shiftURI.getScheme();
        }

        /**
         * Creates a {@link ShiftClientBuilder} for a CF client to Default URL in
         * the channelfinder.properties.
         *
         * @return
         */
        public static ShiftClientBuilder serviceURL() {
            return new ShiftClientBuilder();
        }

        /**
         * Creates a {@link ShiftClientBuilder} for a CF client to URI
         * <tt>uri</tt>.
         *
         * @param uri
         * @return {@link ShiftClientBuilder}
         */
        public static ShiftClientBuilder serviceURL(final String uri) {
            return new ShiftClientBuilder(URI.create(uri));
        }

        /**
         * Creates a {@link ShiftClientBuilder} for a CF client to {@link URI}
         * <tt>uri</tt>.
         *
         * @param uri
         * @return {@link ShiftClientBuilder}
         */
        public static ShiftClientBuilder serviceURL(final URI uri) {
            return new ShiftClientBuilder(uri);
        }

        /**
         * Enable of Disable the HTTP authentication on the client connection.
         *
         * @param withHTTPAuthentication
         * @return {@link ShiftClientBuilder}
         */
        public ShiftClientBuilder withHTTPAuthentication(final boolean withHTTPAuthentication) {
            this.withHTTPAuthentication = withHTTPAuthentication;
            return this;
        }

        /**
         * Set the username to be used for HTTP Authentication.
         *
         * @param username
         * @return {@link ShiftClientBuilder}
         */
        public ShiftClientBuilder username(final String username) {
            this.username = username;
            return this;
        }

        /**
         * Set the password to be used for the HTTP Authentication.
         *
         * @param password
         * @return {@link ShiftClientBuilder}
         */
        public ShiftClientBuilder password(final String password) {
            this.password = password;
            return this;
        }

        /**
         * set the {@link ClientConfig} to be used while creating the
         * channelfinder client connection.
         *
         * @param clientConfig
         * @return {@link ShiftClientBuilder}
         */
        public ShiftClientBuilder withClientConfig(final ClientConfig clientConfig) {
            this.clientConfig = clientConfig;
            return this;
        }

        @SuppressWarnings("unused")
        private ShiftClientBuilder withSSLContext(final SSLContext sslContext) {
            this.sslContext = sslContext;
            return this;
        }

        /**
         * Set the trustManager that should be used for authentication.
         *
         * @param trustManager
         * @return {@link ShiftClientBuilder}
         */
        public ShiftClientBuilder withTrustManager(final TrustManager[] trustManager) {
            this.trustManager = trustManager;
            return this;
        }

        /**
         * Provide your own executor on which the queries are to be made. <br>
         * By default a single threaded executor is used.
         *
         * @param executor
         * @return {@link ShiftClientBuilder}
         */
        public ShiftClientBuilder withExecutor(final ExecutorService executor) {
            this.executor = executor;
            return this;
        }

        public ShiftClientImpl create() throws Exception {
            if (this.protocol.equalsIgnoreCase("http")) { //$NON-NLS-1$
                this.clientConfig = new DefaultClientConfig();
            } else if (this.protocol.equalsIgnoreCase("https")) { //$NON-NLS-1$
                if (this.clientConfig == null) {
                    SSLContext sslContext = null;
                    try {
                        sslContext = SSLContext.getInstance("SSL"); //$NON-NLS-1$
                        sslContext.init(null, this.trustManager, null);
                    } catch (NoSuchAlgorithmException e) {
                        throw new ShiftFinderException();
                    } catch (KeyManagementException e) {
                        throw new ShiftFinderException();
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
            return new ShiftClientImpl(this.shiftURI, this.clientConfig,
                    this.withHTTPAuthentication, this.username, this.password,
                    this.executor);
        }

        private String ifNullReturnPreferenceValue(final String value,final String key, final String Default) {
            if (value == null) {
                return this.properties.getPreferenceValue(key, Default);
            } else {
                return value;
            }
        }

    }
    private ShiftClientImpl(final URI shiftURI, ClientConfig config, final boolean withHTTPBasicAuthFilter,
                            final String username, final String password, final ExecutorService executor) {
        this.executor = executor;
        config.getClasses().add(MultiPartWriter.class);
        final Client client = Client.create(config);
        if (withHTTPBasicAuthFilter) {
            client.addFilter(new HTTPBasicAuthFilter(username, password));
        }
        client.setFollowRedirects(true);
        service = client.resource(UriBuilder.fromUri(shiftURI).build());
    }

    @Override
    public Collection<Shift> listShifts() throws ShiftFinderException {
        return wrappedSubmit(new Callable<Collection<Shift>>() {

            @Override
            public Collection<Shift> call() throws Exception {
                final Collection<Shift> shifts = new LinkedHashSet<Shift>();
                final XmlShifts xmlShifts = service.path("shift")
                        .accept(MediaType.APPLICATION_XML)
                        .accept(MediaType.APPLICATION_JSON).get(XmlShifts.class);
                for (XmlShift xmlShift : xmlShifts.getShifts()) {
                    shifts.add(new Shift(xmlShift));
                }
                return shifts;
            }
        });
    }

    @Override
    public Shift getShift(final Integer shiftId, final String type) throws ShiftFinderException {
        return wrappedSubmit(new Callable<Shift>() {

            @Override
            public Shift call() throws Exception {
                final XmlShift xmlShift = service.path("shift").path(type).path(shiftId.toString())
                        .accept(MediaType.APPLICATION_XML)
                        .accept(MediaType.APPLICATION_JSON).get(XmlShift.class);
                return new Shift(xmlShift);
            }

        });    
    }
    
    @Override
    public Shift getShiftByType(final String type) throws ShiftFinderException {
        return wrappedSubmit(new Callable<Shift>() {

            @Override
            public Shift call() throws Exception {
                final XmlShift xmlShift = service.path("shift").path(type)
                        .accept(MediaType.APPLICATION_XML)
                        .accept(MediaType.APPLICATION_JSON).get(XmlShift.class);
                return new Shift(xmlShift);
            }

        });    
    }

    @Override
    public Shift start(final Shift shift) throws ShiftFinderException {
        return wrappedSubmit(new Callable<Shift>() {
            @Override
            public Shift call() throws Exception {
                final XmlShift xmlShift = shift.toXml();
                final ClientResponse clientResponse = service.path("shift").path("start")
                        .accept(MediaType.APPLICATION_XML)
                        .accept(MediaType.APPLICATION_JSON)
                        .put(ClientResponse.class, xmlShift);
                if (clientResponse.getStatus() < 300)
                    return new Shift(clientResponse.getEntity(XmlShift.class));
                else
                    throw new UniformInterfaceException(clientResponse);
            }
        });
    }

    @Override
    public Shift end(final Shift shift) throws ShiftFinderException {
        return wrappedSubmit(new Callable<Shift>() {
            @Override
            public Shift call() throws Exception {
                final XmlShift xmlShift = shift.toXml();
                final ClientResponse clientResponse = service.path("shift").path("end")
                        .accept(MediaType.APPLICATION_XML)
                        .accept(MediaType.APPLICATION_JSON)
                        .put(ClientResponse.class, xmlShift);
                if (clientResponse.getStatus() < 300)
                    return new Shift(clientResponse.getEntity(XmlShift.class));
                else
                    throw new UniformInterfaceException(clientResponse);
            }
        });    }

    @Override
    public Shift close(final Shift shift) throws ShiftFinderException {
        return wrappedSubmit(new Callable<Shift>() {
            @Override
            public Shift call() throws Exception {
                final XmlShift xmlShift = shift.toXml();
                final ClientResponse clientResponse = service.path("shift").path("close")
                        .accept(MediaType.APPLICATION_XML)
                        .accept(MediaType.APPLICATION_JSON)
                        .put(ClientResponse.class, xmlShift);
                if (clientResponse.getStatus() < 300)
                    return new Shift(clientResponse.getEntity(XmlShift.class));
                else
                    throw new UniformInterfaceException(clientResponse);
            }
        });    }

    @Override
    public Collection<Shift> findShiftsBySearch(String pattern) throws ShiftFinderException {

	    return null;
    }

    @Override
    public Collection<Shift> findShifts(final Map<String, String> map) throws ShiftFinderException {
        final MultivaluedMap<String, String> mMap = new MultivaluedMapImpl();
        final Iterator<Map.Entry<String, String>> itr = map.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, String> entry = itr.next();
            mMap.put(entry.getKey(),
                    Arrays.asList(entry.getValue().split(",")));
        }
        return wrappedSubmit(new Callable<Collection<Shift>>() {
        @Override
        public Collection<Shift> call() throws Exception {
            final Collection<Shift> shifts = new LinkedHashSet<Shift>();
            final XmlShifts xmlShifts = service.path("shift").queryParams(mMap)
                .accept(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_JSON).get(XmlShifts.class);
        for (XmlShift xmlShift : xmlShifts.getShifts()) {
            shifts.add(new Shift(xmlShift));
        }
        return  Collections.unmodifiableCollection(shifts);
        }
        });
    }

    @Override
    public Collection<Shift> findShifts(final MultivaluedMap<String, String> map) throws ShiftFinderException {
        return wrappedSubmit(new Callable<Collection<Shift>>() {
            @Override
            public Collection<Shift> call() throws Exception {
                final Collection<Shift> shifts = new LinkedHashSet<Shift>();
                final XmlShifts xmlShifts = service.path("shift").queryParams(map)
                        .accept(MediaType.APPLICATION_XML)
                        .accept(MediaType.APPLICATION_JSON).get(XmlShifts.class);
                for (XmlShift xmlShift : xmlShifts.getShifts()) {
                    shifts.add(new Shift(xmlShift));
                }
                return  Collections.unmodifiableCollection(shifts);
            }
        });    
    }
    
	@Override
	public Collection<Type> listTypes() throws ShiftFinderException {
		return wrappedSubmit(new Callable<Collection<Type>>() {

            @Override
            public Collection<Type> call() throws Exception {
                final Collection<Type> types = new HashSet<Type>();
                final XmlTypes xmlTypes = service.path("shift").path("type")
                        .accept(MediaType.APPLICATION_XML)
                        .accept(MediaType.APPLICATION_JSON).get(XmlTypes.class);
                for (XmlType xmlType : xmlTypes.getTypes()) {
                    types.add(new Type(xmlType));
                }
                return types;
            }
        });
	}
	

    private <T> T wrappedSubmit(final Callable<T> callable) {
        try {
            return this.executor.submit(callable).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            if (e.getCause() != null
                    && e.getCause() instanceof UniformInterfaceException) {
                throw new ShiftFinderException(
                        (UniformInterfaceException) e.getCause());
            }
            throw new RuntimeException(e);
        }
    }


}
