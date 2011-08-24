
/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
 */

package org.csstudio.alarm.jms2ora.service;

import java.io.File;

/**
 * 
 * @author mmoeller
 * @version 1.0
 * @since 23.08.2011
 */
public class DataDirectory {
    
    /** The path of the data directory for the serialized messages */
    private String dataDirectory;
    
    /**
     * Constructor.
     * 
     * @param dataDir - The path of the data directory for the serialized messages
     */
    public DataDirectory(String dataDir) {
        
        dataDirectory = null;
        File file = new File(dataDir);
        if (file.exists() == false) {
            boolean success = file.mkdirs();
            if (success) {
                dataDirectory = file.getAbsolutePath();
                if (dataDirectory.endsWith(File.pathSeparator) == false) {
                    dataDirectory = dataDirectory + File.pathSeparator;
                }
            }
        }
    }

    public File getDataDirectory() throws DataDirectoryException {
        if (dataDirectory == null) {
            throw new DataDirectoryException("The data directory does not exist.");
        }
        return new File(dataDirectory);
    }
    
    public String getDataDirectoryAsString() throws DataDirectoryException {
        if (dataDirectory == null) {
            throw new DataDirectoryException("The data directory does not exist.");
        }
        return dataDirectory;
    }

    public boolean existsDataDirectory() throws DataDirectoryException {
        return getDataDirectory().exists();
    }
}
