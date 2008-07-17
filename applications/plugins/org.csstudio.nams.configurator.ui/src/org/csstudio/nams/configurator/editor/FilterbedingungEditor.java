package org.csstudio.nams.configurator.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.fachwert.RubrikTypeEnum;
import org.csstudio.nams.common.material.regelwerk.Operator;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.common.material.regelwerk.SuggestedProcessVariableType;
import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.configurator.beans.filters.FilterConditionAddOnBean;
import org.csstudio.nams.configurator.beans.filters.JunctorConditionBean;
import org.csstudio.nams.configurator.beans.filters.PVFilterConditionBean;
import org.csstudio.nams.configurator.beans.filters.StringArrayFilterConditionBean;
import org.csstudio.nams.configurator.beans.filters.StringFilterConditionBean;
import org.csstudio.nams.configurator.beans.filters.TimeBasedFilterConditionBean;
import org.csstudio.nams.configurator.editor.updatevaluestrategies.MessageKeyToModelStrategy;
import org.csstudio.nams.configurator.editor.updatevaluestrategies.StringRegelOperatorToGuiStrategy;
import org.csstudio.nams.configurator.editor.updatevaluestrategies.StringRegelOperatorToModelStrategy;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.TimeBasedType;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class FilterbedingungEditor extends AbstractEditor<FilterbedingungBean> {

	public enum SupportedFilterTypes {
		JUNCTOR_CONDITION("Or Condition", JunctorConditionBean.class), STRING_CONDITION(
				"String Condition", StringFilterConditionBean.class), STRING_ARRAY_CONDITION(
				"StringArray Condition", StringArrayFilterConditionBean.class), PV_CONDITION(
				"PV Condition", PVFilterConditionBean.class), TIMEBASED_CONDITION(
				"TimeBased Condition", TimeBasedFilterConditionBean.class);

		private final String filterName;
		private final Class<?> cls;

		private SupportedFilterTypes(String name, Class<?> cls) {
			filterName = name;
			this.cls = cls;
		}

		@Override
		public String toString() {
			return filterName;
		}

		public static SupportedFilterTypes fromString(String value) {
			for (SupportedFilterTypes pValue : values()) {
				if (pValue.getFilterName().equals(value)) {
					return pValue;
				}
			}
			throw new RuntimeException("Unsupported Filtertype : " + value);
		}

		public static SupportedFilterTypes fromClass(Class<?> cls) {
			for (SupportedFilterTypes pValue : values()) {
				if (pValue.getCls().equals(cls)) {
					return pValue;
				}
			}
			throw new RuntimeException("Unsupported Filtertype : " + cls);
		}

		public String getFilterName() {
			return filterName;
		}

		public Class<?> getCls() {
			return cls;
		}
	}

	private Text _nameTextEntry;
	private Combo _rubrikComboEntry;
	private Text _defaultMessageTextEntry;
	private Composite filterSpecificComposite;
	private Combo _filterTypeEntry;

	private static final String EDITOR_ID = "org.csstudio.nams.configurator.editor.FilterbedingungEditor";
	private StackLayout filterLayout;
	private Composite[] stackComposites;

//	private Text junctorFirstFilterText;
//	private Combo junctorTypeCombo;
//	private Text junctorSecondFilterText;

	private Combo stringOperatorCombo;
	private Text stringCompareValueText;

	private Map<SupportedFilterTypes, AbstractConfigurationBean<?>> specificBeans;

	private Text pvChannelName;
	private Combo pvSuggestedType;
	private Combo pvOperator;
	private Text pvCompareValue;
	private Combo arrayMessageKeyCombo;
	private Combo arrayOperatorCombo;
	private List arrayCompareValueList;
	private Text timeDelayText;
	private Button timeBehaviorCheck;
	private Combo timeStartKeyCombo;
	private Combo timeStartOperatorCombo;
	private Text timeStartCompareText;
	private Combo timeStopKeyCombo;
	private Combo timeStopOperatorCombo;
	private Text timeStopCompareText;
	private ListViewer arrayCompareValueListViewer;
	private ComboViewer _rubrikComboEntryViewer;
	private ComboViewer _filterTypeEntryViewer;
	private ComboViewer junctorTypeComboViewer;
	private ComboViewer stringOperatorComboViewer;
	private ComboViewer arrayMessageKeyComboViewer;
	private ComboViewer arrayOperatorComboViewer;
	private ComboViewer pvSuggestedTypeViewer;
	private ComboViewer pvOperatorViewer;
	private ComboViewer timeStartKeyComboViewer;
	private ComboViewer timeStartOperatorComboViewer;
	private ComboViewer timeStopKeyComvoViewer;
	private ComboViewer timeStopOperatorComboViewer;
	private ComboViewer stringCompareKeyComboViewer;
	private Combo stringCompareKeyCombo;
	private FormToolkit formToolkit;
	private ScrolledForm mainForm;

	public static String getId() {
		return EDITOR_ID;
	}

	@Override
	public void createPartControl(Composite parent) {
		formToolkit = new FormToolkit(parent.getDisplay());
		mainForm = formToolkit.createScrolledForm(parent);
		Composite outermain = mainForm.getBody();
		outermain.setBackground(parent.getBackground());
		outermain.setLayout(new FillLayout(SWT.VERTICAL));

		Composite main = new Composite(outermain, SWT.NONE);
		main.setLayout(new GridLayout(NUM_COLUMNS, false));
		_nameTextEntry = this.createTextEntry(main, "Name:", true);
		_rubrikComboEntryViewer = this.createComboEntry(main, "Rubrik:", true,
				configurationBeanService
						.getRubrikNamesForType(RubrikTypeEnum.FILTER_COND));
		_rubrikComboEntry = _rubrikComboEntryViewer.getCombo();
		this.addSeparator(main);
		_defaultMessageTextEntry = this.createDescriptionTextEntry(main,
				"Description:");
		_filterTypeEntryViewer = this.createComboEntry(main, "Filtertype: ",
				false, array2StringArray(SupportedFilterTypes.values()));
		_filterTypeEntry = _filterTypeEntryViewer.getCombo();

		initializeAddOnBeans();
		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				filterLayout.topControl = stackComposites[_filterTypeEntry
						.getSelectionIndex()];
				filterSpecificComposite.layout();

				beanClone
						.setFilterSpecificBean((FilterConditionAddOnBean) specificBeans
								.get(SupportedFilterTypes
										.fromString(_filterTypeEntry.getText())));
			}
		};
		_filterTypeEntry.addListener(SWT.Modify, listener);

		filterSpecificComposite = new Composite(outermain, SWT.NONE);
		filterLayout = new StackLayout();
		filterSpecificComposite.setLayout(filterLayout);

		stackComposites = new Composite[5];

		// ConjunctionFilterComposite
		Text junctorFirstFilterText;
		Text junctorSecondFilterText;
		stackComposites[0] = new Composite(filterSpecificComposite, SWT.NONE);
		stackComposites[0].setLayout(new GridLayout(NUM_COLUMNS, false));
		new Label(stackComposites[0], SWT.NONE);
		Label label = new Label(stackComposites[0], SWT.LEFT | SWT.WRAP);
		label
				.setText("Dieses Filterbedingung  existiert nur aus Gründen der\n" +
						"Abwärtskompatibilität, bitte realisieren Sie das\n" +
						"gewünschte Verhalten über die neuen Funktionen des Filters");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		junctorFirstFilterText = createTextEntry(stackComposites[0],
				"Filtercondition", false);
		// junctorTypeComboViewer = createComboEntry(stackComposites[0],
		// "Junktor", false, array2StringArray(JunctorConditionType
		// .values()));
		// junctorTypeComboViewer
		// .addSelectionChangedListener(new ISelectionChangedListener() {
		// public void selectionChanged(SelectionChangedEvent event) {
		// checkJunktionType();
		// }
		// });
		// junctorTypeCombo = junctorTypeComboViewer.getCombo();
		junctorSecondFilterText = createTextEntry(stackComposites[0],
				"Filtercondition", false);

		// StringFilterComposite
		stackComposites[1] = new Composite(filterSpecificComposite, SWT.NONE);
		stackComposites[1].setLayout(new GridLayout(NUM_COLUMNS, false));
		stringCompareKeyComboViewer = createComboEntry(stackComposites[1],
				"CompareKey", false, MessageKeyEnum.valuesAsStringArray());
		stringCompareKeyCombo = stringCompareKeyComboViewer.getCombo();
		stringOperatorComboViewer = createComboEntry(stackComposites[1],
				"Operator", false, array2StringArray(StringRegelOperator
						.values()));
		stringOperatorCombo = stringOperatorComboViewer.getCombo();
		stringCompareValueText = createTextEntry(stackComposites[1],
				"CompareValue", true);
		// StringArrayFilterComposite
		stackComposites[2] = new Composite(filterSpecificComposite, SWT.NONE);
		stackComposites[2].setLayout(new GridLayout(NUM_COLUMNS, false));
		arrayMessageKeyComboViewer = createComboEntry(stackComposites[2],
				"MessageKey", false, MessageKeyEnum.valuesAsStringArray());
		arrayMessageKeyCombo = arrayMessageKeyComboViewer.getCombo();
		arrayOperatorComboViewer = createComboEntry(stackComposites[2],
				"Operator", false, array2StringArray(StringRegelOperator
						.values()));
		arrayOperatorCombo = arrayOperatorComboViewer.getCombo();
		arrayCompareValueListViewer = createListEntry(stackComposites[2],
				"CompareValues", true);
		arrayCompareValueList = arrayCompareValueListViewer.getList();
		final Text arrayNewCompareValueText = createTextEntry(
				stackComposites[2], "Neues CompareValue", true);
		arrayNewCompareValueText.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.CR) {
					arrayCompareValueList.add(arrayNewCompareValueText
							.getText());
				}
			}

			public void keyReleased(KeyEvent e) {
			}
		});
		Button buttonAdd = createButtonEntry(stackComposites[2],
				"Eingabe hinzufügen", true);
		buttonAdd.addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
				arrayCompareValueList.add(arrayNewCompareValueText.getText());
			}

			public void mouseUp(MouseEvent e) {
			}
		});
		Button button = createButtonEntry(stackComposites[2],
				"Vergleichswert löschen", true);
		button.addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
				if (arrayCompareValueList.getSelectionIndex() > -1) {
					String[] items = arrayCompareValueList.getItems();
					ArrayList<String> itemList = new ArrayList<String>();
					for (int i = 0; i < items.length; i++) {
						if (arrayCompareValueList.getSelectionIndex() != i)
							itemList.add(items[i]);
					}
					arrayCompareValueList.setItems(itemList
							.toArray(new String[itemList.size()]));
				}
			}

			public void mouseUp(MouseEvent e) {
			}
		});
		// PVComposite
		stackComposites[3] = new Composite(filterSpecificComposite, SWT.NONE);
		stackComposites[3].setLayout(new GridLayout(NUM_COLUMNS, false));
		pvChannelName = createTextEntry(stackComposites[3], "channelName", true);
		pvSuggestedTypeViewer = createComboEntry(stackComposites[3],
				"SuggestedType", false,
				array2StringArray(SuggestedProcessVariableType.values()));
		pvSuggestedType = pvSuggestedTypeViewer.getCombo();
		pvOperatorViewer = createComboEntry(stackComposites[3], "Operator",
				false, array2StringArray(Operator.values()));
		pvOperator = pvOperatorViewer.getCombo();
		pvCompareValue = createTextEntry(stackComposites[3], "Compare value",
				true);
		// TimeBasedComposite
		stackComposites[4] = new Composite(filterSpecificComposite, SWT.NONE);
		stackComposites[4].setLayout(new GridLayout(NUM_COLUMNS, false));

		timeDelayText = createTextEntry(stackComposites[4], "Wartezeit", true);
		timeBehaviorCheck = createCheckBoxEntry(stackComposites[4],
				"Alarm bei Timeout", true);
		addSeparator(stackComposites[4]);
		timeStartKeyComboViewer = createComboEntry(stackComposites[4],
				"Start KeyValue", false, array2StringArray(MessageKeyEnum
						.values()));
		timeStartKeyCombo = timeStartKeyComboViewer.getCombo();
		timeStartOperatorComboViewer = createComboEntry(stackComposites[4],
				"Start Operator", false, array2StringArray(StringRegelOperator
						.values()));
		timeStartOperatorCombo = timeStartOperatorComboViewer.getCombo();
		timeStartCompareText = createTextEntry(stackComposites[4],
				"Start CompareValue", true);
		addSeparator(stackComposites[4]);
		timeStopKeyComvoViewer = createComboEntry(stackComposites[4],
				"Stop KeyValue", false, array2StringArray(MessageKeyEnum
						.values()));
		timeStopKeyCombo = timeStopKeyComvoViewer.getCombo();
		timeStopOperatorComboViewer = createComboEntry(stackComposites[4],
				"Stop Operator", false, array2StringArray(StringRegelOperator
						.values()));
		timeStopOperatorCombo = timeStopOperatorComboViewer.getCombo();
		timeStopCompareText = createTextEntry(stackComposites[4],
				"Stop CompareValue", true);

//		LinkedList<String> types = new LinkedList<String>();
//		for (JunctorConditionType type : JunctorConditionType.values()) {
//			types.add(type.toString());
//		}
//		junctorTypeCombo.setItems(types.toArray(new String[types.size()]));

		FilterConditionAddOnBean filterSpecificBean = (FilterConditionAddOnBean) bean
				.getFilterSpecificBean();
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
		} else
			throw new RuntimeException("Unsupported AddOnBean "
					+ filterSpecificBean.getClass());
		initDataBinding();
		initDND();
		listener.handleEvent(null); // zur initialisierung
//		checkJunktionType();
	}

//	private void checkJunktionType() {
//		String typeString = (String) ((StructuredSelection) junctorTypeComboViewer
//				.getSelection()).getFirstElement();
//		try {
//			JunctorConditionType type = JunctorConditionType
//					.valueOf(typeString);
//			junctorSecondFilterText
//					.setVisible(type != JunctorConditionType.NOT);
//		} catch (Exception e) {
//
//		}
//	}

	private void initDND() {
//		DropTarget target1 = new DropTarget(junctorFirstFilterText,
//				DND.DROP_LINK);
//		DropTarget target2 = new DropTarget(junctorSecondFilterText,
//				DND.DROP_LINK);
//
//		target1.setTransfer(new Transfer[] { LocalSelectionTransfer
//				.getTransfer() });
//		target2.setTransfer(new Transfer[] { LocalSelectionTransfer
//				.getTransfer() });
//
//		target1
//				.addDropListener(new TextDropTarget(junctorFirstFilterText,
//						true));
//		target2.addDropListener(new TextDropTarget(junctorSecondFilterText,
//				false));
	}

	class TextDropTarget extends DropTargetAdapter {
		private final Text text;
		private final boolean first;

		public TextDropTarget(Text junctorFilterText, boolean first) {
			text = junctorFilterText;
			this.first = first;
		}

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
				JunctorConditionBean junctorBean = (JunctorConditionBean) specificBeans
						.get(SupportedFilterTypes.JUNCTOR_CONDITION);
				if (first) {
					junctorBean.setFirstCondition(bean);
				} else {
					junctorBean.setSecondCondition(bean);
				}
				text.setText(bean.getDisplayName());
			} catch (Throwable e) {
			}
		}
	}

	private void initializeAddOnBeans() {
		specificBeans = new HashMap<SupportedFilterTypes, AbstractConfigurationBean<?>>();
		specificBeans.put(SupportedFilterTypes.JUNCTOR_CONDITION,
				new JunctorConditionBean());
		specificBeans.put(SupportedFilterTypes.STRING_CONDITION,
				new StringFilterConditionBean());
		specificBeans.put(SupportedFilterTypes.STRING_ARRAY_CONDITION,
				new StringArrayFilterConditionBean());
		specificBeans.put(SupportedFilterTypes.PV_CONDITION,
				new PVFilterConditionBean());
		specificBeans.put(SupportedFilterTypes.TIMEBASED_CONDITION,
				new TimeBasedFilterConditionBean());
		AbstractConfigurationBean<?> filterSpecificBean = (AbstractConfigurationBean<?>) bean
				.getFilterSpecificBean();

		specificBeans.put(SupportedFilterTypes.fromClass(filterSpecificBean
				.getClass()), filterSpecificBean);

		for (AbstractConfigurationBean<?> bean : specificBeans.values()) {
			bean.addPropertyChangeListener(this);
		}

	}

	private void initTimeBasedAddOnBeanDataBinding(DataBindingContext context) {
		TimeBasedFilterConditionBean addOn = (TimeBasedFilterConditionBean) specificBeans
				.get(SupportedFilterTypes.TIMEBASED_CONDITION);

		IObservableValue timeBasedStartCompareObservable = BeansObservables
				.observeValue(
						addOn,
						TimeBasedFilterConditionBean.PropertyNames.startCompValue
								.name());

		IObservableValue timeBasedStartKeyObservable = BeansObservables
				.observeValue(
						addOn,
						TimeBasedFilterConditionBean.PropertyNames.startKeyValue
								.name());

		IObservableValue timeBasedStartOperator = BeansObservables
				.observeValue(
						addOn,
						TimeBasedFilterConditionBean.PropertyNames.startOperator
								.name());
		IObservableValue timeDelayObservable = BeansObservables.observeValue(
				addOn, TimeBasedFilterConditionBean.PropertyNames.timePeriod
						.name());
		// bind observables
		context.bindValue(
				SWTObservables.observeText(timeDelayText, SWT.Modify),
				timeDelayObservable, new UpdateValueStrategy() {

					@Override
					public Object convert(Object value) {
						Millisekunden result = Millisekunden.valueOf(0);
						try {
							result = Millisekunden.valueOf(Long
									.parseLong((String) value));
						} catch (Throwable e) {
							timeDelayText.setText("0");
						}

						return result;
					}

				}, null);

		context.bindValue(SWTObservables.observeText(timeStartCompareText,
				SWT.Modify), timeBasedStartCompareObservable, null, null);

		context.bindValue(SWTObservables.observeSelection(timeStartKeyCombo),
				timeBasedStartKeyObservable, new MessageKeyToModelStrategy(),
				null);

		context.bindValue(SWTObservables
				.observeSelection(timeStartOperatorCombo),
				timeBasedStartOperator,
				new StringRegelOperatorToModelStrategy(),
				new StringRegelOperatorToGuiStrategy());

		IObservableValue timeBasedStopCompareObservable = BeansObservables
				.observeValue(
						addOn,
						TimeBasedFilterConditionBean.PropertyNames.confirmCompValue
								.name());

		IObservableValue timeBasedStopKeyObservable = BeansObservables
				.observeValue(
						addOn,
						TimeBasedFilterConditionBean.PropertyNames.confirmKeyValue
								.name());

		IObservableValue timeBasedStopOperator = BeansObservables.observeValue(
				addOn,
				TimeBasedFilterConditionBean.PropertyNames.confirmOperator
						.name());

		IObservableValue timeBehaviorObservable = BeansObservables
				.observeValue(addOn,
						TimeBasedFilterConditionBean.PropertyNames.timeBehavior
								.name());
		IObservableValue rubrikTextObservable = BeansObservables.observeValue(
				this.beanClone,
				FilterbedingungBean.AbstractPropertyNames.rubrikName.name());

		// bind observables
		context.bindValue(SWTObservables.observeText(timeStopCompareText,
				SWT.Modify), timeBasedStopCompareObservable, null, null);

		context.bindValue(SWTObservables.observeSelection(timeStopKeyCombo),
				timeBasedStopKeyObservable, new MessageKeyToModelStrategy(),
				null);

		context.bindValue(SWTObservables.observeSelection(timeBehaviorCheck),
				timeBehaviorObservable, new UpdateValueStrategy() {

					@Override
					public Object convert(Object value) {
						Boolean status = (Boolean) value;
						return status ? TimeBasedType.TIMEBEHAVIOR_TIMEOUT_THEN_ALARM
								: TimeBasedType.TIMEBEHAVIOR_CONFIRMED_THEN_ALARM;
					}

				}, new UpdateValueStrategy() {

					@Override
					public Object convert(Object value) {
						return TimeBasedType.TIMEBEHAVIOR_TIMEOUT_THEN_ALARM == value ? true
								: false;
					}

				});

		context.bindValue(SWTObservables
				.observeSelection(timeStopOperatorCombo),
				timeBasedStopOperator,
				new StringRegelOperatorToModelStrategy(),
				new StringRegelOperatorToGuiStrategy());

		context.bindValue(SWTObservables.observeSelection(_rubrikComboEntry),
				rubrikTextObservable, null, null);
	}

	private void initStringArrayAddOnBeanDataBinding(DataBindingContext context) {
		StringArrayFilterConditionBean addOn = (StringArrayFilterConditionBean) specificBeans
				.get(SupportedFilterTypes.STRING_ARRAY_CONDITION);

		IObservableValue arrayKeyValueComboObservable = BeansObservables
				.observeValue(addOn,
						StringArrayFilterConditionBean.PropertyNames.keyValue
								.name());

		IObservableValue arrayOperatorComboObservable = BeansObservables
				.observeValue(addOn,
						StringArrayFilterConditionBean.PropertyNames.operator
								.name());

		IObservableList arrayCompareValueListObservable = BeansObservables
				.observeList(
						context.getValidationRealm(),
						addOn,
						StringArrayFilterConditionBean.PropertyNames.compareValues
								.name());

		// bind observables
		context.bindValue(
				SWTObservables.observeSelection(arrayMessageKeyCombo),
				arrayKeyValueComboObservable, null, null);

		context.bindValue(SWTObservables.observeSelection(arrayOperatorCombo),
				arrayOperatorComboObservable, null, null);

		context.bindList(SWTObservables.observeItems(arrayCompareValueList),
				arrayCompareValueListObservable, null, null);

	}

	private void initPVAddOnBeanDataBinding(DataBindingContext context) {
		IObservableValue pvChannelNameTextObservable = BeansObservables
				.observeValue(specificBeans
						.get(SupportedFilterTypes.PV_CONDITION),
						PVFilterConditionBean.PropertyNames.channelName.name());

		IObservableValue pvCompareValueTextObservable = BeansObservables
				.observeValue(specificBeans
						.get(SupportedFilterTypes.PV_CONDITION),
						PVFilterConditionBean.PropertyNames.compareValue.name());

		IObservableValue pvOperatorObservable = BeansObservables.observeValue(
				specificBeans.get(SupportedFilterTypes.PV_CONDITION),
				PVFilterConditionBean.PropertyNames.operator.name());

		IObservableValue pvTypeObservable = BeansObservables.observeValue(
				specificBeans.get(SupportedFilterTypes.PV_CONDITION),
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

	private void initJunctorAddOnBeanDataBinding(DataBindingContext context) {
		// IObservableValue firstConditionTextObservable = BeansObservables
		// .observeValue(specificBeans
		// .get(SupportedFilterTypes.JUNCTOR_CONDITION),
		// JunctorConditionBean.PropertyNames.firstCondition
		// .name());
		//
		// IObservableValue secondConditionTextObservable = BeansObservables
		// .observeValue(specificBeans
		// .get(SupportedFilterTypes.JUNCTOR_CONDITION),
		// JunctorConditionBean.PropertyNames.secondCondition
		// .name());

//		IObservableValue stringJunctorObservable = BeansObservables
//				.observeValue(specificBeans
//						.get(SupportedFilterTypes.JUNCTOR_CONDITION),
//						JunctorConditionBean.PropertyNames.junctor.name());

		// bind observables
//		context.bindValue(SWTObservables.observeSelection(junctorTypeCombo),
//				stringJunctorObservable, new UpdateValueStrategy() {
//					@Override
//					public Object convert(Object value) {
//						return JunctorConditionType.valueOf((String) value);
//					}
//				}, new UpdateValueStrategy() {
//					@Override
//					public Object convert(Object value) {
//						return ((JunctorConditionType) value).name();
//					}
//				});

		// context.bindValue(SWTObservables.observeText(junctorFirstFilterText,
		// SWT.Modify), firstConditionTextObservable,
		// new UpdateValueStrategy() {
		// @Override
		// public Object convert(Object value) {
		// return junctorFirstFilterText.getData();
		// }
		// }, null);
		//
		// context.bindValue(SWTObservables.observeText(junctorSecondFilterText,
		// SWT.Modify), secondConditionTextObservable,
		// new UpdateValueStrategy() {
		// @Override
		// public Object convert(Object value) {
		// return junctorSecondFilterText.getData();
		// }
		// }, null);

	}

	private void initStringAddOnBeanDataBinding(DataBindingContext context) {
		IObservableValue keyComboObservable = BeansObservables.observeValue(
				specificBeans.get(SupportedFilterTypes.STRING_CONDITION),
				StringFilterConditionBean.PropertyNames.keyValue.name());

		IObservableValue stringCompareValueTextObservable = BeansObservables
				.observeValue(specificBeans
						.get(SupportedFilterTypes.STRING_CONDITION),
						StringFilterConditionBean.PropertyNames.compValue
								.name());

		IObservableValue stringOperatorObservable = BeansObservables
				.observeValue(specificBeans
						.get(SupportedFilterTypes.STRING_CONDITION),
						StringFilterConditionBean.PropertyNames.operator.name());

		// bind observables
		context.bindValue(SWTObservables.observeSelection(stringOperatorCombo),
				stringOperatorObservable,
				new StringRegelOperatorToModelStrategy(),
				new StringRegelOperatorToGuiStrategy());

		context.bindValue(SWTObservables
				.observeSelection(stringCompareKeyCombo), keyComboObservable,
				new MessageKeyToModelStrategy(), null);

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
		DataBindingContext context = new DataBindingContext();

		IObservableValue nameTextObservable = BeansObservables.observeValue(
				this.beanClone, FilterbedingungBean.PropertyNames.name.name());

		IObservableValue descriptionTextObservable = BeansObservables
				.observeValue(this.beanClone,
						FilterbedingungBean.PropertyNames.description.name());

		IObservableValue rubrikTextObservable = BeansObservables.observeValue(
				this.beanClone,
				FilterbedingungBean.AbstractPropertyNames.rubrikName.name());

		// bind observables
		context.bindValue(SWTObservables
				.observeText(_nameTextEntry, SWT.Modify), nameTextObservable,
				null, null);

		context.bindValue(SWTObservables.observeText(_defaultMessageTextEntry,
				SWT.Modify), descriptionTextObservable, null, null);

		initStringAddOnBeanDataBinding(context);
		initJunctorAddOnBeanDataBinding(context);
		initPVAddOnBeanDataBinding(context);
		initStringArrayAddOnBeanDataBinding(context);
		// TODO init TimeBased DataBinding
		initTimeBasedAddOnBeanDataBinding(context);

		context.bindValue(SWTObservables.observeSelection(_rubrikComboEntry),
				rubrikTextObservable, null, null);

	}

	@Override
	public void setFocus() {
		_nameTextEntry.setFocus();
	}

}
