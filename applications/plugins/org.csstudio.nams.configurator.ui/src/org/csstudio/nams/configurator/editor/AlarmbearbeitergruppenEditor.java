package org.csstudio.nams.configurator.editor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.csstudio.nams.common.fachwert.RubrikTypeEnum;
import org.csstudio.nams.configurator.NewConfiguratorActivator;
import org.csstudio.nams.configurator.actions.BeanToEditorId;
import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterGruppenBean;
import org.csstudio.nams.configurator.beans.User2GroupBean;
import org.csstudio.nams.configurator.composite.TableColumnResizeAdapter;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateListStrategy;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
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
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class AlarmbearbeitergruppenEditor extends
		AbstractEditor<AlarmbearbeiterGruppenBean> {

	private static final Image checkedImage = AbstractUIPlugin
			.imageDescriptorFromPlugin(NewConfiguratorActivator.PLUGIN_ID,
					"icons/checked.gif").createImage();
	private static final Image uncheckedImage = AbstractUIPlugin
			.imageDescriptorFromPlugin(NewConfiguratorActivator.PLUGIN_ID,
					"icons/unchecked.gif").createImage();

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
			List<User2GroupBean> oldList = (List<User2GroupBean>) oldInput;
			List<User2GroupBean> newList = (List<User2GroupBean>) newInput;
			if (oldList != null)
				for (User2GroupBean item : oldList) {
					removeDatabinding(item);
				}

			if (newList != null) {
				for (User2GroupBean item : newList) {
					initDataBindingOnUser2GroupBean(item);
				}
			}
			entries = (List<User2GroupBean>) newInput;
		}

		private void initDataBindingOnUser2GroupBean(User2GroupBean item) {
			item.addPropertyChangeListener(User2GroupBean.PropertyNames.active
					.name(), AlarmbearbeitergruppenEditor.this);
			item.addPropertyChangeListener(
					User2GroupBean.PropertyNames.activeReason.name(),
					AlarmbearbeitergruppenEditor.this);
		}

		private void removeDatabinding(User2GroupBean item) {
			item.removePropertyChangeListener(
					User2GroupBean.PropertyNames.active.name(),
					AlarmbearbeitergruppenEditor.this);
			item.removePropertyChangeListener(
					User2GroupBean.PropertyNames.activeReason.name(),
					AlarmbearbeitergruppenEditor.this);

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
				name = createTextEntry(textFieldComp, "Name", true);

				_rubrikComboEntryViewer = this
						.createRubrikCombo(
								textFieldComp,
								"Rubrik:",
								true,
								configurationBeanService
										.getRubrikNamesForType(RubrikTypeEnum.USER_GROUP));
				_rubrikComboEntry = _rubrikComboEntryViewer.getCombo();
				aktiveMitglieder = createTextEntry(textFieldComp,
						"Minimale Anzahl aktiver Mitglieder", true);
				wartezeit = createTextEntry(textFieldComp,
						"Wartezeit bis Rückmeldung (Sek)", true);
				activeButton = createCheckBoxEntry(textFieldComp,
						"Alarmgruppe aktiv", true);
			}

			{
				Composite tabelleUndButtonsComp = new Composite(main, SWT.None);
				tabelleUndButtonsComp.setLayout(new GridLayout(2, false));
				GridDataFactory.fillDefaults().grab(true, true).applyTo(
						tabelleUndButtonsComp);

				{
					Composite tabellenComposite = new Composite(
							tabelleUndButtonsComp, SWT.NONE);
					tabellenComposite.setLayout(new GridLayout(2, false));
					GridDataFactory.fillDefaults().grab(true, true).applyTo(
							tabellenComposite);
					tableViewer = new TableViewer(tabellenComposite,
							SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
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
					tabellenComposite
							.addControlListener(new TableColumnResizeAdapter(
									tabellenComposite, table, hinweisColumn
											.getColumn(), 300));
					hinweisColumn.setEditingSupport(new EditingSupport(
							tableViewer) {

						@Override
						protected boolean canEdit(Object element) {
							return true;
						}

						@Override
						protected CellEditor getCellEditor(Object element) {
							TextCellEditor editor = new TextCellEditor(
									tableViewer.getTable());
							((Text) editor.getControl()).setTextLimit(128);
							return editor;
						}

						@Override
						protected Object getValue(Object element) {
							return ((User2GroupBean) element).getActiveReason();
						}

						@Override
						protected void setValue(Object element, Object value) {
							((User2GroupBean) element)
									.setActiveReason((String) value);
							tableViewer.refresh();
						}
					});
					column = hinweisColumn.getColumn();
					column.setText("Hinweise vom Alarmbearbeiter");
					column.setWidth(300);
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
							((User2GroupBean) element)
									.setActive((Boolean) value);
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
									if (displayedBean.isActive()) {
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
					
					table.addMouseListener(new MouseListener(){

						public void mouseDoubleClick(MouseEvent e) {
							try {
								ConfigurationEditorInput editorInput;
								editorInput = new ConfigurationEditorInput(
										beanClone.getUsers().get(tableViewer.getTable().getSelectionIndex()).getUserBean());
								
								IWorkbenchPage activePage = PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow().getActivePage();
								String editorId = BeanToEditorId.getEnumForClass(AlarmbearbeiterBean.class)
								.getEditorId();
								
								activePage.openEditor(editorInput, editorId);
							} catch (PartInitException pie) {
								pie.printStackTrace();
							}
						}

						public void mouseDown(MouseEvent e) {
						}

						public void mouseUp(MouseEvent e) {
						}
					});
					
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

						Button upup = createButtonEntry(buttonsComp, "to top",
								true);
						upup.addMouseListener(new MouseListener() {

							public void mouseDoubleClick(MouseEvent e) {
							}

							public void mouseDown(MouseEvent e) {
								List<User2GroupBean> users = beanClone
										.getUsers();
								IStructuredSelection selection = (IStructuredSelection) tableViewer
										.getSelection();
								Object element = selection.getFirstElement();
								users.remove(element);
								users.add(0, (User2GroupBean) element);
								beanClone.setUsers(users);
								tableViewer.refresh();
							}

							public void mouseUp(MouseEvent e) {
							}
						});
						Button up = createButtonEntry(buttonsComp, "up", true);
						up.addMouseListener(new MouseListener() {

							public void mouseDoubleClick(MouseEvent e) {
							}

							public void mouseDown(MouseEvent e) {
								List<User2GroupBean> users = beanClone
										.getUsers();
								IStructuredSelection selection = (IStructuredSelection) tableViewer
										.getSelection();
								Object element = selection.getFirstElement();
								int index = tableViewer.getTable()
										.getSelectionIndex();
								if (index > 0)
									index--;
								users.remove(element);
								users.add(index, (User2GroupBean) element);
								beanClone.setUsers(users);
								tableViewer.refresh();
							}

							public void mouseUp(MouseEvent e) {
							}

						});
						Button down = createButtonEntry(buttonsComp, "down",
								true);
						down.addMouseListener(new MouseListener() {

							public void mouseDoubleClick(MouseEvent e) {
							}

							public void mouseDown(MouseEvent e) {
								List<User2GroupBean> users = beanClone
										.getUsers();
								IStructuredSelection selection = (IStructuredSelection) tableViewer
										.getSelection();
								Object element = selection.getFirstElement();
								int index = tableViewer.getTable()
										.getSelectionIndex();
								if (index < tableViewer.getTable()
										.getItemCount())
									index++;
								users.remove(element);
								users.add(index, (User2GroupBean) element);
								beanClone.setUsers(users);
								tableViewer.refresh();
							}

							public void mouseUp(MouseEvent e) {
							}

						});
						Button downdown = createButtonEntry(buttonsComp,
								"to bottom", true);
						downdown.addMouseListener(new MouseListener() {

							public void mouseDoubleClick(MouseEvent e) {
							}

							public void mouseDown(MouseEvent e) {
								List<User2GroupBean> users = beanClone
										.getUsers();
								IStructuredSelection selection = (IStructuredSelection) tableViewer
										.getSelection();
								User2GroupBean element = (User2GroupBean) selection
										.getFirstElement();
								users.remove(element);
								users.add((User2GroupBean) element);
								beanClone.setUsers(users);
								tableViewer.refresh();

							}

							public void mouseUp(MouseEvent e) {
							}

						});
						addSeparator(buttonsComp);
						Button deleteButton = createButtonEntry(buttonsComp,
								"löschen", true);
						deleteButton.addMouseListener(new MouseListener() {

							public void mouseDoubleClick(MouseEvent e) {
							}

							public void mouseDown(MouseEvent e) {
								Table table = tableViewer.getTable();
								if (table.getSelectionIndex() > -1) {
									List<User2GroupBean> users = AlarmbearbeitergruppenEditor.this.beanClone
											.getUsers();
									int[] items = table.getSelectionIndices();
									List<User2GroupBean> removeList = new LinkedList<User2GroupBean>();
									for (int i = 0; i < items.length; i++) {
										removeList.add(users.get(items[i]));
									}
									users.removeAll(removeList);
									AlarmbearbeitergruppenEditor.this.beanClone
											.setUsers(users);
								}
							}

							public void mouseUp(MouseEvent e) {
							}
						});
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
							if (selection.getFirstElement() instanceof AlarmbearbeiterBean && 
									!containsAlarmbearbeiter((AlarmbearbeiterBean)selection.getFirstElement()) )
									{
									event.detail = DND.DROP_LINK;
							} else {
								event.detail = DND.DROP_NONE;
							}
						} catch (Throwable e) {
						}
					}

					private boolean containsAlarmbearbeiter(AlarmbearbeiterBean newUser) {
						List<User2GroupBean> users = beanClone.getUsers();
						
						for (User2GroupBean user2GroupBean : users) {
							if( user2GroupBean.getUserBean().equals(newUser) )
								return true;
						}
						
						return false;
					}
					
					public void drop(DropTargetEvent event) {
						try {
							IStructuredSelection selection = (IStructuredSelection) LocalSelectionTransfer
									.getTransfer().getSelection();
							AlarmbearbeiterBean bean = (AlarmbearbeiterBean) selection
									.getFirstElement();
							List<User2GroupBean> users = AlarmbearbeitergruppenEditor.this.beanClone
									.getUsers();
							users.add(new User2GroupBean(bean));
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
								tableViewer.setInput(beanClone.getUsers());

							}
						});
						return status;
					}

					@Override
					protected IStatus doRemove(IObservableList observableList,
							int index) {
						IStatus status = super.doRemove(observableList, index);
						tableViewer.setInput(beanClone.getUsers());
						tableViewer.refresh();
						return status;
					}

				});

		context.bindValue(SWTObservables.observeText(name, SWT.Modify),
				nameTextObservable, null, null);

		context.bindValue(SWTObservables.observeText(aktiveMitglieder,
				SWT.Modify), aktiveMitgliederTextObservable,
				new UpdateValueStrategy() {
					
					@Override
					public IStatus validateAfterGet(Object value) {
						if (!EditorUIUtils.isValidDigit((String) value)) {
							aktiveMitglieder.setText("0");
							return Status.CANCEL_STATUS;
						} else { 
							Short convertedValue = Short.parseShort((String) value);
							if (convertedValue > beanClone.getUsers().size()){
								aktiveMitglieder.setText("" + beanClone.getUsers().size());
								return Status.CANCEL_STATUS;
							}
						}
						return super.validateAfterGet(value);
					}
				}, null);

		context.bindValue(SWTObservables.observeText(wartezeit, SWT.Modify),
				warteZeitTextObservable, new UpdateValueStrategy() {

					@Override
					public IStatus validateAfterGet(Object value) {
							if (!EditorUIUtils.isValidDigit((String) value)) {
								wartezeit.setText("0");
								return Status.CANCEL_STATUS;
							}
						return super.validateAfterGet(value);
					}
				}, null);

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
