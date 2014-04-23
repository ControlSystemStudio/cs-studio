/**
 * 
 */
package gov.bnl.unitconversion;

import gov.bnl.unitconversion.Device.DeviceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.type.TypeReference;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * @author shroffk
 * 
 */
public class ConversionClient {

    private WebResource service;

    public ConversionClient(String serviceURL) {
	ClientConfig clientConfig = new DefaultClientConfig();
	clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,
		Boolean.TRUE);
	Client client = Client.create(clientConfig);
	service = client.resource(UriBuilder.fromUri(serviceURL).build());
    }

    /**
     * List all the systems
     * 
     * @return Collection of system names
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public Collection<String> listSystems() throws IOException {
	ClientResponse clientResponse = service.path("system")
		.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
	if (clientResponse.getStatus() < 300) {
	    return clientResponse.getEntity(List.class);
	} else {
	    throw new IOException("Failed with error code : "
		    + clientResponse.getStatus());
	}
    }

    public Collection<Device> findDevices(String name) throws IOException {
	MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
	queryParams.add("name", name);
	return findDevices(queryParams);
    }

    public Collection<Device> findDevices(
	    MultivaluedMap<String, String> searchParameters) throws IOException {
	ClientResponse clientResponse = service.path("devices")
		.queryParams(searchParameters)
		.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
	if (clientResponse.getStatus() < 300) {
	    Collection<Device> devices = Arrays.asList(clientResponse
		    .getEntity(Device[].class));
	    return devices;
	} else {
	    throw new IOException("Failed with error code : "
		    + clientResponse.getStatus());
	}
    }

    public Collection<Device> getConversionInfo(String name)
	    throws JsonParseException, JsonMappingException,
	    ClientHandlerException, UniformInterfaceException, IOException {
	MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
	queryParams.add("name", name);
	return getConversionInfo(queryParams);
    }

    public Collection<Device> getConversionInfo(
	    MultivaluedMap<String, String> searchParameters) throws IOException {
	Collection<Device> result = Collections.emptyList();
	ClientResponse clientResponse = service.path("conversion")
		.queryParams(searchParameters)
		.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
	if (clientResponse.getStatus() < 300) {
	    String devices = clientResponse.getEntity(String.class);
	    ObjectMapper mapper = new ObjectMapper();

	    JsonNode tree = mapper.readTree(devices);
	    result = new ArrayList<Device>();
	    Map<String, Map<String, Conversion>> parsedComplexMap = Collections
		    .emptyMap();

	    Iterator<Entry<String, JsonNode>> it = tree.getFields();
	    for (Entry<String, JsonNode> field; it.hasNext();) {
		field = it.next();
		DeviceBuilder d = DeviceBuilder.device(field.getKey());
		parsedComplexMap = mapper
			.readValue(
				field.getValue(),
				new TypeReference<HashMap<String, HashMap<String, Conversion>>>() {
				});
		d.conversionInfo(parsedComplexMap);
		result.add(d.build());
	    }
	    return result;
	} else {
	    throw new IOException("Failed with error code : "
		    + clientResponse.getStatus());
	}
    }

    /**
     * 
     * id','name','from','to','value','unit','energy','mcdata','cache','
     * direction'
     * 
     * @param initialValue
     * @param initialunit
     * @param finalUnit
     * @return
     * @throws IOException
     */
    public Collection<Device> getConversionResult(String name, String from,
	    String to, String value) throws IOException {
	MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
	queryParams.add("name", name);
	queryParams.add("from", from);
	queryParams.add("to", to);
	queryParams.add("value", value);
	return getConversionResult(queryParams);
    }

    public Collection<Device> getConversionResult(
	    MultivaluedMap<String, String> conversionParameters)
	    throws IOException {
	Collection<Device> result = Collections.emptyList();
	ClientResponse clientResponse = service.path("conversion")
		.queryParams(conversionParameters)
		.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
	if (clientResponse.getStatus() < 300) {
	    result = new ArrayList<Device>();
	    String devices = clientResponse.getEntity(String.class);
	    ObjectMapper mapper = new ObjectMapper();

	    JsonNode tree = mapper.readTree(devices);
	    Map<String, Map<String, Conversion>> parsedComplexMap = Collections
		    .emptyMap();

	    Iterator<Entry<String, JsonNode>> it = tree.getFields();
	    for (Entry<String, JsonNode> field; it.hasNext();) {
		field = it.next();
		DeviceBuilder d = DeviceBuilder.device(field.getKey());
		parsedComplexMap = mapper
			.readValue(
				field.getValue(),
				new TypeReference<HashMap<String, HashMap<String, Conversion>>>() {
				});
		d.conversionInfo(parsedComplexMap);
		result.add(d.build());
	    }
	    return result;
	} else {
	    throw new IOException("Failed with error code : "
		    + clientResponse.getStatus());
	}
    }
}