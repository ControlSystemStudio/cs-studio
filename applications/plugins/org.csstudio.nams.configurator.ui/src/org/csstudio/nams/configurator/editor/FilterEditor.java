package org.csstudio.nams.configurator.editor;

import org.csstudio.nams.common.fachwert.RubrikTypeEnum;
import org.csstudio.nams.configurator.beans.FilterBean;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.configurator.beans.filters.JunctorConditionForFilterTreeBean;
import org.csstudio.nams.configurator.beans.filters.NotConditionForFilterTreeBean;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
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

public class FilterEditor extends AbstractEditor<FilterBean> {

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
			new Label(treeAndButtonsComp, SWT.None)
					.setText("Filterconditions");
			{
				filterConditionsTreeViewer = new TreeViewer(
						treeAndButtonsComp);
				GridDataFactory.fillDefaults().grab(true, true).applyTo(
						filterConditionsTreeViewer.getControl());

				filterConditionsTreeViewer
						.setContentProvider(new FilterTreeContentProvider());
				filterConditionsTreeViewer.setLabelProvider(new FilterTreeLabelProvider());
				filterConditionsTreeViewer.setInput(beanClone.getConditions());

				initDND();
			}
			Button button = new Button(treeAndButtonsComp, SWT.PUSH);
			button.setText("Remove Filterconditions");
			button.addMouseListener(new MouseListener() {

				public void mouseDoubleClick(MouseEvent e) {
				}

				public void mouseDown(MouseEvent e) {
//					int[] indices = filterConditionsTreeViewer.getSelection()
//							.getSelectionIndices();
//					List<FilterbedingungBean> list = beanClone.getConditions();
//					for (int i = indices.length - 1; i >= 0; i--) {
//						int j = indices[i];
//						list.remove(j);
//					}
//					beanClone.setConditions(list);
				}

				public void mouseUp(MouseEvent e) {
				}
			});
		}

		initDataBinding();
	}

	private void initDND() {
		filterConditionsTreeViewer.addDropSupport(DND.DROP_LINK,
				new Transfer[] { LocalSelectionTransfer.getTransfer() },
				new ViewerDropAdapter(filterConditionsTreeViewer) {

					@Override
					public boolean performDrop(Object data) {
						Object target = getCurrentTarget();
						IStructuredSelection selection = (IStructuredSelection) data;
						FilterbedingungBean bean = (FilterbedingungBean) selection.getFirstElement();
						boolean result = false;
						if (target instanceof JunctorConditionForFilterTreeBean) {
							JunctorConditionForFilterTreeBean targetBean = (JunctorConditionForFilterTreeBean) target;
							targetBean.addOperand(bean);
							result = true;
						} else if (target instanceof NotConditionForFilterTreeBean) {
							NotConditionForFilterTreeBean targetBean = (NotConditionForFilterTreeBean) target;
							targetBean.setFilterbedingungBean(bean);
							result = true;
						}
						filterConditionsTreeViewer.refresh();
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
						if (target instanceof JunctorConditionForFilterTreeBean || target instanceof NotConditionForFilterTreeBean) {
							IStructuredSelection selection = (IStructuredSelection) LocalSelectionTransfer.getTransfer().getSelection();
							if (selection.getFirstElement() instanceof FilterbedingungBean){
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

//		IObservableTree filterConditionsObservable = BeansObservables
//				.observeList(context.getValidationRealm(), this.beanClone,
//						FilterBean.PropertyNames.conditions.name());

		IObservableValue rubrikTextObservable = BeansObservables.observeValue(
				this.beanClone, FilterBean.AbstractPropertyNames.rubrikName
						.name());

		// bind observables
		context.bindValue(SWTObservables
				.observeText(_nameTextEntry, SWT.Modify), nameTextObservable,
				null, null);

		context.bindValue(SWTObservables.observeText(_defaultMessageTextEntry,
				SWT.Modify), descriptionTextObservable, null, null);
//		IObservableTree observeItems = SWTObservables
//				.observeItems(filterConditionsTreeViewer.getTree());
//		context.bindList(observeItems, filterConditionsObservable, null, null);

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