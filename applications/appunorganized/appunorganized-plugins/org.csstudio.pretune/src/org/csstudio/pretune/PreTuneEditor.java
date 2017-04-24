/**
 *
 */
package org.csstudio.pretune;

import static org.diirt.datasource.ExpressionLanguage.channels;
import static org.diirt.datasource.ExpressionLanguage.mapOf;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.csstudio.graphene.MultiAxisLineGraph2DWidget;
import org.csstudio.ui.util.PopupMenuUtil;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.csstudio.utility.pvmanager.widgets.VTableDisplay;
import org.diirt.datasource.PVManager;
import org.diirt.datasource.PVReader;
import org.diirt.datasource.PVReaderEvent;
import org.diirt.datasource.PVReaderListener;
import org.diirt.datasource.PVWriter;
import org.diirt.datasource.PVWriterEvent;
import org.diirt.datasource.PVWriterListener;
import org.diirt.datasource.formula.ExpressionLanguage;
import org.diirt.graphene.InterpolationScheme;
import org.diirt.util.array.ListNumber;
import org.diirt.util.time.TimeDuration;
import org.diirt.vtype.VTable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/**
 * @author Kunal Shroff
 *
 */
public class PreTuneEditor extends EditorPart implements
        PropertyChangeListener, ISelectionProvider {

    @SuppressWarnings("unused")
    public static final String ID = "org.csstudio.pretune";

    // GUI

    private Label label;
    private Composite composite_1;
    private Label lblReferenceStepSize;
    private Label label_1;
    private Label lblScalingFactor;
    private Label label_2;
    private Text text_referenceStepSize;
    private Text text_scalingFactor;
    private Button btnStepUp;
    private Button btnStepDown;
    private Button btnScaleUp;
    private Button btnScaleDown;
    private Composite composite_2;
    private Composite composite;
    private Text text;
    private VTableDisplay tableDisplay;
    private ErrorBar errorBar;
    private Button btnLogConfig;
    private Button btnFileBrowse;
    private Button btnReset;
    private Combo formulaCombo;

    private MultiAxisLineGraph2DWidget widget;

    // Pre defined calculations
    private static final String STEPUP = "${SP} + (${RefStepSize} * ${Weight})";
    private static final String STEPDOWN = "${SP} - (${RefStepSize} * ${Weight})";
    private static final String SCALEUP = "${SP} * ${ScalingFactor}";
    private static final String SCALEDOWN = "${SP} / ${ScalingFactor}";

    // MODEL

    private volatile boolean initialize = true;
    // predefined special column names to parse
    public static final String SetPointPVLabel = "pvsp";
    public static final String ReadbackPointPVLabel = "pvrb";
    public static final String WeightLabel = "config_weight";

    // Potential Formula
    private List<String> formulaStrings = new ArrayList<String>();

    // Configuration read from the json config file
    private PreTuneModel model;

    private List<String> columns = new ArrayList<String>();
    private List<String> plotColumns = new ArrayList<String>();

    private List<String> setPointPvNames = new ArrayList<String>();

    private List<String> readBackPvNames = new ArrayList<String>();

    private List<Double> weights = new ArrayList<Double>();

    private List<String> weightedSetPointPvNames = new ArrayList<String>();
    private Map<String, Double> values = new HashMap<String, Double>();
    private Map<String, Object> initialSetPoints = new HashMap<String, Object>();

    // Pv's associated with this View
    private PVReader<?> pv;
    private PVWriter<Map<String, Object>> pvWriter;

    private Button btnWrite;

    public PreTuneEditor() {
        System.out.println("constructor");
    }


    @Override
    public void init(IEditorSite site, IEditorInput input)
            throws PartInitException {
       setSite(site);
       setInput(input);
    }

    @Override
    public void createPartControl(Composite parent) {
        parent.setLayout(new FormLayout());

        label = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        FormData fd_label = new FormData();
        fd_label.top = new FormAttachment(50);
        fd_label.left = new FormAttachment(0);
        fd_label.right = new FormAttachment(100);
        label.setLayoutData(fd_label);
        label.addMouseMoveListener(new MouseMoveListener() {
            // TODO add upper and lower bounds
            public void mouseMove(MouseEvent e) {
                FormData fd = (FormData) label.getLayoutData();
                int calNumerator = (int) (fd.top.numerator + (e.y * 100)
                        / e.display.getActiveShell().getClientArea().height);
                fd.top = new FormAttachment(calNumerator <= 100 ? calNumerator
                        : 100, fd.top.offset);
                label.setLayoutData(fd);
                label.getParent().layout();
            }
        });
        label.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_SIZENS));

        composite = new Composite(parent, SWT.NONE);
        FormData fd_composite = new FormData();
        fd_composite.bottom = new FormAttachment(label);
        fd_composite.right = new FormAttachment(100);
        fd_composite.top = new FormAttachment(0);
        fd_composite.left = new FormAttachment(0);
        composite.setLayoutData(fd_composite);
        composite.setLayout(new GridLayout(4, false));

        errorBar = new ErrorBar(composite, SWT.NONE);
        errorBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
                4, 1));
        errorBar.setMarginBottom(5);

        Label lblNewLabel = new Label(composite, SWT.NONE);
        lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false, 1, 1));
        lblNewLabel.setText("Config File:");

        text = new Text(composite, SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        btnFileBrowse = new Button(composite, SWT.NONE);
        btnFileBrowse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(getSite().getShell(),
                        SWT.OPEN);
                dialog.setFilterExtensions(new String[] { "*.json", "*.JSON" });
                dialog.setFilterPath(System.getProperty("user.dir"));
                String result = dialog.open();
                text.setText(result);
            }
        });
        btnFileBrowse.setText("Browse");

        btnLogConfig = new Button(composite, SWT.NONE);
        btnLogConfig.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // load the json file
                initialize(text.getText());
            }
        });
        btnLogConfig.setText("Load");
        tableDisplay = new VTableDisplay(composite);
        tableDisplay.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
                4, 1));
        tableDisplay
                .addSelectionChangedListener(new ISelectionChangedListener() {

                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        if (tableDisplay.getVTable() != null) {
                            selection = new StructuredSelection(tableDisplay
                                    .getVTable());
                        }
                    }
                });

        composite_1 = new Composite(composite, SWT.NONE);
        composite_1.setLayout(new GridLayout(7, false));
        composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false, 4, 1));

        lblReferenceStepSize = new Label(composite_1, SWT.NONE);
        lblReferenceStepSize.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
                false, false, 2, 1));
        lblReferenceStepSize.setText("Reference Step Size:");

        label_1 = new Label(composite_1, SWT.SEPARATOR | SWT.VERTICAL);
        label_1.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false,
                1, 3));

        lblScalingFactor = new Label(composite_1, SWT.NONE);
        lblScalingFactor.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
                false, false, 2, 1));
        lblScalingFactor.setText("Scaling Factor:");

        label_2 = new Label(composite_1, SWT.SEPARATOR | SWT.VERTICAL);
        label_2.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, true,
                1, 4));
        new Label(composite_1, SWT.NONE);

        text_referenceStepSize = new Text(composite_1, SWT.BORDER);
        text_referenceStepSize.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    model.setRefStepSize(Double.valueOf(text_referenceStepSize
                            .getText()));
                }
            }
        });
        text_referenceStepSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
                true, false, 2, 1));
        text_referenceStepSize.setEnabled(false);

        text_scalingFactor = new Text(composite_1, SWT.BORDER);
        text_scalingFactor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    model.setScalingFactor(Double.valueOf(text_scalingFactor
                            .getText()));
                }
            }
        });
        text_scalingFactor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
                true, false, 2, 1));
        text_scalingFactor.setEnabled(false);

        btnReset = new Button(composite_1, SWT.NONE);
        btnReset.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
                1, 1));
        btnReset.setSize(50, 25);
        btnReset.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (initialSetPoints != null) {
                    if (pvWriter != null) {
                        pvWriter.write(initialSetPoints);
                    }
                }
            }
        });
        btnReset.setText("Reset");
        btnReset.setEnabled(false);

        btnStepDown = new Button(composite_1, SWT.TOGGLE);
        btnStepDown.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // SetPoint - (ReferenceStepSize * Weight)
                model.setFormula(STEPDOWN);
            }
        });
        btnStepDown.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false, 1, 1));
        btnStepDown.setText("Step Down");
        btnStepDown.setEnabled(false);

        btnStepUp = new Button(composite_1, SWT.TOGGLE);
        btnStepUp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // SetPoint + (ReferenceStepSize * Weight)
                model.setFormula(STEPUP);
            }
        });
        btnStepUp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
                1, 1));
        btnStepUp.setText("Step Up");
        btnStepUp.setEnabled(false);

        btnScaleDown = new Button(composite_1, SWT.TOGGLE);
        btnScaleDown.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // SetPoint + (SetPoint * ScalingFactor)
                model.setFormula(SCALEDOWN);
            }
        });
        btnScaleDown.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false, 1, 1));
        btnScaleDown.setText("Scale Down");
        btnScaleDown.setEnabled(false);

        btnScaleUp = new Button(composite_1, SWT.TOGGLE);
        btnScaleUp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // SetPoint + (SetPoint / ScalingFactor)
                model.setFormula(SCALEUP);
            }
        });
        btnScaleUp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false, 1, 1));
        btnScaleUp.setText("Scale Up");
        btnScaleUp.setEnabled(false);

        btnWrite = new Button(composite_1, SWT.NONE);
        btnWrite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
                2));
        btnWrite.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // write to the list of pv's
                Map<String, Object> values = new HashMap<String, Object>();
                VTable table = tableDisplay.getVTable();
                List<String> names = new ArrayList<String>();
                ListNumber writeValues = null;
                for (int i = 0; i < table.getColumnCount(); i++) {
                    if (table.getColumnName(i).equalsIgnoreCase(
                            SetPointPVLabel + "_name")) {
                        names = (List<String>) table.getColumnData(i);
                    } else if (table.getColumnName(i).equalsIgnoreCase(
                            "weighted_setpoints")) {
                        writeValues = ((ListNumber) table.getColumnData(i));
                    }
                }
                if (writeValues != null && names.size() == writeValues.size()) {
                    for (int j = 0; j < names.size(); j++) {
                        values.put(names.get(j), writeValues.getDouble(j));
                    }
                    if (pvWriter != null) {
                        pvWriter.write(values);
                    }
                }
            }
        });
        btnWrite.setText("Write");
        btnWrite.setEnabled(false);

        composite_2 = new Composite(composite_1, SWT.NONE);
        GridLayout gl_composite_2 = new GridLayout(2, false);
        gl_composite_2.marginHeight = 0;
        gl_composite_2.verticalSpacing = 0;
        gl_composite_2.marginWidth = 0;
        gl_composite_2.horizontalSpacing = 0;
        composite_2.setLayout(gl_composite_2);
        composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false, 5, 1));

        Label lblNewLabel_1 = new Label(composite_2, SWT.NONE);
        lblNewLabel_1.setSize(56, 15);
        lblNewLabel_1.setText("Custom Formula:");

        formulaCombo = new Combo(composite_2, SWT.BORDER);
        formulaCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false, 1, 1));
        formulaCombo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    model.setFormula(formulaCombo.getText());
                }
            }
        });
        formulaCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                model.setFormula(formulaCombo.getText());
            }
        });
        formulaCombo
                .setToolTipText("use the regular pv manager formulas\r\ne.g. ${SP} + (${SP} * ${Weight})\r\nThe ${SP} will be replaced with the set point pv assocaited with the channel\r\nThe ${Weight} will be replaced with the weight of that channel\r\nThe ${RefStepSize} will be replaced with the Reference Step Size\r\nThe ${ScalingFactor} will be replaced with the Scaling Factor");

        formulaStrings.add(SCALEDOWN);
        formulaStrings.add(SCALEUP);
        formulaStrings.add(STEPDOWN);
        formulaStrings.add(STEPUP);

        formulaCombo.setItems(formulaStrings.toArray(new String[formulaStrings
                .size()]));
        // formulaCombo.select(0);

        Composite composite_multi_line = new Composite(parent, SWT.NONE);
        composite_multi_line.setLayout(new FormLayout());
        FormData fd_composite_multi_line = new FormData();
        fd_composite_multi_line.right = new FormAttachment(100);
        fd_composite_multi_line.left = new FormAttachment(0);
        fd_composite_multi_line.bottom = new FormAttachment(100);
        fd_composite_multi_line.top = new FormAttachment(label);
        composite_multi_line.setLayoutData(fd_composite_multi_line);

        widget = new MultiAxisLineGraph2DWidget(composite_multi_line, SWT.None);
        FormData fd_widget = new FormData();
        fd_widget.height = 1;
        fd_widget.width = 1;
        fd_widget.top = new FormAttachment(0);
        fd_widget.bottom = new FormAttachment(100);
        fd_widget.left = new FormAttachment(0);
        fd_widget.right = new FormAttachment(100);
        widget.setLayoutData(fd_widget);

        PopupMenuUtil.installPopupForView(tableDisplay, getEditorSite(), tableDisplay);
        // get the complete table which makes sense for log entries.
        PopupMenuUtil.installPopupForView(tableDisplay, getEditorSite(), this);
        PopupMenuUtil.installPopupForView(widget, getEditorSite(), widget);

        IEditorInput input = getEditorInput();
        if(input instanceof IPathEditorInput){
            String path = ((IPathEditorInput) input).getPath().toString();
            text.setText(path);
            text.setEnabled(false);
            btnFileBrowse.setEnabled(false);
            btnLogConfig.setEnabled(false);
            initialize(path);
        }
    }

    private void initialize(String text) {
        if (model != null) {
            model.removePropertyChangeListener(this);
        }
        // load the json file
        try {
            errorBar.setException(null);
            model = new PreTuneModel(text);
            // enable all the widgets
            text_referenceStepSize.setEnabled(true);
            text_scalingFactor.setEnabled(true);
            btnStepDown.setEnabled(true);
            btnStepUp.setEnabled(true);
            btnScaleDown.setEnabled(true);
            btnScaleUp.setEnabled(true);
            btnReset.setEnabled(true);
            btnWrite.setEnabled(true);

            // initialize the widgets
            text_referenceStepSize.setText(String.valueOf(model
                    .getRefStepSize()));
            text_scalingFactor
                    .setText(String.valueOf(model.getScalingFactor()));
            if (!formulaStrings.contains(model.getFormula())) {
                formulaStrings.add(model.getFormula());
                formulaCombo.setItems(formulaStrings
                        .toArray(new String[formulaStrings.size()]));
            }
            formulaCombo.select(formulaStrings.indexOf(model.getFormula()));
            model.addPropertyChangeListener(this);
            initialize = true;
        } catch (Exception ex) {
            errorBar.setException(ex);
        } finally {
            reconnect();
        }
    }

    private void reconnect() {
        if (pv != null) {
            pv.close();
            pv = null;
        }
        if (pvWriter != null) {
            pvWriter.close();
            pvWriter = null;
        }
        // Build a Formula from the config file, user formula, ....
        StringBuilder pvFormula = new StringBuilder();
        pvFormula.append("=");
        StringBuilder fileTableFormula = new StringBuilder();
        fileTableFormula.append("tableOf(");

        columns = new ArrayList<String>();
        plotColumns = new ArrayList<String>();

        setPointPvNames = new ArrayList<String>();

        readBackPvNames = new ArrayList<String>();

        weights = new ArrayList<Double>();
        weightedSetPointPvNames = new ArrayList<String>();

        for (String columnHeader : model.getColumnHeaders()) {
            int index;
            StringBuilder columnFormula = new StringBuilder();
            switch (columnHeader) {
            case SetPointPVLabel:
                index = model.getColumnHeaders().indexOf(columnHeader);
                for (List<Object> channel : model.getChannels()) {
                    setPointPvNames.add((String) channel.get(index));
                }
                // Create a column to display the pv names
                columnFormula.append("column(\"" + columnHeader + "_name\"");
                columnFormula.append(",");
                columnFormula.append("arrayOf(");
                columnFormula.append(setPointPvNames.stream().map(setPointPv -> {
                    return "\"" + setPointPv + "\"";
                }).collect(Collectors.joining(",")));
                columnFormula.append(")");
                columnFormula.append(")");
                columns.add(columnFormula.toString());
                // Create a column for active PVs
                columnFormula = new StringBuilder();
                columnFormula.append("column(\"" + columnHeader + "\"");
                columnFormula.append(",");
                columnFormula.append("arrayOf(");
                //
                columnFormula.append(setPointPvNames.stream().map(setPointPv -> {
                    return "'" + setPointPv + "'";
                }).collect(Collectors.joining(",")));
                columnFormula.append(")");
                columnFormula.append(")");
                columns.add(columnFormula.toString());
                plotColumns.add(columnFormula.toString());
                break;
            case ReadbackPointPVLabel:
                index = model.getColumnHeaders().indexOf(columnHeader);
                for (List<Object> channel : model.getChannels()) {
                    readBackPvNames.add((String) channel.get(index));
                }
                columnFormula.append("column(\"" + columnHeader + "_name\"");
                columnFormula.append(",");
                columnFormula.append("arrayOf(");
                columnFormula.append(readBackPvNames.stream().map(readbackPv -> {
                    return "\"" + readbackPv + "\"";
                }).collect(Collectors.joining(",")));
                columnFormula.append(")");
                columnFormula.append(")");
                columns.add(columnFormula.toString());
                // Create a active PV array
                columnFormula = new StringBuilder();
                columnFormula.append("column(\"" + columnHeader + "\"");
                columnFormula.append(",");
                columnFormula.append("arrayOf(");
                columnFormula.append(readBackPvNames.stream().map(readbackPv -> {
                    return "'" + readbackPv + "'";
                }).collect(Collectors.joining(",")));
                columnFormula.append(")");
                columnFormula.append(")");
                columns.add(columnFormula.toString());
                plotColumns.add(columnFormula.toString());
                break;
            case WeightLabel:
                index = model.getColumnHeaders().indexOf(columnHeader);
                weights = new ArrayList<Double>();
                for (List<Object> channel : model.getChannels()) {
                    weights.add((Double) channel.get(index));
                }
                columnFormula.append("column(\"" + columnHeader + "\"");
                columnFormula.append(",");
                columnFormula.append("arrayOf(");
                columnFormula.append(weights.stream().map(String::valueOf).collect(Collectors.joining(",")));
                columnFormula.append(")");
                columnFormula.append(")");
                columns.add(columnFormula.toString());
                break;
            default:
                index = model.getColumnHeaders().indexOf(columnHeader);
                List<String> values = new ArrayList<String>();
                for (List<Object> channel : model.getChannels()) {
                    values.add("\"" + channel.get(index) + "\"");
                }
                // Create a active PV array
                columnFormula.append("column(\"" + columnHeader + "\"");
                columnFormula.append(",");
                columnFormula.append("arrayOf(");
                columnFormula.append(values.stream().collect(Collectors.joining(",")));
                columnFormula.append(")");
                columnFormula.append(")");
                columns.add(columnFormula.toString());
                break;
            }
        }

        if (model.getFormula() != null && !model.getFormula().isEmpty()) {
            // Attach a column with the calculated set points
            if (setPointPvNames.size() == weights.size()) {
                for (String pv : setPointPvNames) {
                    String formula = model.getFormula();
                    formula = formula.replaceAll("\\$\\{SP\\}", "'" + pv + "'");
                    formula = formula.replaceAll("\\$\\{Weight\\}", String
                            .valueOf(weights.get(setPointPvNames.indexOf(pv))));
                    formula = formula.replaceAll("\\$\\{ScalingFactor\\}",
                            String.valueOf(model.getScalingFactor()));
                    formula = formula.replaceAll("\\$\\{RefStepSize\\}",
                            String.valueOf(model.getRefStepSize()));
                    weightedSetPointPvNames.add(formula);
                }
            }

            StringBuilder weightedSetPointFormula = new StringBuilder();
            weightedSetPointFormula.append("column(\"weighted_setpoints\"");
            weightedSetPointFormula.append(",");
            weightedSetPointFormula.append("arrayOf(");
            weightedSetPointFormula.append(weightedSetPointPvNames.stream().collect(Collectors.joining(",")));
            weightedSetPointFormula.append(")");
            weightedSetPointFormula.append(")");
            columns.add(weightedSetPointFormula.toString());
        }

        fileTableFormula.append(columns.stream().collect(Collectors.joining(",")));
        fileTableFormula.append(")");

        pvFormula.append(fileTableFormula.toString());

        pv = PVManager
                .read(ExpressionLanguage.formula(pvFormula.toString(),
                        VTable.class)).notifyOn(SWTUtil.swtThread(this))
                .readListener(new PVReaderListener<Object>() {

                    @Override
                    public void pvChanged(final PVReaderEvent<Object> event) {
                        errorBar.setException(event.getPvReader()
                                .lastException());
                        Object value = event.getPvReader().getValue();
                        if (initialize && pv.isConnected()) {
                            storeInitialValue((VTable) value);
                        }
                        tableDisplay.setVTable((VTable) value);
                    }
                }).maxRate(TimeDuration.ofHertz(10));
        pvWriter = PVManager.write(mapOf(channels(setPointPvNames)))
                .writeListener(new PVWriterListener<Map<String, Object>>() {

                    @Override
                    public void pvChanged(
                            final PVWriterEvent<Map<String, Object>> event) {
                        if (event.isExceptionChanged()) {
                            Display.getCurrent().asyncExec(new Runnable() {

                                @Override
                                public void run() {
                                    errorBar.setException(event.getPvWriter()
                                            .lastWriteException());
                                }
                            });
                        }
                    }
                }).async();
        // Create the PV for the multi line plot
        StringBuilder dataFormula = new StringBuilder();
        dataFormula.append("=tableOf(");
        dataFormula.append(plotColumns.stream().collect(Collectors.joining(",")));
        dataFormula.append(")");

        widget.setDataFormula(dataFormula.toString());
        widget.setInterpolation(InterpolationScheme.CUBIC);
        widget.setConfigurable(true);
    }

    @SuppressWarnings("unchecked")
    private void storeInitialValue(VTable value) {
        if (initialize && value != null) {
            List<String> names = new ArrayList<String>();
            List<Object> values = new ArrayList<Object>();
            for (int i = 0; i < value.getColumnCount(); i++) {
                switch (value.getColumnName(i)) {
                case SetPointPVLabel + "_name":
                    names = (List<String>) value.getColumnData(i);
                    break;
                case SetPointPVLabel:
                    Object o = value.getColumnData(i);
                    if (o instanceof List<?>) {
                        if (((List<Object>) o).size() == names.size()) {
                            values = (List<Object>) o;
                        }
                    } else if (o instanceof ListNumber) {
                        ListNumber initValues = ((ListNumber) value
                                .getColumnData(i));
                        if (initValues != null
                                && names.size() == initValues.size()) {
                            for (int j = 0; j < names.size(); j++) {
                                values.add(initValues.getDouble(j));
                            }
                        }
                    }
                    initialize = false;
                    break;
                default:
                    break;
                }
            }
            for (int j = 0; j < names.size(); j++) {
                initialSetPoints.put(names.get(j), values.get(j));
            }
        }
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        text_referenceStepSize.setText(String.valueOf(model.getRefStepSize()));
        text_scalingFactor.setText(String.valueOf(model.getScalingFactor()));
        if (!formulaStrings.contains(model.getFormula())) {
            formulaStrings.add(model.getFormula());
            formulaCombo.setItems(formulaStrings
                    .toArray(new String[formulaStrings.size()]));
        }
        btnScaleDown.setSelection(model.getFormula().equals(SCALEDOWN));
        btnScaleUp.setSelection(model.getFormula().equals(SCALEUP));
        btnStepDown.setSelection(model.getFormula().equals(STEPDOWN));
        btnStepUp.setSelection(model.getFormula().equals(STEPUP));
        formulaCombo.select(formulaStrings.indexOf(model.getFormula()));
        reconnect();
    }

    @Override
    public void dispose() {
        if (pv != null) {
            pv.close();
        }
        if (pvWriter != null) {
            pvWriter.close();
        }
        if (model != null) {
            model.removePropertyChangeListener(this);
        }

    };

    private List<ISelectionChangedListener> selectionChangedListeners = new ArrayList<ISelectionChangedListener>();

    @Override
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        selectionChangedListeners.add(listener);
    }

    private ISelection selection = new StructuredSelection();

    @Override
    public ISelection getSelection() {
        return selection;
    }

    @Override
    public void removeSelectionChangedListener(
            ISelectionChangedListener listener) {
        selectionChangedListeners.remove(listener);
    }

    @Override
    public void setSelection(ISelection selection) {
        this.selection = selection;
        if (selection == null)
            this.selection = new StructuredSelection();
        fireSelectionChangedListener();
    }

    private void fireSelectionChangedListener() {
        for (ISelectionChangedListener listener : selectionChangedListeners) {
            listener.selectionChanged(new SelectionChangedEvent(this,
                    getSelection()));
        }
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        // TODO Auto-generated method stub

    }

    @Override
    public void doSaveAs() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isDirty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        // TODO Auto-generated method stub
        return false;
    }

}
