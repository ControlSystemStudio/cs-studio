package org.csstudio.trends.databrowser.fileimport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.csstudio.platform.data.IValue;
import org.csstudio.trends.databrowser.model.IPVModelItem;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.ploteditor.PlotEditor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.PlatformUI;
import org.nfunk.jep.JEP;

/**
 * Read the sample file and add the samples to an IValue array.
 * 
 * @author jhatje
 * @author $Author$
 * @version $Revision$
 * @since 25.04.2008
 */
public class SampleFileReader extends CSVFileReader {

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
    public SampleFileReader(final String name, final File dataFile, final Model model,
            final SampleFileImportSettings setting) {
        super(name);
        _fileName = dataFile;
        _model = model;
        _setting = setting;
        if (_fileName != null) {
            parseHeader();
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
                    if (line.startsWith(",")) { //$NON-NLS-1$
                        if (lineOffset == 0) {
                            lineOffset = lineCount;
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
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
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
     * -- Start Date<br>
     *  |- End Date<br>
     */
    private void parseHeader() {

        try {
            BufferedReader br = new BufferedReader(new FileReader(_fileName));
            String line;
            try {
                int numberOfHeaderLines = 1;
                while ((line = br.readLine()) != null && numberOfHeaderLines > 0) {
                    String[] column = line.split(","); //$NON-NLS-1$
                    column[0] = column[0].replaceAll("\"", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$
                    int size = column.length - 1;
                    if (column[0].equals("Header Size")) { //$NON-NLS-1$
                        if (size == 1 && column[1] != null) {
                            column[1] = column[1].replaceAll("\"", "").trim(); //$NON-NLS-1$//$NON-NLS-2$
                            numberOfHeaderLines = Integer.parseInt(column[1]);
                        }
                    } else if (column[0].equals("TraceName")) { //$NON-NLS-1$
                        for (int i = 0; i < size; i++) {
                            _setting._channelList.add(new Channel(column[i + 1]
                                    .replaceAll("\"", "").trim(), _setting)); //$NON-NLS-1$//$NON-NLS-2$
                        }
                    } else if (column[0].equals("BlockSize")) { //$NON-NLS-1$
                        for (int i = 0; i < size; i++) {
                            Channel channel = _setting._channelList.get(i);
                            int total = Integer.parseInt(column[i + 1].replaceAll("\"", "").trim());//$NON-NLS-1$//$NON-NLS-2$
                            channel.setSamplesTotal(total);
                        }
                    } else if (column[0].equals("VUnit")) { //$NON-NLS-1$
                        for (int i = 0; i < size; i++) {
                            Channel channel = _setting._channelList.get(i);
                            channel.setUnit(column[i + 1].replaceAll("\"", "").trim()); //$NON-NLS-1$//$NON-NLS-2$
                        }
                    } else if (column[0].equals("HResolution")) { //$NON-NLS-1$
                        for (int i = 0; i < size; i++) {
                            Channel channel = _setting._channelList.get(i);
                            Double d = Double.valueOf(column[i + 1].replaceAll("\"", "").trim()); //$NON-NLS-1$ //$NON-NLS-2$
                            d *= 1000000.0;
                            channel.setResolution(d.longValue());
                            // _hResolution[i] = d.longValue();
                        }
                    } else if (column[0].equals("Date")) { //$NON-NLS-1$
                        for (int i = 0; i < size; i++) {
                            Channel channel = _setting._channelList.get(i);
                            String[] dateAsString = column[i + 1]
                                    .replaceAll("\"", "").trim().split("/"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                            GregorianCalendar calendar = new GregorianCalendar(Integer
                                    .parseInt(dateAsString[0]),
                                    Integer.parseInt(dateAsString[1]) - 1, Integer
                                            .parseInt(dateAsString[2]));
                            channel.setStartTime(calendar);
                        }
                    } else if (column[0].equals("Time")) { //$NON-NLS-1$
                        for (int i = 0; i < size; i++) {
                            Channel channel = _setting._channelList.get(i);
                            String[] timeAsString = column[i + 1]
                                    .replaceAll("\"", "").trim().split(":"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                            channel.getStartTime().add(GregorianCalendar.HOUR_OF_DAY,
                                    Integer.parseInt(timeAsString[0]));
                            channel.getStartTime().add(GregorianCalendar.MINUTE,
                                    Integer.parseInt(timeAsString[1]));
                            channel.getStartTime().add(GregorianCalendar.MILLISECOND,
                                    Integer.parseInt(timeAsString[2].replaceAll("\\.", "")) * 10); //$NON-NLS-1$ //$NON-NLS-2$

                        }
                    }

                    numberOfHeaderLines--;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (_setting._startTime == null && _setting._channelList.get(0).getStartTime() != null
                && _setting._channelList.get(0).getEndTime() != null) {
            _setting._startTime = (Calendar) _setting._channelList.get(0).getStartTime().clone();
            _setting._endTime = (Calendar) _setting._channelList.get(0).getEndTime().clone();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        if (_setting != null && _setting.getSelectedSize() > 0) {
            if (_model == null) {
                PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS"); //$NON-NLS-1$ 
                        SampleFileReader.this._model = PlotEditor.createInstance().getModel();
                        SampleFileReader.this._model.enableScroll(false);
                        try {
                            SampleFileReader.this._model.setTimeSpecifications(sdf
                                    .format(_setting._startTime.getTime()), sdf
                                    .format(_setting._endTime.getTime()));
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
     * {@inheritDoc}
     */
    @Override
    synchronized IStatus fillModel() {
        if (_parsSamples != null) {
            for (int i = 0; i < _setting._channelList.size(); i++) {
                final int j = i;
                final Channel channel = _setting._channelList.get(i);
                if (channel.isSelected()) {
                    PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            ArrayList<IValue> samples = _parsSamples.get(j);
                            if (_parsSamples.get(j) != null) {
                                String pvName = channel.getName() + " (" + channel.getUnit() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
                                IPVModelItem pv = _model.addPV(pvName);
                                pv.setRequestType(IPVModelItem.RequestType.RAW);
                                pv.addArchiveSamples(pvName, samples.toArray(new IValue[samples
                                        .size()]));
                            }
                        }
                    });
                }
            }
            return Status.OK_STATUS;
        }
        return Status.CANCEL_STATUS;
    }

}
