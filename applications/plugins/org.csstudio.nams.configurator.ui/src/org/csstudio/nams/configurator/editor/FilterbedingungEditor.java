package org.csstudio.nams.configurator.editor;

import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.configurator.beans.filters.AddOnBean;
import org.csstudio.nams.configurator.beans.filters.JunctorConditionBean;
import org.csstudio.nams.configurator.beans.filters.StringFilterConditionBean;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.action.SubCoolBarManager;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;

public class FilterbedingungEditor extends AbstractEditor<FilterbedingungBean> {

	private Text _nameTextEntry;
	private Combo _groupComboEntry;
	private Text _defaultMessageTextEntry;
	private Composite filterSpecificComposite;
	private Combo _filterTypeEntry;

	private static final String EDITOR_ID = "org.csstudio.nams.configurator.editor.FilterbedingungEditor";
	private StackLayout filterLayout;
	private Composite[] stackComposites;
	
	private Text junctorFirstFilterText;
	private Combo junctorTypeCombo;
	private Text junctorSecondFilterText;
	
	private Text stringCompareKeyText;
	private Combo stringOperatorCombo;
	private Text stringCompareValueText;

	public static String getId() {
		return EDITOR_ID;
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite outermain = new Composite(parent, SWT.NONE);
		outermain.setLayout(new FillLayout(SWT.VERTICAL));

		Composite main = new Composite(outermain, SWT.NONE);
		main.setLayout(new GridLayout(NUM_COLUMNS, false));
		_nameTextEntry = this.createTextEntry(main, "Name:", true);
		_groupComboEntry = this.createComboEntry(main, "Group:", true);
		this.addSeparator(main);
		_defaultMessageTextEntry = this.createDescriptionTextEntry(main,
				"Description:");
		_filterTypeEntry = this.createComboEntry(main, "Filtertype: ", true);
		_filterTypeEntry.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event event) {
				filterLayout.topControl = stackComposites[_filterTypeEntry
						.getSelectionIndex()];
				filterSpecificComposite.layout();
				
				AddOnBean specificBeans;
//				beanClone.setFilterSpecificBean(specificBeans[_filterTypeEntry.getSelectionIndex()]);
			}
		});

		_filterTypeEntry.add("Junctor Conditions");
		_filterTypeEntry.add("String Condition");
		_filterTypeEntry.add("StringArray Condition");
		_filterTypeEntry.add("PV Condition");
		_filterTypeEntry.add("TimeBased Condition");

		initDataBinding();

		filterSpecificComposite = new Composite(outermain, SWT.NONE);
		filterLayout = new StackLayout();
		filterSpecificComposite.setLayout(filterLayout);

		stackComposites = new Composite[5];
		// ConjunctionFilterComposite
		stackComposites[0] = new Composite(filterSpecificComposite, SWT.NONE);
		stackComposites[0].setLayout(new GridLayout(NUM_COLUMNS, false));
		junctorFirstFilterText = createTextEntry(stackComposites[0], "Filtercondition", false);
		junctorTypeCombo = createComboEntry(stackComposites[0], "Junktor", true);
		junctorSecondFilterText = createTextEntry(stackComposites[0], "Filtercondition", false);
		// StringFilterComposite
		stackComposites[1] = new Composite(filterSpecificComposite, SWT.NONE);
		stackComposites[1].setLayout(new GridLayout(NUM_COLUMNS, false));
		stringCompareKeyText = createTextEntry(stackComposites[1], "CompareKey", true);
		stringOperatorCombo = createComboEntry(stackComposites[1], "Operator", true);
		stringCompareValueText = createTextEntry(stackComposites[1], "CompareValue", true);
		// StringArrayFilterComposite
		stackComposites[2] = new Composite(filterSpecificComposite, SWT.NONE);
		new Label(stackComposites[2], SWT.NONE).setText("2");
		// PVComposite
		stackComposites[3] = new Composite(filterSpecificComposite, SWT.NONE);
		new Label(stackComposites[3], SWT.NONE).setText("3");
		// TimeBasedComposite
		stackComposites[4] = new Composite(filterSpecificComposite, SWT.NONE);
		new Label(stackComposites[4], SWT.NONE).setText("4");
		
		doFilterSpecificDataBinding();
		
		AddOnBean filterSpecificBean = bean.getFilterSpecificBean();
		if (filterSpecificBean instanceof JunctorConditionBean) {
			_filterTypeEntry.select(0);
		} else if (filterSpecificBean instanceof StringFilterConditionBean){
			_filterTypeEntry.select(1);
		}  //TODO add the other cases
	}

	private void doFilterSpecificDataBinding() {
		//TODO implement it!
		
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
		// TODO add group binding
		// TODO add filtertype binding
		DataBindingContext context = new DataBindingContext();

		IObservableValue nameTextObservable = BeansObservables.observeValue(
				this.beanClone, FilterbedingungBean.PropertyNames.name.name());

		IObservableValue descriptionTextObservable = BeansObservables
				.observeValue(this.beanClone,
						FilterbedingungBean.PropertyNames.description.name());

		// bind observables
		context.bindValue(SWTObservables
				.observeText(_nameTextEntry, SWT.Modify), nameTextObservable,
				null, null);

		context.bindValue(SWTObservables.observeText(_defaultMessageTextEntry,
				SWT.Modify), descriptionTextObservable, null, null);
	}

	@Override
	public void setFocus() {
		_nameTextEntry.setFocus();
	}
}
