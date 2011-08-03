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

package org.csstudio.utility.ldapupdater.files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.utility.ldapupdater.preferences.LdapUpdaterPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

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

    /**
     * Line Processor for the history file.
     *
     * @author bknerr
     * @since 03.08.2011
     */
    private final class HistoryFileLineProcessor implements LineProcessor<HistoryFileContentModel> {
        private final Logger _log = LoggerFactory
                .getLogger(HistoryFileAccess.HistoryFileLineProcessor.class);
        private HistoryFileContentModel _model = new HistoryFileContentModel();

        private final LdapUpdaterPreferencesService _histPrefsService;

        /**
         * Constructor.
         */
        public HistoryFileLineProcessor(@Nonnull final LdapUpdaterPreferencesService prefs) {
            _histPrefsService = prefs;
        }

        @Override
        public boolean processLine(@Nonnull final String line) throws IOException {

            if (isNotEmptyOrComment(line)) {
                final Pattern p = Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\S+).*");
                // matches any rows with at least 3 columns separated by spaces
                final Matcher m = p.matcher(line);
                if(!m.matches()) {
                    _log.error("Error during file parsing in {}, row: " + "{}", _histPrefsService.getHistoryDatFilePath(), line);
                } else {
                    _model = updateRecordTimeStampsInModel(m.group(1),
                                                           TimeInstantBuilder.fromSeconds(Long.parseLong(m.group(3))),
                                                           _model);
                }
            }
            return true;
        }

        private boolean isNotEmptyOrComment(@Nonnull final String line) {
            return line.length() > 0 && Pattern.matches("\\s*#.*", line);
        }

        @Override
        @Nonnull
        public HistoryFileContentModel getResult() {
            return _model;
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(HistoryFileAccess.class);
    private final LdapUpdaterPreferencesService _prefsService;

    /**
     * Constructor.
     */
    public HistoryFileAccess (@Nonnull final LdapUpdaterPreferencesService prefsService) {
        _prefsService = prefsService;
    }

    /**
     * Reads the history file and extracts the time stamp information
     * @return a model of the file contents
     */
    @CheckForNull
    public HistoryFileContentModel readFile() {

        final File file = _prefsService.getHistoryDatFilePath();
        HistoryFileContentModel model = null;
        try {
            model = Files.readLines(file,
                                    Charset.defaultCharset(),
                                    new HistoryFileLineProcessor(_prefsService));
            LOG.info("IOC names in history-file : {}",  model.getEntrySet().size());

        } catch (final IOException e) {
            LOG.error ("I/O-Exception while handling {}", file.getAbsolutePath() );
        }

        return model;

    }

    @Nonnull HistoryFileContentModel updateRecordTimeStampsInModel(@Nonnull final String record,
                                                                  @Nonnull final TimeInstant timeInstant,
                                                                  @Nonnull final HistoryFileContentModel model) {
        final TimeInstant storedLastUpdated = model.getTimeForRecord(record);
        if (storedLastUpdated == null || storedLastUpdated.isBefore(timeInstant)) {
            model.setEntry(record, timeInstant);
        }
        return model;
    }

    /**
     * Append a line to the history file.
     *
     * @param iocName the name of ioc to be inserted in the history file
     * @param numOfRecordsWritten .
     * @param numOfRecordsInFile .
     * @param numOfRecordsInLDAP .
     */
    public static void appendLineToHistfile(@Nonnull final File historyFilePath,
                                            @Nonnull final String iocName,
                                            final int numOfRecordsWritten,
                                            final int numOfRecordsInFile,
                                            final int numOfRecordsInLDAP) {

        try {
            final TimeInstant now = TimeInstantBuilder.fromNow();
            final String line = String.format("%1$-20sxxx%2$15s   %3$s   %4$-12s(%5$s)%6$s",
                                              iocName,
                                              now.getMillis(),
                                              now.formatted(),
                                              String.valueOf(numOfRecordsWritten + "/" + numOfRecordsInFile),
                                              String.valueOf(numOfRecordsInLDAP),
                                              System.getProperty("line.separator"));
            Files.append(line, historyFilePath, Charset.defaultCharset());
        } catch (final IOException e) {
            LOG.error("I/O-Exception while trying to append a line to {}", historyFilePath.getAbsoluteFile());
        }
    }
}

