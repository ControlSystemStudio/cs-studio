/**
 * 
 */
package gov.bnl.unitconversion;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

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

    @SuppressWarnings("unchecked")
    public Collection<Device> findDevices(String name) throws IOException {
	MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
	queryParams.add("name", name);
	ClientResponse clientResponse = service.path("devices")
		.queryParams(queryParams).accept(MediaType.APPLICATION_JSON)
		.get(ClientResponse.class);
	if (clientResponse.getStatus() < 300) {
	    Collection<Device> devices = Arrays.asList(clientResponse
		    .getEntity(Device[].class));
	    return devices;
	} else {
	    throw new IOException("Failed with error code : "
		    + clientResponse.getStatus());
	}
    }

    public Map<String, Map<String, Conversion>> getConversionInfo(String name) throws JsonParseException, JsonMappingException, ClientHandlerException, UniformInterfaceException, IOException {
	MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
	queryParams.add("name", name);
	ClientResponse clientResponse = service.path("conversion")
		.queryParams(queryParams).accept(MediaType.APPLICATION_JSON)
		.get(ClientResponse.class);
	if (clientResponse.getStatus() < 300) {
	    ObjectMapper mapper = new ObjectMapper();
	    String src = "{\"LN-SO5\":{\"municonv_chain\":{\"standard\":{\"raw\":\"(-0.000456230223511*I+-0.000234111416058)*(1-0.0723486665586)\",\"i2b\":[0,\"-0.000423222575196*input -0.00021717376728\"]}}}}";
	    System.out.println(src);
	    JsonFactory f = new JsonFactory();
	    JsonParser jp = f.createJsonParser(src);
	    jp.nextToken();
	    while(jp.nextToken() != JsonToken.END_OBJECT){
		String token = jp.getCurrentName();
		System.out.println(token+":"+jp.getText());
//		jp.nextToken();		
	    }
	    return null;
	} else {
	    throw new IOException("Failed with error code : "
		    + clientResponse.getStatus());
	}
    }
}