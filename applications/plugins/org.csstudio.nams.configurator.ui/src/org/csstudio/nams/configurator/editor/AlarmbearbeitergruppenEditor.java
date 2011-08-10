package org.csstudio.nams.configurator.editor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.csstudio.nams.common.fachwert.RubrikTypeEnum;
import org.csstudio.nams.configurator.Messages;
import org.csstudio.nams.configurator.NewConfiguratorActivator;
import org.csstudio.nams.configurator.actions.BeanToEditorId;
import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterGruppenBean;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
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

	private final class IStructuredContentProviderImplementation extends
			AbstractConfigurationBean<IStructuredContentProviderImplementation>
			implements IStructuredContentProvider {
		private List<User2GroupBean> entries = new ArrayList<User2GroupBean>();

		@Override
        public void dispose() {
		    // Nothing to do
		}

		@Override
        public String getDisplayName() {
			return ""; //$NON-NLS-1$
		}

		@Override
        public Object[] getElements(final Object inputElement) {
			return this.entries.toArray();
		}

		public List<User2GroupBean> getEntries() {
			return this.entries;
		}

		@Override
        public int getID() {
			return 0;
		}

		@Override
        public void inputChanged(final Viewer viewer, final Object oldInput,
				final Object newInput) {
			final List<User2GroupBean> oldList = (List<User2GroupBean>) oldInput;
			final List<User2GroupBean> newList = (List<User2GroupBean>) newInput;
			if (oldList != null) {
				for (final User2GroupBean item : oldList) {
					this.removeDatabinding(item);
				}
			}

			if (newList != null) {
				for (final User2GroupBean item : newList) {
					this.initDataBindingOnUser2GroupBean(item);
				}
			}
			this.entries = (List<User2GroupBean>) newInput;
		}

		public void setEntries(final List<User2GroupBean> entries) {
			final List<User2GroupBean> oldValue = this.entries;
			this.entries = entries;
			this.pcs.firePropertyChange("entries", oldValue, entries); //$NON-NLS-1$
		}

		@Override
        public void setID(final int id) {
		}

		@Override
		protected void doUpdateState(
				final IStructuredContentProviderImplementation bean) {
		}

		private void initDataBindingOnUser2GroupBean(final User2GroupBean item) {
			item.addPropertyChangeListener(User2GroupBean.PropertyNames.active
					.name(), AlarmbearbeitergruppenEditor.this);
			item.addPropertyChangeListener(
					User2GroupBean.PropertyNames.activeReason.name(),
					AlarmbearbeitergruppenEditor.this);
		}

		private void removeDatabinding(final User2GroupBean item) {
			item.removePropertyChangeListener(
					User2GroupBean.PropertyNames.active.name(),
					AlarmbearbeitergruppenEditor.this);
			item.removePropertyChangeListener(
					User2GroupBean.PropertyNames.activeReason.name(),
					AlarmbearbeitergruppenEditor.this);

		}

		public void setDisplayName(String name) {
			// nothing to do here			
		}
	}

	private static final Image checkedImage = AbstractUIPlugin
			.imageDescriptorFromPlugin(NewConfiguratorActivator.PLUGIN_ID,
					"icons/checked.gif").createImage(); //$NON-NLS-1$

	private static final Image uncheckedImage = AbstractUIPlugin
			.imageDescriptorFromPlugin(NewConfiguratorActivator.PLUGIN_ID,
					"icons/unchecked.gif").createImage(); //$NON-NLS-1$

	private static final String EDITOR_ID = "org.csstudio.nams.configurator.editor.AlarmbearbeitergruppenEditor"; //$NON-NLS-1$

	public static String getId() {
		return AlarmbearbeitergruppenEditor.EDITOR_ID;
	}

	private Text name;
	private Combo _rubrikComboEntry;
	private Text aktiveMitglieder;

	private Text wartezeit;
	private Button activeButton;
	private TableViewer tableViewer;
	private ComboViewer _rubrikComboEntryViewer;
	private FormToolkit formToolkit;
	private ScrolledForm mainForm;

	private IStructuredContentProviderImplementation userContentProvider;

	@Override
	public void createPartControl(final Composite parent) {
		this.formToolkit = new FormToolkit(parent.getDisplay());
		this.mainForm = this.formToolkit.createScrolledForm(parent);
		final Composite main = this.mainForm.getBody();
		main.setBackground(parent.getBackground());
		main.setLayout(new GridLayout(1, true));
		// main.setLayout(new FillLayout(SWT.VERTICAL));
		{
			final Composite textFieldComp = new Composite(main, SWT.None);
			textFieldComp.setLayout(new GridLayout(2, false));
			GridDataFactory.fillDefaults().grab(true, false).applyTo(
					textFieldComp);

			{
				this.name = this.createTextEntry(textFieldComp, Messages.AlarmbearbeitergruppenEditor_name, true);

				this._rubrikComboEntryViewer = this.createComboEntry(
						textFieldComp, Messages.AlarmbearbeitergruppenEditor_category, true, AbstractEditor
								.getConfigurationBeanService()
								.getRubrikNamesForType(
										RubrikTypeEnum.USER_GROUP));
				this._rubrikComboEntry = this._rubrikComboEntryViewer
						.getCombo();
				this.aktiveMitglieder = this.createTextEntry(textFieldComp,
						Messages.AlarmbearbeitergruppenEditor_min_members, true);
				this.wartezeit = this.createTextEntry(textFieldComp,
						Messages.AlarmbearbeitergruppenEditor_timeout, true);
				this.activeButton = this.createCheckBoxEntry(textFieldComp,
						Messages.AlarmbearbeitergruppenEditor_group_active, true);
			}

			{
				final Composite tabelleUndButtonsComp = new Composite(main,
						SWT.None);
				tabelleUndButtonsComp.setLayout(new GridLayout(2, false));
				GridDataFactory.fillDefaults().grab(true, true).applyTo(
						tabelleUndButtonsComp);

				{
					final Composite tabellenComposite = new Composite(
							tabelleUndButtonsComp, SWT.NONE);
					tabellenComposite.setLayout(new GridLayout(2, false));
					GridDataFactory.fillDefaults().grab(true, true).applyTo(
							tabellenComposite);
					this.tableViewer = new TableViewer(tabellenComposite,
							SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL
									| SWT.V_SCROLL);
					final Table table = this.tableViewer.getTable();

					final TableViewerColumn nameColumn = new TableViewerColumn(
							this.tableViewer, SWT.NONE);
					TableColumn column = nameColumn.getColumn();
					column.setText(Messages.AlarmbearbeitergruppenEditor_alarm_issuer);
					column.setWidth(200);
					final TableViewerColumn tableViewerColumn = new TableViewerColumn(
							this.tableViewer, SWT.None);
					column = tableViewerColumn.getColumn();
					column.setText(Messages.AlarmbearbeitergruppenEditor_issuer_active);
					column.setWidth(100);

					final TableViewerColumn hinweisColumn = new TableViewerColumn(
							this.tableViewer, SWT.None);
					tabellenComposite
							.addControlListener(new TableColumnResizeAdapter(
									tabellenComposite, table, hinweisColumn
											.getColumn(), 300));
					hinweisColumn.setEditingSupport(new EditingSupport(
							this.tableViewer) {

						@Override
						protected boolean canEdit(final Object element) {
							return true;
						}

						@Override
						protected CellEditor getCellEditor(final Object element) {
							final TextCellEditor editor = new TextCellEditor(
									AlarmbearbeitergruppenEditor.this.tableViewer
											.getTable());
							((Text) editor.getControl()).setTextLimit(128);
							return editor;
						}

						@Override
						protected Object getValue(final Object element) {
							return ((User2GroupBean) element).getActiveReason();
						}

						@Override
						protected void setValue(final Object element,
								final Object value) {
							((User2GroupBean) element)
									.setActiveReason((String) value);
							AlarmbearbeitergruppenEditor.this.tableViewer
									.refresh();
						}
					});
					column = hinweisColumn.getColumn();
					column.setText(Messages.AlarmbearbeitergruppenEditor_issuer_notice);
					column.setWidth(300);
					final EditingSupport editingSupport = new EditingSupport(
							this.tableViewer) {

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
							AlarmbearbeitergruppenEditor.this.tableViewer
									.refresh();
						}

					};
					tableViewerColumn.setEditingSupport(editingSupport);
					this.tableViewer
							.setLabelProvider(new ITableLabelProvider() {

								public void addListener(
										final ILabelProviderListener listener) {
								}

								public void dispose() {
								}

								public Image getColumnImage(
										final Object element,
										final int columnIndex) {
									if (columnIndex == 1) {
										if (element instanceof User2GroupBean) {
											final User2GroupBean displayedBean = (User2GroupBean) element;
											if (displayedBean.isActive()) {
												return AlarmbearbeitergruppenEditor.checkedImage;
											} else {
												return AlarmbearbeitergruppenEditor.uncheckedImage;
											}
										}
									}
									return null;
								}

								public String getColumnText(
										final Object element,
										final int columnIndex) {
									if (element instanceof User2GroupBean) {
										final User2GroupBean displayedBean = (User2GroupBean) element;
										switch (columnIndex) {
										case 0:
											return displayedBean.getUserName();
										case 1:
											return String.valueOf(displayedBean
													.isActive());
										case 2:
											return displayedBean
													.getActiveReason();
										}
									}
									return null;
								}

								public boolean isLabelProperty(
										final Object element,
										final String property) {
									return false;
								}

								public void removeListener(
										final ILabelProviderListener listener) {
								}

							});

					GridDataFactory.fillDefaults().grab(true, true).applyTo(
							this.tableViewer.getControl());

					this.initDND();

					this.userContentProvider = new IStructuredContentProviderImplementation();
					this.tableViewer
							.setContentProvider(this.userContentProvider);

					table.setHeaderVisible(true);
					table.setLinesVisible(true);
					table.setSize(400, 300);

					table.addMouseListener(new MouseListener() {

						public void mouseDoubleClick(final MouseEvent e) {
							try {
								ConfigurationEditorInput editorInput;
								editorInput = new ConfigurationEditorInput(
										AlarmbearbeitergruppenEditor.this
												.getWorkingCopyOfEditorInput()
												.getUsers()
												.get(
														AlarmbearbeitergruppenEditor.this.tableViewer
																.getTable()
																.getSelectionIndex())
												.getUserBean());

								final IWorkbenchPage activePage = PlatformUI
										.getWorkbench()
										.getActiveWorkbenchWindow()
										.getActivePage();
								final String editorId = BeanToEditorId
										.getEnumForClass(
												AlarmbearbeiterBean.class)
										.getEditorId();

								activePage.openEditor(editorInput, editorId);
							} catch (final PartInitException pie) {
								pie.printStackTrace();
							}
						}

						@Override
                        public void mouseDown(final MouseEvent e) {
						    // Nothing to do
						}

						@Override
                        public void mouseUp(final MouseEvent e) {
						 // Nothing to do
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
					final Composite buttonsComp = new Composite(
							tabelleUndButtonsComp, SWT.None);
					buttonsComp.setLayout(new GridLayout(1, false));
					GridDataFactory.fillDefaults().grab(false, true).applyTo(
							buttonsComp);
					{

						final Button upup = this.createButtonEntry(buttonsComp,
								Messages.AlarmbearbeitergruppenEditor_move_to_top, true, 1);
						upup.addMouseListener(new MouseListener() {

							@Override
                            public void mouseDoubleClick(final MouseEvent e) {
							 // Nothing to do
							}

							@Override
                            public void mouseDown(final MouseEvent e) {
								final List<User2GroupBean> users = AlarmbearbeitergruppenEditor.this
										.getWorkingCopyOfEditorInput()
										.getUsers();
								final IStructuredSelection selection = (IStructuredSelection) AlarmbearbeitergruppenEditor.this.tableViewer
										.getSelection();
								final Object element = selection
										.getFirstElement();
								users.remove(element);
								users.add(0, (User2GroupBean) element);
								AlarmbearbeitergruppenEditor.this
										.getWorkingCopyOfEditorInput()
										.setUsers(users);
								AlarmbearbeitergruppenEditor.this.tableViewer
										.refresh();
							}

							@Override
                            public void mouseUp(final MouseEvent e) {
							 // Nothing to do
							}
						});
						final Button up = this.createButtonEntry(buttonsComp,
								Messages.AlarmbearbeitergruppenEditor_move_up, true, 1);
						up.addMouseListener(new MouseListener() {

							@Override
                            public void mouseDoubleClick(final MouseEvent e) {
							 // Nothing to do
							}

							@Override
                            public void mouseDown(final MouseEvent e) {
								final List<User2GroupBean> users = AlarmbearbeitergruppenEditor.this
										.getWorkingCopyOfEditorInput()
										.getUsers();
								final IStructuredSelection selection = (IStructuredSelection) AlarmbearbeitergruppenEditor.this.tableViewer
										.getSelection();
								final Object element = selection
										.getFirstElement();
								int index = AlarmbearbeitergruppenEditor.this.tableViewer
										.getTable().getSelectionIndex();
								if (index > 0) {
									index--;
								}
								users.remove(element);
								users.add(index, (User2GroupBean) element);
								AlarmbearbeitergruppenEditor.this
										.getWorkingCopyOfEditorInput()
										.setUsers(users);
								AlarmbearbeitergruppenEditor.this.tableViewer
										.refresh();
							}

							@Override
                            public void mouseUp(final MouseEvent e) {
							 // Nothing to do
							}

						});
						final Button down = this.createButtonEntry(buttonsComp,
								Messages.AlarmbearbeitergruppenEditor_move_down, true, 1);
						down.addMouseListener(new MouseListener() {

							public void mouseDoubleClick(final MouseEvent e) {
							}

							public void mouseDown(final MouseEvent e) {
								final List<User2GroupBean> users = AlarmbearbeitergruppenEditor.this
										.getWorkingCopyOfEditorInput()
										.getUsers();
								final IStructuredSelection selection = (IStructuredSelection) AlarmbearbeitergruppenEditor.this.tableViewer
										.getSelection();
								final Object element = selection
										.getFirstElement();
								int index = AlarmbearbeitergruppenEditor.this.tableViewer
										.getTable().getSelectionIndex();
								if (index < AlarmbearbeitergruppenEditor.this.tableViewer
										.getTable().getItemCount()) {
									index++;
								}
								users.remove(element);
								users.add(index, (User2GroupBean) element);
								AlarmbearbeitergruppenEditor.this
										.getWorkingCopyOfEditorInput()
										.setUsers(users);
								AlarmbearbeitergruppenEditor.this.tableViewer
										.refresh();
							}

							@Override
                            public void mouseUp(final MouseEvent e) {
							 // Nothing to do
							}

						});
						final Button downdown = this.createButtonEntry(
								buttonsComp, Messages.AlarmbearbeitergruppenEditor_move_to_bottom, true, 1);
						downdown.addMouseListener(new MouseListener() {

							@Override
                            public void mouseDoubleClick(final MouseEvent e) {
							 // Nothing to do
							}

							@Override
                            public void mouseDown(final MouseEvent e) {
								final List<User2GroupBean> users = AlarmbearbeitergruppenEditor.this
										.getWorkingCopyOfEditorInput()
										.getUsers();
								final IStructuredSelection selection = (IStructuredSelection) AlarmbearbeitergruppenEditor.this.tableViewer
										.getSelection();
								final User2GroupBean element = (User2GroupBean) selection
										.getFirstElement();
								users.remove(element);
								users.add(element);
								AlarmbearbeitergruppenEditor.this
										.getWorkingCopyOfEditorInput()
										.setUsers(users);
								AlarmbearbeitergruppenEditor.this.tableViewer
										.refresh();

							}

							@Override
                            public void mouseUp(final MouseEvent e) {
							 // Nothing to do
							}

						});
						this.addSeparator(buttonsComp);
						final Button deleteButton = this.createButtonEntry(
								buttonsComp, Messages.AlarmbearbeitergruppenEditor_delete, true, 1);
						deleteButton.addMouseListener(new MouseListener() {

							public void mouseDoubleClick(final MouseEvent e) {
							}

							public void mouseDown(final MouseEvent e) {
								final Table table = AlarmbearbeitergruppenEditor.this.tableViewer
										.getTable();
								if (table.getSelectionIndex() > -1) {
									final List<User2GroupBean> users = AlarmbearbeitergruppenEditor.this
											.getWorkingCopyOfEditorInput()
											.getUsers();
									final int[] items = table
											.getSelectionIndices();
									final List<User2GroupBean> removeList = new LinkedList<User2GroupBean>();
									for (final int element : items) {
										removeList.add(users.get(element));
									}
									users.removeAll(removeList);
									AlarmbearbeitergruppenEditor.this
											.getWorkingCopyOfEditorInput()
											.setUsers(users);
								}
							}

							public void mouseUp(final MouseEvent e) {
							}
						});
					}

				}
			}
		}
		this.initDataBinding();

	}

	@Override
	public void onBeanInsert(final IConfigurationBean bean) {
		if (this.tableViewer != null) {
			this.tableViewer.refresh();
		}
		super.onBeanInsert(bean);
	}

	@Override
	public void onBeanUpdate(final IConfigurationBean bean) {
		if (this.tableViewer != null) {
			this.tableViewer.refresh();
		}
		super.onBeanUpdate(bean);
	}

	@Override
	public void setFocus() {
		this.name.setFocus();
	}

	@Override
	protected void doInit(final IEditorSite site, final IEditorInput input) {
	}

	@Override
	protected int getNumColumns() {
		return 2;
	}

	@Override
	protected void initDataBinding() {
		final DataBindingContext context = new DataBindingContext();

		final IObservableValue nameTextObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						AlarmbearbeiterGruppenBean.PropertyNames.name.name());

		final IObservableValue aktiveMitgliederTextObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						AlarmbearbeiterGruppenBean.PropertyNames.minGroupMember
								.name());

		final IObservableValue warteZeitTextObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						AlarmbearbeiterGruppenBean.PropertyNames.timeOutSec
								.name());

		final IObservableValue rubrikTextObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						AlarmbearbeiterBean.AbstractPropertyNames.rubrikName
								.name());

		final IObservableList usersListObservable = BeansObservables
				.observeList(context.getValidationRealm(), this
						.getWorkingCopyOfEditorInput(),
						AlarmbearbeiterGruppenBean.PropertyNames.users.name());

		final IObservableValue activeCheckboxObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						AlarmbearbeiterGruppenBean.PropertyNames.active.name());

		final IObservableList usersListInTableObservable = BeansObservables
				.observeList(context.getValidationRealm(),
						this.userContentProvider, "entries"); //$NON-NLS-1$

		// bind observables
		context.bindList(usersListInTableObservable, usersListObservable, null,
				new UpdateListStrategy() {

					@Override
					protected IStatus doAdd(
							final IObservableList observableList,
							final Object element, final int index) {
						IStatus status;
						status = super.doAdd(observableList, element, index);
						Display.getCurrent().asyncExec(new Runnable() {
							public void run() {
								AlarmbearbeitergruppenEditor.this.tableViewer
										.setInput(AlarmbearbeitergruppenEditor.this
												.getWorkingCopyOfEditorInput()
												.getUsers());

							}
						});
						return status;
					}

					@Override
					protected IStatus doRemove(
							final IObservableList observableList,
							final int index) {
						final IStatus status = super.doRemove(observableList,
								index);
						AlarmbearbeitergruppenEditor.this.tableViewer
								.setInput(AlarmbearbeitergruppenEditor.this
										.getWorkingCopyOfEditorInput()
										.getUsers());
						AlarmbearbeitergruppenEditor.this.tableViewer.refresh();
						return status;
					}

				});

		context.bindValue(SWTObservables.observeText(this.name, SWT.Modify),
				nameTextObservable, null, null);

		context.bindValue(SWTObservables.observeText(this.aktiveMitglieder,
				SWT.Modify), aktiveMitgliederTextObservable,
				new UpdateValueStrategy() {

					@Override
					public IStatus validateAfterGet(final Object value) {
						if (!EditorUIUtils.isValidDigit((String) value)) {
							AlarmbearbeitergruppenEditor.this.aktiveMitglieder
									.setText("0"); //$NON-NLS-1$
							return Status.CANCEL_STATUS;
						} else {
							final Short convertedValue = Short
									.parseShort((String) value);
							if (convertedValue > AlarmbearbeitergruppenEditor.this
									.getWorkingCopyOfEditorInput().getUsers()
									.size()) {
								AlarmbearbeitergruppenEditor.this.aktiveMitglieder
										.setText("" //$NON-NLS-1$
												+ AlarmbearbeitergruppenEditor.this
														.getWorkingCopyOfEditorInput()
														.getUsers().size());
								return Status.CANCEL_STATUS;
							}
						}
						return super.validateAfterGet(value);
					}
				}, null);

		context.bindValue(SWTObservables
				.observeText(this.wartezeit, SWT.Modify),
				warteZeitTextObservable, new UpdateValueStrategy() {

					@Override
					public IStatus validateAfterGet(final Object value) {
						if (!EditorUIUtils.isValidDigit((String) value)) {
							AlarmbearbeitergruppenEditor.this.wartezeit
									.setText("0"); //$NON-NLS-1$
							return Status.CANCEL_STATUS;
						}
						return super.validateAfterGet(value);
					}
				}, null);

		context.bindValue(SWTObservables.observeSelection(this.activeButton),
				activeCheckboxObservable, null, null);

		context.bindValue(SWTObservables
				.observeSelection(this._rubrikComboEntry),
				rubrikTextObservable, null, null);

	}

	private void initDND() {
		this.tableViewer.addDropSupport(DND.DROP_LINK,
				new Transfer[] { LocalSelectionTransfer.getTransfer() },
				new DropTargetAdapter() {

					@Override
					public void dragEnter(final DropTargetEvent event) {
						try {
							final IStructuredSelection selection = (IStructuredSelection) LocalSelectionTransfer
									.getTransfer().getSelection();
							if ((selection.getFirstElement() instanceof AlarmbearbeiterBean)
									&& !this
											.containsAlarmbearbeiter((AlarmbearbeiterBean) selection
													.getFirstElement())) {
								event.detail = DND.DROP_LINK;
							} else {
								event.detail = DND.DROP_NONE;
							}
						} catch (final Throwable e) {
						}
					}

					@Override
					public void drop(final DropTargetEvent event) {
						try {
							final IStructuredSelection selection = (IStructuredSelection) LocalSelectionTransfer
									.getTransfer().getSelection();
							final AlarmbearbeiterBean bean = (AlarmbearbeiterBean) selection
									.getFirstElement();
							final List<User2GroupBean> users = AlarmbearbeitergruppenEditor.this
									.getWorkingCopyOfEditorInput().getUsers();
							users.add(new User2GroupBean(bean));
							AlarmbearbeitergruppenEditor.this
									.getWorkingCopyOfEditorInput().setUsers(
											users);
						} catch (final Throwable e) {
						}
					}

					private boolean containsAlarmbearbeiter(
							final AlarmbearbeiterBean newUser) {
						final List<User2GroupBean> users = AlarmbearbeitergruppenEditor.this
								.getWorkingCopyOfEditorInput().getUsers();

						for (final User2GroupBean user2GroupBean : users) {
							if (user2GroupBean.getUserBean().equals(newUser)) {
								return true;
							}
						}

						return false;
					}

				});
	}
}
