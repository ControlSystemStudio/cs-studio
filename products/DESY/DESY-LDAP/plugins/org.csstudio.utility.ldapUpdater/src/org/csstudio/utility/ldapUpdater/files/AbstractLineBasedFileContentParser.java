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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;

/**
 * A line based file parser.
 * Reading in a file in a {@link BufferedReader} and processing the file line by line.
 * Implementors should override {@link AbstractLineBasedFileContentParser#processLine(String)}.
 *
 * @author bknerr
 * @since 28.04.2011
 */
public abstract class AbstractLineBasedFileContentParser {

    private static final Logger LOG =
            CentralLogger.getInstance().getLogger(AbstractLineBasedFileContentParser.class);

    /**
     * Constructor.
     */
    protected AbstractLineBasedFileContentParser() {
        // Empty
    }

    /**
     * Parses a given file line by line, invoking
     * {@link AbstractLineBasedFileContentParser#processLine(String)} on each one.
     *
     * @param filePath
     * @throws IOException while parsing line by line
     * @throws IllegalArgumentException if filePath is not a file or could not be found
     */
    public void parseFile(@Nonnull final File filePath) throws IOException {
        if (!filePath.exists()) {
            throw new FileNotFoundException(filePath + "'s contents cannot be parsed per line, it doesn't exist!");

        }
        if (!filePath.isFile()) {
            throw new IllegalArgumentException(filePath + "'s contents cannot be parsed per line." +
                                               " It is not a file!");
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = br.readLine()) != null)   {
                processLine(line);
            }

        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (final IOException e) {
                   LOG.warn("Buffered reader could not be closed properly after line based file parsing.", e);
                }
            }
        }
    }

    /**
     * Invoked on parsing each line.
     * @param line the currently processed line of the file
     */
    protected abstract void processLine(@Nonnull final String line);
}
