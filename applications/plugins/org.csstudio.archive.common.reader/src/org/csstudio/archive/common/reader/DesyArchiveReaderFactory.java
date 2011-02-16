/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.archive.common.reader;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.IArchiveReaderService;
import org.csstudio.archive.common.service.requesttypes.IArchiveRequestType;
import org.csstudio.archivereader.ArchiveInfo;
import org.csstudio.archivereader.ArchiveReader;
import org.csstudio.archivereader.ArchiveReaderFactory;
import org.csstudio.archivereader.ValueIterator;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.service.osgi.OsgiServiceUnavailableException;

import com.google.common.collect.ImmutableSet;

/**
 * The plugin.xml registers this factory for ArchiveReaders
 * when the URL prefix indicates this implementation.
 *
 * @author bknerr
 * @since 22.12.2010
 */
// CHECKSTYLE:OFF Due to misleading 'Class X must be declared as 'abstract' warning'.
//                Seems to be a CS 5.3 problem
public final class DesyArchiveReaderFactory implements ArchiveReaderFactory {
// CHECKSTYLE:ON

    /**
     * The DESY archive reader implementation.
     *
     * @author bknerr
     * @since 03.02.2011
     */
    private static final class DesyArchiveReader implements ArchiveReader {
        /**
         * Constructor.
         */
        public DesyArchiveReader() {
            // Empty
        }

        @Override
        public String getServerName() {
            return "Which is not in ArchiveInfo already?";
        }

        @Override
        public String getURL() {
            return "The URL should not be read-only at most by the ArchiveReader client!";
        }

        @Override
        public String getDescription() {
            return "Description of what now?";
        }

        @Override
        public int getVersion() {
            return 10;
        }

        @Override
        public ArchiveInfo[] getArchiveInfos() {
            return new ArchiveInfo[] {new ArchiveInfo("Desy Archive", "Optimized MySql", 5)};
        }

        @Override
        public String[] getNamesByPattern(final int key, final String globPattern) throws Exception {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String[] getNamesByRegExp(final int key, final String regExp) throws Exception {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ValueIterator getRawValues(final int key,
                                          final String name,
                                          final ITimestamp start,
                                          final ITimestamp end) throws Exception {



            return new DesyArchiveValueIterator(name, start, end, getRawType());
        }

        private IArchiveRequestType getRawType() throws OsgiServiceUnavailableException {
            final IArchiveReaderService s = Activator.getDefault().getArchiveReaderService();
            final ImmutableSet<IArchiveRequestType> types = s.getRequestTypes();
            for (final IArchiveRequestType type : types) {
                // this should have been decided type safe by the client app (typically the user)...
                if (type.getTypeIdentifier().equals("RAW")) {
                    return type;
                }
            }
            return null;
        }

        @Override
        public ValueIterator getOptimizedValues(final int key,
                                                final String name,
                                                final ITimestamp start,
                                                final ITimestamp end,
                                                final int count) throws Exception {
            return new DesyArchiveValueIterator(name, start, end, null);
        }

        @Override
        public void cancel() {
            // TODO Auto-generated method stub
        }

        @Override
        public void close() {
            // TODO Auto-generated method stub
        }
    }

    /**
     * Constructor.
     */
    public DesyArchiveReaderFactory() {
        // Empty
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArchiveReader getArchiveReader(@Nonnull final String url) throws Exception {
        return new DesyArchiveReader();
    }
}
