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
 */
package org.csstudio.utility.ldapUpdater.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.domain.desy.file.FilteredRecursiveFilePathParser;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.model.IOC;
import org.csstudio.utility.ldapUpdater.UpdaterLdapConstants;

import com.google.common.collect.Maps;

/**
 * Traverses a directory recursively, filters for files ending on
 * {@link UpdaterLdapConstants#RECORDS_FILE_SUFFIX} and extracts the timestamp info of the
 * files last modification.
 * On traversal a map from the record file name (without suffix) to an {@link IOC} instance is
 * created.
 *
 * @author bknerr
 * @since 28.04.2011
 */
public class RecordsFileTimeStampParser extends FilteredRecursiveFilePathParser {

    private static final Logger LOG =
            CentralLogger.getInstance().getLogger(RecordsFileTimeStampParser.class);

    private final Map<String, IOC> _iocFileMap = Maps.newHashMap();

    /**
     * Constructor.
     * @param i
     * @param file
     * @throws FileNotFoundException
     */
    public RecordsFileTimeStampParser(@Nonnull final File dir, final int finalDepth) throws FileNotFoundException {
        super(new SuffixBasedFileFilter(UpdaterLdapConstants.RECORDS_FILE_SUFFIX, finalDepth));
        startTraversal(dir, finalDepth);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void processFilteredFile(@Nonnull final File file,
                                                final int currentDepth) {
        final TimeInstant dateTime = TimeInstantBuilder.fromMillis(file.lastModified());
        final String fileName = file.getName();

        final String iocName = fileName.replace(UpdaterLdapConstants.RECORDS_FILE_SUFFIX, "");
        _iocFileMap.put(iocName, new IOC(iocName, dateTime));

        LOG.debug("File found for IOC: " + iocName);
    }

    @CheckForNull
    public IOC getIOCByName(@Nonnull final String iocName) {
        return _iocFileMap.get(iocName);
    }

    @Nonnull
    public Map<String, IOC> getIocFileMap() {
        return _iocFileMap;
    }

}
