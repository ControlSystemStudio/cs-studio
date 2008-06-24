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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.csstudio.apputil.ui.time.StartEndDialog;
import org.csstudio.trends.databrowser.model.Model;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

/**
 * 
 * @author jhatje
 * @author $Author$
 * @version $Revision$
 * @since 25.04.2008
 */
public class SampleFileDialog extends Dialog {

    /**
     * A List whit all Label for the selected Sample number.
     */
    private ArrayList<Label> _channelSampleSelectedSizeList = new ArrayList<Label>();

    /**
     * The number of selected channels.
     */
    private int _countChannel = 0;

    /**
     * The OK Button.
     */
    private Button _okButton;

    /**
     * The global import file settings.
     */
    private SampleFileImportSettings _result;

    /**
     * The label to display the sum over all of Samples.
     */
    private Label _sumLabel;

    /**
     * The date of the last sample from the File.
     */
    private GregorianCalendar _fileEndGregorianCalendar;

    /**
     * The model for the date {@link Browser}.
     */
    private Model _dataBrowserModel;

    /**
     * The constructor whit style setting.
     * 
     * @param parent
     *            the Parent shell for the Dialog.
     * @param model
     *            The model for the date {@link Browser}.
     * @param style
     *            set the Style for the Dialog.
     * @param result
     *            The global import file settings.
     */
    public SampleFileDialog(final Shell parent, final Model model, final int style,
            final SampleFileImportSettings result) {
        super(parent, style);
        _result = result;
        _dataBrowserModel = model;
    }

    /**
     * The constructor.
     * 
     * @param parent
     *            the Parent shell for the Dialog.
     * @param model
     *            The model for the date {@link Browser}.
     * @param result
     *            The global import file settings.
     */
    public SampleFileDialog(final Shell parent, final Model model,
            final SampleFileImportSettings result) {
        this(parent, model, 0, result); // your default style bits go here (not
        // the Shell's style bits)
    }

    /**
     * Es wird �berpr�ft ob die gew�hlten Zeitgrenzen inhalb des zu Parsenden
     * Files liegen. liegt eine Zeitgrenze ausserhalb wird sie entsprechend auf
     * den Start oder Endpunkt des Files gesetzt. Dadurch wird verhindert Daten
     * zu Importiern die nicht da sind.
     * 
     * @param startTimeLabel
     *            The label for the start time.
     * @param endTimeLabel
     *            The label for the end time.
     * @param sdf
     *            The {@link SimpleDateFormat} to display the date to the Label.
     * @param startCalendar
     *            The Calendar whit the start time.
     * @param endCalendar
     *            The Calendar whit the end time.
     */
    private void checkDateInFileTimeRange(final Label startTimeLabel, final Label endTimeLabel,
            final SimpleDateFormat sdf, final Calendar startCalendar, final Calendar endCalendar) {
        long compStart = startCalendar.getTimeInMillis() // user selected
                // time
                - _result._startTime.getTimeInMillis(); // 
        long compEnd;
        // Ist gew�hlter Startpunkt nach File start.
        if (compStart >= 0) {
            compEnd = startCalendar.getTimeInMillis() - _fileEndGregorianCalendar.getTimeInMillis();
            // Ist gew�hlter Startpunkt vor File end.
            if (compEnd <= 0) {
                _result._startTime = startCalendar;
            } else {
                _result._startTime = (GregorianCalendar) _fileEndGregorianCalendar.clone();
            }
        } else {
            _result._startTime = (GregorianCalendar) _result._startTime.clone();
        }

        compStart = endCalendar.getTimeInMillis() - _result._startTime.getTimeInMillis();
        // Ist gew�hlter Endpunkt nach File start.
        if (compStart >= 0) {
            compEnd = endCalendar.getTimeInMillis() - _fileEndGregorianCalendar.getTimeInMillis();
            // Ist gew�hlter Startpunkt vor File end.
            if (compEnd <= 0) {
                _result._endTime = endCalendar;
            } else {
                _result._endTime = (GregorianCalendar) _fileEndGregorianCalendar.clone();
            }
        } else {
            _result._endTime = (GregorianCalendar) _result._startTime.clone();
        }

        startTimeLabel.setText(sdf.format(startCalendar.getTime()));
        endTimeLabel.setText(sdf.format(endCalendar.getTime()));

        calulate();
    }

    /**
     * 
     * @param parseInt
     */
    private void calulate() {
        _result.setSelectedSize(0);
        for (int i = 0; i < _result._channelList.size(); i++) {
            Label label = _channelSampleSelectedSizeList.get(i);
            Channel channel = _result._channelList.get(i);
            int sum = channel.getSamplesSelected();
            label.setText("" + sum); //$NON-NLS-1$
            if (channel.isSelected()) {
                _result.setSelectedSize(sum+_result.getSelectedSize());
            }
        }
        if (_result.getSelectedSize() > 300000) {
            _sumLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        } else {
            _sumLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
        }
        _sumLabel.setText("" + _result.getSelectedSize()); //$NON-NLS-1$
        _sumLabel.getParent().pack();
    }

    /**
     * Make the UI for the Dialog when open.
     */
    public final void open() {
        final SimpleDateFormat sdf = new SimpleDateFormat(
                Messages.SampleFileDialog_SimpleDataFormatToDisplayDates);
        // Dialog
        final Shell shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        shell.setText(Messages.SampleFileDialog_ShellHeadLine);
        shell.open();
        shell.setLayout(new GridLayout(1, false));
        final Composite main = new Composite(shell, SWT.NONE);
        main.setLayout(new GridLayout(15, false));

        // Channels
        final Group channelTable = new Group(main, SWT.NONE);
        channelTable.setLayout(new GridLayout(8, false));
        channelTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 15, 1));
        channelTable.setText(Messages.SampleFileDialog_ChannelTableHeadline);
        addChannel(channelTable);

        // -----------------------------------------------------------------------
        final Group rangeTimeComposite = new Group(main, SWT.NONE);
        rangeTimeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
        rangeTimeComposite.setLayout(new GridLayout(3, true));
        rangeTimeComposite.setText(Messages.SampleFileDialog_3);

        new Label(rangeTimeComposite, SWT.NONE).setText(Messages.SampleFileDialog_StartTimeLabel);
        final Label startTimeLabel = new Label(rangeTimeComposite, SWT.BORDER);
        startTimeLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));

        new Label(rangeTimeComposite, SWT.NONE).setText(Messages.SampleFileDialog_EndTimeLabel);
        final Label endTimeLabel = new Label(rangeTimeComposite, SWT.BORDER);
        endTimeLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));

        _fileEndGregorianCalendar = (GregorianCalendar) _result._endTime.clone();

        if (_dataBrowserModel != null) {
            checkDateInFileTimeRange(startTimeLabel, endTimeLabel, sdf, _dataBrowserModel
                    .getStartTime().toCalendar(), _dataBrowserModel.getEndTime().toCalendar());
        } else {
            startTimeLabel.setText(sdf.format(_result._startTime.getTime()));
            endTimeLabel.setText(sdf.format(_result._endTime.getTime()));
            calulate();
        }

        Button startEndDialogButton = new Button(rangeTimeComposite, SWT.PUSH);
        startEndDialogButton.setText(Messages.SampleFileDialog_TimeDialogButton);

        Button graphTimeButton = new Button(rangeTimeComposite, SWT.PUSH);
        graphTimeButton.setText(Messages.SampleFileDialog_TimeFromGraphButton);
        if (_dataBrowserModel == null) {
            graphTimeButton.setEnabled(false);
        }

        Button fileTimeButton = new Button(rangeTimeComposite, SWT.PUSH);
        fileTimeButton.setText(Messages.SampleFileDialog_TimeFromFileButton);

        startEndDialogButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
                selectionAction();
            }

            public void widgetSelected(final SelectionEvent e) {
                selectionAction();
            }

            private void selectionAction() {
                SimpleDateFormat startEndDialogSDF = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS"); //$NON-NLS-1$
                StartEndDialog startEndDialog = new StartEndDialog(shell, startEndDialogSDF
                        .format(_result._startTime.getTime()), startEndDialogSDF
                        .format(_result._endTime.getTime()));
                if (startEndDialog.open() == StartEndDialog.OK) {
                    checkDateInFileTimeRange(startTimeLabel, endTimeLabel, sdf, startEndDialog
                            .getStartCalendar(), startEndDialog.getEndCalendar());
                }
                shell.layout();
            }
        });

        graphTimeButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
                selectionAction();
            }

            public void widgetSelected(final SelectionEvent e) {
                selectionAction();
            }

            private void selectionAction() {
                checkDateInFileTimeRange(startTimeLabel, endTimeLabel, sdf, _dataBrowserModel
                        .getStartTime().toCalendar(), _dataBrowserModel.getEndTime().toCalendar());
                startTimeLabel.setText(sdf.format(_dataBrowserModel.getStartTime().toCalendar()
                        .getTime()));
                endTimeLabel.setText(sdf.format(_dataBrowserModel.getEndTime().toCalendar()
                        .getTime()));
                shell.pack();
            }

        });

        fileTimeButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
                selectionAction();
            }

            public void widgetSelected(final SelectionEvent e) {
                selectionAction();
            }

            private void selectionAction() {
                _result._startTime = (GregorianCalendar) _result._channelList.get(0).getStartTime().clone();
                _result._endTime = (GregorianCalendar) _fileEndGregorianCalendar.clone();

                startTimeLabel.setText(sdf.format(_result._startTime.getTime()));
                endTimeLabel.setText(sdf.format(_result._endTime.getTime()));
                calulate();
                shell.pack();
            }

        });

        // Sample Raid
        final Group sampleRaidComposite = new Group(main, SWT.NONE);
        sampleRaidComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 4, 1));
        sampleRaidComposite.setLayout(new GridLayout(2, false));
        sampleRaidComposite.setText(Messages.SampleFileDialog_FactorGroup);

        Label factorLabel = new Label(sampleRaidComposite, SWT.NONE);
        factorLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
        factorLabel.setText(Messages.SampleFileDialog_FactorHeadlineLabel);
        final Button enableFactroButton = new Button(sampleRaidComposite, SWT.CHECK);
        final Text factorText = new Text(sampleRaidComposite, SWT.SINGLE | SWT.BORDER);
        factorText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        factorText.setEnabled(enableFactroButton.getSelection());
        factorText.addVerifyListener(new VerifyListener() {
            public void verifyText(final VerifyEvent e) {
                if (e.character == SWT.DEL || e.character == SWT.BS) {
                    e.doit = true;
                } else {
                    try {
                        Integer.parseInt(e.text);
                        e.doit = true;
                    } catch (NumberFormatException nfe) {
                        e.doit = false;
                    }
                }

            }

        });
        
        factorText.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent e) {
                try {
                    _result.setFactor(Integer.parseInt(factorText.getText()));
                } catch (NumberFormatException nfe) {
                    _result.setFactor(1);
                }
                calulate();

            }

        });
        enableFactroButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
                selectionAction();
            }

            public void widgetSelected(final SelectionEvent e) {
                selectionAction();
            }

            private void selectionAction() {
                factorText.setEnabled(enableFactroButton.getSelection());
                try {
                    if (factorText.getEnabled()) {
                        _result.setFactor(Integer.parseInt(factorText.getText()));
                    } else {
                        _result.setFactor(1);
                    }
                } catch (NumberFormatException e) {
                    _result.setFactor(1);
                }
                calulate();
            }

        });

        // -----------------------------------------------------------------------
        // Offset Time part
        Group timeComposite = new Group(main, SWT.NONE);
        timeComposite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 3, 1));
        timeComposite.setLayout(new GridLayout(1, false));
        timeComposite.setText(Messages.SampleFileDialog_TimeOffsetGroup);

        TabFolder timeOffsetTabFolder = new TabFolder(timeComposite, SWT.HORIZONTAL);
        TabItem absoluteTabItem = new TabItem(timeOffsetTabFolder, SWT.NONE);
        absoluteTabItem.setText(Messages.SampleFileDialog_AbsolutTab);
        Composite absolulteComposite = new Composite(timeOffsetTabFolder, SWT.NONE);
        absolulteComposite.setLayout(new GridLayout(3, false));

        TabItem relativeTabItem = new TabItem(timeOffsetTabFolder, SWT.NONE);
        relativeTabItem.setText(Messages.SampleFileDialog_RelativTab);
        Composite relativeComposite = new Composite(timeOffsetTabFolder, SWT.NONE);
        relativeComposite.setLayout(new GridLayout(4, true));

        new Label(absolulteComposite, SWT.NONE).setText(Messages.SampleFileDialog_AbsolutDate);
        new Label(absolulteComposite, SWT.NONE).setText(Messages.SampleFileDialog_AbgsolutTime);
        new Label(absolulteComposite, SWT.NONE).setText(Messages.SampleFileDialog_AbsolutMillisec);

        final DateTime date = new DateTime(absolulteComposite, SWT.DATE | SWT.MEDIUM);
        date.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        date.setYear(_result._startTime.get(Calendar.YEAR));
        date.setMonth(_result._startTime.get(Calendar.MONTH));
        date.setDay(_result._startTime.get(Calendar.DAY_OF_MONTH));

        final DateTime time = new DateTime(absolulteComposite, SWT.TIME | SWT.LONG);
        time.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        time.setHours(_result._startTime.get(Calendar.HOUR_OF_DAY));
        time.setMinutes(_result._startTime.get(Calendar.MINUTE));
        time.setSeconds(_result._startTime.get(Calendar.SECOND));

        final Spinner miliSpinner = new Spinner(absolulteComposite, SWT.BORDER | SWT.WRAP
                | SWT.RIGHT);
        miliSpinner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        miliSpinner.setMaximum(999);
        miliSpinner.setMinimum(-999);
        miliSpinner.setSelection(_result._startTime.get(Calendar.MILLISECOND));

        // realtiv offset time
        new Label(relativeComposite, SWT.NONE).setText(""); //$NON-NLS-1$
        new Label(relativeComposite, SWT.NONE).setText(Messages.SampleFileDialog_RelativYear);
        new Label(relativeComposite, SWT.NONE).setText(Messages.SampleFileDialog_RelativMonth);
        new Label(relativeComposite, SWT.NONE).setText(Messages.SampleFileDialog_RelativDay);

        final Button pmButton = new Button(relativeComposite, SWT.TOGGLE);
        pmButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        pmButton.setText(Messages.SampleFileDialog_21);

        final Spinner yearSpinner = new Spinner(relativeComposite, SWT.WRAP);
        yearSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        yearSpinner.setMaximum(100);
        yearSpinner.setMinimum(-100);

        final Spinner monthSpinner = new Spinner(relativeComposite, SWT.WRAP);
        monthSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        monthSpinner.setMaximum(12);
        monthSpinner.setMinimum(-12);

        final Spinner daySpinner = new Spinner(relativeComposite, SWT.WRAP);
        daySpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        daySpinner.setMaximum(31);
        daySpinner.setMinimum(-31);

        new Label(relativeComposite, SWT.NONE).setText(Messages.SampleFileDialog_RelativHour);
        new Label(relativeComposite, SWT.NONE).setText(Messages.SampleFileDialog_RelativMinute);
        new Label(relativeComposite, SWT.NONE).setText(Messages.SampleFileDialog_RelativSecond);
        new Label(relativeComposite, SWT.NONE)
                .setText(Messages.SampleFileDialog_RelativMillisecond);

        final Spinner hourSpinner = new Spinner(relativeComposite, SWT.WRAP);
        hourSpinner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        hourSpinner.setMaximum(24);
        hourSpinner.setMinimum(-24);

        final Spinner minSpinner = new Spinner(relativeComposite, SWT.WRAP);
        minSpinner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        minSpinner.setMaximum(59);
        minSpinner.setMinimum(-59);

        final Spinner secSpinner = new Spinner(relativeComposite, SWT.WRAP);
        secSpinner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        secSpinner.setMaximum(59);
        secSpinner.setMinimum(-59);

        final Spinner relMiliSpinner = new Spinner(relativeComposite, SWT.WRAP);
        relMiliSpinner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        relMiliSpinner.setMaximum(999);
        relMiliSpinner.setMinimum(-999);

        absoluteTabItem.setControl(absolulteComposite);
        relativeTabItem.setControl(relativeComposite);
        // -----------------------------------------------------------------------
        Composite buttonComposite = new Composite(main, SWT.NONE);
        buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 7, 1));
        buttonComposite.setLayout(new GridLayout(2, true));
        _okButton = new Button(buttonComposite, SWT.PUSH);
        _okButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        _okButton.setText(Messages.SampleFileDialog_OkButton);
        _okButton.setEnabled(false);
        Button cancelButton = new Button(buttonComposite, SWT.PUSH);
        cancelButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        cancelButton.setText(Messages.SampleFileDialog_CancelButton);
        shell.pack();

        _okButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
                selectAction();
            }

            public void widgetSelected(final SelectionEvent e) {
                selectAction();
            }

            private void selectAction() {

                Control[] children = channelTable.getChildren();
                int rowSize = 8;
                // set Units and formulas
                for (int i = 1; i <= _result._channelList.size(); i++) {
                    if (children[i * rowSize] instanceof Button) {
                        Button button = (Button) children[i * rowSize];
                        if (button.getSelection()) {
                            int nameTextOffset = 1;
                            if (children[i * rowSize + nameTextOffset] instanceof Text) {
                                Text text = (Text) children[i * rowSize + nameTextOffset];
                                _result._channelList.get(i - 1).setName(text.getText());
                            }

                            int unitTextOffset = 6;
                            if (children[i * rowSize + unitTextOffset] instanceof Text) {
                                Text text = (Text) children[i * rowSize + unitTextOffset];
                                _result._channelList.get(i - 1).setUnit(text.getText());
                            }
                            int formulaTextOffset = 7;
                            if (children[i * rowSize + formulaTextOffset] instanceof Text) {
                                Text text = (Text) children[i * rowSize + formulaTextOffset];
                                _result._channelList.get(i - 1).setFormula(text.getText());
                            }
                        }
                    }
                }

                // Berechnung des gew�hlten Start-Samples
                Double startTmp = (_result._startTime.getTimeInMillis() - _result._channelList.get(0)
                        .getStartTime().getTimeInMillis())
                        / _result.getDefaultResolution();
                startTmp = Math.ceil(startTmp);
                _result.setStartSample(startTmp.intValue());

                // Berechnung des gew�hlten End-Samples
                startTmp = (_result._endTime.getTimeInMillis() - _result._channelList.get(0)
                        .getStartTime().getTimeInMillis())
                        / _result.getDefaultResolution();
                _result._endSample = startTmp.intValue();

                // Setzen des Startzeitpunktes und
                // sicherstellen das der Startzeitpunkt auch mit einem
                // Samplepunkt �bereinstimmt.
                Double sampleOffset = (_result._startTime.getTimeInMillis() - _result._startTime
                        .getTimeInMillis())
                        / _result.getDefaultResolution();
                sampleOffset = Math.ceil(sampleOffset);
                sampleOffset = sampleOffset * _result.getDefaultResolution() / 1000;
                _result._startTime.add(Calendar.MILLISECOND, sampleOffset.intValue());

                // Verschieben des Start- und Endzeitpunktes um die absolut
                // Offset Time.
                _result._startTime.add(Calendar.YEAR, date.getYear()
                        - _result._startTime.get(Calendar.YEAR));
                _result._endTime.add(Calendar.YEAR, date.getYear()
                        - _result._startTime.get(Calendar.YEAR));
                _result._startTime.add(Calendar.MONTH, date.getMonth()
                        - _result._startTime.get(Calendar.MONTH));
                _result._endTime.add(Calendar.MONTH, date.getMonth()
                        - _result._startTime.get(Calendar.MONTH));
                _result._startTime.add(Calendar.DAY_OF_MONTH, date.getDay()
                        - _result._startTime.get(Calendar.DAY_OF_MONTH));
                _result._endTime.add(Calendar.DAY_OF_MONTH, date.getDay()
                        - _result._startTime.get(Calendar.DAY_OF_MONTH));
                int offset = 0;
                offset += (time.getHours() - _result._startTime.get(Calendar.HOUR_OF_DAY)) * 60 * 60 * 1000;
                offset += (time.getMinutes() - _result._startTime.get(Calendar.MINUTE)) * 60 * 1000;
                offset += (time.getSeconds() - _result._startTime.get(Calendar.SECOND)) * 1000;
                offset += miliSpinner.getSelection() - _result._startTime.get(Calendar.MILLISECOND);
                _result._startTime.add(Calendar.MILLISECOND, offset);
                _result._endTime.add(Calendar.MILLISECOND, offset);

                // Factor setzen.
                if (enableFactroButton.getSelection()) {
                    int fac = 1;
                    try {
                        fac = Integer.parseInt(factorText.getText());
                    } catch (NumberFormatException nfe) {
                        fac = 1;
                    }
                    _result.setFactor(fac);
                }

                shell.dispose();
            }
        });

        cancelButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
//                _result = null;
                _result.setSelectedSize(0);
                shell.dispose();
            }

            public void widgetSelected(final SelectionEvent e) {
//                _result = null;
                _result.setSelectedSize(0);
                shell.dispose();
            }

        });

        SelectionListener offsetTimeSelectionListener = new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
                selectAction();
            }

            public void widgetSelected(final SelectionEvent e) {
                selectAction();
            }

            private void selectAction() {
                int multi = 1;
                if (pmButton.getSelection()) {
                    pmButton.setText(Messages.SampleFileDialog_33);
                    multi = -1;
                } else {
                    pmButton.setText(Messages.SampleFileDialog_34);
                }
                date.setYear(_result._startTime.get(Calendar.YEAR) + multi
                        * yearSpinner.getSelection());
                date.setMonth(_result._startTime.get(Calendar.MONTH) + multi
                        * monthSpinner.getSelection());
                date.setDay(_result._startTime.get(Calendar.DAY_OF_MONTH) + multi
                        * daySpinner.getSelection());
                time.setHours(_result._startTime.get(Calendar.HOUR_OF_DAY) + multi
                        * hourSpinner.getSelection());
                time.setMinutes(_result._startTime.get(Calendar.MINUTE) + multi
                        * minSpinner.getSelection());
                time.setSeconds(_result._startTime.get(Calendar.SECOND) + multi
                        * secSpinner.getSelection());
                miliSpinner.setSelection(_result._startTime.get(Calendar.MILLISECOND) + multi
                        * relMiliSpinner.getSelection());
                shell.layout();
            }
        };

        pmButton.addSelectionListener(offsetTimeSelectionListener);
        yearSpinner.addSelectionListener(offsetTimeSelectionListener);
        monthSpinner.addSelectionListener(offsetTimeSelectionListener);
        daySpinner.addSelectionListener(offsetTimeSelectionListener);
        hourSpinner.addSelectionListener(offsetTimeSelectionListener);
        minSpinner.addSelectionListener(offsetTimeSelectionListener);
        secSpinner.addSelectionListener(offsetTimeSelectionListener);
        relMiliSpinner.addSelectionListener(offsetTimeSelectionListener);

        calulate();
        Display display = getParent().getDisplay();
        shell.pack();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    /**
     * @param parent
     *            the parent Composite.
     */
    private void addChannel(final Composite parent) {
        String[] headlines = new String[] { Messages.SampleFileDialog_TableHead1Empty,
                Messages.SampleFileDialog_TableHead2Name,
                Messages.SampleFileDialog_TableHead3DateFrom,
                Messages.SampleFileDialog_TableHead4DateTo,
                Messages.SampleFileDialog_TableHead5SampleUsed,
                Messages.SampleFileDialog_TableHead6SampleTotal,
                Messages.SampleFileDialog_TableHead7Unit,
                Messages.SampleFileDialog_TableHead8Fomula };
        for (String headline : headlines) {
            new Label(parent, SWT.NONE).setText(headline);
        }
        for (final Channel channel : _result._channelList) {
            // Selection Checkbox
            final Button check = new Button(parent, SWT.CHECK);
            Label lValue;
            GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);

            // Name field
            Text nameText = new Text(parent, SWT.BORDER | SWT.SINGLE);
            nameText.setText(channel.getName());
            gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
            gd.minimumWidth = 150;
            nameText.setLayoutData(gd);

            // Start-Date field
            SimpleDateFormat sdf = new SimpleDateFormat(
                    Messages.SampleFileDialog_SimpleDataFormatToDisplayDates);
            lValue = new Label(parent, SWT.BORDER);
            lValue.setText(sdf.format(channel.getStartTime().getTime()));
            gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
            lValue.setLayoutData(gd);

            // End-Date field
            gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
            gd.minimumWidth = lValue.getBounds().width;
            lValue = new Label(parent, SWT.BORDER);
            lValue.setLayoutData(gd);
            lValue.setText(sdf.format(channel.getEndTime().getTime()));

            // Sample subset
            // -- Label
            // l = new Label(parent, SWT.NONE);
            // l.setText("Sample: ");
            // gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
            // gd.minimumWidth = 30;
            // l.setLayoutData(gd);
            // -- Value field
            lValue = new Label(parent, SWT.BORDER | SWT.RIGHT);
            gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
            gd.minimumWidth = 30;
            lValue.setLayoutData(gd);
            _channelSampleSelectedSizeList.add(lValue);

            // Sample max size
            // l = new Label(parent, SWT.NONE);
            // l.setText(" / ");
            // l.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1,
            // 1));
            lValue = new Label(parent, SWT.BORDER | SWT.RIGHT);
            lValue.setText("" + channel.getSamplesTotal()); //$NON-NLS-1$
            lValue.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
            // Unit
            // l = new Label(parent, SWT.NONE);
            // l.setText("Unit: ");
            // l.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1,
            // 1));
            Text unitText = new Text(parent, SWT.BORDER);
            gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
            gd.minimumWidth = 45;
            unitText.setLayoutData(gd);
            unitText.setText(channel.getUnit());
            // unitText.setData(i);
            // Formula
            // l = new Label(parent, SWT.NONE);
            // l.setText("Formula: ");
            // l.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1,
            // 1));
            final Text formulaText = new Text(parent, SWT.BORDER | SWT.SEARCH);
            gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
            gd.minimumWidth = 150;
            formulaText.setLayoutData(gd);
            formulaText.setMessage(Messages.SampleFileDialog_45);
            formulaText.addFocusListener(new FocusListener() {

                public void focusGained(final FocusEvent e) {
                    // do nothing
                }

                public void focusLost(final FocusEvent e) {
                    String formula = formulaText.getText().trim();
                    if (formula.length() > 0) {
                        JEP parser = new JEP(); // Create a new parser
                        parser.addStandardFunctions();
                        parser.addStandardConstants();
                        try {
                            parser.addVariable(Messages.SampleFileDialog_JepVariable, 0d);
                            Node node = parser.parseExpression(formula);
                            parser.evaluate(node);
                            formulaText.setBackground(parent.getDisplay().getSystemColor(
                                    SWT.COLOR_LIST_BACKGROUND));

                        } catch (ParseException e1) {
                            formulaText.setBackground(parent.getDisplay().getSystemColor(
                                    SWT.COLOR_RED));
                        }
                    }

                }

            });
            check.addSelectionListener(new SelectionListener() {

                public void widgetDefaultSelected(final SelectionEvent e) {
                    selectionAction();
                }

                public void widgetSelected(final SelectionEvent e) {
                    selectionAction();
                }

                private void selectionAction() {
                    channel.setSelected(check.getSelection());
                    if (check.getSelection()) {
                        _countChannel++;
                    } else {
                        _countChannel--;
                    }
                    _okButton.setEnabled(_countChannel > 0);
                    calulate();
                }

            });
        }
        new Label(parent, SWT.NONE);
        new Label(parent, SWT.NONE);
        new Label(parent, SWT.NONE);
        Label label = new Label(parent, SWT.RIGHT);
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));
        label.setText(Messages.SampleFileDialog_SampleSum);

        _sumLabel = new Label(parent, SWT.BORDER | SWT.RIGHT);
        _sumLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
    }

}
