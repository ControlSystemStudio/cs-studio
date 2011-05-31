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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.util.AbstractToFileDataRescuer;
import org.csstudio.archive.common.service.util.DataRescueException;
import org.csstudio.archive.common.service.util.DataRescueResult;
import org.csstudio.domain.desy.time.TimeInstant;

import com.google.common.collect.Lists;

/**
 * Implements the data rescue of the given SQL statement strings to a file with .sql
 * suffix.
 *
 * @author bknerr
 * @since 11.04.2011
 */
public class PersistDataToFileRescuer extends AbstractToFileDataRescuer {

    private static final String FILE_SUFFIX = ".sql";

    private final List<String> _statements;

    /**
     * Constructor.
     */
    PersistDataToFileRescuer(@Nonnull final List<String> statements) {
        super();
        _statements = Lists.newLinkedList(statements);
    }

    @Nonnull
    public static PersistDataToFileRescuer with(@Nonnull final List<String> statements) {
        return new PersistDataToFileRescuer(statements);
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
     */
    @Override
    @Nonnull
    protected String composeRescueFileName() {
        return "rescue_" + getTimeStamp().formatted(TimeInstant.STD_DATETIME_FMT_FOR_FS) + FILE_SUFFIX;
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
