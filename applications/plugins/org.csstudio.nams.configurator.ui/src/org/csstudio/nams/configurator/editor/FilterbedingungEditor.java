package org.csstudio.nams.configurator.editor;

import java.util.LinkedList;

import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.configurator.beans.filters.AddOnBean;
import org.csstudio.nams.configurator.beans.filters.JunctorConditionBean;
import org.csstudio.nams.configurator.beans.filters.PVFilterConditionBean;
import org.csstudio.nams.configurator.beans.filters.StringArrayFilterConditionBean;
import org.csstudio.nams.configurator.beans.filters.StringFilterConditionBean;
import org.csstudio.nams.configurator.beans.filters.TimeBasedFilterConditionBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
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
	private AbstractConfigurationBean[] specificBeans;
	private Text pvChannelName;
	private Combo pvSuggestedType;
	private Combo pvOperator;
	private Text pvCompareValue;

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

		initializeAddOnBeans();
		_filterTypeEntry.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event event) {
				filterLayout.topControl = stackComposites[_filterTypeEntry
						.getSelectionIndex()];
				filterSpecificComposite.layout();

				beanClone.setFilterSpecificBean((AddOnBean) specificBeans[_filterTypeEntry
						.getSelectionIndex()]);
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
		junctorFirstFilterText = createTextEntry(stackComposites[0],
				"Filtercondition", false);
		junctorTypeCombo = createComboEntry(stackComposites[0], "Junktor", true);
		junctorSecondFilterText = createTextEntry(stackComposites[0],
				"Filtercondition", false);
		// StringFilterComposite
		stackComposites[1] = new Composite(filterSpecificComposite, SWT.NONE);
		stackComposites[1].setLayout(new GridLayout(NUM_COLUMNS, false));
		stringCompareKeyText = createTextEntry(stackComposites[1],
				"CompareKey", true);
		stringOperatorCombo = createComboEntry(stackComposites[1], "Operator",
				true);
		stringCompareValueText = createTextEntry(stackComposites[1],
				"CompareValue", true);
		// StringArrayFilterComposite
		stackComposites[2] = new Composite(filterSpecificComposite, SWT.NONE);
		stackComposites[2].setLayout(new GridLayout(NUM_COLUMNS, false));
		// TODO create StringArrayGUI

		// PVComposite
		stackComposites[3] = new Composite(filterSpecificComposite, SWT.NONE);
		stackComposites[3].setLayout(new GridLayout(NUM_COLUMNS, false));
		pvChannelName = createTextEntry(stackComposites[3], "channelName", true);
		pvSuggestedType = createComboEntry(stackComposites[3], "SuggestedType",
				true);
		pvOperator = createComboEntry(stackComposites[3], "Operator", true);
		pvCompareValue = createTextEntry(stackComposites[3], "Compare value",
				true);
		// TimeBasedComposite
		stackComposites[4] = new Composite(filterSpecificComposite, SWT.NONE);
		stackComposites[4].setLayout(new GridLayout(NUM_COLUMNS, false));
		//TODO createTimeBasedGUI
		
		
		LinkedList<String> types = new LinkedList<String>();
		for (JunctorConditionType type : JunctorConditionType.values()) {
			types.add(type.toString());
		}
		junctorTypeCombo.setItems(types.toArray(new String[types.size()]));
		
		doFilterSpecificDataBinding();

		AddOnBean filterSpecificBean = (AddOnBean) bean.getFilterSpecificBean();
		if (filterSpecificBean instanceof JunctorConditionBean) {
			_filterTypeEntry.select(0);
		} else if (filterSpecificBean instanceof StringFilterConditionBean) {
			_filterTypeEntry.select(1);
		} else if (filterSpecificBean instanceof StringArrayFilterConditionBean) {
			_filterTypeEntry.select(2);
		} else if (filterSpecificBean instanceof PVFilterConditionBean) {
			_filterTypeEntry.select(3);
		} else if (filterSpecificBean instanceof TimeBasedFilterConditionBean) {
			_filterTypeEntry.select(4);
		} else throw new RuntimeException("Unsupported AddOnBean " + filterSpecificBean.getClass());
	}

	private void initializeAddOnBeans() {
		specificBeans = new AbstractConfigurationBean[5];
		specificBeans[0] = new JunctorConditionBean();
		specificBeans[1] = new StringFilterConditionBean();
		specificBeans[2] = new StringArrayFilterConditionBean();
		specificBeans[3] = new PVFilterConditionBean();
		specificBeans[4] = new TimeBasedFilterConditionBean();
		AbstractConfigurationBean filterSpecificBean =  (AbstractConfigurationBean) bean.getFilterSpecificBean();
		if (filterSpecificBean instanceof JunctorConditionBean)
			specificBeans[0] = filterSpecificBean;
		else if (filterSpecificBean instanceof StringFilterConditionBean)
			specificBeans[1] = filterSpecificBean;
		else if (filterSpecificBean instanceof StringArrayFilterConditionBean)
			specificBeans[2] = filterSpecificBean;
		else if (filterSpecificBean instanceof PVFilterConditionBean)
			specificBeans[3] = filterSpecificBean;
		else if (filterSpecificBean instanceof TimeBasedFilterConditionBean)
			specificBeans[4] = filterSpecificBean;
		else
			throw new RuntimeException("Unsupported AddOnBeanType "
					+ filterSpecificBean.getClass());
		for (int i = 0; i < specificBeans.length; i++) {
			specificBeans[i].addPropertyChangeListener(this);
		}
	}

	private void doFilterSpecificDataBinding() {
		initStringAddOnBeanDataBinding();
		initJunctorAddOnBeanDataBinding();
		initPVAddOnBeanDataBinding();
		// TODO init all AddOnBean data bindings
	}

	private void initPVAddOnBeanDataBinding() {
		DataBindingContext context = new DataBindingContext();

		IObservableValue pvChannelNameTextObservable = BeansObservables
				.observeValue(specificBeans[3],
						PVFilterConditionBean.PropertyNames.channelName.name());

		IObservableValue pvCompareValueTextObservable = BeansObservables
				.observeValue(specificBeans[3],
						PVFilterConditionBean.PropertyNames.compareValue.name());

		IObservableValue pvOperatorObservable = BeansObservables.observeValue(
				specificBeans[3], PVFilterConditionBean.PropertyNames.operator
						.name());

		IObservableValue pvTypeObservable = BeansObservables.observeValue(
				specificBeans[3],
				PVFilterConditionBean.PropertyNames.suggestedType.name());

		// bind observables
		context.bindValue(SWTObservables.observeSelection(pvOperator),
				pvOperatorObservable, null, null);

		context.bindValue(
				SWTObservables.observeText(pvChannelName, SWT.Modify),
				pvChannelNameTextObservable, null, null);

		context.bindValue(SWTObservables
				.observeText(pvCompareValue, SWT.Modify),
				pvCompareValueTextObservable, null, null);

		context.bindValue(SWTObservables.observeSelection(pvSuggestedType),
				pvTypeObservable, null, null);
	}

	private void initJunctorAddOnBeanDataBinding() {
		DataBindingContext context = new DataBindingContext();

		IObservableValue firstConditionTextObservable = BeansObservables
				.observeValue(specificBeans[0],
						JunctorConditionBean.PropertyNames.firstCondition
								.name());

		IObservableValue secondConditionTextObservable = BeansObservables
				.observeValue(specificBeans[0],
						JunctorConditionBean.PropertyNames.secondCondition
								.name());

		IObservableValue stringJunctorObservable = BeansObservables
				.observeValue(specificBeans[0],
						JunctorConditionBean.PropertyNames.junctor.name());

		// bind observables
		context.bindValue(SWTObservables.observeSelection(junctorTypeCombo),
				stringJunctorObservable, 
				new UpdateValueStrategy() {
					@Override
					public Object convert(Object value) {
						return JunctorConditionType.valueOf((String) value);
					}
				}, new UpdateValueStrategy() {
					@Override
					public Object convert(Object value) {
						return ((JunctorConditionType) value).name();
					}
				});

		context.bindValue(SWTObservables.observeText(junctorFirstFilterText,
				SWT.Modify), firstConditionTextObservable,
//		 new UpdateValueStrategy() {
//				 @Override
//				 public Object convert(Object value) {
//				 return JunctorConditionType.valueOf((String) value);
//				 }
//				 }, new UpdateValueStrategy() {
//				 @Override
//				 public Object convert(Object value) {
//				 return ((FilterbedingungBean) value).getDisplayName();
//				 }
//				 });
				null, null);

		context.bindValue(SWTObservables.observeText(junctorSecondFilterText,
				SWT.Modify), secondConditionTextObservable,
		// new UpdateValueStrategy() {
				// @Override
				// public Object convert(Object value) {
				// return JunctorConditionType.valueOf((String) value);
				// }
				// }, new UpdateValueStrategy() {
				// @Override
				// public Object convert(Object value) {
				// return ((FilterbedingungBean) value).getDisplayName();
				// }
				// });
				null, null);

	}

	private void initStringAddOnBeanDataBinding() {
		DataBindingContext context = new DataBindingContext();

		IObservableValue keyTextObservable = BeansObservables.observeValue(
				specificBeans[1],
				StringFilterConditionBean.PropertyNames.keyValue.name());

		IObservableValue stringCompareValueTextObservable = BeansObservables
				.observeValue(specificBeans[1],
						StringFilterConditionBean.PropertyNames.compValue
								.name());

		IObservableValue stringOperatorObservable = BeansObservables
				.observeValue(specificBeans[1],
						StringFilterConditionBean.PropertyNames.operator.name());

		// bind observables
		context.bindValue(SWTObservables.observeSelection(stringOperatorCombo),
				stringOperatorObservable, new UpdateValueStrategy() {
					@Override
					public Object convert(Object value) {
						return StringRegelOperator.valueOf((String) value);
					}
				}, new UpdateValueStrategy() {
					@Override
					public Object convert(Object value) {
						return ((StringRegelOperator) value).name();
					}
				});

		context.bindValue(SWTObservables.observeText(stringCompareKeyText,
				SWT.Modify), keyTextObservable, null, null);

		context.bindValue(SWTObservables.observeText(stringCompareValueText,
				SWT.Modify), stringCompareValueTextObservable, null, null);
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
