/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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

import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreference.HISTORY_DAT_FILEPATH;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * Class to access the dedicated history file that holds the time stamps of the last update times
 * for any IOC file.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 13.04.2010
 *
 * @deprecated 'Last modification' information shall be replaced by a time stamp per IOC sticky in
 * the LDAP persistence layer.
 */
@Deprecated
public class HistoryFileAccess {

    private static final Logger LOG = LoggerFactory.getLogger(HistoryFileAccess.class);

    /**
     * Constructor.
     */
    public HistoryFileAccess () {
        // Empty
    }

    /**
     * Reads the history file and extracts the time stamp information
     * @return a model of the file contents
     */
    @Nonnull
    public HistoryFileContentModel readFile() {

        HistoryFileContentModel model = new HistoryFileContentModel();
        BufferedReader fr = null;

        final File filePath = HISTORY_DAT_FILEPATH.getValue();
        try {
            fr = new BufferedReader(new FileReader(filePath));

            model = processLineByLine(model, fr);
            fr.close();
        } catch (final FileNotFoundException e) {
            LOG.error ("Error : File not Found(r) : {}", filePath );
        } catch (final IOException e) {
            LOG.error ("I/O-Exception while handling {}", filePath );
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (final IOException e) {
                LOG.error ("I/O-Exception while closing " + filePath );
            }
        }
        LOG.info("IOC names in history-file : " + model.getEntrySet().size());

        return model;

    }

    @Nonnull
    private HistoryFileContentModel processLineByLine(@Nonnull final HistoryFileContentModel model,
                                                      @Nonnull final BufferedReader fr) throws IOException {
        HistoryFileContentModel newModel = model;
        String line;
        while ((line = fr.readLine()) != null) {
            if (line.length() > 0) {
                final Pattern comment = Pattern.compile("\\s*#.*");
                final Matcher commentMatcher = comment.matcher(line);
                if (commentMatcher.matches()) {
                    continue;
                }
                final Pattern p = Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\S+).*");
                // matches any rows with at least 3 columns separated by spaces
                final Matcher m = p.matcher(line);
                if(!m.matches()) {
                    final String emsg = "Error during file parsing in " + HISTORY_DAT_FILEPATH.getValue() + ", row: " + "\"" + line + "\"";
                    LOG.error(emsg);
                    throw new RuntimeException(emsg);
                }
                newModel = updateRecordTimeStampsInModel(m.group(1),
                                                         TimeInstantBuilder.fromSeconds(Long.parseLong(m.group(3))),
                                                         model);
            }
        }
        return newModel;
    }

    @Nonnull
    private HistoryFileContentModel updateRecordTimeStampsInModel(@Nonnull final String record,
                                                                  @Nonnull final TimeInstant timeInstant,
                                                                  @Nonnull final HistoryFileContentModel model) {
        final TimeInstant storedLastUpdated = model.getTimeForRecord(record);
        if (storedLastUpdated == null || storedLastUpdated.isBefore(timeInstant)) {
            model.setEntry(record, timeInstant);
        }
        return model;
    }

    /**
     * append a line to the history file.
     *
     * @param iocName the name of ioc to be inserted in the history file
     * @param numOfRecordsWritten .
     * @param numOfRecordsInFile .
     * @param numOfRecordsInLDAP .
     */
    public static void appendLineToHistfile(@Nonnull final String iocName,
                                            final int numOfRecordsWritten,
                                            final int numOfRecordsInFile,
                                            final int numOfRecordsInLDAP) {

        FileWriter fw;
        try {
            fw = new FileWriter(HISTORY_DAT_FILEPATH.getValue(), true);


            final TimeInstant now = TimeInstantBuilder.fromNow();
            final String line = String.format("%1$-20sxxx%2$15s   %3$s   %4$-12s(%5$s)%6$s",
                                              iocName,
                                              now.getMillis(),
                                              now.formatted(),
                                              String.valueOf(numOfRecordsWritten + "/" + numOfRecordsInFile),
                                              String.valueOf(numOfRecordsInLDAP),
                                              System.getProperty("line.separator"));

            fw.append ( line );
            fw.flush();
            fw.close();
        } catch (final IOException e) {
            LOG.error("I/O-Exception while trying to append a line to {}", HISTORY_DAT_FILEPATH.getValue());
        }
    }
}

