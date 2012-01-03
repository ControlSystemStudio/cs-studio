/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id$
 */
package org.csstudio.utility.epicsDataBaseCompare.ui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 27.10.2009
 */
public class EpicsDBParser {

    @Nonnull
    public EpicsDBFile parseFile(@Nonnull final String file) throws IOException {
        final EpicsDBFile epicsDBFile = new EpicsDBFile(file);
        final BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        EpicsRecord epicsRecord = null;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            epicsRecord = parseLine(epicsDBFile, line, epicsRecord);
        }
        br.close();
        return epicsDBFile;
    }

    @Nonnull
    private EpicsRecord parseLine(@Nonnull final EpicsDBFile epicsDBFile,
                                  @Nonnull final String line,
                                  @Nonnull final EpicsRecord epicsRecord) {
        EpicsRecord myEpicsRecord = epicsRecord;
        // CHECKSTYLE OFF: EmptyBlock
        if (line.startsWith("#")) {
            // CHECKSTYLE ON: EmptyBlock
            // Ignore! Is a command
        } else if (line.startsWith("record")) {
            final String[] split = line.split("[\"\\(]");
            if (split.length > 3) {
                myEpicsRecord = new EpicsRecord(epicsDBFile, split[2], split[1].replaceAll(",", ""));
            }
        } else if (isValidEndOfRecord(line, epicsRecord)) {
            epicsDBFile.add(epicsRecord);
        } else if (!line.isEmpty()) {
            handleField(line, epicsRecord);
        }
        return myEpicsRecord;
    }

    private void handleField(@Nonnull final String line, @Nonnull final EpicsRecord epicsRecord) {
        final String[] split0 = line.split("[\"]");
        final String[] split1 = split0[0].split("[\\(,]");
        if (split0.length > 1 && split1.length > 1) {
            epicsRecord.setField(split1[1], split0[1]);
        } else {
            System.out.println("No vaild Field: " + line);
        }
    }

    private boolean isValidEndOfRecord(@Nonnull final String line,
                                       @CheckForNull final EpicsRecord epicsRecord) {
        final String cleanLine = line.trim();
        return cleanLine.startsWith("}") && epicsRecord != null && !epicsRecord.isEmpty();
    }

}
