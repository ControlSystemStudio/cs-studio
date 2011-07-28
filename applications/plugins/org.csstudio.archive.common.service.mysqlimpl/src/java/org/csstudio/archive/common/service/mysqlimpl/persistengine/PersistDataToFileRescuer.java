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
package org.csstudio.archive.common.service.mysqlimpl.persistengine;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.util.AbstractToFileDataRescuer;
import org.csstudio.archive.common.service.util.DataRescueException;
import org.csstudio.archive.common.service.util.DataRescueResult;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

/**
 * Implements the data rescue of the given SQL statement strings to a file with .sql
 * suffix.
 *
 * @author bknerr
 * @since 11.04.2011
 */
public class PersistDataToFileRescuer extends AbstractToFileDataRescuer {

    private static final String RESCUE_FILE_PREFIX = "failed_statements";
    private static final String RESCUE_FILE_SUFFIX = ".sql";

    private final List<String> _statements;
    private final long _fileLengthInBytesThreshold;

    /**
     * Constructor.
     */
    private PersistDataToFileRescuer(@Nonnull final Iterable<String> statements,
                                     final long maxFileLengthInBytes) {
        super();
        _statements = Lists.newLinkedList(statements);
        _fileLengthInBytesThreshold = maxFileLengthInBytes;
    }

    @Nonnull
    public static PersistDataToFileRescuer with(@Nonnull final Iterable<String> statements,
                                                final long maxFileLengthInBytes) {
        return new PersistDataToFileRescuer(statements, maxFileLengthInBytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeToFile(@Nonnull final OutputStream outStream) throws IOException {

        final OutputStreamWriter writer = new OutputStreamWriter(outStream);
        for (final String statement : _statements) {
            writer.write(statement);
            writer.write("\n");
        }
        writer.close();
    }

    /**
     * {@inheritDoc}
     *
     * Creates a rescue file. If it already exists, it is checked whether it is already larger than
     * the given threshold. In this case the file is moved to a different file with the current time's
     * timestamp and a new log file is created.
     */
    @Override
    @Nonnull
    protected File createRescueFile(@Nonnull final File path) throws IOException {
        final File file = new File(path, RESCUE_FILE_PREFIX + RESCUE_FILE_SUFFIX);
        if (!file.createNewFile()) { // exists already
            if (file.length() > _fileLengthInBytesThreshold) {
                Files.move(file, new File(path,
                                          RESCUE_FILE_PREFIX +
                                          TimeInstantBuilder.fromNow().formatted(TimeInstant.STD_DATETIME_FMT_FOR_FS) +
                                          RESCUE_FILE_SUFFIX));
                file.createNewFile();
            }
        }
        return file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean determineAppendPolicy() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected DataRescueResult handleExceptionForRescueResult(@Nonnull final Exception e) throws DataRescueException {
        throw new DataRescueException("Mmh", e);
    }

}
