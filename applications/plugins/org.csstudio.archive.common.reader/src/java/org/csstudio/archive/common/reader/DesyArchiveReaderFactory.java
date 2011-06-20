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

import java.util.Collection;
import java.util.regex.Pattern;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.common.reader.facade.ServiceProvider;
import org.csstudio.archive.common.requesttype.IArchiveRequestType;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archivereader.ArchiveInfo;
import org.csstudio.archivereader.ArchiveReader;
import org.csstudio.archivereader.ArchiveReaderFactory;
import org.csstudio.archivereader.ValueIterator;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.domain.desy.regexp.SimplePattern;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;

import com.google.common.base.Strings;
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

    static final ValueIterator EMPTY_ITER = new ValueIterator() {
        @Override
        @CheckForNull
        public IValue next() throws Exception {
            return null;
        }
        @Override
        public boolean hasNext() {
            return false;
        }
        @Override
        public void close() {
            // EMPTY
        }
    };

    /**
     * The DESY archive reader implementation.
     *
     * @author bknerr
     * @since 03.02.2011
     */
    private static final class DesyArchiveReader implements ArchiveReader {

        private final ServiceProvider _provider;

        /**
         * Constructor.
         * @param serviceProvider
         */
        public DesyArchiveReader(@Nonnull final ServiceProvider serviceProvider) {
            _provider = serviceProvider;
        }

        @Override
        @Nonnull
        public String getServerName() {
            return "The server name is already in the ArchiveInfo field!";
        }

        @Override
        @Nonnull
        public String getURL() {
            return "The URL should not be modifiable and rather not be exported at all!";
        }

        @Override
        @Nonnull
        public String getDescription() {
            return "The dedicated DESY archive reader is more like a databrowser backend.";
        }

        @Override
        public int getVersion() {
            return 10;
        }

        @Override
        @Nonnull
        public ArchiveInfo[] getArchiveInfos() {
            return new ArchiveInfo[] {new ArchiveInfo("Desy Archive",
                                                      "Optimized MySql",
                                                      5)};
        }

        @Override
        @Nonnull
        public String[] getNamesByPattern(final int key, @Nonnull final String globPattern) throws Exception {

            return getNamesByRegExp(key, SimplePattern.toRegExp(globPattern));
        }

        @Override
        @Nonnull
        public String[] getNamesByRegExp(final int key, @Nonnull final String regExp) throws Exception {

            // FIXME (kasemir) : Empty string is announced as 'match all' by the databrowser, but the
            // databrowser does not supply then the matching regexp to the interface.
            // We are forced to handle that here, where are not supposed to know that in the first place.
            // Any other app using this interface should better know this detail, too...
            final Pattern pattern = Pattern.compile(Strings.isNullOrEmpty(regExp) ? ".*" : regExp);

            final IArchiveReaderFacade service = _provider.getReaderFacade();
            final Collection<String> names = service.getChannelsByNamePattern(pattern);
            return names.toArray(new String[]{});
        }

        @Override
        @Nonnull
        public ValueIterator getRawValues(final int key,
                                          @Nonnull final String name,
                                          @Nonnull final ITimestamp start,
                                          @Nonnull final ITimestamp end) throws Exception {

            final TimeInstant s = BaseTypeConversionSupport.toTimeInstant(start);
            final TimeInstant e = BaseTypeConversionSupport.toTimeInstant(end);
            return new DesyArchiveValueIterator(_provider, name, s, e, getRawType());
        }


        @Override
        @Nonnull
        public ValueIterator getOptimizedValues(final int key,
                                                @Nonnull final String name,
                                                @Nonnull final ITimestamp start,
                                                @Nonnull final ITimestamp end,
                                                final int count) throws Exception {

            final TimeInstant s = BaseTypeConversionSupport.toTimeInstant(start);
            final TimeInstant e = BaseTypeConversionSupport.toTimeInstant(end);

            // Check for optimizability (base type convertible to Double)
            final IArchiveReaderFacade service = _provider.getReaderFacade();
            final IArchiveChannel channel = service.getChannelByName(name);

            if (BaseTypeConversionSupport.isDataTypeConvertibleToDouble(channel.getDataType(), "java.lang", "org.csstudio.domain.desy.epics.types")) {
                return new EquidistantTimeBinsIterator<Object>(_provider, name, s, e, null, count);
            }
            return EMPTY_ITER;
        }

        @Override
        public void cancel() {
            // TODO Auto-generated method stub
        }

        @Override
        public void close() {
            // TODO Auto-generated method stub
        }

        /**
         * Helper method
         * @return
         * @throws OsgiServiceUnavailableException
         */
        @CheckForNull
        private IArchiveRequestType getRawType() throws OsgiServiceUnavailableException {
            final IArchiveReaderFacade s = _provider.getReaderFacade();
            final ImmutableSet<IArchiveRequestType> types = s.getRequestTypes();
            for (final IArchiveRequestType type : types) {
                // this should have been decided type safe by the client app (typically the user)...
                if (type.getTypeIdentifier().equals("RAW")) {
                    return type;
                }
            }
            return null;
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
    @Nonnull
    public ArchiveReader getArchiveReader(@Nonnull final String url) throws Exception {
        return new DesyArchiveReader(new ServiceProvider());
    }
}
