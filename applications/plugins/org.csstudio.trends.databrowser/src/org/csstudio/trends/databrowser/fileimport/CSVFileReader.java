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
package org.csstudio.trends.databrowser.fileimport;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;

import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.trends.databrowser.model.IPVModelItem;
import org.csstudio.trends.databrowser.model.Model;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.PlatformUI;
import org.nfunk.jep.JEP;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 28.04.2008
 */
public abstract class CSVFileReader extends Job{
    
    /**
     * A List of all IValues for each Channel.
     */
    HashMap<Integer, ArrayList<IValue>> _parsSamples;
    
    /**
     * The global file Settings.
     */
    SampleFileImportSettings _setting;
    
    /**
     * The File to parse.
     */
    File _fileName;


    /**
     * Data model for a chart. Holds a list of PVs, subscribes to new values for
     * those PVs.
     */
    Model _model;

    /**
     * @param name the name off the Job.
     */
    public CSVFileReader(final String name) {
        super(name);
    }

    /**
     * 
     * @return the global file settings.
     */
    public final CSVImportSettings getSettings() {
        return _setting;
    }
    
    /**
     *  Generate a {@link IValue} from a Value, Time and Unit. 
     * @param value the Sample Value
     * @param microsecond the time in microsec of the sample 
     * @param unit the unit of the sample.
     * @return a IValue which represent a Sample.
     */
    final IValue addSample(final double[] value,final long microsecond, final String unit) {
        long seconds = microsecond/1000000;
        long nanoseconds = (microsecond - seconds*1000000)*1000;
        final ITimestamp time = TimestampFactory.createTimestamp(seconds, nanoseconds);
        INumericMetaData metaData = ValueFactory
                .createNumericMetaData(0, 100, 0, 0, 0, 0, 0, unit);

        IValue values = ValueFactory.createDoubleValue(time, ValueFactory.createOKSeverity(), "ok", //$NON-NLS-1$
                metaData, IValue.Quality.Original, value);

        return values;
    }

    /**
     * 
     * @return the status of the end.
     */
    IStatus fillModel(){
        if (_parsSamples != null) {
            for (int i = 0; i < _setting._channelList.size(); i++) {
                final int j = i;
                final Channel channel = _setting._channelList.get(i);
                if (channel.isSelected()) {
                    PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            ArrayList<IValue> samples = _parsSamples.get(j);
                            if (_parsSamples.get(j) != null) {
                                String pvName = channel.getName()
                                        + " (" + channel.getUnit() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
                                IPVModelItem pv = _model.addPV(pvName);
                                pv.setRequestType(IPVModelItem.RequestType.RAW);
                                pv.addArchiveSamples(pvName, samples
                                        .toArray(new IValue[samples.size()]));
                            }
                        }
                    });
                }
            }
            return Status.OK_STATUS;
        }
        return Status.CANCEL_STATUS;
    }
    
    /**
     * @param br The BufferReader of the Import File.
     * @param startLineCount the actual line Number of the File.
     * @param lineOffset Offset to the First Sample line.
     * @param startLine the actual read line of the Import file.
     * @param sampleSize the Number of Samples of the Import Files.
     * @param jeps the {@link JEP} formulas to manipulate the Values.
     * @throws IOException from BufferReader
     */
    protected void parsedBody(final BufferedReader br, final String startLine, final int lineOffset, final int startLineCount, final int sampleSize, final JEP[] jeps) throws IOException {
        String line = startLine;
        int lineCount = startLineCount;
        int sampleNr = 0;
        int factor = 0;
        while (line != null && (_setting._endSample + lineOffset) >= lineCount
                && sampleNr / _setting.getFactor() <= sampleSize) {
            if (factor == 0) {
                String[] values = line.split(","); //$NON-NLS-1$
                for (int i = 0; i < _setting._channelList.size(); i++) {
                    Channel channel = _setting._channelList.get(i);
                    if (channel.isSelected() && values.length > 1) {

                        long microseconds = (_setting._startTime.getTimeInMillis() * 1000)
                                + channel.getResolution() * sampleNr;

                        double tempValue = Double.parseDouble(values[i + 1]);
                        tempValue = calculateJEPFormula(tempValue, jeps[i], channel.getFormula());
                        if (Double.isNaN(tempValue) || Double.isInfinite(tempValue)) {
                            continue;
                        }
                        double[] value = new double[] { tempValue };
                        IValue iValue = addSample(value, microseconds, _setting._channelList
                                .get(i).getUnit());
                        if (iValue != null) {
                            _parsSamples.get(i).add(iValue);
                        }
                    }
                }
                factor = _setting.getFactor();
            }
            sampleNr++;
            factor--;
            line = br.readLine();
            lineCount++;
        }
    }
    
    /**
     * 
     * @param value the value for the calculation.
     * @param jep the {@link JEP} to Calculate.
     * @param formula the string Formula.
     * @return the manipulated value.
     */
    public static double calculateJEPFormula(final double value, final JEP jep, final String formula){
        Double tmp = value; 
        if (jep != null) {
            jep.addVariable(Messages.SampleFileDialog_JepVariable, value);
            tmp = jep.getValue();
            Formatter formatter = new Formatter();
            formatter.format(Messages.SampleFileReader_WarningInvalidValueFound,value, formula);
            if ((tmp.isInfinite() && tmp.isNaN())) {
                CentralLogger.getInstance().warn(
                        CSVFileReader.class,
                        formatter.toString());
                return Double.NaN;
            }
        }
        return tmp;
    }

}
