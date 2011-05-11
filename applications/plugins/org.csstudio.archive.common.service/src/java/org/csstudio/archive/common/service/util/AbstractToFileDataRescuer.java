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
package org.csstudio.archive.common.service.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.slf4j.LoggerFactory;

/**
 * Abstract data rescue class to write files to a safe location.
 *
 * @author bknerr
 * @since Mar 28, 2011
 */
public abstract class AbstractToFileDataRescuer {

    private static final Logger LOG = LoggerFactory
            .getLogger(AbstractToFileDataRescuer.class);

    private File _rescueDir;
    private File _rescueFilePath;

    private TimeInstant _timeStamp;

    protected AbstractToFileDataRescuer() {
        setTimeStamp(TimeInstantBuilder.fromNow());
    }

    @Nonnull
    public DataRescueResult rescue() throws DataRescueException {
        OutputStream output = null;
        try {
            output = createOutputStream();
            writeToFile(output);
            output.flush();
        } catch (final Exception e) {
            return handleExceptionForRescueResult(e);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (final IOException e) {
                    LOG.warn("Closing of output stream for data rescue file failed.", e);
                }
            }
        }
        return DataRescueResult.success(_rescueFilePath.toString(), _timeStamp);
    }

    @Nonnull
    public AbstractToFileDataRescuer to(@Nonnull final File rescueDir) {
        _rescueDir = rescueDir;
        return this;
    }

    protected abstract void writeToFile(@Nonnull final OutputStream output) throws IOException;
    @Nonnull
    protected abstract String composeRescueFileName();
    @Nonnull
    protected abstract DataRescueResult handleExceptionForRescueResult(@Nonnull final Exception e) throws DataRescueException;

    @Nonnull
    public AbstractToFileDataRescuer at(@Nonnull final TimeInstant time) {
        setTimeStamp(time);
        return this;
    }

    @Nonnull
    private OutputStream createOutputStream() throws IOException {
        final String fileName = composeRescueFileName();
        final File path = _rescueDir;

        _rescueFilePath = new File(path, fileName);

        final OutputStream ostream = new FileOutputStream(_rescueFilePath);
        final OutputStream buffer = new BufferedOutputStream(ostream);
        return buffer;
    }


    protected void setTimeStamp(@Nonnull final TimeInstant timeStamp) {
        _timeStamp = timeStamp;
    }

    @Nonnull
    protected TimeInstant getTimeStamp() {
        return _timeStamp;
    }


}
