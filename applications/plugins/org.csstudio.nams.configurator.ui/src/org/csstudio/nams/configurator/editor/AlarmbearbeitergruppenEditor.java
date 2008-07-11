package org.csstudio.nams.configurator.editor;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.nams.common.fachwert.RubrikTypeEnum;
import org.csstudio.nams.configurator.NewConfiguratorActivator;
import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterGruppenBean;
import org.csstudio.nams.configurator.beans.User2GroupBean;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateListStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class AlarmbearbeitergruppenEditor extends
		AbstractEditor<AlarmbearbeiterGruppenBean> {

	private static  final Image checkedImage = AbstractUIPlugin.imageDescriptorFromPlugin(NewConfiguratorActivator.PLUGIN_ID, "icons/checked.gif").createImage();
	private static final Image uncheckedImage = AbstractUIPlugin.imageDescriptorFromPlugin(NewConfiguratorActivator.PLUGIN_ID, "icons/unchecked.gif").createImage();
	
	private final class IStructuredContentProviderImplementation extends
			AbstractConfigurationBean<IStructuredContentProviderImplementation>
			implements IStructuredContentProvider {
		private List<User2GroupBean> entries = new ArrayList<User2GroupBean>();

		public Object[] getElements(Object inputElement) {
			return entries.toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			System.out.println("Input changed");
			List<User2GroupBean> oldList = (List<User2GroupBean>) oldInput;
			List<User2GroupBean> newList = (List<User2GroupBean>) newInput;

			if (oldList != null)
				for (User2GroupBean item : oldList) {
					removeDatabinding(item);
				}
			for (User2GroupBean item : newList) {
				initDataBindingOnUser2GroupBean(item);
			}
			entries = (List<User2GroupBean>) newInput;
		}

		private void initDataBindingOnUser2GroupBean(User2GroupBean item) {
			// TODO Auto-generated method stub

		}

		private void removeDatabinding(User2GroupBean item) {
			// TODO Auto-generated method stub

		}

		public List<User2GroupBean> getEntries() {
			return entries;
		}

		public void setEntries(List<User2GroupBean> entries) {
			List<User2GroupBean> oldValue = this.entries;
			this.entries = entries;
			pcs.firePropertyChange("entries", oldValue, entries);
		}

		@Override
		protected void doUpdateState(
				IStructuredContentProviderImplementation bean) {
		}

		public String getDisplayName() {
			return "";
		}

		public int getID() {
			return 0;
		}

		public void setID(int id) {
		}
	}

	private Text name;
	private Combo _rubrikComboEntry;
	private Text aktiveMitglieder;
	private Text wartezeit;
	private Button activeButton;

	private static final String EDITOR_ID = "org.csstudio.nams.configurator.editor.AlarmbearbeitergruppenEditor";
	private TableViewer tableViewer;
	private ComboViewer _rubrikComboEntryViewer;
	private FormToolkit formToolkit;
	private ScrolledForm mainForm;
	private IStructuredContentProviderImplementation userContentProvider;

	public static String getId() {
		return EDITOR_ID;
	}

	@Override
	public void createPartControl(Composite parent) {
		formToolkit = new FormToolkit(parent.getDisplay());
		mainForm = formToolkit.createScrolledForm(parent);
		Composite main = mainForm.getBody();
		main.setBackground(parent.getBackground());
		main.setLayout(new GridLayout(1, false));
		{
			Composite textFieldComp = new Composite(main, SWT.None);
			textFieldComp.setLayout(new GridLayout(2, false));
			GridDataFactory.fillDefaults().grab(true, true).applyTo(
					textFieldComp);

			{
				new Label(textFieldComp, SWT.READ_ONLY).setText("Name");

				name = new Text(textFieldComp, SWT.BORDER);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(name);

				// new Label(textFieldComp, SWT.READ_ONLY).setText("Gruppe");

				_rubrikComboEntryViewer = this
						.createComboEntry(
								main,
								"Group:",
								true,
								configurationBeanService
										.getRubrikNamesForType(RubrikTypeEnum.USER_GROUP));

				_rubrikComboEntryViewer = this.createComboEntry(main, "Rubrik:", true, configurationBeanService
								.getRubrikNamesForType(RubrikTypeEnum.USER_GROUP));
				
				_rubrikComboEntry = _rubrikComboEntryViewer.getCombo();
				GridDataFactory.fillDefaults().grab(true, false).applyTo(
						_rubrikComboEntry);

				new Label(textFieldComp, SWT.READ_ONLY)
						.setText("Minimale Anzahl aktiver Mitglieder");

				aktiveMitglieder = new Text(textFieldComp, SWT.BORDER);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(
						aktiveMitglieder);

				new Label(textFieldComp, SWT.READ_ONLY)
						.setText("Wartezeit bis Rückmeldung (Sek)");

				wartezeit = new Text(textFieldComp, SWT.BORDER);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(
						wartezeit);

				new Label(textFieldComp, SWT.READ_ONLY)
						.setText("Alarmgruppe aktiv");
				activeButton = new Button(textFieldComp, SWT.CHECK);
			}

			{
				Composite tabelleUndButtonsComp = new Composite(main, SWT.None);
				tabelleUndButtonsComp.setLayout(new GridLayout(2, false));
				GridDataFactory.fillDefaults().grab(true, true).applyTo(
						tabelleUndButtonsComp);

				{
					tableViewer = new TableViewer(tabelleUndButtonsComp, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
					Table table = tableViewer.getTable();

					TableViewerColumn nameColumn = new TableViewerColumn(
							tableViewer, SWT.NONE);
					TableColumn column = nameColumn.getColumn();
					column.setText("Alarmbearbeiter");
					column.setWidth(200);
					TableViewerColumn tableViewerColumn = new TableViewerColumn(
							tableViewer, SWT.None);
					column = tableViewerColumn.getColumn();
					column.setText("Aktiv");
					column.setWidth(100);

					TableViewerColumn hinweisColumn = new TableViewerColumn(
							tableViewer, SWT.None);
					hinweisColumn.setEditingSupport(new EditingSupport(tableViewer){

						@Override
						protected boolean canEdit(Object element) {
							return true;
						}

						@Override
						protected CellEditor getCellEditor(Object element) {
							return new TextCellEditor();
						}

						@Override
						protected Object getValue(Object element) {
							return ((User2GroupBean) element).getActiveReason();
						}

						@Override
						protected void setValue(Object element, Object value) {
							((User2GroupBean) element).setActiveReason((String) value);
							tableViewer.refresh();
						}});
					column = hinweisColumn.getColumn();
					column.setText("Hinweise vom Alarmbearbeiter");
					column.setWidth(200);
					EditingSupport editingSupport = new EditingSupport(
							tableViewer) {

						@Override
						protected boolean canEdit(Object element) {
							return true;
						}

						@Override
						protected CellEditor getCellEditor(Object element) {
							return new CheckboxCellEditor();
						}

						@Override
						protected Object getValue(Object element) {
							return ((User2GroupBean) element).isActive();
						}

						@Override
						protected void setValue(Object element, Object value) {
							((User2GroupBean) element).setActive((Boolean) value);
							tableViewer.refresh();
						}

					};
					tableViewerColumn.setEditingSupport(editingSupport);
					tableViewer.setLabelProvider(new ITableLabelProvider() {

						public Image getColumnImage(Object element,
								int columnIndex) {
							if (columnIndex == 1)
							if (element instanceof User2GroupBean) {
								User2GroupBean displayedBean = (User2GroupBean) element;
									if (displayedBean.isActive()){
										return checkedImage;
									} else {
										return uncheckedImage;
									}
							}
							return null;
						}

						public String getColumnText(Object element,
								int columnIndex) {
							if (element instanceof User2GroupBean) {
								User2GroupBean displayedBean = (User2GroupBean) element;
								switch (columnIndex) {
								case 0:
									return displayedBean.getUserName();
								case 1:
									return String.valueOf(displayedBean
											.isActive());
								case 2:
									return displayedBean.getActiveReason();
								}
							}
							return null;
						}

						public void addListener(ILabelProviderListener listener) {
						}

						public void dispose() {
						}

						public boolean isLabelProperty(Object element,
								String property) {
							return false;
						}

						public void removeListener(
								ILabelProviderListener listener) {
						}

					});


					GridDataFactory.fillDefaults().grab(true, true).applyTo(
							tableViewer.getControl());

					initDND();

					userContentProvider = new IStructuredContentProviderImplementation();
					tableViewer.setContentProvider(userContentProvider);

					table.setHeaderVisible(true);
					table.setLinesVisible(true);
					table.setSize(400, 300);

					// TableColumn alarmbearbieter = new TableColumn(table,
					// SWT.LEFT);
					// alarmbearbieter.setText("Alarmbearbeiter");
					// alarmbearbieter.setWidth(200);
					//
					// TableColumn aktiv = new TableColumn(table, SWT.CHECK);
					// aktiv.setText("Aktiv");
					// aktiv.setWidth(100);
					//
					// TableColumn hinweis = new TableColumn(table, SWT.RIGHT);
					// hinweis.setText("Hinweis vom Alarmbearbeiter");
					// hinweis.setWidth(250);
				}

				{
					Composite buttonsComp = new Composite(
							tabelleUndButtonsComp, SWT.None);
					buttonsComp.setLayout(new GridLayout(1, false));
					GridDataFactory.fillDefaults().grab(false, true).applyTo(
							buttonsComp);
					{

						Button upup = new Button(buttonsComp, SWT.PUSH);
						upup.setText("UpUp");
						Button up = new Button(buttonsComp, SWT.PUSH);
						up.setText("Up");
						Button down = new Button(buttonsComp, SWT.PUSH);
						down.setText("Down");
						Button downdown = new Button(buttonsComp, SWT.PUSH);
						downdown.setText("DownDown");
					}
				}
			}
		}
		initDataBinding();
	}

	private void initDND() {
		tableViewer.addDropSupport(DND.DROP_LINK,
				new Transfer[] { LocalSelectionTransfer.getTransfer() },
				new DropTargetAdapter() {

					public void dragEnter(DropTargetEvent event) {
						try {
							IStructuredSelection selection = (IStructuredSelection) LocalSelectionTransfer
									.getTransfer().getSelection();
							if (selection.getFirstElement() instanceof AlarmbearbeiterBean) {
								event.detail = DND.DROP_LINK;
							}
						} catch (Throwable e) {
						}
					}

					public void drop(DropTargetEvent event) {
						try {
							IStructuredSelection selection = (IStructuredSelection) LocalSelectionTransfer
									.getTransfer().getSelection();
							AlarmbearbeiterBean bean = (AlarmbearbeiterBean) selection
									.getFirstElement();
							List<User2GroupBean> users = AlarmbearbeitergruppenEditor.this.beanClone
									.getUsers();
							users.add(new User2GroupBean(bean,
									AlarmbearbeitergruppenEditor.this.bean,
									tableViewer.getTable().getItemCount() + 1));
							AlarmbearbeitergruppenEditor.this.beanClone
									.setUsers(users);
						} catch (Throwable e) {
						}
					}

				});
	}

	@Override
	protected void doInit(IEditorSite site, IEditorInput input) {
	}

	@Override
	protected int getNumColumns() {
		return 2;
	}

	@Override
	protected void initDataBinding() {
		DataBindingContext context = new DataBindingContext();

		IObservableValue nameTextObservable = BeansObservables.observeValue(
				this.beanClone, AlarmbearbeiterGruppenBean.PropertyNames.name
						.name());

		IObservableValue aktiveMitgliederTextObservable = BeansObservables
				.observeValue(this.beanClone,
						AlarmbearbeiterGruppenBean.PropertyNames.minGroupMember
								.name());

		IObservableValue warteZeitTextObservable = BeansObservables
				.observeValue(this.beanClone,
						AlarmbearbeiterGruppenBean.PropertyNames.timeOutSec
								.name());

		IObservableValue rubrikTextObservable = BeansObservables.observeValue(
				this.beanClone,
				AlarmbearbeiterBean.AbstractPropertyNames.rubrikName.name());

		IObservableList usersListObservable = BeansObservables.observeList(
				context.getValidationRealm(), this.beanClone,
				AlarmbearbeiterGruppenBean.PropertyNames.users.name());
		// IObservableValue activeMembersTextObservable = BeansObservables
		// .observeValue(this.beanClone,
		// AlarmbearbeiterGruppenBean.PropertyNames.minGroupMember.name());

		IObservableValue activeCheckboxObservable = BeansObservables
				.observeValue(this.beanClone,
						AlarmbearbeiterGruppenBean.PropertyNames.active.name());

		IObservableList usersListInTableObservable = BeansObservables
				.observeList(context.getValidationRealm(), userContentProvider,
						"entries");

		// bind observables
		context.bindList(usersListInTableObservable, usersListObservable, null,
				new UpdateListStrategy() {

					@Override
					protected IStatus doAdd(IObservableList observableList,
							Object element, int index) {
						IStatus status;
						status = super.doAdd(observableList, element, index);
						Display.getCurrent().asyncExec(new Runnable() {

							public void run() {
								// TODO Auto-generated method stub
								tableViewer.setInput(beanClone.getUsers());

							}
						});
						// new Asyn
						// tableViewer.refresh();
						return status;
					}

					@Override
					protected IStatus doRemove(IObservableList observableList,
							int index) {
						IStatus status = super.doRemove(observableList, index);
						tableViewer.setInput(userContentProvider.getEntries());
						tableViewer.refresh();
						return status;
					}

				});

		context.bindValue(SWTObservables.observeText(name, SWT.Modify),
				nameTextObservable, null, null);

		context.bindValue(SWTObservables.observeText(aktiveMitglieder,
				SWT.Modify), aktiveMitgliederTextObservable, null, null);

		context.bindValue(SWTObservables.observeText(wartezeit, SWT.Modify),
				warteZeitTextObservable, null, null);

		context.bindValue(SWTObservables.observeSelection(activeButton),
				activeCheckboxObservable, null, null);

		context.bindValue(SWTObservables.observeSelection(_rubrikComboEntry),
				rubrikTextObservable, null, null);

	}

	@Override
	public void setFocus() {
		name.setFocus();
	}

}
