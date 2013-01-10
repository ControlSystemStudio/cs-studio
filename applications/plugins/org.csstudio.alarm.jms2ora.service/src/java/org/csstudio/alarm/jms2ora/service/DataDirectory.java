
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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 *
 * @author mmoeller
 * @version 1.0
 * @since 23.08.2011
 */
public class DataDirectory {

    /** The path of the data directory for the serialized messages */
    private String dataDirectory;

    /** The alternative path of the data directory for the serialized messages */
    private String dataAltDirectory;

    /**
     * @param dataDir - The path of the data directory for the serialized messages
     */
    public DataDirectory(@Nonnull final String dataDir, @CheckForNull final String dataAltDir) {

        dataDirectory = null;
        final File file = new File(dataDir);
        if (!file.exists()) {
            final boolean success = file.mkdirs();
            if (success) {
                dataDirectory = file.getAbsolutePath();
                if (!dataDirectory.endsWith(File.separator)) {
                    dataDirectory = dataDirectory + File.separator;
                }
            }
        } else {
            dataDirectory = file.getAbsolutePath();
            if (!dataDirectory.endsWith(File.separator)) {
                dataDirectory = dataDirectory + File.separator;
            }
        }
        
        dataAltDirectory = null;
        if (dataAltDir != null) {
            if (!dataAltDir.trim().isEmpty()) {
                final File altFile = new File(dataAltDir);
                if (!altFile.exists()) {
                    final boolean success = altFile.mkdirs();
                    if (success) {
                        dataAltDirectory = altFile.getAbsolutePath();
                        if (!dataAltDirectory.endsWith(File.separator)) {
                            dataAltDirectory = dataAltDirectory + File.separator;
                        }
                    }
                } else {
                    dataAltDirectory = altFile.getAbsolutePath();
                    if (!dataAltDirectory.endsWith(File.separator)) {
                        dataAltDirectory = dataAltDirectory + File.separator;
                    }
                }
            }
        }
    }

    public DataDirectory(@Nonnull final String dataDir) {
        this(dataDir, null);
    }
    
    @Nonnull
    public final File getDataDirectory() throws DataDirectoryException {
        if (dataDirectory == null) {
            throw new DataDirectoryException("The data directory does not exist.");
        }
        return new File(dataDirectory);
    }

    @Nonnull
    public final String getDataDirectoryAsString() throws DataDirectoryException {
        if (dataDirectory == null) {
            throw new DataDirectoryException("The data directory does not exist.");
        }
        return dataDirectory;
    }

    public final boolean existsDataDirectory() {
        boolean exists;
        try {
            exists = getDataDirectory().exists();
        } catch (final DataDirectoryException dde) {
            exists = false;
        }
        return exists;
    }
    
    @Nonnull
    public final File getAltDataDirectory() throws DataDirectoryException {
        if (dataAltDirectory == null) {
            throw new DataDirectoryException("The alternative data directory does not exist.");
        }
        return new File(dataAltDirectory);
    }

    @Nonnull
    public final String getAltDataDirectoryAsString() throws DataDirectoryException {
        if (dataAltDirectory == null) {
            throw new DataDirectoryException("The alternative data directory does not exist.");
        }
        return dataAltDirectory;
    }

    public final boolean existsAltDataDirectory() {
        boolean exists;
        try {
            exists = getAltDataDirectory().exists();
        } catch (final DataDirectoryException dde) {
            exists = false;
        }
        return exists;
    }
}
