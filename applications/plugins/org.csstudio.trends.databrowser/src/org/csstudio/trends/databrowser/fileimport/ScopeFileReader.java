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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.csstudio.platform.data.IValue;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.ploteditor.PlotEditor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.PlatformUI;
import org.nfunk.jep.JEP;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 29.04.2008
 */
public class ScopeFileReader extends CSVFileReader {

    /**
     * if true when the file format a manipulated is and a trigger time have.
     */
    private boolean _hasTime;
    /**
     * The trigger time.
     */
    private Calendar _triggerTime;

    /**
     * @param name
     *            The Name of the Job.
     * @param dataFile
     *            The File to Parse.
     * @param model
     *            The Datebrowser Model
     * @param setting
     *            The global import File setting.
     */
    public ScopeFileReader(final String name, final File dataFile, final Model model,
            final SampleFileImportSettings setting) {
        super(name);
        _fileName = dataFile;
        _model = model;
        _setting = setting;
        if (_fileName != null) {
            parseStartTime();
        }
    }

    /**
     * Parse the Sample values from import File.
     */
    private void parseSamples() {
        try {
            // Set Buffer size to 32768 for better performance
            BufferedReader br = new BufferedReader(new FileReader(_fileName), 32768);
            String line;
            int sampleSize = ((_setting._endSample - _setting.getStartSample()) / _setting
                    .getFactor());
            _parsSamples = new HashMap<Integer, ArrayList<IValue>>(_setting.getSelectedSize(), 1);
            JEP[] jeps = new JEP[_setting._channelList.size()];
            try {
                for (int i = 0; i < _setting._channelList.size(); i++) {
                    Channel channel = _setting._channelList.get(i);
                    if (channel.isSelected()) {
                        _parsSamples.put(i, new ArrayList<IValue>(sampleSize + 100));
                        String formula = channel.getFormula();
                        if (formula != null && formula.trim().length() > 0) {
                            jeps[i] = new JEP();
                            jeps[i].addStandardFunctions();
                            jeps[i].addStandardConstants();
                            jeps[i].addVariable(Messages.SampleFileDialog_JepVariable, 0);
                            jeps[i].parseExpression(formula);
                        }
                    }
                }
                int lineOffset = 0;
                int lineCount = 0;
                int startSample = 0;

                // read header.
                while ((line = br.readLine()) != null && lineOffset == 0) {
                    lineCount++;
                    if (!line.startsWith("\"")) { //$NON-NLS-1$
                        if (lineOffset == 0) {
                            lineOffset = lineCount - 1;
                            startSample = (lineOffset + _setting.getStartSample());
                            break;
                        }
                    }
                }

                // go to first selected sample.
                while (lineCount < startSample && line != null) {
                    lineCount++;
                    line = br.readLine();
                }

                parsedBody(br, line, lineOffset, lineCount, sampleSize, jeps);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * Parse the Start-Time from import File.
     */
    private void parseStartTime() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(_fileName));
            String line;
            line = br.readLine();
            if (line != null) {
                String[] column = line.split(","); //$NON-NLS-1$
                for (int i = 0; i < column.length; i++) {
                    if (i == 0) {
                        _hasTime = false;
                        if (!column[i].equals("\"TIME\"")) { //$NON-NLS-1$
                            String[] dateTime = column[i].replaceAll("\"", "").split(" "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            if (dateTime.length == 2) {
                                String[] date = dateTime[0].split("/"); //$NON-NLS-1$
                                if (date.length == 3) {
                                    _triggerTime = new GregorianCalendar(Integer.parseInt(date[0]),
                                            Integer.parseInt(date[1]) - 1, Integer
                                                    .parseInt(date[2]));
                                }
                                String[] timeAsString = dateTime[1].split(":"); //$NON-NLS-1$
                                if (timeAsString.length == 3) {
                                    _triggerTime.add(GregorianCalendar.HOUR_OF_DAY, Integer
                                            .parseInt(timeAsString[0]));
                                    _triggerTime.add(GregorianCalendar.MINUTE, Integer
                                            .parseInt(timeAsString[1]));
                                    _triggerTime.add(GregorianCalendar.MILLISECOND, Integer
                                            .parseInt(timeAsString[2].replaceAll("\\.", ""))); //$NON-NLS-1$ //$NON-NLS-2$
                                    _hasTime = true;
                                }
                            }
                        }
                    } else {
                        _setting._channelList.add(new Channel(
                                column[i].replaceAll("\"", "").trim(), _setting)); //$NON-NLS-1$//$NON-NLS-2$
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // TODO: handle exception
        } catch (IOException e) {
            // TODO: handle exception
        }
    }

    /**
     * Parse the Import File header.<br>
     * From header get:<br>
     * -- Header Size<br>
     * -- Channel Name<br>
     * -- Sample size<br>
     * -- Sample Unit<br>
     * -- Resolution<br>
     * -- Start Date (trigger Time)<br>
     *  |- End Date<br>
     *  @param triggerTime the Trigger time.
     */
    final void parseHeader(final Calendar triggerTime) {
        _triggerTime = triggerTime;
        parseHeader();
    }

    /**
     * Parse the Import File header.<br>
     * From header get:<br>
     * -- Header Size<br>
     * -- Channel Name<br>
     * -- Sample size<br>
     * -- Sample Unit<br>
     * -- Resolution<br>
     * Set the Start Date to now.<br>
     *  |- End Date<br>
     */
    final void parseHeader() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(_fileName));
            String line;

            line = br.readLine();

            int sampleSum = 0;
            /**
             * relativeStartTime in microsec
             */
            long relativeStartTime = 0;
            /**
             * relativeEndTime in microsec
             */
            long relativeEndTime = 0;
            long resolution = 0;
            while ((line = br.readLine()) != null) {
                String value = line.split(",")[0].trim(); //$NON-NLS-1$
                if (value.length() > 0) {
                    Double valueDouble = Double.parseDouble(value) * 1000000;
                    relativeEndTime = valueDouble.longValue();// in microsec
                    if (sampleSum == 0) {
                        relativeStartTime = relativeEndTime;
                    } else if (sampleSum == 1) {
                        resolution = (relativeEndTime - relativeStartTime);
                    }
                    sampleSum++;
                }
            }
            relativeEndTime += resolution;
            if (_triggerTime == null) {
                _triggerTime = new GregorianCalendar();
                _triggerTime.add(GregorianCalendar.DAY_OF_MONTH, -1);
            }

            _setting._startTime = (Calendar) _triggerTime.clone();
            _setting._startTime.add(Calendar.MILLISECOND, (int) (relativeStartTime / 1000));
            _setting._endTime = (Calendar) _triggerTime.clone();
            _setting._endTime.add(Calendar.MILLISECOND, (int) (relativeEndTime / 1000));
            System.out.println(_setting._startTime);
            System.out.println(_setting._endTime);

            for (Channel channel : _setting._channelList) {
                channel.setResolution(resolution);
                channel.setStartTime((Calendar) _setting._startTime.clone());
                channel.setSamplesTotal(sampleSum);
                channel.setUnit(""); //$NON-NLS-1$
            }
        } catch (FileNotFoundException e) {
            // TODO: handle exception
        } catch (IOException e) {
            // TODO: handle exception
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IStatus run(IProgressMonitor monitor) {
        if (_setting != null && _setting.getSelectedSize() > 0) {
            if (_model == null) {
                PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS"); //$NON-NLS-1$ 
                        _model = PlotEditor.createInstance().getModel();
                        _model.enableScroll(false);
                        try {
                            _model.setTimeSpecifications(sdf.format(_setting._startTime.getTime()),
                                    sdf.format(_setting._endTime.getTime()));
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            }

            parseSamples();

            return fillModel();
        }
        return Status.CANCEL_STATUS;
    }

    /**
     * 
     * @return true has the Import file a Trigger Time in the Header.
     */
    public final boolean hasTime() {
        return _hasTime;
    }

    /**
     * 
     * @param hasTime set to true has the Import File a Trigger Time.
     */
    public final void setHasTime(final boolean hasTime) {
        _hasTime = hasTime;
    }

}
