package org.csstudio.config.kryonamebrowser.ui.filter;

import java.util.List;

import org.csstudio.config.kryonamebrowser.logic.KryoNameBrowserLogic;
import org.csstudio.config.kryonamebrowser.model.entry.KryoNameEntry;
import org.csstudio.config.kryonamebrowser.model.entry.KryoObjectEntry;
import org.csstudio.config.kryonamebrowser.model.entry.KryoPlantEntry;
import org.csstudio.config.kryonamebrowser.model.entry.KryoProcessEntry;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
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
	private KryoNameBrowserLogic logic;

	public void setViewer(TableViewer viewer) {
		this.viewer = viewer;
	}

	public TableViewer getViewer() {
		return viewer;
	}

	public void setLogic(KryoNameBrowserLogic logic) {
		this.logic = logic;

		Binder.bindPlant(plant, logic);
		Binder.bindPlant(plant, subplant1, logic);
		Binder.bindPlant(subplant1, subplant2, logic);
		Binder.bindPlant(subplant2, subplant3, logic);

		Binder.bindObject(object, logic);
		Binder.bindObject(object, function, logic);
		Binder.bindObject(function, subfunction, logic);

		Binder.bindProcess(process, logic);
	}

	/**
	 * Create the composite
	 * 
	 * @param parent
	 * @param style
	 */
	public FilterComposite(Composite parent, int style) {
		super(parent, style);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);


		final Button searchButton;

		Composite composite;

		Composite composite_1;
		composite = new Composite(this, SWT.NONE);
		composite.setLayout(new FillLayout());

		plant = new Combo(composite, SWT.NONE);

		plantNo = new Text(composite, SWT.BORDER);
		plantNo.setTextLimit(2);

		subplant1 = new Combo(composite, SWT.NONE);
		subplant1.setEnabled(false);

		plant1No = new Text(composite, SWT.BORDER);
		plant1No.setTextLimit(2);

		subplant2 = new Combo(composite, SWT.NONE);
		subplant2.setEnabled(false);

		subplant2No = new Text(composite, SWT.BORDER);
		subplant2No.setTextLimit(2);

		subplant3 = new Combo(composite, SWT.NONE);
		subplant3.setEnabled(false);

		subplant3No = new Text(composite, SWT.BORDER);
		subplant3No.setTextLimit(2);
		composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayout(new FillLayout());

		object = new Combo(composite_1, SWT.NONE);

		function = new Combo(composite_1, SWT.NONE);
		function.setVisibleItemCount(15);

		subfunction = new Combo(composite_1, SWT.NONE);

		process = new Combo(composite_1, SWT.NONE);

		processNo = new Text(composite_1, SWT.BORDER);
		processNo.setTextLimit(2);
		searchButton = new Button(composite_1, SWT.NONE);
		searchButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				searchButton.setEnabled(false);

				// create the example Name Entry
				KryoNameEntry example = new KryoNameEntry();

				// get lowest possible selected plant
				if (subplant3.isEnabled() && subplant3.getSelectionIndex() > 0) {
					List<KryoPlantEntry> list = (List<KryoPlantEntry>) subplant3
							.getData();
					example.setPlantId(list.get(
							subplant3.getSelectionIndex()
									+ Binder.BLANK_ENTRY_INDEX_OFFSET).getId());
				} else if (subplant2.isEnabled()
						&& subplant2.getSelectionIndex() > 0) {
					List<KryoPlantEntry> list = (List<KryoPlantEntry>) subplant2
							.getData();
					example.setPlantId(list.get(
							subplant2.getSelectionIndex()
									+ Binder.BLANK_ENTRY_INDEX_OFFSET).getId());
				} else if (subplant1.isEnabled()
						&& subplant1.getSelectionIndex() > 0) {
					List<KryoPlantEntry> list = (List<KryoPlantEntry>) subplant1
							.getData();
					example.setPlantId(list.get(
							subplant1.getSelectionIndex()
									+ Binder.BLANK_ENTRY_INDEX_OFFSET).getId());
				} else if (plant.isEnabled() && plant.getSelectionIndex() > 0) {
					List<KryoPlantEntry> list = (List<KryoPlantEntry>) plant
							.getData();
					example.setPlantId(list.get(
							plant.getSelectionIndex()
									+ Binder.BLANK_ENTRY_INDEX_OFFSET).getId());
				}

				// get lowest possible selected object
				if (subfunction.isEnabled()
						&& subfunction.getSelectionIndex() > 0) {
					List<KryoObjectEntry> list = (List<KryoObjectEntry>) subfunction
							.getData();
					example.setObjectId(list.get(
							subfunction.getSelectionIndex()
									+ Binder.BLANK_ENTRY_INDEX_OFFSET).getId());
				} else if (function.isEnabled()
						&& function.getSelectionIndex() > 0) {
					List<KryoObjectEntry> list = (List<KryoObjectEntry>) function
							.getData();
					example.setObjectId(list.get(
							function.getSelectionIndex()
									+ Binder.BLANK_ENTRY_INDEX_OFFSET).getId());
				} else if (object.isEnabled() && object.getSelectionIndex() > 0) {
					List<KryoObjectEntry> list = (List<KryoObjectEntry>) object
							.getData();
					example.setObjectId(list.get(
							object.getSelectionIndex()
									+ Binder.BLANK_ENTRY_INDEX_OFFSET).getId());
				}

				// get process if applicable
				if (process.isEnabled() && process.getSelectionIndex() > 0) {
					List<KryoProcessEntry> list = (List<KryoProcessEntry>) process
							.getData();
					example.setProcessId(list.get(
							process.getSelectionIndex()
									+ Binder.BLANK_ENTRY_INDEX_OFFSET).getId());
				}

				viewer.setInput(example);
				searchButton.setEnabled(true);

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		searchButton.setText("search");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
