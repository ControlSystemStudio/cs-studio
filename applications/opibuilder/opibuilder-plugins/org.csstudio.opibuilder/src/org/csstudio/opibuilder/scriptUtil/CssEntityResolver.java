package org.csstudio.opibuilder.scriptUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CssEntityResolver implements EntityResolver {

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        try {
            URI uri = new URI(systemId);
            File file = new File(uri);
            if (!file.exists()) {
                IPath path = ResourceUtil.getPathFromString(file.getPath());
                InputStream inputStream = ResourceUtil.pathToInputStream(path);

                if (inputStream != null) {
                    InputSource source = new InputSource(systemId);
                    source.setByteStream(inputStream);
                    return source;
                }
            }
        } catch (Exception e) {
            // Entity may not be found and this may throw exception. This is normal and FileUtil will revert to xi:fallback
        }
        return null;
    }

}
