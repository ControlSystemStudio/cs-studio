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

package org.csstudio.utility.ldapUpdater;

import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceKey.LDAP_HIST_PATH;
import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferences.getValueFromPreferences;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldapUpdater.model.HistoryFileContentModel;

public class HistoryFileAccess {
    
    public static final String HISTORY_DAT_FILE = "history.dat";
    
    private final Logger LOGGER = CentralLogger.getInstance().getLogger(this);
    
    /**
     * Constructor.
     */
    public HistoryFileAccess () {
        // Empty
    }
    
    public HistoryFileContentModel readFile() {
        
        HistoryFileContentModel model = new HistoryFileContentModel();
        BufferedReader fr;
        
        try {
            final String path = getValueFromPreferences(LDAP_HIST_PATH);
            fr = new BufferedReader(new FileReader(path + HISTORY_DAT_FILE ));
            
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
                        final String emsg = "Error during file parsing in " + HISTORY_DAT_FILE + ", row: " + "\"" + line + "\"" ;
                        LOGGER.error(emsg);
                        throw new RuntimeException(emsg);
                    }
                    model = storeRecentRecordEntry(m.group(1), Long.parseLong(m.group(3)), model);
                }
            }
        } catch (final FileNotFoundException e) {
            LOGGER.error ("Error : File not Found(r) : " + LDAP_HIST_PATH.getDescription() + HISTORY_DAT_FILE );
        } catch (final IOException e) {
            LOGGER.error ("I/O-Exception while handling " + LDAP_HIST_PATH.getDescription() + HISTORY_DAT_FILE );
        }
        
        LOGGER.info("IOC names in history-file : " + model.getEntrySet().size());
        
        return model;
        
    }
    
    private HistoryFileContentModel storeRecentRecordEntry(final String record, final Long lastUpdated, final HistoryFileContentModel model) {
        final Long storedLastUpdated = model.getTimeForRecord(record);
        if ((storedLastUpdated == null) || (storedLastUpdated < lastUpdated)) {
            model.setEntry(record, lastUpdated);
        }
        return model;
    }
    
    /**
     * append a line to the history file.
     * 
     * @param iocName the name of ioc to be inserted in the history file
     * @param numOfRecordsWritten
     * @param numOfRecordsInFile
     * @param numOfRecordsInLDAP
     * @throws IOException when the history file could not be accessed
     */
    public static void appendLineToHistfile(final String iocName,
                                            final int numOfRecordsWritten,
                                            final int numOfRecordsInFile,
                                            final int numOfRecordsInLDAP) throws IOException {
        
        final String histFilePath = getValueFromPreferences(LDAP_HIST_PATH) + HistoryFileAccess.HISTORY_DAT_FILE;
        final FileWriter fw =
            new FileWriter(histFilePath, true);
        
        final long now = System.currentTimeMillis();
        final String dateTime = LdapUpdater.convertMillisToDateTimeString(now, LdapUpdater.DATETIME_FORMAT);
        
        final String line = String.format("%1$-20sxxx%2$15s   %3$s   %4$-12s(%5$s)%6$s",
                                          iocName,
                                          String.valueOf(now / 1000),
                                          dateTime,
                                          String.valueOf(numOfRecordsWritten + "/" + numOfRecordsInFile),
                                          String.valueOf(numOfRecordsInLDAP),
                                          System.getProperty("line.separator" ));
        
        fw.append ( line );
        fw.flush();
        fw.close();
    }
    
}
