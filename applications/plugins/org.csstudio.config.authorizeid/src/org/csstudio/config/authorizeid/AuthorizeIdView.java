package org.csstudio.config.authorizeid;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Locale;

import org.csstudio.config.authorizeid.ldap.AuthorizationIdGRManagement;
import org.csstudio.config.authorizeid.ldap.AuthorizationIdManagement;
import org.csstudio.config.authorizeid.ldap.LdapEain;
import org.csstudio.config.authorizeid.ldap.LdapGroups;
import org.csstudio.config.authorizeid.ldap.LdapProp;
import org.csstudio.config.authorizeid.ldap.ObjectClass1;
import org.csstudio.config.authorizeid.ldap.ObjectClass2;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

/**
 * {@code AuthorizeIdView} is a class, which main task is to display GUI of
 * AuthorizeId plugin.
 * @author Rok Povsic
 */
public class AuthorizeIdView extends ViewPart {

	public static final String ID = "org.csstudio.config.authorizeid";//$NON-NLS-1$
	
	private Label label;
	private Combo combo;
	private TableViewer tableViewer1;
	private TableViewer tableViewer2;
	private Table table1;
	private Table table2;

	static final int COL_EAIG = 0;
	static final int COL_EAIR = 1;
	/**
	 * Creates a view for the plugin.
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		Group g = new Group(parent, SWT.NONE);
		g.setText(Messages.AuthorizeIdView_SELECT_GROUP);
		g.setLayout(new FillLayout(SWT.HORIZONTAL));

		label = new Label(g, SWT.CENTER);
		label.setText(Messages.AuthorizeIdView_GROUP);

		combo = new Combo(g, SWT.NONE | SWT.READ_ONLY);

		LdapGroups ld = new LdapGroups();

		String[] groups = new String[] { Messages.AuthorizeIdView_MessageWrong1 };

			try {
				groups = ld.getGroups();
			} catch (Exception e) {
				e.printStackTrace();
			}
	
			for (int i = 0; i < groups.length; i++) {
				combo.add(groups[i]);
			}

		combo.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				refreshTable1();

				table2.removeAll();
				table2.clearAll();

			}

		});

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new RowLayout());

		Composite c1 = new Composite(composite, SWT.FILL);
		createTableViewer1(c1);
		tableViewer1.getTable().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		table1.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				refreshTable2();
			}

		});

		Composite c2 = new Composite(composite, SWT.NONE);
		c2.setLayout(new FillLayout(SWT.VERTICAL));
		createButtons1(c2);


		// is used to create empty space between first set of buttons and
		// second table
		@SuppressWarnings("unused") //$NON-NLS-1$
		Composite emptySpace = new Composite(composite, SWT.NONE);

		Composite c3 = new Composite(composite, SWT.NONE);
		createTableViewer2(c3);
		tableViewer2.getTable().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite c4 = new Composite(composite, SWT.NONE);
		c4.setLayout(new FillLayout(SWT.VERTICAL));
		createButtons2(c4);

		tableViewer2.setContentProvider(new AuthorizeIdContentProvider());
		tableViewer2.setLabelProvider(new AuthorizeIdLabelProvider());

		// TODO ask what this does
		getSite().setSelectionProvider(tableViewer1);

	}

	/**
	 * Creates a first table.
	 * @param parent a composite
	 */
	private void createTableViewer1(Composite parent) {
		tableViewer1 = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tableViewer1.setColumnProperties(new String[] { Messages.AuthorizeIdView_EAIN});

		table1 = tableViewer1.getTable();

		table1.setSize(184, 500);

		table1.setHeaderVisible(true);
		table1.setLinesVisible(true);

		TableColumn column = new TableColumn(table1, SWT.LEFT, 0);
		column.setText(Messages.AuthorizeIdView_EAIN);
		column.setWidth(180);

		column.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				// sort column 1
				TableItem[] items = table1.getItems();
				Collator collator = Collator.getInstance(Locale.getDefault());
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(0);
					for (int j = 0; j < i; j++) {
						String value2 = items[j].getText(0);
						if (collator.compare(value1, value2) < 0) {
							String[] values = { items[i].getText(0),
									items[i].getText(1) };
							items[i].dispose();
							TableItem item = new TableItem(table1, SWT.NONE, j);
							item.setText(values);
							items = table1.getItems();
							break;
						}
					}
				}
			}
		});

	}

	/**
	 * Creates first set of buttons.
	 * @param parenta composite
	 */
	private void createButtons1(final Composite parent) {
		/**
		 * "New" button for the first table.
		 */
		Button _new = new Button(parent, SWT.PUSH);
		_new.setText(Messages.AuthorizeIdView_NEW);
		_new.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (combo.getText().equals("")) { //$NON-NLS-1$
					Status status = new Status(IStatus.ERROR, Messages.AuthorizeIdView_Error,
							0, Messages.AuthorizeIdView_InvalidGroup, null);

					ErrorDialog.openError(
							Display.getCurrent().getActiveShell(),
							Messages.AuthorizeIdView_GroupError, Messages.AuthorizeIdView_GroupErrorDesc,
							status);
				} else {
					InputDialog dialog = new InputDialog(Display.getCurrent()
							.getActiveShell(), Messages.AuthorizeIdView_NEW,
							Messages.AuthorizeIdView_Name, "", //$NON-NLS-1$
							new NewDataValidator());
					if (dialog.open() == Window.OK) {

						String _name = dialog.getValue();
						String _group = combo.getText();

						ObjectClass1 oclass = ObjectClass1.AUTHORIZEID;

						AuthorizationIdManagement nd = new AuthorizationIdManagement();
						nd.insertNewData(_name, _group, oclass);

						refreshTable1();
					}
				}
			}
		});

		/**
		 * "Edit" button for the first table.
		 */
		Button _edit = new Button(parent, SWT.PUSH);
		_edit.setText(Messages.AuthorizeIdView_EDIT);
		_edit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String name = table1.getSelection()[0].getText();
				String _group = combo.getText();

				InputDialog dialog = new InputDialog(Display.getCurrent()
						.getActiveShell(), Messages.AuthorizeIdView_EDIT, Messages.AuthorizeIdView_NameEdit, name,
						new NewDataValidator());
				if (dialog.open() == Window.OK) {

					String _name = dialog.getValue();

					ObjectClass2 oclass2 = ObjectClass2.AUTHORIZEID;
					AuthorizationIdGRManagement ndGr = new AuthorizationIdGRManagement();

					ArrayList<String> eaig = new ArrayList<String>();
					ArrayList<String> eair = new ArrayList<String>();

					for (int i = 0; i < table2.getItemCount(); i++) {
						eaig.add(table2.getItem(i).getText(0));
						eair.add(table2.getItem(i).getText(1));

						ndGr.deleteData(name, eair.get(i), eaig.get(i), _group);
					}

					ObjectClass1 oclass = ObjectClass1.AUTHORIZEID;

					AuthorizationIdManagement nd = new AuthorizationIdManagement();
					nd.deleteData(name, _group);
					nd.insertNewData(_name, _group, oclass);

					for (int i = 0; i < table2.getItemCount(); i++) {

						ndGr.insertNewData(_name, _group, oclass2, eair.get(i),
								eaig.get(i));
					}

					refreshTable1();
				}

			}
		});

		/**
		 * "Delete" button for the first table.
		 */
		Button _delete = new Button(parent, SWT.PUSH);
		_delete.setText(Messages.AuthorizeIdView_DELETE);
		_delete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				String _name = table1.getSelection()[0].getText();
				String _group = combo.getText();

				MessageBox messageBox = new MessageBox(Display.getCurrent()
						.getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				messageBox
						.setMessage(Messages.AuthorizeIdView_DelWarn
								+ Messages.AuthorizeIdView_DelWarn2);
				messageBox.setText(Messages.AuthorizeIdView_DelEntry);
				int response = messageBox.open();
				if (response == SWT.YES) {

					deleteWholeTable2(_name, _group);

					AuthorizationIdManagement aim = new AuthorizationIdManagement();
					aim.deleteData(_name, _group);

					refreshTable1();
					refreshTable2();
					System.out.println("Deleted."); //$NON-NLS-1$

				}
			}
		});
	}

	/**
	 * Creates a second table.
	 * @param parenta composite
	 */
	private void createTableViewer2(Composite parent) {
		tableViewer2 = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tableViewer2.setColumnProperties(new String[] { "2", "3" }); //$NON-NLS-1$ //$NON-NLS-2$

		table2 = tableViewer2.getTable();
		table2.setSize(184 * 2, 500);
		table2.setHeaderVisible(true);
		table2.setLinesVisible(true);

		TableColumn column1 = new TableColumn(table2, SWT.LEFT, COL_EAIG);
		column1.setText(Messages.AuthorizeIdView_EAIG);
		column1.setWidth(180);

		column1.addListener(SWT.Selection, new MyListener(0));

		TableColumn column2 = new TableColumn(table2, SWT.LEFT, COL_EAIR);
		column2.setText(Messages.AuthorizeIdView_EAIR);
		column2.setWidth(180);

		column2.addListener(SWT.Selection, new MyListener(1));

	}

	/**
	 * Listener for sorting columns for second table.
	 */
	private class MyListener implements Listener {

		private int i;

		public MyListener(int i) {
			super();
			this.i = i;
		}

		public void handleEvent(Event event) {
			sortColumn(i);
		}
	}

	/**
	 * Sorts column alphabetically, when clicking on it's "header".
	 * @param colNum the number of column in table (starts with 0)
	 */
	private void sortColumn(int colNum) {
		TableItem[] items = table2.getItems();
		Collator collator = Collator.getInstance(Locale.getDefault());
		for (int i = 1; i < items.length; i++) {
			String value1 = items[i].getText(colNum);
			for (int j = 0; j < i; j++) {
				String value2 = items[j].getText(colNum);
				if (collator.compare(value1, value2) < 0) {
					String[] values = { items[i].getText(0),
							items[i].getText(1) };
					items[i].dispose();
					TableItem item = new TableItem(table2, SWT.NONE, j);
					item.setText(values);
					items = table2.getItems();
					break;
				}
			}
		}
	}

	/**
	 * Creates second set of buttons.
	 * 
	 * @param parent
	 *            a composite
	 */
	private void createButtons2(Composite parent) {
		/**
		 * "New" button for second table.
		 */
		Button _new = new Button(parent, SWT.PUSH);
		_new.setText(Messages.AuthorizeIdView_NEW);
		_new.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String _name = table1.getSelection()[0].getText();
				CustomInputDialog dialog = new CustomInputDialog(Display
						.getCurrent().getActiveShell(), Messages.AuthorizeIdView_NEW,
						Messages.AuthorizeIdView_SelGroup,
						Messages.AuthorizeIdView_SelRole, null,null);
				
				if (dialog.open() == Window.OK) {
					String _group = combo.getText();
					String _eaig = dialog.getValue();
					String _eair = dialog.getValue2();

					ObjectClass2 oclass2 = ObjectClass2.AUTHORIZEID;

					AuthorizationIdGRManagement nd = new AuthorizationIdGRManagement();
					nd.insertNewData(_name, _group, oclass2, _eair, _eaig);

					refreshTable2();

				}
			}
		});

		/**
		 * "Edit" button for second table.
		 */
		Button _edit = new Button(parent, SWT.PUSH);
		_edit.setText(Messages.AuthorizeIdView_EDIT);
		_edit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String _name = table1.getSelection()[0].getText();
				String eaigSel = table2.getSelection()[0].getText(0); 
				String eairSel = table2.getSelection()[0].getText(1); 
				CustomInputDialog dialog = new CustomInputDialog(Display
						.getCurrent().getActiveShell(), Messages.AuthorizeIdView_EDIT,
						Messages.AuthorizeIdView_GroupEdit, Messages.AuthorizeIdView_RoleEdit, eaigSel,eairSel );
				if (dialog.open() == Window.OK) {
					String _group = combo.getText();
					String _eaig = dialog.getValue();
					String _eair = dialog.getValue2();

					ObjectClass2 oclass2 = ObjectClass2.AUTHORIZEID;

					AuthorizationIdGRManagement nd = new AuthorizationIdGRManagement();
					nd.deleteData(_name, eairSel, eaigSel, _group);
					nd.insertNewData(_name, _group, oclass2, _eair, _eaig);

					refreshTable2();

				}
			}
		});

		/**
		 * "Delete" button for second table.
		 */
		Button _delete = new Button(parent, SWT.PUSH);
		_delete.setText(Messages.AuthorizeIdView_DELETE);
		_delete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String _name = table1.getSelection()[0].getText();
				String _eaig = table2.getSelection()[0].getText();
				String _eair = table2.getSelection()[0].getText(1);
				String _group = combo.getText();

				MessageBox messageBox = new MessageBox(Display.getCurrent()
						.getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				messageBox
						.setMessage(Messages.AuthorizeIdView_DelWarn);
				messageBox.setText(Messages.AuthorizeIdView_DelEntry);
				int response = messageBox.open();
				if (response == SWT.YES) {
					AuthorizationIdGRManagement aim = new AuthorizationIdGRManagement();
					aim.deleteData(_name, _eair, _eaig, _group);
					refreshTable2();
				}
			}
		});
	}

	/**
	 * Deletes all data and fills the first table again.
	 */
	private void refreshTable1() {
		LdapEain le = new LdapEain();
		String[] items = le.getEain(combo.getText());

		table1.removeAll();
		table1.clearAll();

		for (int i = 0; i < items.length; i++) {

			TableItem item = new TableItem(table1, SWT.NONE);
			item.setText(items[i]);

		}
	}

	/**
	 * Deletes all data and fills the second table again.
	 */
	private void refreshTable2() {
		table2.removeAll();
		table2.clearAll();

		LdapProp lp = new LdapProp();

		AuthorizeIdEntry[] entries = lp.getProp(table1.getSelection()[0]
				.getText(), combo.getText());

		tableViewer2.setInput(entries);

	}

	/**
	 * Deletes whole second table.
	 * @param name
	 * @param group
	 */
	private void deleteWholeTable2(String name, String group) {
		AuthorizationIdGRManagement aim2 = new AuthorizationIdGRManagement();

		for (int i = 0; i < table2.getItemCount(); i++) {
			if (table2.getItem(i).getText(0).equals("")) { //$NON-NLS-1$
				break;
			}

			String _eaig = table2.getItem(i).getText(0);
			String _eair = table2.getItem(i).getText(1);
			aim2.deleteData(name, _eair, _eaig, group);

		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

}
