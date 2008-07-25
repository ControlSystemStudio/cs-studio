package org.csstudio.nams.configurator.editor;

import java.util.Iterator;

import org.csstudio.nams.common.fachwert.RubrikTypeEnum;
import org.csstudio.nams.configurator.actions.BeanToEditorId;
import org.csstudio.nams.configurator.beans.FilterActionBean;
import org.csstudio.nams.configurator.beans.FilterBean;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.beans.filters.JunctorConditionForFilterTreeBean;
import org.csstudio.nams.configurator.beans.filters.NotConditionForFilterTreeBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.EditorPart;

public class FilterEditor extends AbstractEditor<FilterBean> {

	private final FilterTreeContentProvider filterTreeContentProvider = new FilterTreeContentProvider();
	private Text _nameTextEntry;
	private Combo _rubrikComboEntry;
	private Text _defaultMessageTextEntry;
	private static final String EDITOR_ID = "org.csstudio.nams.configurator.editor.FilterEditor";
	private ComboViewer _rubrikComboEntryViewer;
	private TreeViewer filterConditionsTreeViewer;
	private FormToolkit formToolkit;
	private ScrolledForm mainForm;
	private TableViewer actionTableViewer;

	@Override
	protected void afterSafe() {
		// Sonderfall für die Filterbean, da sie einen Baum von unterbeans
		// enthält.
		this.getWorkingCopyOfEditorInput().updateState(
				this.getOriginalEditorInput());
	}

	@Override
	public void createPartControl(Composite parent) {
		formToolkit = new FormToolkit(parent.getDisplay());
		mainForm = formToolkit.createScrolledForm(parent);
		Composite outerFormMain = mainForm.getBody();
		outerFormMain.setBackground(parent.getBackground());
		outerFormMain.setLayout(new GridLayout(1, false));

		Composite main = new Composite(outerFormMain, SWT.NONE);
		main.setLayout(new GridLayout(NUM_COLUMNS, false));
		this.addSeparator(main);
		_nameTextEntry = this.createTextEntry(main, "Name:", true);
		_rubrikComboEntryViewer = this.createRubrikCombo(main, "Rubrik:", true,
				getConfigurationBeanService().getRubrikNamesForType(
						RubrikTypeEnum.FILTER));
		_rubrikComboEntry = _rubrikComboEntryViewer.getCombo();
		this.addSeparator(main);
		_defaultMessageTextEntry = this.createDescriptionTextEntry(main,
				"Description:");

		{
			Composite treeAndButtonsComp = new Composite(outerFormMain,
					SWT.None);
			treeAndButtonsComp.setLayout(new GridLayout(1, false));
			GridDataFactory.fillDefaults().grab(true, true).applyTo(
					treeAndButtonsComp);
			new Label(treeAndButtonsComp, SWT.None).setText("Filterconditions");
			{
				filterConditionsTreeViewer = new TreeViewer(treeAndButtonsComp,
						SWT.MULTI);
				Tree filterTree = filterConditionsTreeViewer.getTree();
				GridDataFactory.fillDefaults().grab(true, true).applyTo(
						filterConditionsTreeViewer.getControl());

				GridData treeLayout = (GridData) filterTree.getLayoutData();
				treeLayout.minimumHeight = 100;
				treeLayout.minimumWidth = 300;

				filterConditionsTreeViewer
						.setContentProvider(filterTreeContentProvider);
				filterConditionsTreeViewer
						.setLabelProvider(new FilterTreeLabelProvider());
				filterConditionsTreeViewer
						.setInput(getWorkingCopyOfEditorInput().getConditions());

				NewJunctorAction newAndAction = new NewJunctorAction(
						JunctorConditionType.AND);
				NewJunctorAction newOrAction = new NewJunctorAction(
						JunctorConditionType.OR);
				NewNotAction newNotAction = new NewNotAction();

				filterConditionsTreeViewer
						.addSelectionChangedListener(newAndAction);
				filterConditionsTreeViewer
						.addSelectionChangedListener(newOrAction);
				filterConditionsTreeViewer
						.addSelectionChangedListener(newNotAction);

				filterConditionsTreeViewer.expandAll();

				MenuManager menuManager = new MenuManager();
				menuManager.add(newAndAction);
				menuManager.add(newOrAction);
				menuManager.add(newNotAction);

				filterTree.setMenu(menuManager.createContextMenu(filterTree));

				filterTree.addMouseListener(new MouseListener() {

					public void mouseDoubleClick(MouseEvent e) {
						try {
							ConfigurationEditorInput editorInput;
							IStructuredSelection selection = (IStructuredSelection) filterConditionsTreeViewer
									.getSelection();
							IConfigurationBean filterBedingung = (IConfigurationBean) selection
									.getFirstElement();
							if (!(filterBedingung instanceof JunctorConditionForFilterTreeBean || filterBedingung instanceof NotConditionForFilterTreeBean)) {
								editorInput = new ConfigurationEditorInput(
										filterBedingung);

								IWorkbenchPage activePage = PlatformUI
										.getWorkbench()
										.getActiveWorkbenchWindow()
										.getActivePage();
								String editorId = BeanToEditorId
										.getEnumForClass(
												FilterbedingungBean.class)
										.getEditorId();

								activePage.openEditor(editorInput, editorId);
							}
						} catch (PartInitException pie) {
							pie.printStackTrace();
						}
					}

					public void mouseDown(MouseEvent e) {
					}

					public void mouseUp(MouseEvent e) {
					}
				});

				createFilterActionWidget(outerFormMain);

				initDND();
			}
			Button button = new Button(treeAndButtonsComp, SWT.PUSH);
			button.setText("Remove Filterconditions");
			button.addMouseListener(new MouseListener() {

				public void mouseDoubleClick(MouseEvent e) {
				}

				@SuppressWarnings("unchecked")
				public void mouseDown(MouseEvent e) {
					TreeSelection selection = (TreeSelection) filterConditionsTreeViewer
							.getSelection();
					for (Iterator<FilterbedingungBean> iter = selection
							.iterator(); iter.hasNext();) {
						FilterbedingungBean bean2remove = (FilterbedingungBean) iter
								.next();
						Object parent = filterTreeContentProvider
								.getParent(bean2remove);
						JunctorConditionForFilterTreeBean junctorParent = null;
						if (parent instanceof NotConditionForFilterTreeBean) {
							junctorParent = (JunctorConditionForFilterTreeBean) ((NotConditionForFilterTreeBean) parent)
									.getFilterbedingungBean();
						}
						if (parent instanceof JunctorConditionForFilterTreeBean) {
							junctorParent = (JunctorConditionForFilterTreeBean) parent;
						}
						if (junctorParent != null) {
							junctorParent.removeOperand(bean2remove);
							filterConditionsTreeViewer.refresh();
							updateBeanAndFireEvent();
						}

					}

				}

				public void mouseUp(MouseEvent e) {
				}
			});
		}

		initDataBinding();
	}

	private void createFilterActionWidget(Composite outerFormMain) {
		actionTableViewer = new TableViewer(outerFormMain);

		GridDataFactory.fillDefaults().grab(true, true).applyTo(
				actionTableViewer.getControl());

		GridData treeLayout = (GridData) actionTableViewer.getTable()
				.getLayoutData();
		treeLayout.minimumHeight = 100;
		treeLayout.minimumWidth = 300;

		actionTableViewer.getTable().setHeaderVisible(true);
		actionTableViewer.getTable().setLinesVisible(true);
		actionTableViewer.setContentProvider(new ArrayContentProvider());

		String[] titles = { "Empfänger", "Alarmaktion", "Nachricht" };
		int[] bounds = { 100, 100, 100 };

		TableViewerColumn[] tableViewerColumns = new TableViewerColumn[3];
		
		for (int i = 0; i < titles.length; i++) {
			tableViewerColumns[i] = new TableViewerColumn(actionTableViewer, SWT.LEFT);
			
			TableColumn column = tableViewerColumns[i].getColumn();
			column.setText(titles[i]);
			column.setWidth(bounds[i]);
			column.setResizable(true);
		}
		
		// Empfänger
		tableViewerColumns[0].setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				// TODO 
//				return ((FilterActionBean)element).toString();
				return "TODO!!";
			}
		});
		
		// Alarmaktion
		tableViewerColumns[1].setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				// TODO 
//				return super.getText(element);
				return "TODO2";
			}
		});
		tableViewerColumns[1].setEditingSupport(new EditingSupport(actionTableViewer) {

			@Override
			protected boolean canEdit(Object element) {
				// TODO schaun ob wir editieren können
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				// TODO (gs) string array anpassen auf die jeweiligen Möglichkeiten
				String[] strings = new String[]{"test", "test2"};
				return new ComboBoxCellEditor(actionTableViewer.getTable(), strings, SWT.READ_ONLY);
			}

			@Override
			protected Object getValue(Object element) {
				// TODO index des selektierten
				return 0;
			}

			@Override
			protected void setValue(Object element, Object value) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		// Nachricht
		tableViewerColumns[2].setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				// TODO 
//				return super.getText(element);
				return "TODO3";
			}
		});
		tableViewerColumns[2].setEditingSupport(new EditingSupport(actionTableViewer) {
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				// TODO (gs) validator hinzufügen
				TextCellEditor textEditor = new TextCellEditor(actionTableViewer.getTable());
				((Text) textEditor.getControl()).setTextLimit(1024);
				return textEditor;
			}

			@Override
			protected Object getValue(Object element) {
				//TODO 
				return "test";
			}

			@Override
			protected void setValue(Object element, Object value) {
				//TODO
			}
			
		});
		
		actionTableViewer.setInput(getWorkingCopyOfEditorInput().getActions()
				.toArray());

	}

	protected void updateBeanAndFireEvent() {
		getWorkingCopyOfEditorInput().setConditions(
				filterTreeContentProvider.getContentsOfRootANDCondition());
		FilterEditor.this.firePropertyChange(EditorPart.PROP_DIRTY);
	}

	private class NewJunctorAction extends Action implements
			ISelectionChangedListener {
		private JunctorConditionForFilterTreeBean selectedBean;
		private final JunctorConditionType type;

		private NewJunctorAction(JunctorConditionType type) {
			this.type = type;
		}

		@Override
		public void run() {
			JunctorConditionForFilterTreeBean node = new JunctorConditionForFilterTreeBean();
			node.setJunctorConditionType(type);
			boolean added = selectedBean.addOperand(node);
			filterConditionsTreeViewer.refresh();
			if (added) {
				filterConditionsTreeViewer.expandToLevel(node, 0);
				filterConditionsTreeViewer
						.setSelection(new StructuredSelection(node));
				updateBeanAndFireEvent();
			}
		}

		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection selection = (IStructuredSelection) event
					.getSelection();
			Object element = selection.getFirstElement();
			selectedBean = null;
			if (element instanceof JunctorConditionForFilterTreeBean) {
				selectedBean = (JunctorConditionForFilterTreeBean) element;
				setEnabled(true);
			} else if (element instanceof NotConditionForFilterTreeBean) {
				NotConditionForFilterTreeBean notBean = (NotConditionForFilterTreeBean) element;
				if (notBean.getFilterbedingungBean() instanceof JunctorConditionForFilterTreeBean) {
					selectedBean = (JunctorConditionForFilterTreeBean) notBean
							.getFilterbedingungBean();
					setEnabled(true);
				} else {
					setEnabled(false);
				}
			} else {
				setEnabled(false);
			}
		}

		@Override
		public String getText() {
			return "add " + type.name();
		}
	}

	private class NewNotAction extends Action implements
			ISelectionChangedListener {
		private FilterbedingungBean selectedBean;
		private boolean not;

		@Override
		public void run() {
			FilterbedingungBean parent = (FilterbedingungBean) filterTreeContentProvider
					.getParent(selectedBean);
			FilterbedingungBean newBean = null;
			if (parent != null) {
				JunctorConditionForFilterTreeBean junction = null;
				if (parent instanceof NotConditionForFilterTreeBean) {
					junction = (JunctorConditionForFilterTreeBean) ((NotConditionForFilterTreeBean) parent)
							.getFilterbedingungBean();
				} else if (parent instanceof JunctorConditionForFilterTreeBean) {
					junction = (JunctorConditionForFilterTreeBean) parent;
				}
				if (not) {
					NotConditionForFilterTreeBean notBean = new NotConditionForFilterTreeBean();
					newBean = notBean;
					junction.removeOperand(selectedBean);
					notBean.setFilterbedingungBean(selectedBean);
					junction.addOperand(notBean);
				} else {
					junction.removeOperand(selectedBean);
					NotConditionForFilterTreeBean notBean = (NotConditionForFilterTreeBean) selectedBean;
					FilterbedingungBean filterbedingungBean = notBean
							.getFilterbedingungBean();
					newBean = filterbedingungBean;
					junction.addOperand(filterbedingungBean);
				}
				updateBeanAndFireEvent();
			}

			filterConditionsTreeViewer.refresh();
			if (newBean != null) {
				filterConditionsTreeViewer.expandToLevel(newBean, 0);
				filterConditionsTreeViewer
						.setSelection(new StructuredSelection(newBean));
			}

		}

		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection selection = (IStructuredSelection) event
					.getSelection();
			Object element = selection.getFirstElement();
			setEnabled(true);
			if (element instanceof FilterbedingungBean) {
				selectedBean = (FilterbedingungBean) element;
				if (!(element instanceof NotConditionForFilterTreeBean)) {
					setText("NOT hinzufügen");
					not = true;
				} else {
					setText("NOT entfernen");
					not = false;
				}
			}
		}
	}

	private void initDND() {
		filterConditionsTreeViewer.addDropSupport(DND.DROP_LINK,
				new Transfer[] { LocalSelectionTransfer.getTransfer() },
				new ViewerDropAdapter(filterConditionsTreeViewer) {

					@Override
					public boolean performDrop(Object data) {
						Object target = getCurrentTarget();
						IStructuredSelection selection = (IStructuredSelection) data;
						FilterbedingungBean bean = (FilterbedingungBean) selection
								.getFirstElement();
						boolean result = false;
						if (target instanceof JunctorConditionForFilterTreeBean) {
							JunctorConditionForFilterTreeBean targetBean = (JunctorConditionForFilterTreeBean) target;
							targetBean.addOperand(bean);
							result = true;
						} else if (target instanceof NotConditionForFilterTreeBean) {

							NotConditionForFilterTreeBean targetBean = (NotConditionForFilterTreeBean) target;
							FilterbedingungBean filterbedingungBean = targetBean
									.getFilterbedingungBean();
							if (filterbedingungBean instanceof JunctorConditionForFilterTreeBean) {
								JunctorConditionForFilterTreeBean junctorFilterbedingungBean = (JunctorConditionForFilterTreeBean) filterbedingungBean;
								junctorFilterbedingungBean.addOperand(bean);
								result = true;
							}
						}
						filterConditionsTreeViewer.refresh();
						if (result) {
							filterConditionsTreeViewer.expandToLevel(bean, 0);
							filterConditionsTreeViewer
									.setSelection(new StructuredSelection(bean));
							updateBeanAndFireEvent();
						}
						return result;
					}

					@Override
					public void dragEnter(DropTargetEvent event) {
						event.detail = DND.DROP_LINK;
						super.dragEnter(event);
					}

					@Override
					public boolean validateDrop(Object target, int operation,
							TransferData transferType) {
						boolean result = false;
						if (target instanceof JunctorConditionForFilterTreeBean
								|| target instanceof NotConditionForFilterTreeBean) {
							IStructuredSelection selection = (IStructuredSelection) LocalSelectionTransfer
									.getTransfer().getSelection();
							if (selection.getFirstElement() instanceof FilterbedingungBean) {
								result = true;
							}
						}
						return result;
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
				this.getWorkingCopyOfEditorInput(),
				FilterBean.PropertyNames.name.name());

		IObservableValue descriptionTextObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						FilterBean.PropertyNames.defaultMessage.name());

		// IObservableTree filterConditionsObservable = BeansObservables
		// .observeList(context.getValidationRealm(), this.beanClone,
		// FilterBean.PropertyNames.conditions.name());

		IObservableValue rubrikTextObservable = BeansObservables.observeValue(
				this.getWorkingCopyOfEditorInput(),
				FilterBean.AbstractPropertyNames.rubrikName.name());

		// bind observables
		context.bindValue(SWTObservables
				.observeText(_nameTextEntry, SWT.Modify), nameTextObservable,
				null, null);

		context.bindValue(SWTObservables.observeText(_defaultMessageTextEntry,
				SWT.Modify), descriptionTextObservable, null, null);
		// IObservableTree observeItems = SWTObservables
		// .observeItems(filterConditionsTreeViewer.getTree());
		// context.bindList(observeItems, filterConditionsObservable, null,
		// null);

		context.bindValue(SWTObservables.observeSelection(_rubrikComboEntry),
				rubrikTextObservable, null, null);
	}

	@Override
	public void onBeanInsert(IConfigurationBean bean) {
		// FIXME synchronize bean and beanClone
		if (filterConditionsTreeViewer != null) {
			filterConditionsTreeViewer.refresh();
		}
		// if (!isDirty()) {
		// afterSafe();
		// }
		super.onBeanInsert(bean);
	}

	@Override
	public void onBeanUpdate(IConfigurationBean bean) {
		// FIXME synchronize bean and beanClone
		if (filterConditionsTreeViewer != null) {
			filterConditionsTreeViewer.refresh();
		}
		// if (!isDirty()) {
		// afterSafe();
		// }
		super.onBeanUpdate(bean);
	}

	@Override
	public void setFocus() {
		_nameTextEntry.setFocus();
	}

	public static String getId() {
		return EDITOR_ID;
	}
}