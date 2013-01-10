
/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 */

package org.csstudio.archive.sdds.server.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.archive.sdds.server.SddsServerActivator;
import org.csstudio.archive.sdds.server.conversion.SampleParameters;
import org.csstudio.archive.sdds.server.data.EpicsRecordData;
import org.csstudio.archive.sdds.server.data.RecordDataCollection;
import org.csstudio.archive.sdds.server.internal.ServerPreferenceKey;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * The class reads a specified SDDS file and offers a method to get the data from the file.
 *
 * @author Markus Moeller
 * @version 1.0
 *
 */
public class SddsFileReader {

    /** The logger for this class */
    private static final Logger LOG = LoggerFactory.getLogger(SddsFileReader.class);

    /** The path to the data files */
    private final ArchiveLocation archiveLocation;

    /** Indicates if byte order is little endian */
    private final boolean littleEndian;

    private final long maxFileSize;

    private final boolean ignoreBigFiles;

    private final boolean useCompressedFiles;

    /**
     * Constructor that gets a string containing the path to the data files.
     *
     * @throws DataPathNotFoundException
     */
    public SddsFileReader(@Nonnull final String dataSourceFile) throws DataPathNotFoundException {
        final IPreferencesService pref = Platform.getPreferencesService();
        if (pref != null) {
            littleEndian = pref.getBoolean(SddsServerActivator.PLUGIN_ID,
                                           ServerPreferenceKey.P_SDDS_LITTLE_ENDIAN,
                                           false, null);
            LOG.info("Assuming little endian files: {}", littleEndian);
            ignoreBigFiles = pref.getBoolean(SddsServerActivator.PLUGIN_ID,
                                             ServerPreferenceKey.P_IGNORE_BIG_FILES,
                                             true, null);
            LOG.info("Ignoring big files: {}", ignoreBigFiles);
            maxFileSize = pref.getLong(SddsServerActivator.PLUGIN_ID,
                                       ServerPreferenceKey.P_MAX_FILE_SIZE,
                                       5242880L, null);
            LOG.info("Max. file size: {}", maxFileSize);
            useCompressedFiles = pref.getBoolean(SddsServerActivator.PLUGIN_ID,
                                                 ServerPreferenceKey.P_USE_COMPRESSED_FILES,
                                                 true, null);
            LOG.info("Use compressed files: {}", ignoreBigFiles);

        } else {
            LOG.warn("Cannot read endianness. Using default: BIG ENDIAN.");
            littleEndian = false;
            LOG.warn("Cannot read if I should ignore big files. Using default: true.");
            ignoreBigFiles = true;
            LOG.warn("Cannot read max. file size. Using default: 5MB");
            maxFileSize = 5242880L;
            LOG.warn("Cannot read if I should use compressed files. Using default: true.");
            useCompressedFiles = true;
        }
        archiveLocation = new ArchiveLocation(dataSourceFile);
    }

    /**
     * Reads the data of a specified SDDS data file.
     *
     * @param recordName
     * @param startTimeInS
     * @param endTimeInS
     * @return Array of data objects
     */
    @Nonnull
    public final RecordDataCollection readData(@Nonnull final String recordName,
                                         final long startTimeInS,
                                         final long endTimeInS) throws SddsFileLengthException {

        final String[] filePaths = archiveLocation.getAllPaths(1000L * startTimeInS,
                                                               1000L * endTimeInS);

        final long st = System.currentTimeMillis();

        // TODO: First check which paths do exist, then create the arrays
        final ThreadGroup threadGroup = new ThreadGroup("DataReader");
        final ArrayList<Thread> readerThread = Lists.newArrayList();
        final ArrayList<SddsDataReader> reader = Lists.newArrayList();

        for(int i = 0; i < filePaths.length; i++) {

            filePaths[i] = getCorrectFilename(filePaths[i], recordName);
            if(filePaths[i] != null) {

                // TODO: This is a hack to avoid OutOfMemory exceptions
                //       1048576 Byte = 1 MB
                final File file = new File(filePaths[i]);
                final long fileLength = file.length();

                if (LOG.isDebugEnabled()) {
                    LOG.debug(" " + filePaths[i] + " - Length: " + fileLength);
                }

                // If the file length is greater then max. allowed file size
                // AND the server have to ignore them
                // THEN DO NOT read it
                if (ignoreBigFiles && fileLength > maxFileSize) {
                    throw new SddsFileLengthException("File '" + filePaths[i] + "' too big to be read (" + fileLength + ")");
                }

                final SddsDataReader dataReader = new SddsDataReader(filePaths[i], startTimeInS, endTimeInS, littleEndian);
                reader.add(dataReader);

                final Thread thread = new Thread(threadGroup, dataReader);
                readerThread.add(thread);
                thread.start();
            }
        }

        synchronized(threadGroup) {
            while(threadGroup.activeCount() > 0) {
                try {
                    threadGroup.wait(10);
                } catch(final InterruptedException ie) {
                    LOG.warn("*** Interrupted ***");
                    Thread.currentThread().interrupt();
                }
            }
        }

        final long et = System.currentTimeMillis() - st;

        if (LOG.isDebugEnabled()) {
            LOG.debug("Finished in " + et + " Millisekunden (" + et / 1000 + " sec)");
        }

        final RecordDataCollection dataCollection = new RecordDataCollection();

        final List<EpicsRecordData> allResults = Lists.newLinkedList();
        for(final SddsDataReader o : reader) {
            if(o != null) {
                if (!o.hasCausedError()) {
                    allResults.addAll(o.getResult());
                } else {
                    LOG.warn(o.getError().toString() + ": " + o.getErrorDescription());
                }
            }
        }

        dataCollection.setData(allResults);
        if(reader.size() > 0) {
            dataCollection.setSampleParameter(reader.get(0).getSampleCtrl());
        } else {
            dataCollection.setSampleParameter(new SampleParameters());
        }

        return dataCollection;
    }

    @Nonnull
    private String getCorrectFilename(@Nonnull final String path,
                                      @Nonnull final String filename) {

        String result = null;
        if (useCompressedFiles) {
            final File file = new File(path + filename + ".cmp");
            if (file.exists()) {
                return path + filename + ".cmp";
            }
        }

        File file = new File(path + filename);
        if(!file.exists()) {
            file = null;
            file = new File(path + filename + ".gz");
            if(file.exists()) {
                result = path + filename + ".gz";
            }
        } else {
            result = path + filename;
        }

        return result;
    }

    /**
     * New start search time is '01-mmm-yyyy 00:00:00' - 1 sec .
     * Get year and month from current epoch, increase the month by one,
     * modify others and call 'mktime()' to get new start_time.
     *
     * @param dwStartTime
     * @return The last timestamp of the month
     */
    @SuppressWarnings("unused")
    private long getEndTimeOfPreviousMonth(final long dwStartTime) {

        GregorianCalendar cal = null;
        long epoch;

        cal = new GregorianCalendar();
        cal.setTimeInMillis(dwStartTime * 1000);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        epoch = cal.getTimeInMillis() / 1000 - 1;

        return epoch;
    }
}
