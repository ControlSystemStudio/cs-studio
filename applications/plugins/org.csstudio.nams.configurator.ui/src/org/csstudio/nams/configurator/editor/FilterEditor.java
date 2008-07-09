package org.csstudio.nams.configurator.editor;

import java.util.Iterator;
import java.util.List;

import org.csstudio.nams.common.fachwert.RubrikTypeEnum;
import org.csstudio.nams.configurator.beans.FilterBean;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
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
	private Composite filterSpecificComposite;

	private static final String EDITOR_ID = "org.csstudio.nams.configurator.editor.FilterEditor";
	private ComboViewer _rubrikComboEntryViewer;
	private ListViewer filterConditionsListViewer;

	@Override
	public void createPartControl(Composite parent) {
		Composite outermain = new Composite(parent, SWT.NONE);
		outermain.setLayout(new FillLayout(SWT.VERTICAL));
		Composite main = new Composite(outermain, SWT.NONE);
		main.setLayout(new GridLayout(NUM_COLUMNS, false));
		this.addSeparator(main);
		_nameTextEntry = this.createTextEntry(main, "Name:", true);
		_rubrikComboEntryViewer = this.createComboEntry(main, "Group:", true,
				configurationBeanService.getRubrikNamesForType(RubrikTypeEnum.FILTER));
		_rubrikComboEntry = _rubrikComboEntryViewer.getCombo();
		this.addSeparator(main);
		_defaultMessageTextEntry = this.createDescriptionTextEntry(main,
				"Description:");

		{
			Composite tabelleUndButtonsComp = new Composite(outermain, SWT.None);
			tabelleUndButtonsComp.setLayout(new GridLayout(1, false));
			GridDataFactory.fillDefaults().grab(true, true).applyTo(
					tabelleUndButtonsComp);
			new Label(tabelleUndButtonsComp, SWT.None)
					.setText("Filterbedingungen");
			{
				filterConditionsListViewer = new ListViewer(
						tabelleUndButtonsComp);
				GridDataFactory.fillDefaults().grab(true, true).applyTo(
						filterConditionsListViewer.getControl());

				filterConditionsListViewer
						.setContentProvider(new ArrayContentProvider());
				
				initDND();
			}
			Button button = new Button(tabelleUndButtonsComp, SWT.PUSH);
			button.setText("remove");
			button.addMouseListener(new MouseListener(){

				public void mouseDoubleClick(MouseEvent e) {
					ISelection selection = filterConditionsListViewer.getSelection();
					IStructuredSelection selection2 = (IStructuredSelection) selection;
					for (Object element : selection2.toList()) {
						filterConditionsListViewer.remove(element);
					}
				}

				public void mouseDown(MouseEvent e) {
				}

				public void mouseUp(MouseEvent e) {
				}});
		}
		
		initDataBinding();
	}

	private void initDND() {
		filterConditionsListViewer.addDropSupport(DND.DROP_LINK,
				new Transfer[] { LocalSelectionTransfer.getTransfer() },
				new DropTargetAdapter() {
					public void dragEnter(DropTargetEvent event) {
						try {
							IStructuredSelection selection = (IStructuredSelection) LocalSelectionTransfer
									.getTransfer().getSelection();
							if (selection.getFirstElement() instanceof FilterbedingungBean) {
								event.detail = DND.DROP_LINK;
							}
						} catch (Throwable e) {
						}
					}

					public void drop(DropTargetEvent event) {
						try {
							IStructuredSelection selection = (IStructuredSelection) LocalSelectionTransfer
									.getTransfer().getSelection();
							FilterbedingungBean bean = (FilterbedingungBean) selection
									.getFirstElement();
							List<FilterbedingungBean> list = FilterEditor.this.beanClone.getConditions();
							list.add(bean);
							FilterEditor.this.beanClone.setConditions(list);
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
				this.beanClone, FilterBean.PropertyNames.name.name());

		IObservableValue descriptionTextObservable = BeansObservables
				.observeValue(this.beanClone,
						FilterBean.PropertyNames.defaultMessage.name());

		IObservableList filterConditionsObservable = BeansObservables
				.observeList(context.getValidationRealm(), this.beanClone,
						FilterBean.PropertyNames.conditions.name());
		
		IObservableValue rubrikTextObservable = BeansObservables.observeValue(
				this.beanClone, FilterBean.AbstractPropertyNames.rubrikName.name());

		// bind observables
		context.bindValue(SWTObservables
				.observeText(_nameTextEntry, SWT.Modify), nameTextObservable,
				null, null);

		context.bindValue(SWTObservables.observeText(_defaultMessageTextEntry,
				SWT.Modify), descriptionTextObservable, null, null);
		IObservableList observeItems = SWTObservables.observeItems(filterConditionsListViewer.getList());
		context.bindList(observeItems, filterConditionsObservable, null, null);
		
		context.bindValue(SWTObservables
				.observeSelection(_rubrikComboEntry),
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