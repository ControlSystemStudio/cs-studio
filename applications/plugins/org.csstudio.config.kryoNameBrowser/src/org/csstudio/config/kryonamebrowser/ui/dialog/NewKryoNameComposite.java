package org.csstudio.config.kryonamebrowser.ui.dialog;

import org.csstudio.config.kryonamebrowser.logic.KryoNameBrowserLogic;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.swtdesigner.SWTResourceManager;

public class NewKryoNameComposite extends TitleAreaDialog {

	private Combo combo_7;
	private Combo combo_6;
	private Combo combo_5;
	private Combo combo_4;
	private Combo combo_3;
	private Combo combo_2;
	private Combo combo_1;
	private Combo combo;
	private Text kryoNum;
	private Text subplant3No;
	private Text subplant2No;
	private Text plantNo;
	private Text subplant1No;
	private KryoNameBrowserLogic logic;
	private Label nameLabel;

	public NewKryoNameComposite(Shell parentShell) {
		super(parentShell);
	}

	public void setLogic(KryoNameBrowserLogic logic) {
		this.logic = logic;

	}

	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		// Set the title
		setTitle("Add New");
		// Set the message
		setMessage("You can add a new  Kryo Name here",
				IMessageProvider.INFORMATION);
		return contents;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// return super.createDialogArea(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		parent.setLayout(layout);

		nameLabel = new Label(parent, SWT.NONE);
		final GridData gd_nameLabel = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		nameLabel.setLayoutData(gd_nameLabel);
		nameLabel.setText("123");
		nameLabel.setFont(SWTResourceManager.getFont("", 20, SWT.NONE));

		final Group plants = new Group(parent, SWT.NONE);
		plants.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		plants.setLayout(gridLayout);
		new Label(plants, SWT.NONE);

		final Label plantLabel = new Label(plants, SWT.NONE);
		plantLabel.setLayoutData(new GridData());
		plantLabel.setText("Plant");

		final Label noLabel = new Label(plants, SWT.NONE);
		noLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		noLabel.setText("No");

		Label label1 = new Label(plants, SWT.BORDER);
		label1.setText("Plant");

		final ComboViewer plant = new ComboViewer(plants, SWT.BORDER);
		combo = plant.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		plantNo = new Text(plants, SWT.BORDER);
		plantNo.setLayoutData(new GridData());
		Label label2 = new Label(plants, SWT.BORDER);
		label2.setText("Sub plant 1");
		
		
		

		final ComboViewer subplant1 = new ComboViewer(plants, SWT.BORDER);
		combo_1 = subplant1.getCombo();
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		subplant1No = new Text(plants, SWT.BORDER);

		final Label subPlant2Label = new Label(plants, SWT.NONE);
		subPlant2Label.setText("Sub plant 2");

		final ComboViewer subplant2 = new ComboViewer(plants, SWT.BORDER);
		combo_2 = subplant2.getCombo();
		combo_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		subplant2No = new Text(plants, SWT.BORDER);
		final GridData gd_subplant2No = new GridData(SWT.LEFT, SWT.CENTER,
				true, false);
		subplant2No.setLayoutData(gd_subplant2No);

		final Label subPlant3Label = new Label(plants, SWT.NONE);
		subPlant3Label.setText("Sub plant 3");

		final ComboViewer subplant3 = new ComboViewer(plants, SWT.BORDER);
		combo_3 = subplant3.getCombo();
		combo_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		subplant3No = new Text(plants, SWT.BORDER);
		final GridData gd_subplant3No = new GridData(SWT.LEFT, SWT.CENTER,
				true, false);
		subplant3No.setLayoutData(gd_subplant3No);
		new Label(parent, SWT.NONE);

		final Group objects = new Group(parent, SWT.NONE);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		objects.setLayout(gridLayout_1);

		final Label objectLabel = new Label(objects, SWT.NONE);
		objectLabel.setText("Object");

		final ComboViewer object = new ComboViewer(objects, SWT.BORDER);
		combo_4 = object.getCombo();
		combo_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Label functionLabel = new Label(objects, SWT.NONE);
		functionLabel.setText("Function");

		final ComboViewer function = new ComboViewer(objects, SWT.BORDER);
		combo_5 = function.getCombo();
		combo_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Label subFunctionLabel = new Label(objects, SWT.NONE);
		subFunctionLabel.setText("Sub function");

		final ComboViewer subfunction = new ComboViewer(objects, SWT.BORDER);
		combo_6 = subfunction.getCombo();
		combo_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Group proc = new Group(parent, SWT.NONE);
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 3;
		proc.setLayout(gridLayout_2);

		final Label label_1 = new Label(proc, SWT.NONE);
		new Label(proc, SWT.NONE);

		final Label noLabel_1 = new Label(proc, SWT.NONE);
		noLabel_1.setText("Kryo Num");

		final Label processLabel = new Label(proc, SWT.NONE);
		processLabel.setText("Process");

		final ComboViewer process = new ComboViewer(proc, SWT.BORDER);
		
		combo_7 = process.getCombo();
		combo_7.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		kryoNum = new Text(proc, SWT.BORDER);
		final GridData gd_kryoNum = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		kryoNum.setLayoutData(gd_kryoNum);

		return parent;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		((GridLayout) parent.getLayout()).numColumns += 2;
		Button addButton = new Button(parent, SWT.PUSH);
		addButton.setText("Add");
		addButton.setFont(JFaceResources.getDialogFont());
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (subplant1No.getText().length() != 0) {
					close();
				} else {
					setErrorMessage("Please maintain the last name");
				}
			}
		});

		Button closeButton = new Button(parent, SWT.PUSH);
		closeButton.setText("Close");
		closeButton.setFont(JFaceResources.getDialogFont());
		closeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				close();

			}
		});
	}

	private void updateNameLabel() {

	}

}
