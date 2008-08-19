package org.csstudio.config.kryonamebrowser.ui.filter;

import java.sql.SQLException;
import java.util.List;

import org.csstudio.config.kryonamebrowser.logic.KryoNameBrowserLogic;
import org.csstudio.config.kryonamebrowser.model.resolved.KryoNameResolved;
import org.csstudio.config.kryonamebrowser.ui.UIModelBridge;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FilterComposite extends Composite {

	private Text searchExpression;
	private Text subplant3No;
	private Text subplant2No;
	private Text plant1No;
	private Text plantNo;
	private Combo subplant3;
	private Combo subplant2;
	private Combo subplant1;
	private Combo plant;
	private Combo process;
	private Combo subfunction;
	private Text processNo;
	private Combo object;
	private Combo function;
	private TableViewer viewer;
	private UIModelBridge bridge;
	private Button searchButton;
	private KryoNameBrowserLogic logic;
	private boolean advancedMode;

	public void setViewer(TableViewer viewer) {
		this.viewer = viewer;
	}

	public TableViewer getViewer() {
		return viewer;
	}

	public void setLogic(KryoNameBrowserLogic logic) {

		this.logic = logic;
		bridge = new UIModelBridge();
		bridge.registerPlant(plant, plantNo);
		bridge.registerSubPlant1(subplant1, plant1No);
		bridge.registerSubPlant2(subplant2, subplant2No);
		bridge.registerSubPlant3(subplant3, subplant3No);
		bridge.registerObject(object);
		bridge.registerFunction(function);
		bridge.registerSubfunction(subfunction);
		bridge.registerProcess(process);
		bridge.registerKryoNumber(processNo);

		bridge.setLogic(logic);

		bridge.bind();
	}

	/**
	 * Create the composite
	 * 
	 * @param parent
	 * @param style
	 */
	public FilterComposite(final Composite parent, int style) {
		super(parent, style);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);

		Composite composite_15;

		Composite composite_16;
		composite_15 = new Composite(this, SWT.NONE);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.verticalSpacing = 0;
		gridLayout_1.horizontalSpacing = 0;
		gridLayout_1.marginHeight = 0;
		gridLayout_1.marginWidth = 0;
		composite_15.setLayout(gridLayout_1);
		final GridData gd_composite_15 = new GridData(SWT.FILL, SWT.FILL, true,
				true);
		gd_composite_15.minimumHeight = 100;
		composite_15.setLayoutData(gd_composite_15);

		advancedPanel = new Composite(composite_15, SWT.NONE);
		advancedPanel.setLayout(new FillLayout());
		final GridData gd_advancedPanel = new GridData(SWT.FILL, SWT.CENTER,
				true, true);
		advancedPanel.setLayoutData(gd_advancedPanel);

		Label searchStringLabel;

		final Composite composite_1 = new Composite(advancedPanel, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.VERTICAL));

		final Label label_1 = new Label(composite_1, SWT.NONE);
		searchStringLabel = new Label(composite_1, SWT.NONE);
		searchStringLabel.setText("Search expression");

		final Composite composite = new Composite(advancedPanel, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.VERTICAL));

		final Label eg12Label = new Label(composite, SWT.NONE);
		eg12Label.setText("e.g. %:%12 or XI%:T%");

		searchExpression = new Text(composite, SWT.BORDER);
		basicTop = new Composite(composite_15, SWT.NONE);
		final GridData gd_basicTop = new GridData(SWT.LEFT, SWT.CENTER, true,
				false);
		basicTop.setLayoutData(gd_basicTop);
		final RowLayout rowLayout = new RowLayout();
		rowLayout.spacing = 0;
		rowLayout.marginTop = 0;
		rowLayout.marginRight = 0;
		rowLayout.marginLeft = 0;
		rowLayout.marginBottom = 0;
		rowLayout.fill = true;
		basicTop.setLayout(rowLayout);

		KeyAdapter adapter = new KeyAdapter() {
			public void keyReleased(final KeyEvent e) {
				if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					updateTable(parent.getShell());
				}
			}
		};

		final Composite composite_2 = new Composite(basicTop, SWT.NONE);
		composite_2.setLayout(new RowLayout(SWT.VERTICAL));

		final Label plantLabel = new Label(composite_2, SWT.NONE);
		plantLabel.setText("Plant");

		plant = new Combo(composite_2, SWT.READ_ONLY);
		plant.setToolTipText("Plant");

		final Composite composite_3 = new Composite(basicTop, SWT.NONE);
		composite_3.setLayout(new RowLayout(SWT.VERTICAL));

		final Label noLabel = new Label(composite_3, SWT.NONE);
		noLabel.setText("No");

		plantNo = new Text(composite_3, SWT.BORDER);
		plantNo.addKeyListener(adapter);
		final RowData rd_plantNo = new RowData();
		rd_plantNo.width = 50;
		plantNo.setLayoutData(rd_plantNo);
		plantNo.setTextLimit(10);

		final Composite composite_4 = new Composite(basicTop, SWT.NONE);
		composite_4.setLayout(new RowLayout(SWT.VERTICAL));

		final Label subPlant1Label = new Label(composite_4, SWT.NONE);
		subPlant1Label.setText("Sub plant 1");

		subplant1 = new Combo(composite_4, SWT.READ_ONLY);
		subplant1.setEnabled(false);

		final Composite composite_5 = new Composite(basicTop, SWT.NONE);
		composite_5.setLayout(new RowLayout(SWT.VERTICAL));

		final Label noLabel_1 = new Label(composite_5, SWT.NONE);
		noLabel_1.setText("No");

		plant1No = new Text(composite_5, SWT.BORDER);
		plant1No.addKeyListener(adapter);
		final RowData rd_plant1No = new RowData();
		rd_plant1No.width = 50;
		plant1No.setLayoutData(rd_plant1No);
		plant1No.setTextLimit(10);

		final Composite composite_6 = new Composite(basicTop, SWT.NONE);
		composite_6.setLayout(new RowLayout(SWT.VERTICAL));

		final Label subPlant2Label = new Label(composite_6, SWT.NONE);
		subPlant2Label.setText("Sub plant 2");

		subplant2 = new Combo(composite_6, SWT.READ_ONLY);
		subplant2.setEnabled(false);

		final Composite composite_7 = new Composite(basicTop, SWT.NONE);
		composite_7.setLayout(new RowLayout(SWT.VERTICAL));

		final Label noLabel_2 = new Label(composite_7, SWT.NONE);
		noLabel_2.setText("No");

		subplant2No = new Text(composite_7, SWT.BORDER);
		subplant2No.addKeyListener(adapter);
		final RowData rd_subplant2No = new RowData();
		rd_subplant2No.width = 50;
		subplant2No.setLayoutData(rd_subplant2No);
		subplant2No.setTextLimit(10);

		final Composite composite_8 = new Composite(basicTop, SWT.NONE);
		composite_8.setLayout(new RowLayout(SWT.VERTICAL));

		final Label subPlant3Label = new Label(composite_8, SWT.NONE);
		subPlant3Label.setText("Sub plant 3");

		subplant3 = new Combo(composite_8, SWT.READ_ONLY);
		subplant3.setEnabled(false);

		final Composite composite_9 = new Composite(basicTop, SWT.NONE);
		composite_9.setLayout(new RowLayout(SWT.VERTICAL));

		final Label noLabel_3 = new Label(composite_9, SWT.NONE);
		noLabel_3.setText("No");

		subplant3No = new Text(composite_9, SWT.BORDER);
		subplant3No.addKeyListener(adapter);
		final RowData rd_subplant3No = new RowData();
		rd_subplant3No.width = 50;
		subplant3No.setLayoutData(rd_subplant3No);
		subplant3No.setTextLimit(10);
		basicBottom = new Composite(composite_15, SWT.NONE);
		basicBottom.setLayout(new RowLayout());
		final GridData gd_basicBottom = new GridData(SWT.FILL, SWT.FILL, true,
				true);
		basicBottom.setLayoutData(gd_basicBottom);

		final Composite composite_10 = new Composite(basicBottom, SWT.NONE);
		composite_10.setLayout(new RowLayout(SWT.VERTICAL));

		final Label objectLabel = new Label(composite_10, SWT.NONE);
		objectLabel.setText("Object");

		object = new Combo(composite_10, SWT.READ_ONLY);

		final Composite composite_11 = new Composite(basicBottom, SWT.NONE);
		composite_11.setLayout(new RowLayout(SWT.VERTICAL));

		final Label functionLabel = new Label(composite_11, SWT.NONE);
		functionLabel.setText("Function");

		function = new Combo(composite_11, SWT.READ_ONLY);
		function.setVisibleItemCount(15);

		final Composite composite_12 = new Composite(basicBottom, SWT.NONE);
		composite_12.setLayout(new RowLayout(SWT.VERTICAL));

		final Label subfunctionLabel = new Label(composite_12, SWT.NONE);
		subfunctionLabel.setText("Subfunction");

		subfunction = new Combo(composite_12, SWT.READ_ONLY);

		final Composite composite_13 = new Composite(basicBottom, SWT.NONE);
		composite_13.setLayout(new RowLayout(SWT.VERTICAL));

		final Label processLabel = new Label(composite_13, SWT.NONE);
		processLabel.setText("Process");

		process = new Combo(composite_13, SWT.READ_ONLY);

		process.addKeyListener(adapter);

		final Composite composite_14 = new Composite(basicBottom, SWT.NONE);
		composite_14.setLayout(new RowLayout(SWT.VERTICAL));

		final Label sequenceNoLabel = new Label(composite_14, SWT.NONE);
		sequenceNoLabel.setText("Sequence No");

		processNo = new Text(composite_14, SWT.BORDER);
		processNo.addKeyListener(adapter);
		final RowData rd_processNo = new RowData();
		rd_processNo.width = 80;
		processNo.setLayoutData(rd_processNo);
		processNo.setTextLimit(2);
		composite_16 = new Composite(this, SWT.NONE);
		final GridData gd_composite_16 = new GridData(SWT.CENTER, SWT.CENTER,
				true, true);
		gd_composite_16.minimumWidth = 90;
		composite_16.setLayoutData(gd_composite_16);
		final RowLayout rowLayout_2 = new RowLayout();
		rowLayout_2.spacing = 0;
		rowLayout_2.marginTop = 0;
		rowLayout_2.marginRight = 0;
		rowLayout_2.marginLeft = 0;
		rowLayout_2.marginBottom = 0;
		composite_16.setLayout(rowLayout_2);
		searchButton = new Button(composite_16, SWT.NONE);
		final RowData rd_searchButton = new RowData();
		rd_searchButton.width = 80;
		searchButton.setLayoutData(rd_searchButton);
		searchButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateTable(parent.getShell());

			}

		});

		searchButton.setText("Search");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public KryoNameResolved getSearchExample() {
		return bridge.calculateExampleEntry();

	}

	private KryoNameResolved exampleEntry;
	private Composite basicTop;
	private Composite basicBottom;
	private Composite advancedPanel;
	private String searchText;

	/**
	 * Updates the table using the values from the filter. This method is safe to call from non-UI thread.
	 * 
	 * @param shell
	 */
	public synchronized void updateTable(final Shell shell) {

		// must access UI stuff via UI thread
		shell.getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				searchButton.setEnabled(false);
				searchButton.setText("Loading...");
				viewer.getTable().setEnabled(false);
				exampleEntry = bridge.calculateExampleEntry();
				searchText = searchExpression.getText();
			}
		});

		// do the update without blocking the UI
		new Thread(new Runnable() {
			public void run() {
				try {

					if (advancedMode) {
						final List<KryoNameResolved> search = logic
								.search(searchText);
						shell.getDisplay().syncExec(new Runnable() {
							public void run() {
								viewer.setInput(search);
								searchButton.setText("Search");
								searchButton.setEnabled(true);
								viewer.getTable().setEnabled(true);
							}
						});
					} else {
						final List<KryoNameResolved> search = logic
								.search(exampleEntry);
						shell.getDisplay().syncExec(new Runnable() {
							public void run() {
								viewer.setInput(search);
								searchButton.setText("Search");
								searchButton.setEnabled(true);
								viewer.getTable().setEnabled(true);
							}
						});
					}

				} catch (SQLException e) {
					MessageDialog.openError(shell, "Error", e.getMessage());
					shell.getDisplay().syncExec(new Runnable() {
						public void run() {
							searchButton.setText("Search");
							searchButton.setEnabled(true);
							viewer.getTable().setEnabled(true);
						}
					});
				}

			}
		}).start();

	}

	public void advancedMode() {
		GridData data = (GridData) advancedPanel.getLayoutData();

		advancedMode = true;
		basicTop.setVisible(false);
		basicBottom.setVisible(false);
		advancedPanel.setVisible(true);

	}

	public void basicMode() {
		GridData data = (GridData) advancedPanel.getLayoutData();
		data.exclude = false;
		advancedMode = false;
		basicTop.setVisible(true);
		basicBottom.setVisible(true);
		advancedPanel.setVisible(false);
	}

}
