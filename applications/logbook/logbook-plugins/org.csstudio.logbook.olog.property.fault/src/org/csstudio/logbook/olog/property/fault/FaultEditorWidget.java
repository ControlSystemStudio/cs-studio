package org.csstudio.logbook.olog.property.fault;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.csstudio.apputil.ui.time.DateTimePickerDialog;
import org.csstudio.logbook.olog.property.fault.Fault.BeamLossState;
import org.csstudio.ui.util.dialogs.StringListSelectionDialog;
import org.csstudio.ui.util.widgets.MultipleSelectionCombo;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class FaultEditorWidget extends Composite {

    private Composite composite;

    // general information

    private Label lblFaultId;
    private Label lblFaultID;
    private Text text;

    private Label lblArea;
    private CCombo comboArea;
    private Label lblSubSystem;
    private CCombo comboSubSystem;
    private Label lblDevice;
    private CCombo comboDevice;

    // Fault assignment and ownership
    private Label lblFault;
    private Label lblAssign;
    private CCombo comboAssign;
    private Label lblContact;
    private Text textContact;

    // Time related controls
    private Text textTimeOccoured;
    private Text textTimeCleared;
    private Button btnTimeOccoured;
    private CCombo comboBeamLossStatus;
    private Label lblNewLabel;
    private Label lblTimeCleared;
    private Button btnTimeCleared;
    private Text textBeamLossStart;
    private Button btnBeamLossTime;
    private Label lblRestored;
    private Text textBeamRestoredTime;
    private Button btnBeamRestoredTime;

    // comments
    private Text textCause;
    private Text textRepair;
    private Text textCorrectiveAction;
    private Text textLogIds;
    private Label lblLogIds;
    private Button btnTags;
    private Label lblLogbooks;
    private Label lblTags;
    private Button btnLogbooks;
    private MultipleSelectionCombo<String> multiSelectionComboLogbook;
    private MultipleSelectionCombo<String> multiSelectionComboTag;

    private org.eclipse.swt.graphics.Color redColor;
    private org.eclipse.swt.graphics.Color defaultColor;

    // Configuration information
    private final FaultConfiguration fc;
    private final List<String> availableLogbooks;
    private final List<String> availableTags;

    public FaultEditorWidget(Composite parent, int style, FaultConfiguration fc, List<String> availableLogbooks,
            List<String> availableTags) {
        super(parent, style);
        redColor = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        defaultColor = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);

        this.fc = fc;
        this.availableLogbooks = availableLogbooks;
        this.availableTags = availableTags;
        setLayout(new GridLayout(1, false));

        composite = new Composite(this, SWT.NONE);
        composite.setLayout(new GridLayout(6, false));

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gridData.minimumHeight = 600;
        gridData.minimumWidth = 400;
        composite.setLayoutData(gridData);

        lblFaultId = new Label(composite, SWT.NONE);
        lblFaultId.setText("Fault Id:");

        lblFaultID = new Label(composite, SWT.NONE);
        lblFaultID.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 5, 1));
        lblFaultID.setText("New Fault Report");

        lblArea = new Label(composite, SWT.NONE);
        lblArea.setText("Area:*");

        comboArea = new CCombo(composite, SWT.BORDER);
        comboArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
        comboArea.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                fault.setArea(comboArea.getItem(comboArea.getSelectionIndex()));
            }
        });

        lblSubSystem = new Label(composite, SWT.NONE);
        lblSubSystem.setAlignment(SWT.RIGHT);
        lblSubSystem.setText("Sub System:*");

        comboSubSystem = new CCombo(composite, SWT.BORDER);
        comboSubSystem.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
        comboSubSystem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                fault.setSubsystem(comboSubSystem.getItem(comboSubSystem.getSelectionIndex()));
            }
        });

        lblDevice = new Label(composite, SWT.NONE);
        lblDevice.setAlignment(SWT.RIGHT);
        lblDevice.setText("Device:*");

        comboDevice = new CCombo(composite, SWT.BORDER);
        comboDevice.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
        comboDevice.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                fault.setDevice(comboDevice.getItem(comboDevice.getSelectionIndex()));
            }
        });

        lblFault = new Label(composite, SWT.NONE);
        lblFault.setText("Fault:*");

        text = new Text(composite, SWT.BORDER | SWT.V_SCROLL);
        GridData gd_text = new GridData(SWT.FILL, SWT.FILL, true, true, 5, 3);
        gd_text.widthHint = 312;
        text.setLayoutData(gd_text);
        text.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                fault.setDescription(text.getText());
            }
        });
        new Label(composite, SWT.NONE);
        new Label(composite, SWT.NONE);
        
                lblAssign = new Label(composite, SWT.NONE);
                lblAssign.setText("Assign:");
        
                comboAssign = new CCombo(composite, SWT.BORDER);
                comboAssign.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
                comboAssign.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        String assigned = comboAssign.getItem(comboAssign.getSelectionIndex());
                        fault.setAssigned(assigned);
                        Optional<FaultConfiguration.Group> assignedGroup = fc.getGroups().stream()
                                .filter(new Predicate<FaultConfiguration.Group>() {
                                    @Override
                                    public boolean test(FaultConfiguration.Group t) {
                                        return t.getName().equals(assigned);
                                    }
                                }).findFirst();
                        assignedGroup.ifPresent(group -> {
                            fault.setContact(group.getOwner() + "<" + group.getContact() + ">");
                            textContact.setText(group.getOwner() + "<" + group.getContact() + ">");
                        });

                    }
                });
        
                lblContact = new Label(composite, SWT.NONE);
                lblContact.setText("Contact:");
        
                textContact = new Text(composite, SWT.BORDER);
                textContact.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        labelTime = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
        labelTime.setText("Time:");
        labelTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1));

        lblNewLabel = new Label(composite, SWT.NONE);
        lblNewLabel.setText("Occurred:*");

        textTimeOccoured = new Text(composite, SWT.BORDER);
        textTimeOccoured.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        textTimeOccoured.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    Instant i = LocalDateTime.parse(textTimeOccoured.getText()).atZone(ZoneId.systemDefault())
                            .toInstant();
                    textTimeOccoured.setForeground(defaultColor);
                    fault.setFaultOccuredTime(i);
                } catch (Exception e1) {
                    textTimeOccoured.setForeground(redColor);
                }
            }
        });
        textTimeOccoured.setToolTipText("example: 2014-11-01T22:07:24");

        btnTimeOccoured = new Button(composite, SWT.NONE);
        btnTimeOccoured.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Runnable openSearchDialog = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Display.getDefault().asyncExec(new Runnable() {
                                public void run() {
                                    DateTimePickerDialog dialog = new DateTimePickerDialog(getShell());
                                    dialog.setDateTime(Date.from(fault.getFaultOccuredTime() == null ? Instant.now()
                                            : fault.getFaultOccuredTime()));
                                    dialog.setBlockOnOpen(true);
                                    if (dialog.open() == IDialogConstants.OK_ID) {
                                        fault.setFaultOccuredTime(dialog.getDateTime().toInstant());
                                        textTimeOccoured.setText(ZonedDateTime
                                                .ofInstant(dialog.getDateTime().toInstant(), ZoneId.systemDefault())
                                                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                                    }
                                }
                            });
                        } catch (final Exception e) {
                            Display.getDefault().asyncExec(new Runnable() {

                                @Override
                                public void run() {
                                    // errorBar.setException(e);
                                }
                            });
                        }
                    }
                };
                BusyIndicator.showWhile(Display.getDefault(), openSearchDialog);
            }
        });
        btnTimeOccoured.setText("...");

        lblTimeCleared = new Label(composite, SWT.NONE);
        lblTimeCleared.setText("Cleared:");

        textTimeCleared = new Text(composite, SWT.BORDER);
        textTimeCleared.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        textTimeCleared.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    Instant i = LocalDateTime.parse(textTimeCleared.getText()).atZone(ZoneId.systemDefault())
                            .toInstant();
                    textTimeCleared.setForeground(defaultColor);
                    fault.setFaultClearedTime(i);
                } catch (Exception e1) {
                    textTimeCleared.setForeground(redColor);
                }
            }
        });
        textTimeCleared.setToolTipText("example: 2014-11-01T22:07:24");

        btnTimeCleared = new Button(composite, SWT.NONE);
        btnTimeCleared.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Runnable openSearchDialog = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Display.getDefault().asyncExec(new Runnable() {
                                public void run() {
                                    DateTimePickerDialog dialog = new DateTimePickerDialog(getShell());
                                    dialog.setDateTime(Date.from(fault.getFaultClearedTime() == null ? Instant.now()
                                            : fault.getFaultClearedTime()));
                                    dialog.setBlockOnOpen(true);
                                    if (dialog.open() == IDialogConstants.OK_ID) {
                                        fault.setFaultClearedTime(dialog.getDateTime().toInstant());
                                        textTimeCleared.setText(ZonedDateTime
                                                .ofInstant(dialog.getDateTime().toInstant(), ZoneId.systemDefault())
                                                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                                    }
                                }
                            });
                        } catch (final Exception e) {
                            Display.getDefault().asyncExec(new Runnable() {

                                @Override
                                public void run() {
                                    // errorBar.setException(e);
                                }
                            });
                        }
                    }
                };
                BusyIndicator.showWhile(Display.getDefault(), openSearchDialog);
            }
        });
        btnTimeCleared.setText("...");

        Label lblBeamlost = new Label(composite, SWT.NONE);
        lblBeamlost.setText("Beam Lost:*");

        comboBeamLossStatus = new CCombo(composite, SWT.BORDER);
        comboBeamLossStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        comboBeamLossStatus.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String selection = comboBeamLossStatus.getItem(comboBeamLossStatus.getSelectionIndex());
                fault.setBeamLossState(BeamLossState.valueOf(selection));
                switch (selection) {
                case "True":
                    btnBeamLossTime.setEnabled(true);
                    textBeamLossStart.setEnabled(true);
                    btnBeamRestoredTime.setEnabled(true);
                    textBeamRestoredTime.setEnabled(true);
                    break;
                default:
                    btnBeamLossTime.setEnabled(false);
                    textBeamLossStart.setEnabled(false);
                    btnBeamRestoredTime.setEnabled(false);
                    textBeamRestoredTime.setEnabled(false);
                    break;
                }
            }
        });
        new Label(composite, SWT.NONE);
        new Label(composite, SWT.NONE);
        new Label(composite, SWT.NONE);

        Label lblBeamLossTime = new Label(composite, SWT.NONE);
        lblBeamLossTime.setText("Start:");

        textBeamLossStart = new Text(composite, SWT.BORDER);
        textBeamLossStart.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        textBeamLossStart.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    Instant i = LocalDateTime.parse(textBeamLossStart.getText()).atZone(ZoneId.systemDefault())
                            .toInstant();
                    textBeamLossStart.setForeground(defaultColor);
                    fault.setBeamlostTime(i);
                } catch (Exception e1) {
                    textBeamLossStart.setForeground(redColor);
                }
            }
        });
        textBeamLossStart.setToolTipText("example: 2014-11-01T22:07:24");

        btnBeamLossTime = new Button(composite, SWT.NONE);
        btnBeamLossTime.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Runnable openSearchDialog = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Display.getDefault().asyncExec(new Runnable() {
                                public void run() {
                                    DateTimePickerDialog dialog = new DateTimePickerDialog(getShell());
                                    dialog.setDateTime(Date.from(
                                            fault.getBeamlostTime() == null ? Instant.now() : fault.getBeamlostTime()));
                                    dialog.setBlockOnOpen(true);
                                    if (dialog.open() == IDialogConstants.OK_ID) {
                                        fault.setBeamlostTime(dialog.getDateTime().toInstant());
                                        textBeamLossStart.setText(ZonedDateTime
                                                .ofInstant(dialog.getDateTime().toInstant(), ZoneId.systemDefault())
                                                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                                    }
                                }
                            });
                        } catch (final Exception e) {
                            Display.getDefault().asyncExec(new Runnable() {

                                @Override
                                public void run() {
                                    // errorBar.setException(e);
                                }
                            });
                        }
                    }
                };
                BusyIndicator.showWhile(Display.getDefault(), openSearchDialog);
            }
        });
        btnBeamLossTime.setText("...");

        lblRestored = new Label(composite, SWT.NONE);
        lblRestored.setText("Restored:");

        textBeamRestoredTime = new Text(composite, SWT.BORDER);
        textBeamRestoredTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        textBeamRestoredTime.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    Instant i = LocalDateTime.parse(textBeamRestoredTime.getText()).atZone(ZoneId.systemDefault())
                            .toInstant();
                    textBeamRestoredTime.setForeground(defaultColor);
                    fault.setBeamRestoredTime(i);
                } catch (Exception e1) {
                    textBeamRestoredTime.setForeground(redColor);
                }
            }
        });
        textBeamRestoredTime.setToolTipText("example: 2014-11-01T22:07:24");

        btnBeamRestoredTime = new Button(composite, SWT.NONE);
        btnBeamRestoredTime.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Runnable openSearchDialog = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Display.getDefault().asyncExec(new Runnable() {
                                public void run() {
                                    DateTimePickerDialog dialog = new DateTimePickerDialog(getShell());
                                    dialog.setDateTime(Date.from(fault.getBeamRestoredTime() == null ? Instant.now()
                                            : fault.getBeamRestoredTime()));
                                    dialog.setBlockOnOpen(true);
                                    if (dialog.open() == IDialogConstants.OK_ID) {
                                        fault.setBeamRestoredTime(dialog.getDateTime().toInstant());
                                        textBeamRestoredTime.setText(ZonedDateTime
                                                .ofInstant(dialog.getDateTime().toInstant(), ZoneId.systemDefault())
                                                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                                    }
                                }
                            });
                        } catch (final Exception e) {
                            Display.getDefault().asyncExec(new Runnable() {

                                @Override
                                public void run() {
                                    // errorBar.setException(e);
                                }
                            });
                        }
                    }
                };
                BusyIndicator.showWhile(Display.getDefault(), openSearchDialog);
            }
        });
        btnBeamRestoredTime.setText("...");

        labelComments = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
        labelComments.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1));
        labelComments.setText("Comments:");

        Label lblCause = new Label(composite, SWT.NONE);
        lblCause.setText("Cause:");

        textCause = new Text(composite, SWT.BORDER);
        textCause.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 2));
        textCause.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                fault.setRootCause(textCause.getText());
            }
        });
        new Label(composite, SWT.NONE);

        Label lblRepair = new Label(composite, SWT.NONE);
        lblRepair.setText("Repair:");

        textRepair = new Text(composite, SWT.BORDER);
        textRepair.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 2));
        textRepair.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                fault.setRepairAction(textRepair.getText());
            }
        });
        new Label(composite, SWT.NONE);

        Label lblCorrectiveAction = new Label(composite, SWT.NONE);
        lblCorrectiveAction.setText("Corrective:");

        textCorrectiveAction = new Text(composite, SWT.BORDER);
        textCorrectiveAction.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 2));
        textCorrectiveAction.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                fault.setCorrectiveAction(textCorrectiveAction.getText());
            }
        });
        new Label(composite, SWT.NONE);

        labelLogs = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
        labelLogs.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1));
        labelLogs.setText("Logs");

        lblLogIds = new Label(composite, SWT.NONE);
        lblLogIds.setText("Log Ids:");

        textLogIds = new Text(composite, SWT.BORDER);
        textLogIds.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
        textLogIds.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                fault.setLogIds(Arrays.asList(textLogIds.getText().split(";")).stream().map(Integer::valueOf)
                        .collect(Collectors.toList()));
            }
        });

        lblLogbooks = new Label(composite, SWT.NONE);
        lblLogbooks.setText("Logbooks:");

        multiSelectionComboLogbook = new MultipleSelectionCombo<String>(composite, SWT.NONE);
        multiSelectionComboLogbook.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        multiSelectionComboLogbook.addPropertyChangeListener(e -> {
            if (e.getPropertyName().equals("selection")) {
                logbooks = multiSelectionComboLogbook.getSelection();
            }
        });

        btnLogbooks = new Button(composite, SWT.NONE);
        btnLogbooks.setText("...");
        btnLogbooks.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // Open a dialog which allows users to select tags
                StringListSelectionDialog dialog = new StringListSelectionDialog(parent.getShell(), availableLogbooks,
                        logbooks, "Add Logbooks");
                if (dialog.open() == IDialogConstants.OK_ID) {
                    logbooks = dialog.getSelectedValues();
                    multiSelectionComboLogbook.setSelection(dialog.getSelectedValues());
                }
            }
        });

        lblTags = new Label(composite, SWT.NONE);
        lblTags.setText("Tags:");

        multiSelectionComboTag = new MultipleSelectionCombo<String>(composite, SWT.NONE);
        multiSelectionComboTag.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        multiSelectionComboTag.addPropertyChangeListener(e -> {
            if (e.getPropertyName().equals("selection")) {
                tags = multiSelectionComboTag.getSelection();
            }
        });

        btnTags = new Button(composite, SWT.NONE);
        btnTags.setText("...");
        btnTags.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // Open a dialog which allows users to select tags
                StringListSelectionDialog dialog = new StringListSelectionDialog(parent.getShell(), availableTags, tags,
                        "Add Tags");
                if (dialog.open() == IDialogConstants.OK_ID) {
                    tags = dialog.getSelectedValues();
                    multiSelectionComboTag.setSelection(dialog.getSelectedValues());
                }
            }
        });
        initialize();
    }

    /**
     * Initialize the widget with the defaults form the
     * 
     */
    private void initialize() {
        comboArea.setItems(fc.getAreas().toArray(new String[fc.getAreas().size()]));
        comboSubSystem.setItems(fc.getSubsystems().toArray(new String[fc.getSubsystems().size()]));
        comboDevice.setItems(fc.getDevices().toArray(new String[fc.getDevices().size()]));

        comboAssign.setItems(fc.getGroups().stream().map(FaultConfiguration.Group::getName).toArray(String[]::new));
        comboBeamLossStatus.setItems(
                Arrays.asList(BeamLossState.values()).stream().map(BeamLossState::toString).toArray(String[]::new));

        multiSelectionComboLogbook.setItems(availableLogbooks);
        multiSelectionComboTag.setItems(availableTags);

        updateUI();
    }

    // Model
    private Fault fault = new Fault();
    private List<String> logbooks = Collections.emptyList();
    private List<String> tags = Collections.emptyList();
    private Label labelTime;
    private Label labelComments;
    private Label labelLogs;

    /**
     * 
     */
    private void updateUI() {
        if (fault.getId() != 0) {
            lblFaultID.setText(String.valueOf(fault.getId()));
        } else {
            lblFaultID.setText("New Fault Report");
        }
        comboArea.setText(fault.getArea() != null ? fault.getArea() : "");
        comboSubSystem.setText(fault.getSubsystem() != null ? fault.getSubsystem() : "");
        comboDevice.setText(fault.getDevice() != null ? fault.getDevice() : "");

        text.setText(fault.getDescription() != null ? fault.getDescription() : "");

        comboAssign.setText(fault.getAssigned() != null ? fault.getAssigned() : "");
        textContact.setText(fault.getContact() != null ? fault.getContact() : "");

        if (fault.getFaultOccuredTime() == null) {
            fault.setFaultOccuredTime(Instant.now());
        }
        textTimeOccoured.setText(fault.getFaultOccuredTime() != null
                ? ZonedDateTime.ofInstant(fault.getFaultOccuredTime(), ZoneId.systemDefault())
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : "");
        textTimeCleared.setText(fault.getFaultClearedTime() != null
                ? ZonedDateTime.ofInstant(fault.getFaultClearedTime(), ZoneId.systemDefault())
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : "");
        if (fault.getBeamLossState() != null) {
            comboBeamLossStatus.setText(fault.getBeamLossState().toString());
            switch (comboBeamLossStatus.getItem(comboBeamLossStatus.getSelectionIndex())) {
            case "True":
                btnBeamLossTime.setEnabled(true);
                textBeamLossStart.setEnabled(true);
                btnBeamRestoredTime.setEnabled(true);
                textBeamRestoredTime.setEnabled(true);
                break;
            default:
                btnBeamLossTime.setEnabled(false);
                textBeamLossStart.setEnabled(false);
                btnBeamRestoredTime.setEnabled(false);
                textBeamRestoredTime.setEnabled(false);
                break;
            }
        }

        textBeamLossStart.setText(fault.getBeamlostTime() != null
                ? ZonedDateTime.ofInstant(fault.getBeamlostTime(), ZoneId.systemDefault())
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : "");
        textBeamRestoredTime.setText(fault.getBeamRestoredTime() != null
                ? ZonedDateTime.ofInstant(fault.getBeamlostTime(), ZoneId.systemDefault())
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : "");

        textCause.setText(fault.getRootCause() != null ? fault.getRootCause() : "");
        textRepair.setText(fault.getRepairAction() != null ? fault.getRepairAction() : "");
        textCorrectiveAction.setText(fault.getCorrectiveAction() != null ? fault.getCorrectiveAction() : "");

        textLogIds.setText(String.join(";",
                fault.getLogIds().stream().sorted().map(String::valueOf).collect(Collectors.toList())));

        multiSelectionComboLogbook.setSelection(logbooks);
        multiSelectionComboTag.setSelection(tags);
    }

    public Fault getFault() {
        return this.fault;
    }

    public void setFault(Fault fault) {
        this.fault = fault;
        updateUI();
    }

    public List<String> getLogbooks() {
        return logbooks;
    }

    public void setLogbooks(List<String> logbooks) {
        this.logbooks = logbooks;
        multiSelectionComboLogbook.setSelection(this.logbooks);
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
        multiSelectionComboTag.setSelection(this.tags);
    }

    public void setLogIds(List<Integer> logIds) {
        this.fault.setLogIds(logIds);
        updateUI();
    }

    public List<String> getLogIds() {
        return this.fault.getLogIds().stream().map(String::valueOf).collect(Collectors.toList());
    }

    @Override
    public void dispose() {
        super.dispose();
        redColor.dispose();
        defaultColor.dispose();
    }
}
