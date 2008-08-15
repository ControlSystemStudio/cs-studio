package org.csstudio.config.kryonamebrowser.ui.filter;

import org.csstudio.config.kryonamebrowser.logic.KryoNameBrowserLogic;
import org.csstudio.config.kryonamebrowser.model.resolved.KryoNameResolved;
import org.csstudio.config.kryonamebrowser.ui.UIModelBridge;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FilterComposite extends Composite {

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

	public void setViewer(TableViewer viewer) {
		this.viewer = viewer;
	}

	public TableViewer getViewer() {
		return viewer;
	}

	public void setLogic(KryoNameBrowserLogic logic) {

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
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);

		Composite composite;

		Composite composite_1;
		composite = new Composite(this, SWT.NONE);
		composite.setLayout(new RowLayout());
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true));

		plant = new Combo(composite, SWT.NONE);
		plant.setToolTipText("Plant");

		plantNo = new Text(composite, SWT.BORDER);
		final RowData rd_plantNo = new RowData();
		rd_plantNo.width = 30;
		plantNo.setLayoutData(rd_plantNo);
		plantNo.setTextLimit(10);

		subplant1 = new Combo(composite, SWT.NONE);
		subplant1.setEnabled(false);

		plant1No = new Text(composite, SWT.BORDER);
		final RowData rd_plant1No = new RowData();
		rd_plant1No.width = 30;
		plant1No.setLayoutData(rd_plant1No);
		plant1No.setTextLimit(10);

		subplant2 = new Combo(composite, SWT.NONE);
		subplant2.setEnabled(false);

		subplant2No = new Text(composite, SWT.BORDER);
		final RowData rd_subplant2No = new RowData();
		rd_subplant2No.width = 30;
		subplant2No.setLayoutData(rd_subplant2No);
		subplant2No.setTextLimit(10);

		subplant3 = new Combo(composite, SWT.NONE);
		subplant3.setEnabled(false);

		subplant3No = new Text(composite, SWT.BORDER);
		final RowData rd_subplant3No = new RowData();
		rd_subplant3No.width = 30;
		subplant3No.setLayoutData(rd_subplant3No);
		subplant3No.setTextLimit(10);
		composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true));
		composite_1.setLayout(new FillLayout());

		object = new Combo(composite_1, SWT.NONE);

		function = new Combo(composite_1, SWT.NONE);
		function.setVisibleItemCount(15);

		subfunction = new Combo(composite_1, SWT.NONE);

		process = new Combo(composite_1, SWT.NONE);

		processNo = new Text(composite_1, SWT.BORDER);
		processNo.setTextLimit(2);
		searchButton = new Button(composite_1, SWT.NONE);
		searchButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateTable(parent.getShell());

			}

		});

		searchButton.setText("search");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public KryoNameResolved getSearchExample() {
		return bridge.calculateExampleEntry();

	}

	public void updateTable(final Shell shell) {

		searchButton.setEnabled(false);
		searchButton.setText("Loading...");

		shell.getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				viewer.setInput(bridge.calculateExampleEntry());

			}
		});

		searchButton.setText("Search");
		searchButton.setEnabled(true);
	}

}
