package gov.bnl.channelfinder.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class Activator implements BundleActivator {
	
	private static final Logger log = Logger.getLogger(Activator.class.getName());
	
	@Override
	public void start(BundleContext context) {
      Client client2 = Client.create();
      WebResource service = client2
              .resource("http://localhost:9090/ChannelFinder/");
      String response = service.path("resources/tags")
              .accept(MediaType.APPLICATION_JSON).get(String.class);
      List<XmlTag> xmltags = new ArrayList<XmlTag>();
      try {
          ObjectMapper mapper = new ObjectMapper();
          xmltags = mapper.readValue(response,
                  new TypeReference<List<XmlTag>>() {
                  });
      } catch (Exception e) {
          e.printStackTrace();
      }
      for (XmlTag xmlTag : xmltags) {
          System.out.println(xmlTag.toString());
      }
		try {
			ChannelFinder.setClient(configuredClient());
		} catch (Exception e) {
			log.warning("Failed to create client from current preferences");
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}
	/**
	 * Retrieves the data sources that have been registered through the extension point.
	 * 
	 * @return the registered data sources
	 */
	public static ChannelFinderClient configuredClient() {
		try {
			IConfigurationElement[] config = Platform.getExtensionRegistry()
			.getConfigurationElementsFor("gov.bnl.channelfinder.api.client");
			
			if (config.length == 0) {
				log.log(Level.INFO, "No configured client for ChannelFinder found: using default configuration");
				return null;
			}
			
			if (config.length == 1) {
				ChannelFinderClient client = (ChannelFinderClient) config[0].createExecutableExtension("channelfinderclient");
				return client;
			}
			
			throw new IllegalStateException("More than one ChannelFinderClient was configured through extensions.");
		} catch (Exception e) {
			log.log(Level.SEVERE, "Could not retrieve configured client for ChannelFinder", e);
			return null;
		}
	}
	

}
