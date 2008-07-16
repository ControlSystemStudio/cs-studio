package org.csstudio.nams.configurator.editor;

import org.csstudio.nams.common.fachwert.RubrikTypeEnum;
import org.csstudio.nams.configurator.beans.FilterBean;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;
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
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.part.EditorPart;

public class FilterEditor extends AbstractEditor<FilterBean> {

	private final FilterTreeContentProvider filterTreeContentProvider = new FilterTreeContentProvider();
	private Text _nameTextEntry;
	private Combo _rubrikComboEntry;
	private Text _defaultMessageTextEntry;
	private static final String EDITOR_ID = "org.csstudio.nams.configurator.editor.FilterEditor";
	private ComboViewer _rubrikComboEntryViewer;
	private TreeViewer filterConditionsTreeViewer;

	@Override
	public void createPartControl(Composite parent) {
		Composite outermain = new Composite(parent, SWT.NONE);
		outermain.setLayout(new FillLayout(SWT.VERTICAL));
		Composite main = new Composite(outermain, SWT.NONE);
		main.setLayout(new GridLayout(NUM_COLUMNS, false));
		this.addSeparator(main);
		_nameTextEntry = this.createTextEntry(main, "Name:", true);
		_rubrikComboEntryViewer = this.createComboEntry(main, "Rubrik:", true,
				configurationBeanService
						.getRubrikNamesForType(RubrikTypeEnum.FILTER));
		_rubrikComboEntry = _rubrikComboEntryViewer.getCombo();
		this.addSeparator(main);
		_defaultMessageTextEntry = this.createDescriptionTextEntry(main,
				"Description:");

		{
			Composite treeAndButtonsComp = new Composite(outermain, SWT.None);
			treeAndButtonsComp.setLayout(new GridLayout(1, false));
			GridDataFactory.fillDefaults().grab(true, true).applyTo(
					treeAndButtonsComp);
			new Label(treeAndButtonsComp, SWT.None).setText("Filterconditions");
			{
				filterConditionsTreeViewer = new TreeViewer(treeAndButtonsComp);
				GridDataFactory.fillDefaults().grab(true, true).applyTo(
						filterConditionsTreeViewer.getControl());

				filterConditionsTreeViewer
						.setContentProvider(filterTreeContentProvider);
				filterConditionsTreeViewer
						.setLabelProvider(new FilterTreeLabelProvider());
				filterConditionsTreeViewer.setInput(beanClone.getConditions());

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

				MenuManager menuManager = new MenuManager();
				menuManager.add(newAndAction);
				menuManager.add(newOrAction);
				menuManager.add(newNotAction);

				filterConditionsTreeViewer.getTree().setMenu(
						menuManager
								.createContextMenu(filterConditionsTreeViewer
										.getTree()));

				initDND();
			}
			Button button = new Button(treeAndButtonsComp, SWT.PUSH);
			button.setText("Remove Filterconditions");
			button.addMouseListener(new MouseListener() {

				public void mouseDoubleClick(MouseEvent e) {
				}

				public void mouseDown(MouseEvent e) {
					 IStructuredSelection selection = (IStructuredSelection) filterConditionsTreeViewer.getSelection();
					 Object element = selection.getFirstElement();
					 if (element != null) {
						 FilterbedingungBean bean2remove = (FilterbedingungBean) element;
						 Object parent = filterTreeContentProvider.getParent(bean2remove);
						 JunctorConditionForFilterTreeBean junctorParent = null;
						 if (parent instanceof NotConditionForFilterTreeBean) {
							 junctorParent = (JunctorConditionForFilterTreeBean) ((NotConditionForFilterTreeBean) parent).getFilterbedingungBean();
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

	protected void updateBeanAndFireEvent()
	{
		beanClone.setConditions(filterTreeContentProvider.getContentsOfRootANDCondition());
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
				filterConditionsTreeViewer.setSelection(new StructuredSelection(node));
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
					selectedBean = (JunctorConditionForFilterTreeBean) notBean.getFilterbedingungBean();
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
			FilterbedingungBean parent = (FilterbedingungBean) filterTreeContentProvider.getParent(selectedBean);
			FilterbedingungBean newBean = null;
			if (parent != null) {
				JunctorConditionForFilterTreeBean junction = null;
				if (parent instanceof NotConditionForFilterTreeBean) {
					junction = (JunctorConditionForFilterTreeBean) ((NotConditionForFilterTreeBean) parent).getFilterbedingungBean();
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
			if (newBean != null){
				filterConditionsTreeViewer.expandToLevel(newBean, 0);
				filterConditionsTreeViewer.setSelection(new StructuredSelection(newBean));
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
					setText("NOT");
					not = true;
				} else {
					setText("NOT");
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
							filterConditionsTreeViewer.setSelection(new StructuredSelection(bean));
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
				this.beanClone, FilterBean.PropertyNames.name.name());

		IObservableValue descriptionTextObservable = BeansObservables
				.observeValue(this.beanClone,
						FilterBean.PropertyNames.defaultMessage.name());

		// IObservableTree filterConditionsObservable = BeansObservables
		// .observeList(context.getValidationRealm(), this.beanClone,
		// FilterBean.PropertyNames.conditions.name());

		IObservableValue rubrikTextObservable = BeansObservables.observeValue(
				this.beanClone, FilterBean.AbstractPropertyNames.rubrikName
						.name());

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
	public void setFocus() {
		_nameTextEntry.setFocus();
	}

	public static String getId() {
		return EDITOR_ID;
	}
}