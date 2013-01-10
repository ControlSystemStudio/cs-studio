
/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.utility.screenshot.desy.dialog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import org.csstudio.utility.screenshot.desy.DestinationPlugin;
import org.csstudio.utility.screenshot.desy.LogbookEntry;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @version 1.0
 * @since 08.08.2012
 */
public class LogbookEntryStorage {
    
    private static final Logger LOG = LoggerFactory.getLogger(LogbookEntryStorage.class);
    
    private File dataFile;
    
    public LogbookEntryStorage() {
        Bundle bundle = DestinationPlugin.getBundleContext().getBundle();
        File dataDir = bundle.getDataFile("dialog");
        boolean success = dataDir.exists();
        if (!success) {
            success = dataDir.mkdir();
            if (!success) {
                LOG.error("The data folder for bundle {} cannot be created.", DestinationPlugin.PLUGIN_ID);
            }
        }
        if (success) {
            dataFile = bundle.getDataFile("dialog/dialog.properties");
        } else {
            dataFile = new File("./dialog.properties");
        }
    }
    
    public boolean storeLogbookEntry(LogbookEntry entry) {
        boolean success = false;    
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(dataFile));
            writer.write(entry.createPropertiesList());
            success = true;
        } catch (IOException e) {
            success = false;
        } finally {
            if (writer!=null) {try{writer.close();}catch(Exception e){/*Ignore Me*/}}
        }
        return success;
    }

    public LogbookEntry readLogbookEntry() {
        LogbookEntry entry = new LogbookEntry();
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(dataFile));
            if (prop.containsKey("LOGBOOKNAME")) {
                entry.setLogbookName(prop.getProperty("LOGBOOKNAME"));
                prop.remove("LOGBOOKNAME");
            }
            Enumeration<?> keys = prop.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                entry.setLogbookProperty(key, prop.getProperty(key));
            }
        } catch (FileNotFoundException e) {
            LOG.info("[*** FileNotFoundException ***]: The file does not exist yet, but will be created.");
            entry = new LogbookEntry();
        } catch (IOException e) {
            LOG.error("[*** IOException ***]: {}", e.getMessage());
            entry = new LogbookEntry();
        }

        return entry;
    }
}
