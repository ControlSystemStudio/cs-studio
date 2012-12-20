
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
 *
 */

package org.csstudio.archive.sdds.server.file;

import java.util.TreeSet;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.sdds.server.conversion.SampleParameters;
import org.csstudio.archive.sdds.server.data.EpicsRecordData;
import org.csstudio.archive.sdds.server.type.TypeFactory;
import org.csstudio.archive.sdds.server.type.TypeNotSupportedException;

import SDDS.java.SDDS.SDDSFile;

import com.google.common.base.Strings;

import de.desy.aapi.AapiServerError;

/**
 * @author Markus Moeller
 *
 */
public class SddsDataReader implements Runnable {

    /** Results */
    // private Vector<EpicsRecordData> result;
    private final TreeSet<EpicsRecordData> data;

    /** The SDDSFile object for reading the archive file */
    private final SDDSFile sddsFile;

    /** Holds the parameter of a PV */
    private SampleParameters sampleParameters;

    /** Path of SDDS file to read */
    private final String filePath;

    /** */
    @SuppressWarnings("unused")
    private final long startTime;

    /** */
    @SuppressWarnings("unused")
    private final long endTime;

    /** This field indicates an error */
    private AapiServerError error;

    /** Additional error description */
    private String errorDesc;

    /**
     *
     * @param path
     * @param timeStart
     * @param timeEnd
     */
    public SddsDataReader(@Nonnull final String path,
                          final long timeStart,
                          final long timeEnd, final boolean littleEndian) {

        filePath = path;
        this.startTime = timeStart;
        this.endTime = timeEnd;
        sddsFile = new SDDSFile();
        sampleParameters = new SampleParameters();
        sddsFile.setFileName(filePath);
        sddsFile.setEndian(littleEndian);
        data = new TreeSet<EpicsRecordData>(new TimeComparator());
        error = AapiServerError.NO_ERROR;
        errorDesc = "";
    }

    /**
     *
     * @return EpicsRecordData
     */
    @Nonnull
    public final EpicsRecordData[] getResultAsArray() {

        EpicsRecordData[] r = null;

        if (!data.isEmpty()) {
            r = new EpicsRecordData[data.size()];
            r = data.toArray(r);
        } else {
            r = new EpicsRecordData[0];
        }

        return r;
    }

    /**
     *
     * @return TreeSet<EpicsRecordData>
     */
    @Nonnull
    public final TreeSet<EpicsRecordData> getResult() {
        return data;
    }

    /**
     * Returns the number of data sets.
     *
     * @return Number of results
     */
    public final int getResultCount() {
        return data.size();
    }

    /**
     * Returns the object that holds the parameter values of the PV.
     *
     * @return SampleParameters
     */
    @Nonnull
    public final SampleParameters getSampleCtrl() {
        return sampleParameters;
    }

    public final boolean hasCausedError() {
        return error != AapiServerError.NO_ERROR;
    }

    @Nonnull
    public final AapiServerError getError() {
        return error;
    }

    @Nonnull
    public final String getErrorDescription() {
        return errorDesc;
    }

    /**
     *
     */
    @Override
    public final void run() {

        final boolean success = sddsFile.readFile();
        if (!success) {
            error = AapiServerError.CAN_T_OPEN_FILE;
            errorDesc = sddsFile.getErrors().trim();
            return;
        }

        final String[] list = sddsFile.getParameterNames();
        // types = sddsFile.getParameterTypes();
        sampleParameters = this.getParameters(list);

        final int indexOfTime = sddsFile.getColumnIndex("time");
        final int indexOfNano = sddsFile.getColumnIndex("n_time");
        final int indexOfStatus = sddsFile.getColumnIndex("stat");
        final int indexOfvalue = sddsFile.getColumnIndex("val");

        final Object[] time = sddsFile.getColumnValues(indexOfTime, 1, false);
        final Object[] value = sddsFile.getColumnValues(indexOfvalue, 1, false);

        Object[] nanoSec = null;
        if (indexOfNano != -1) {
            nanoSec = sddsFile.getColumnValues(indexOfNano, 1, false);
        }

        Object[] status = null;
        if (indexOfStatus != -1) {
            status = sddsFile.getColumnValues(indexOfStatus, 1, false);
        }

        try {
            if (nanoSec != null && status != null) {

                for(int i = 0; i < time.length; i++) {

                    data.add(new EpicsRecordData(TypeFactory.toLong(time[i]), TypeFactory.toLong(nanoSec[i]),
                                                 TypeFactory.toLong(status[i]), value[i]));
                }
            } else {
                for(int i = 0; i < time.length; i++) {
                    data.add(new EpicsRecordData(TypeFactory.toLong(time[i]), value[i]));
                }
            }
        } catch (final TypeNotSupportedException tnse) {
            error = AapiServerError.BAD_HR_FILE;
            errorDesc = tnse.getMessage().trim();
        }
    }

    /**
     *
     * @param list
     * @return
     */
    @Nonnull
    private SampleParameters getParameters(@CheckForNull final String[] list) {

        final SampleParameters result = new SampleParameters();
        String[] name = null;
        String value = null;

        if (list != null) {
            for(final String s : list) {
                if(!Strings.isNullOrEmpty(s)) {
                    final String trim = s.trim();

                    if (trim.startsWith("record.")) {
                        name = trim.split("\\.");
                        if (name.length == 2) {
                            value = name[1].trim();

                            dispatchValueIntoResult(result, value, s);
                        }
                    }
                }
            }
        }

        return result;
    }

    private void dispatchValueIntoResult(@Nonnull final SampleParameters result,
                                         @Nonnull final String value,
                                         @Nonnull final String s) {
        final int index = sddsFile.getParameterIndex(s);

        if ("PREC".equalsIgnoreCase(value)) {
            result.setPrecision(((Long) sddsFile.getParameterValue(index, 1, true)).intValue());
        } else if ("HOPR".equalsIgnoreCase(value)) {
            result.setDisplayHigh(((Float) sddsFile.getParameterValue(index, 1, true)).doubleValue());
        } else if ("LOPR".equalsIgnoreCase(value)) {
            result.setDisplayLow(((Float) sddsFile.getParameterValue(index, 1, true)).doubleValue());
        } else if ("HIHI".equalsIgnoreCase(value)) {
            result.setHighAlarm(((Float) sddsFile.getParameterValue(index, 1, true)).doubleValue());
        } else if ("HIGH".equalsIgnoreCase(value)) {
            result.setHighWarning(((Float) sddsFile.getParameterValue(index, 1, true)).doubleValue());
        } else if ("LOLO".equalsIgnoreCase(value)) {
            result.setLowAlarm(((Float) sddsFile.getParameterValue(index, 1, true)).doubleValue());
        } else if ("LOW".equalsIgnoreCase(value)) {
            result.setLowWarning(((Float) sddsFile.getParameterValue(index, 1, true)).doubleValue());
        } else if ("EGU".equalsIgnoreCase(value)) {
            String tmp = (String) sddsFile.getParameterValue(index, 1, true);
            if (tmp != null) {
                if ("\"\"".equals(tmp)) {
                    tmp = "N/A";
                }
            } else {
                tmp = "N/A";
            }
            result.setUnits(tmp);
        }
    }
}
