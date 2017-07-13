package org.csstudio.logbook.olog.property.fault;

import java.io.File;
import java.net.URI;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

/**
 * Returns a faultConfiguration from the defined preferences
 *
 * @author Kunal Shroff
 *
 */
public class FaultConfigurationFactory {

    private FaultConfigurationFactory() {

    }

    public static FaultConfiguration getConfiguration() {
        try {
            JAXBContext context = JAXBContext.newInstance(FaultConfiguration.class);
            Unmarshaller um = context.createUnmarshaller();
            String file = "resources/default_fault_config.xml";
            URI filePath;
            try{
                file = Platform.getPreferencesService().getString(
                        "org.csstudio.logbook.olog.property.fault", "config.file",
                        "platform:/plugin/org.csstudio.logbook.olog.property.fault/resources/default_fault_config.xml",
                        null);
                filePath = FileLocator.resolve(new URL(file)).toURI();
            } catch (Exception e) {
                filePath = new File(file).toURI();
            }

            JAXBElement<FaultConfiguration> fc = um.unmarshal(
                    new StreamSource(new File(filePath)), FaultConfiguration.class);
            return fc.getValue();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }
}
