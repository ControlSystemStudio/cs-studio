package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.AxisConfig;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.csstudio.trends.databrowser2.model.PVItem;
import org.csstudio.trends.databrowser2.model.RequestType;
import org.csstudio.trends.databrowser2.model.TraceType;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
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
public class EditItemsDialog extends Dialog {
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
	private ModelItem[] items;
	
	/** Subjected model */
	private Model model;
	
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
	public EditItemsDialog(Shell parent, ModelItem[] items, Model model) {
		super(parent);
		this.items = items;
		this.model = model;
	}
	
	protected Point getInitialSize() {
		// TODO: Adjust the size of this dialog more appropriately.
		return new Point(600, 600);
	}
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		
		// Set the title of this dialog.
		newShell.setText(Messages.EditItem);
	}
	
	@Override
	protected boolean isResizable() {
		// Make this dialog resizable.
		return true;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		composite.setLayout(new GridLayout(3, false));

		Label labelApply = new Label(composite, SWT.NONE);
		labelApply.setText(Messages.ApplyChanges);
		
		Label labelEmpty = new Label(composite, SWT.NONE);
		labelEmpty.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		labelEmpty.setLayoutData(gridData);
		
		// Show property
		chkApplyShow = new Button(composite, SWT.CHECK);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
		chkApplyShow.setLayoutData(gridData);
		
		Label labelShow = new Label(composite, SWT.NONE);
		labelShow.setText(Messages.TraceVisibility);
		
		chkShow = new Button(composite, SWT.CHECK);
		if (items.length >= 1)
			chkShow.setSelection(items[0].isVisible());
		chkShow.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				chkApplyShow.setSelection(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		// Item property
		chkApplyItem = new Button(composite, SWT.CHECK);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
		chkApplyItem.setLayoutData(gridData);

		Label labelItem = new Label(composite, SWT.NONE);
		labelItem.setText(Messages.ItemName);
		
		textItem = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textItem.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (items.length >= 1)
			textItem.setText(items[0].getName());
		textItem.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				chkApplyItem.setSelection(true);
			}
		});
		
		// Display name property
		chkApplyDisplayName = new Button(composite, SWT.CHECK);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
		chkApplyDisplayName.setLayoutData(gridData);
		
		Label labelDisplayName = new Label(composite, SWT.NONE);
		labelDisplayName.setText(Messages.TraceDisplayName);
		
		textDisplayName = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textDisplayName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (items.length >= 1)
			textDisplayName.setText(items[0].getDisplayName());
		textDisplayName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				chkApplyDisplayName.setSelection(true);
			}
		});
		// Color property
		chkApplyColor = new Button(composite, SWT.CHECK);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
		chkApplyColor.setLayoutData(gridData);
		
		Label labelColor = new Label(composite, SWT.NONE);
		labelColor.setText(Messages.Color);
		
		blobColor = new ColorBlob(composite, new RGB(0, 0, 0));
		if (items.length >= 1)
			blobColor.setColor(items[0].getColor());
		blobColor.addMouseListener(new MouseListener() {
			@Override
			public void mouseDown(MouseEvent e) {
				ColorDialog dialog = new ColorDialog(getShell());
				RGB color = dialog.open();
				if (color != null) {
					blobColor.setColor(color);
					chkApplyColor.setSelection(true);
				}
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {}

			@Override
			public void mouseUp(MouseEvent e) {}
		});
		
		// Scan period property
		chkApplyScan = new Button(composite, SWT.CHECK);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
		chkApplyScan.setLayoutData(gridData);
		
		Label labelScan = new Label(composite, SWT.NONE);
		labelScan.setText(Messages.ScanPeriod);
		
		textScan = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textScan.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
		textScan.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				chkApplyScan.setSelection(true);
			}
		});
		// Buffer size property 
		chkApplyBufferSize = new Button(composite, SWT.CHECK);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
		chkApplyBufferSize.setLayoutData(gridData);
		
		Label labelBufferSize = new Label(composite, SWT.NONE);
		labelBufferSize.setText(Messages.LiveSampleBufferSize);
		
		textBufferSize = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textBufferSize.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
		textBufferSize.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				chkApplyBufferSize.setSelection(true);
			}
		});
		
		// Width property
		chkApplyWidth = new Button(composite, SWT.CHECK);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
		chkApplyWidth.setLayoutData(gridData);
		
		Label labelWidth = new Label(composite, SWT.NONE);
		labelWidth.setText(Messages.TraceLineWidth);
		
		textWidth = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textWidth.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (items.length >= 1)
			textWidth.setText(Integer.toString(items[0].getLineWidth()));
		textWidth.addVerifyListener(new NumericalVerifyListener(textWidth, false));
		textWidth.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				chkApplyWidth.setSelection(true);
			}
		});
		
		// Axis property
		chkApplyAxis = new Button(composite, SWT.CHECK);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
		chkApplyAxis.setLayoutData(gridData);

		Label labelAxis = new Label(composite, SWT.NONE);
		labelAxis.setText(Messages.Axis);
		
		cmbAxis = new Combo(composite, SWT.READ_ONLY);
		for (int i = 0; i < model.getAxisCount(); i++) {
			cmbAxis.add(model.getAxis(i).getName());
			if (items.length >= 0 && items[0].getAxisIndex() == i)
				cmbAxis.select(i);
		}
		cmbAxis.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				chkApplyAxis.setSelection(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		// Trace type property
		chkApplyTraceType = new Button(composite, SWT.CHECK);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
		chkApplyTraceType.setLayoutData(gridData);

		Label labelTraceType = new Label(composite, SWT.NONE);
		labelTraceType.setText(Messages.TraceType);
		
		cmbTraceType = new Combo(composite, SWT.READ_ONLY);
		for (int i = 0; i < TraceType.values().length; i++) {
			TraceType type = TraceType.values()[i];
			cmbTraceType.add(type.toString());
			if (items.length >= 1 && type == items[0].getTraceType())
				cmbTraceType.select(i);
		}
		cmbTraceType.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				chkApplyTraceType.setSelection(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		// Request property
		chkApplyRequest = new Button(composite, SWT.CHECK);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
		chkApplyRequest.setLayoutData(gridData);

		Label labelRequest = new Label(composite, SWT.NONE);
		labelRequest.setText(Messages.RequestType);
		
		cmbRequest = new Combo(composite, SWT.READ_ONLY);
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
			for (int i = 0; i<RequestType.values().length; i++) {
				RequestType type = RequestType.values()[i];
				cmbRequest.add(type.toString());
				if (type == defaultType)
					cmbRequest.select(i);
			}
		}
		cmbRequest.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				chkApplyRequest.setSelection(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		// Index property
		chkApplyIndex = new Button(composite, SWT.CHECK);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
		chkApplyIndex.setLayoutData(gridData);

		Label labelIndex = new Label(composite, SWT.NONE);
		labelIndex.setText(Messages.WaveformIndex);
		
		textIndex = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textIndex.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (items.length >= 1)
			textIndex.setText(Integer.toString(items[0].getWaveformIndex()));
		textIndex.addVerifyListener(new NumericalVerifyListener(textIndex, false));
		textIndex.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				chkApplyIndex.setSelection(true);
			}
		});
		
		return composite;
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		// Save the result for later use.
		if (IDialogConstants.OK_ID == buttonId) {
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
			try {
				result.scan = Double.parseDouble(textScan.getText());
			} catch (NumberFormatException ex) {
				result.applyScan = false;
			}
			try {
				result.bufferSize = Integer.parseInt(textBufferSize.getText());
			} catch (NumberFormatException ex) {
				result.applyBufferSize = false;
			}
			try {
				result.width = Integer.parseInt(textBufferSize.getText());
			} catch (NumberFormatException ex) {
				result.applyWidth = false;
			}
			result.axis = model.getAxis(cmbAxis.getSelectionIndex());
			result.traceType = TraceType.values()[cmbTraceType.getSelectionIndex()];
			result.request = RequestType.values()[cmbRequest.getSelectionIndex()];
			try {
				result.index = Integer.parseInt(textIndex.getText());
			} catch (NumberFormatException ex) {
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
	public Result getResult() {
		return result;
	}
}
