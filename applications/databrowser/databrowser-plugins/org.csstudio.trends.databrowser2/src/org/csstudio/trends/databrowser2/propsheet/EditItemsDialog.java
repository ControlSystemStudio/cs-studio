package org.csstudio.trends.databrowser2.propsheet;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.swt.rtplot.TraceType;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.AxisConfig;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.csstudio.trends.databrowser2.model.PVItem;
import org.csstudio.trends.databrowser2.model.RequestType;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog to edit PVItem and FormulaItem.
 * @author Takashi Nakamoto
 */
public class EditItemsDialog extends Dialog
{
    /**
     * Edit result of this dialog.
     * @author Takashi Nakamoto
     */
    public class Result {
        private boolean visible;
        private String item;
        private String displayName;
        private RGB color;
        private double scan;
        private int bufferSize;
        private int width;
        private AxisConfig axis;
        private TraceType traceType;
        private RequestType request;
        private int index;

        private boolean applyVisible = false;
        private boolean applyItem = false;
        private boolean applyDisplayName = false;
        private boolean applyColor = false;
        private boolean applyScan = false;
        private boolean applyBufferSize = false;
        private boolean applyWidth = false;
        private boolean applyAxis = false;
        private boolean applyTraceType = false;
        private boolean applyRequest = false;
        private boolean applyIndex = false;

        public boolean isVisible() { return visible; }
        public String getItem() { return item; }
        public String getDisplayName() { return displayName; }
        public RGB getColor() { return color; }
        public double getScan() { return scan; }
        public int getBufferSize() { return bufferSize; }
        public int getWidth() { return width; }
        public AxisConfig getAxis() { return axis; }
        public TraceType getTraceType() { return traceType; }
        public RequestType getRequest() { return request; }
        public int getIndex() { return index; }

        public boolean appliedVisible() { return applyVisible; }
        public boolean appliedItem() { return applyItem; }
        public boolean appliedDisplayName() { return applyDisplayName; }
        public boolean appliedColor() { return applyColor; }
        public boolean appliedScan() { return applyScan; }
        public boolean appliedBufferSize() { return applyBufferSize; }
        public boolean appliedWidth() { return applyWidth; }
        public boolean appliedAxis() { return applyAxis; }
        public boolean appliedTraceType() { return applyTraceType; }
        public boolean appliedRequest() { return applyRequest; }
        public boolean appliedIndex() { return applyIndex; }
    }

    /**
     * Implementation of VerifyListener to check if the entered text is a numerical value.
     * @author Takashi Nakamoto
     */
    private class NumericalVerifyListener implements VerifyListener {
        private Text text = null;
        private boolean isDouble = false;

        /**
         * Constructor.
         * @param text SWT Text widget to be verified.
         * @param isDouble True to check if the entered text is a double value.
         *                 Flase to check if is an integer value.
         */
        public NumericalVerifyListener(Text text, boolean isDouble) {
            this.text = text;
            this.isDouble = isDouble;
        }

        @Override
        public void verifyText(VerifyEvent e) {
            try {
                final String str  = text.getText();
                final String pre = str.substring(0, e.start);
                final String app = str.substring(e.end, str.length());
                final String res = pre + e.text + app;
                if (res.isEmpty()) {
                    e.doit = true;
                    return;
                }
                if (isDouble)
                    Double.parseDouble(res);
                else
                    Integer.parseInt(res);
                e.doit = true;
            } catch (NumberFormatException ex) {
                e.doit = false;
            }
        }
    }

    /** The instance that represents the result of this dialog. */
    private Result result = null;

    /** Subjected items that will be edited by this dialog. */
    private List<ModelItem> items;

    final private List<AxisConfig> axes;

    private Button chkApplyShow = null;
    private Button chkApplyItem = null;
    private Button chkApplyDisplayName = null;
    private Button chkApplyColor = null;
    private Button chkApplyScan = null;
    private Button chkApplyBufferSize = null;
    private Button chkApplyWidth = null;
    private Button chkApplyAxis = null;
    private Button chkApplyTraceType = null;
    private Button chkApplyRequest = null;
    private Button chkApplyIndex = null;

    private Button chkShow = null;
    private Text textItem = null;
    private Text textDisplayName = null;
    private ColorBlob blobColor = null;
    private Text textScan = null;
    private Text textBufferSize = null;
    private Text textWidth = null;
    private Combo cmbAxis = null;
    private Combo cmbTraceType = null;
    private Combo cmbRequest = null;
    private Text textIndex = null;

    /**
     * Initialize this dialog.
     * @param parent Parent shell for dialog.
     * @param items Subjected items that will be edited by this dialog.
     */
    public EditItemsDialog(final Shell parent, final List<ModelItem> items, final Model model)
    {
        super(parent);
        this.items = items;
        axes = new ArrayList<>();
        for (AxisConfig axis : model.getAxes())
            axes.add(axis);
    }

    // Set the title of this dialog.
    @Override
    protected void configureShell(final Shell newShell)
    {
        super.configureShell(newShell);
        newShell.setText(Messages.EditItems);
    }

    // Make this dialog resizable.
    @Override
    protected boolean isResizable()
    {
        return true;
    }

    @Override
    protected Control createDialogArea(final Composite parent)
    {
        final Composite composite = (Composite)super.createDialogArea(parent);
        composite.setLayout(new GridLayout(3, false));

        Label label = new Label(composite, SWT.NONE);
        label.setText(Messages.ApplyChanges);
        label.setLayoutData(new GridData(SWT.LEFT, 0, true, false, 3, 1));

        // Show property
        chkApplyShow = new Button(composite, SWT.CHECK);
        chkApplyShow.setLayoutData(new GridData());

        label = new Label(composite, SWT.NONE);
        label.setText(Messages.TraceVisibility);
        label.setLayoutData(new GridData());

        chkShow = new Button(composite, SWT.CHECK);
        chkShow.setToolTipText(Messages.TraceVisibilityTT);
        if (! items.isEmpty())
            chkShow.setSelection(items.get(0).isVisible());
        chkShow.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                chkApplyShow.setSelection(true);
            }
        });
        chkShow.setLayoutData(new GridData());

        // Item property
        chkApplyItem = new Button(composite, SWT.CHECK);
        chkApplyItem.setLayoutData(new GridData());

        label = new Label(composite, SWT.NONE);
        label.setText(Messages.ItemName);
        label.setLayoutData(new GridData());

        textItem = new Text(composite, SWT.SINGLE | SWT.BORDER);
        textItem.setToolTipText(Messages.ItemNameTT);
        if (! items.isEmpty())
            textItem.setText(items.get(0).getName());
        textItem.addModifyListener((ModifyEvent e) -> chkApplyItem.setSelection(true));
        textItem.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        // Display name property
        chkApplyDisplayName = new Button(composite, SWT.CHECK);
        chkApplyDisplayName.setLayoutData(new GridData());

        label = new Label(composite, SWT.NONE);
        label.setText(Messages.TraceDisplayName);
        label.setLayoutData(new GridData());

        textDisplayName = new Text(composite, SWT.SINGLE | SWT.BORDER);
        textDisplayName.setToolTipText(Messages.TraceDisplayNameTT);
        if (! items.isEmpty())
            textDisplayName.setText(items.get(0).getDisplayName());
        textDisplayName.addModifyListener((ModifyEvent e) -> chkApplyDisplayName.setSelection(true));
        textDisplayName.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        // Color property
        chkApplyColor = new Button(composite, SWT.CHECK);
        chkApplyColor.setLayoutData(new GridData());

        label = new Label(composite, SWT.NONE);
        label.setText(Messages.Color);
        label.setLayoutData(new GridData());

        blobColor = new ColorBlob(composite, new RGB(0, 0, 0));
        blobColor.setToolTipText(Messages.ColorTT);
        if (! items.isEmpty())
            blobColor.setColor(items.get(0).getColor());
        blobColor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                ColorDialog dialog = new ColorDialog(getShell());
                RGB color = dialog.open();
                if (color != null) {
                    blobColor.setColor(color);
                    chkApplyColor.setSelection(true);
                }
            }
        });
        GridData gd = new GridData(SWT.LEFT, 0, true, false);
        gd.widthHint = 40;
        gd.heightHint = 15;
        blobColor.setLayoutData(gd);

        // Scan period property
        chkApplyScan = new Button(composite, SWT.CHECK);
        chkApplyScan.setLayoutData(new GridData());

        label = new Label(composite, SWT.NONE);
        label.setText(Messages.ScanPeriod);
        label.setLayoutData(new GridData());

        textScan = new Text(composite, SWT.SINGLE | SWT.BORDER);
        textScan.setToolTipText(Messages.ScanPeriodTT);
        boolean enableScan = false;
        for (ModelItem item : items) {
            if (item instanceof PVItem) {
                textScan.setText(Double.toString(((PVItem)item).getScanPeriod()));
                enableScan = true;
                break;
            }
        }
        chkApplyScan.setEnabled(enableScan);
        textScan.setEnabled(enableScan);
        textScan.addVerifyListener(new NumericalVerifyListener(textScan, true));
        textScan.addModifyListener((ModifyEvent e) -> chkApplyScan.setSelection(true));
        textScan.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        // Buffer size property
        chkApplyBufferSize = new Button(composite, SWT.CHECK);
        chkApplyBufferSize.setLayoutData(new GridData());

        label = new Label(composite, SWT.NONE);
        label.setText(Messages.LiveSampleBufferSize);
        label.setLayoutData(new GridData());

        textBufferSize = new Text(composite, SWT.SINGLE | SWT.BORDER);
        boolean enableBufferSize = false;
        for (ModelItem item : items) {
            if (item instanceof PVItem) {
                textBufferSize.setText(Integer.toString(((PVItem)item).getLiveCapacity()));
                enableBufferSize = true;
                break;
            }
        }
        chkApplyBufferSize.setEnabled(enableBufferSize);
        textBufferSize.setEnabled(enableBufferSize);
        textBufferSize.addVerifyListener(new NumericalVerifyListener(textBufferSize, false));
        textBufferSize.addModifyListener((ModifyEvent e) -> chkApplyBufferSize.setSelection(true));
        textBufferSize.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        // Width property
        chkApplyWidth = new Button(composite, SWT.CHECK);
        chkApplyWidth.setLayoutData(new GridData());

        label = new Label(composite, SWT.NONE);
        label.setText(Messages.TraceLineWidth);
        label.setLayoutData(new GridData());

        textWidth = new Text(composite, SWT.SINGLE | SWT.BORDER);
        textWidth.setToolTipText(Messages.TraceLineWidthTT);
        if (! items.isEmpty())
            textWidth.setText(Integer.toString(items.get(0).getLineWidth()));
        textWidth.addVerifyListener(new NumericalVerifyListener(textWidth, false));
        textWidth.addModifyListener((ModifyEvent e) -> chkApplyWidth.setSelection(true));
        textWidth.setLayoutData(new GridData(SWT.FILL, 0, true, false));


        // Axis property
        chkApplyAxis = new Button(composite, SWT.CHECK);
        chkApplyAxis.setLayoutData(new GridData());

        label = new Label(composite, SWT.NONE);
        label.setText(Messages.Axis);
        label.setLayoutData(new GridData());

        cmbAxis = new Combo(composite, SWT.READ_ONLY);
        cmbAxis.setToolTipText(Messages.AxisTT);
        int i = 0;
        for (AxisConfig axis : axes)
        {
            cmbAxis.add(axis.getName());
            if (items.size() >= 0 && items.get(0).getAxisIndex() == i)
                cmbAxis.select(i);
            ++i;
        }
        cmbAxis.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                chkApplyAxis.setSelection(true);
            }
        });
        cmbAxis.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        // Trace type property
        chkApplyTraceType = new Button(composite, SWT.CHECK);
        chkApplyTraceType.setLayoutData(new GridData());

        label = new Label(composite, SWT.NONE);
        label.setText(Messages.TraceType);
        label.setLayoutData(new GridData());

        cmbTraceType = new Combo(composite, SWT.READ_ONLY);
        cmbTraceType.setToolTipText(Messages.TraceTypeTT);
        for (i = 0; i < TraceType.values().length; i++) {
            TraceType type = TraceType.values()[i];
            cmbTraceType.add(type.toString());
            if (items.size() >= 1 && type == items.get(0).getTraceType())
                cmbTraceType.select(i);
        }
        cmbTraceType.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                chkApplyTraceType.setSelection(true);
            }
        });
        cmbTraceType.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        // Request property
        chkApplyRequest = new Button(composite, SWT.CHECK);
        chkApplyRequest.setLayoutData(new GridData());

        label = new Label(composite, SWT.NONE);
        label.setText(Messages.RequestType);
        label.setLayoutData(new GridData());

        cmbRequest = new Combo(composite, SWT.READ_ONLY);
        cmbRequest.setToolTipText(Messages.RequestTypeTT);
        RequestType defaultType = RequestType.OPTIMIZED;
        boolean enableRequest = false;
        for (ModelItem item : items) {
            if (item instanceof PVItem) {
                defaultType = ((PVItem)item).getRequestType();
                enableRequest = true;
                break;
            }
        }
        chkApplyRequest.setEnabled(enableRequest);
        cmbRequest.setEnabled(enableRequest);
        if (enableRequest) {
            for (i = 0; i<RequestType.values().length; i++) {
                RequestType type = RequestType.values()[i];
                cmbRequest.add(type.toString());
                if (type == defaultType)
                    cmbRequest.select(i);
            }
        }
        cmbRequest.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                chkApplyRequest.setSelection(true);
            }
        });
        cmbRequest.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        // Index property
        chkApplyIndex = new Button(composite, SWT.CHECK);
        chkApplyIndex.setLayoutData(new GridData());

        label = new Label(composite, SWT.NONE);
        label.setText(Messages.WaveformIndex);
        label.setLayoutData(new GridData());

        textIndex = new Text(composite, SWT.SINGLE | SWT.BORDER);
        textIndex.setToolTipText(Messages.WaveformIndexColTT);
        if (! items.isEmpty())
            textIndex.setText(Integer.toString(items.get(0).getWaveformIndex()));
        textIndex.addVerifyListener(new NumericalVerifyListener(textIndex, false));
        textIndex.addModifyListener((ModifyEvent e) -> chkApplyIndex.setSelection(true));
        textIndex.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        return composite;
    }

    @Override
    protected void buttonPressed(final int buttonId)
    {
        // Save the result for later use.
        if (IDialogConstants.OK_ID == buttonId)
        {
            result = new Result();

            result.applyVisible = chkApplyShow.getSelection();
            result.applyItem = chkApplyItem.getSelection();
            result.applyDisplayName = chkApplyDisplayName.getSelection();
            result.applyColor = chkApplyColor.getSelection();
            result.applyScan = chkApplyScan.getSelection();
            result.applyBufferSize = chkApplyBufferSize.getSelection();
            result.applyWidth = chkApplyWidth.getSelection();
            result.applyAxis = chkApplyAxis.getSelection();
            result.applyTraceType = chkApplyTraceType.getSelection();
            result.applyRequest = chkApplyRequest.getSelection();
            result.applyIndex = chkApplyIndex.getSelection();

            result.visible = chkShow.getSelection();
            result.item = textItem.getText();
            result.displayName = textDisplayName.getText();
            result.color = blobColor.getColor();
            try
            {
                result.scan = Double.parseDouble(textScan.getText());
            }
            catch (NumberFormatException ex)
            {
                result.applyScan = false;
            }
            try
            {
                result.bufferSize = Integer.parseInt(textBufferSize.getText());
            }
            catch (NumberFormatException ex)
            {
                result.applyBufferSize = false;
            }
            try
            {
                result.width = Integer.parseInt(textWidth.getText());
            }
            catch (NumberFormatException ex)
            {
                result.applyWidth = false;
            }
            result.axis = axes.get(cmbAxis.getSelectionIndex());
            result.traceType = TraceType.values()[cmbTraceType.getSelectionIndex()];
            result.request = RequestType.values()[cmbRequest.getSelectionIndex()];
            try
            {
                result.index = Integer.parseInt(textIndex.getText());
            }
            catch (NumberFormatException ex)
            {
                result.applyIndex = false;
            }
        }

        super.buttonPressed(buttonId);
    }

    /**
     * Get the result of this dialog. This method returns null if the dialog is not closed yet,
     * or if the dialog is closed with "Cancel" button.
     * @return The instance of result.
     */
    public Result getResult()
    {
        return result;
    }
}
