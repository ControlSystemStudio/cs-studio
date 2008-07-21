package org.csstudio.nams.configurator.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.beans.filters.FilterConditionAddOnBean;
import org.csstudio.nams.configurator.beans.filters.JunctorConditionBean;
import org.csstudio.nams.configurator.beans.filters.PVFilterConditionBean;
import org.csstudio.nams.configurator.beans.filters.StringArrayFilterConditionBean;
import org.csstudio.nams.configurator.beans.filters.StringFilterConditionBean;
import org.csstudio.nams.configurator.beans.filters.TimeBasedFilterConditionBean;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.TimeBasedType;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class FilterbedingungEditor extends AbstractEditor<FilterbedingungBean> {

	public static class FilterTypeBean extends
			AbstractConfigurationBean<FilterTypeBean> {
		private SupportedFilterTypes type;

		public SupportedFilterTypes getType() {
			return type;
		}

		public void setType(SupportedFilterTypes type) {
			SupportedFilterTypes oldValue = this.type;
			this.type = type;
			pcs.firePropertyChange("type", oldValue, type);
		}

		@Override
		protected void doUpdateState(FilterTypeBean bean) {
			// Kommt nicht vor...
		}

		public String getDisplayName() {
			return "(internal bean for storing selcted filter type in filter condition editor)";
		}

		public int getID() {
			return 0;
		}

		public void setID(int id) {
			// Ignored.
		}
	}

	private FilterTypeBean selectedFilterType = new FilterTypeBean();

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

	private static final String EDITOR_ID = "org.csstudio.nams.configurator.editor.FilterbedingungEditor";
	private StackLayout filterLayout;
	private Composite[] stackComposites;

	private Text stringCompareValueText;

	private Map<SupportedFilterTypes, AbstractConfigurationBean<?>> specificBeans;

	private Text pvChannelName;
	private Text pvCompareValue;
	private List arrayCompareValueList;
	private Text timeDelayText;
	private Button timeBehaviorCheck;
	private Text timeStartCompareText;
	private ListViewer arrayCompareValueListViewer;
	private ComboViewer _rubrikComboEntryViewer;
	private FormToolkit formToolkit;
	private ScrolledForm mainForm;
	private Text timeStopCompareText;
	private static IProcessVariableConnectionService pvConnectionService;

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
		_rubrikComboEntryViewer = this.createRubrikCombo(main, "Rubrik:", true,
				configurationBeanService
						.getRubrikNamesForType(RubrikTypeEnum.FILTER_COND));
		_rubrikComboEntry = _rubrikComboEntryViewer.getCombo();
		this.addSeparator(main);
		_defaultMessageTextEntry = this.createDescriptionTextEntry(main,
				"Description:");
		this.createTitledComboForEnumValues(main, "Filtertype: ",
				SupportedFilterTypes.values(), this.selectedFilterType, "type");

		initializeAddOnBeans();

		this.selectedFilterType
				.addPropertyChangeListener(new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						filterLayout.topControl = stackComposites[FilterbedingungEditor.this.selectedFilterType
								.getType().ordinal()];
						filterSpecificComposite.layout();

						beanClone
								.setFilterSpecificBean((FilterConditionAddOnBean) specificBeans
										.get(FilterbedingungEditor.this.selectedFilterType
												.getType()));
					}
				});

		filterSpecificComposite = new Composite(outermain, SWT.NONE);
		filterLayout = new StackLayout();
		filterSpecificComposite.setLayout(filterLayout);

		stackComposites = new Composite[5];

		// ConjunctionFilterComposite
		stackComposites[0] = new Composite(filterSpecificComposite, SWT.TOP);
		stackComposites[0].setLayout(new GridLayout(NUM_COLUMNS, false));
		new Label(stackComposites[0], SWT.NONE);
		Label label = new Label(stackComposites[0], SWT.LEFT | SWT.WRAP);
		label.setText("Dieses Filterbedingung  existiert nur aus Gründen der\n"
				+ "Abwärtskompatibilität, bitte realisieren Sie das\n"
				+ "gewünschte Verhalten über die neuen Funktionen des Filters");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		createTextEntry(stackComposites[0], "Filtercondition", false);
		createTextEntry(stackComposites[0], "Filtercondition", false);

		// StringFilterComposite
		stackComposites[1] = new Composite(filterSpecificComposite, SWT.TOP);
		stackComposites[1].setLayout(new GridLayout(NUM_COLUMNS, false));
		IConfigurationBean stringConfigurationBean = specificBeans
				.get(SupportedFilterTypes.STRING_CONDITION);

		createTitledComboForEnumValues(stackComposites[1], "CompareKey",
				MessageKeyEnum.values(), stringConfigurationBean,
				StringFilterConditionBean.PropertyNames.keyValue.name());

		createTitledComboForEnumValues(stackComposites[1], "Operator",
				StringRegelOperator.values(), stringConfigurationBean,
				StringFilterConditionBean.PropertyNames.operator.name());

		//			
		// createComboEntry(stackComposites[1],
		// "Operator", false, array2StringArray(StringRegelOperator
		// .values()));

		stringCompareValueText = createTextEntry(stackComposites[1],
				"CompareValue", true);
		// StringArrayFilterComposite
		stackComposites[2] = new Composite(filterSpecificComposite, SWT.TOP);
		stackComposites[2].setLayout(new GridLayout(NUM_COLUMNS, false));

		IConfigurationBean stringArrayConfigurationBean = specificBeans
				.get(SupportedFilterTypes.STRING_ARRAY_CONDITION);
		createTitledComboForEnumValues(stackComposites[2], "MessageKey",
				MessageKeyEnum.values(), stringArrayConfigurationBean,
				StringArrayFilterConditionBean.PropertyNames.keyValue.name());

		createTitledComboForEnumValues(stackComposites[2], "Operator",
				StringRegelOperator.values(), stringArrayConfigurationBean,
				StringArrayFilterConditionBean.PropertyNames.operator.name());

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
				StringArrayFilterConditionBean specificBean = (StringArrayFilterConditionBean) beanClone
						.getFilterSpecificBean();
				java.util.List<String> list = specificBean.getCompareValues();
				if (!list.contains(arrayNewCompareValueText.getText())) {
					list.add(arrayNewCompareValueText.getText());
					specificBean.setCompareValues(list);
					arrayNewCompareValueText.setText("");
				}
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
					String[] items = ((StringArrayFilterConditionBean) beanClone
							.getFilterSpecificBean()).getCompareValues()
							.toArray(new String[0]);
					ArrayList<String> itemList = new ArrayList<String>();
					for (int i = 0; i < items.length; i++) {
						if (arrayCompareValueList.getSelectionIndex() != i)
							itemList.add(items[i]);
					}
					((StringArrayFilterConditionBean) beanClone
							.getFilterSpecificBean())
							.setCompareValues(itemList);
				}
			}

			public void mouseUp(MouseEvent e) {
			}
		});
		// PVComposite
		stackComposites[3] = new Composite(filterSpecificComposite, SWT.TOP);
		stackComposites[3].setLayout(new GridLayout(NUM_COLUMNS, false));
		pvChannelName = createTextEntry(stackComposites[3], "channelName", true);

		final PVFilterConditionBean pvConfigurationBean = (PVFilterConditionBean) specificBeans
				.get(SupportedFilterTypes.PV_CONDITION);
		createTitledComboForEnumValues(stackComposites[3], "SuggestedType",
				SuggestedProcessVariableType.values(), pvConfigurationBean,
				PVFilterConditionBean.PropertyNames.suggestedType.name());

		createTitledComboForEnumValues(stackComposites[3], "Operator", Operator
				.values(), pvConfigurationBean,
				PVFilterConditionBean.PropertyNames.operator.name());

		pvCompareValue = createTextEntry(stackComposites[3], "Compare value",
				true);
		Button checkPVChannel = createButtonEntry(stackComposites[3],
				"PV Verbindung überprüfen", true);
		checkPVChannel.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
				String channelName = pvChannelName.getText();
				if (channelName != null && channelName.length() > 0) {
					try {
						SuggestedProcessVariableType suggestedType = pvConfigurationBean
								.getSuggestedType();
						if (SuggestedProcessVariableType.DOUBLE
								.equals(suggestedType)) {
							pvConnectionService
									.getValueAsDouble(ProcessVariableAdressFactory
											.getInstance()
											.createProcessVariableAdress(
													channelName));
						} else if (SuggestedProcessVariableType.LONG
								.equals(suggestedType)) {
							pvConnectionService
									.getValueAsLong(ProcessVariableAdressFactory
											.getInstance()
											.createProcessVariableAdress(
													channelName));
						} else if (SuggestedProcessVariableType.STRING
								.equals(suggestedType)) {
							pvConnectionService
									.getValueAsString(ProcessVariableAdressFactory
											.getInstance()
											.createProcessVariableAdress(
													channelName));
						}
					} catch (ConnectionException connectionException) {
						MessageDialog
						.openError(
								e.widget.getDisplay().getActiveShell(),
								"PV channel state for channel: " + channelName,
								"Connection to PV channel failed.\n\n" +
								"Reason:\n" + 
								EditorUIUtils.throwableAsMessageString(connectionException));
						connectionException.printStackTrace();
						return;
					}
					MessageDialog
							.openInformation(
									e.widget.getDisplay().getActiveShell(),
									"PV channel state for channel: " + channelName,
									"Connection to PV channel successfully established.\n\n" +
									"(This only indicates that your adress is correct and the PV is currently accessible\n" +
									"It is no quaranty for successfully access all over the time.\n" +
									"If the connection fails during message processing, this condition will match constantly)");
				}
			}

			public void mouseUp(MouseEvent e) {
			}
		});

		// TimeBasedComposite
		stackComposites[4] = new Composite(filterSpecificComposite, SWT.TOP);
		stackComposites[4].setLayout(new GridLayout(NUM_COLUMNS, false));
		IConfigurationBean timeBasedConfigurationBean = specificBeans
				.get(SupportedFilterTypes.TIMEBASED_CONDITION);

		timeDelayText = createTextEntry(stackComposites[4], "Wartezeit", true);
		timeBehaviorCheck = createCheckBoxEntry(stackComposites[4],
				"Alarm bei Timeout", true);
		addSeparator(stackComposites[4]);
		createTitledComboForEnumValues(stackComposites[4], "Start KeyValue",
				MessageKeyEnum.values(), timeBasedConfigurationBean,
				TimeBasedFilterConditionBean.PropertyNames.startKeyValue.name());

		createTitledComboForEnumValues(stackComposites[4], "Start Operator",
				StringRegelOperator.values(), timeBasedConfigurationBean,
				TimeBasedFilterConditionBean.PropertyNames.startOperator.name());

		timeStartCompareText = createTextEntry(stackComposites[4],
				"Start CompareValue", true);
		addSeparator(stackComposites[4]);
		createTitledComboForEnumValues(stackComposites[4], "Stop KeyValue",
				MessageKeyEnum.values(), timeBasedConfigurationBean,
				TimeBasedFilterConditionBean.PropertyNames.confirmKeyValue
						.name());

		createTitledComboForEnumValues(stackComposites[4], "Stop Operator",
				StringRegelOperator.values(), timeBasedConfigurationBean,
				TimeBasedFilterConditionBean.PropertyNames.confirmOperator
						.name());

		timeStopCompareText = createTextEntry(stackComposites[4],
				"Stop CompareValue", true);

		// LinkedList<String> types = new LinkedList<String>();
		// for (JunctorConditionType type : JunctorConditionType.values()) {
		// types.add(type.toString());
		// }
		// junctorTypeCombo.setItems(types.toArray(new String[types.size()]));

		FilterConditionAddOnBean filterSpecificBean = (FilterConditionAddOnBean) bean
				.getFilterSpecificBean();
		if (filterSpecificBean instanceof JunctorConditionBean) {
			this.selectedFilterType
					.setType(SupportedFilterTypes.JUNCTOR_CONDITION);
		} else if (filterSpecificBean instanceof StringFilterConditionBean) {
			this.selectedFilterType
					.setType(SupportedFilterTypes.STRING_CONDITION);
		} else if (filterSpecificBean instanceof StringArrayFilterConditionBean) {
			this.selectedFilterType
					.setType(SupportedFilterTypes.STRING_ARRAY_CONDITION);
		} else if (filterSpecificBean instanceof PVFilterConditionBean) {
			this.selectedFilterType.setType(SupportedFilterTypes.PV_CONDITION);
		} else if (filterSpecificBean instanceof TimeBasedFilterConditionBean) {
			this.selectedFilterType
					.setType(SupportedFilterTypes.TIMEBASED_CONDITION);
		} else
			throw new RuntimeException("Unsupported AddOnBean "
					+ filterSpecificBean.getClass());
		initDataBinding();
		initDND();
		// listener.handleEvent(null); // zur initialisierung
		// checkJunktionType();
	}

	// private void checkJunktionType() {
	// String typeString = (String) ((StructuredSelection)
	// junctorTypeComboViewer
	// .getSelection()).getFirstElement();
	// try {
	// JunctorConditionType type = JunctorConditionType
	// .valueOf(typeString);
	// junctorSecondFilterText
	// .setVisible(type != JunctorConditionType.NOT);
	// } catch (Exception e) {
	//
	// }
	// }

	private void initDND() {
		// DropTarget target1 = new DropTarget(junctorFirstFilterText,
		// DND.DROP_LINK);
		// DropTarget target2 = new DropTarget(junctorSecondFilterText,
		// DND.DROP_LINK);
		//
		// target1.setTransfer(new Transfer[] { LocalSelectionTransfer
		// .getTransfer() });
		// target2.setTransfer(new Transfer[] { LocalSelectionTransfer
		// .getTransfer() });
		//
		// target1
		// .addDropListener(new TextDropTarget(junctorFirstFilterText,
		// true));
		// target2.addDropListener(new TextDropTarget(junctorSecondFilterText,
		// false));
	}

	/*-class TextDropTarget extends DropTargetAdapter {
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
	}*/

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
		AbstractConfigurationBean<?> filterSpecificBean = (AbstractConfigurationBean<?>) beanClone
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

		// IObservableValue timeBasedStartKeyObservable = BeansObservables
		// .observeValue(
		// addOn,
		// TimeBasedFilterConditionBean.PropertyNames.startKeyValue
		// .name());

		// IObservableValue timeBasedStartOperator = BeansObservables
		// .observeValue(
		// addOn,
		// TimeBasedFilterConditionBean.PropertyNames.startOperator
		// .name());
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

		// context.bindValue(SWTObservables.observeSelection(timeStartKeyCombo),
		// timeBasedStartKeyObservable, new MessageKeyToModelStrategy(),
		// null);
		//
		// context.bindValue(SWTObservables
		// .observeSelection(timeStartOperatorCombo),
		// timeBasedStartOperator,
		// new StringRegelOperatorToModelStrategy(),
		// new StringRegelOperatorToGuiStrategy());

		IObservableValue timeBasedStopCompareObservable = BeansObservables
				.observeValue(
						addOn,
						TimeBasedFilterConditionBean.PropertyNames.confirmCompValue
								.name());

		// IObservableValue timeBasedStopKeyObservable = BeansObservables
		// .observeValue(
		// addOn,
		// TimeBasedFilterConditionBean.PropertyNames.confirmKeyValue
		// .name());
		//
		// IObservableValue timeBasedStopOperator =
		// BeansObservables.observeValue(
		// addOn,
		// TimeBasedFilterConditionBean.PropertyNames.confirmOperator
		// .name());

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

		// context.bindValue(SWTObservables.observeSelection(timeStopKeyCombo),
		// timeBasedStopKeyObservable, new MessageKeyToModelStrategy(),
		// null);

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
		//
		// context.bindValue(SWTObservables
		// .observeSelection(timeStopOperatorCombo),
		// timeBasedStopOperator,
		// new StringRegelOperatorToModelStrategy(),
		// new StringRegelOperatorToGuiStrategy());

		context.bindValue(SWTObservables.observeSelection(_rubrikComboEntry),
				rubrikTextObservable, null, null);
	}

	private void initStringArrayAddOnBeanDataBinding(DataBindingContext context) {
		StringArrayFilterConditionBean addOn = (StringArrayFilterConditionBean) specificBeans
				.get(SupportedFilterTypes.STRING_ARRAY_CONDITION);

		// IObservableValue arrayKeyValueComboObservable = BeansObservables
		// .observeValue(addOn,
		// StringArrayFilterConditionBean.PropertyNames.keyValue
		// .name());
		//
		// IObservableValue arrayOperatorComboObservable = BeansObservables
		// .observeValue(addOn,
		// StringArrayFilterConditionBean.PropertyNames.operator
		// .name());

		IObservableList arrayCompareValueListObservable = BeansObservables
				.observeList(
						context.getValidationRealm(),
						addOn,
						StringArrayFilterConditionBean.PropertyNames.compareValues
								.name());

		// bind observables
		// context.bindValue(
		// SWTObservables.observeSelection(arrayMessageKeyCombo),
		// arrayKeyValueComboObservable, null, null);
		//
		// context.bindValue(SWTObservables.observeSelection(arrayOperatorCombo),
		// arrayOperatorComboObservable, null, null);

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

		// IObservableValue pvOperatorObservable =
		// BeansObservables.observeValue(
		// specificBeans.get(SupportedFilterTypes.PV_CONDITION),
		// PVFilterConditionBean.PropertyNames.operator.name());

		// IObservableValue pvTypeObservable = BeansObservables.observeValue(
		// specificBeans.get(SupportedFilterTypes.PV_CONDITION),
		// PVFilterConditionBean.PropertyNames.suggestedType.name());

		// bind observables
		// context.bindValue(SWTObservables.observeSelection(pvOperator),
		// pvOperatorObservable, null, null);

		context.bindValue(
				SWTObservables.observeText(pvChannelName, SWT.Modify),
				pvChannelNameTextObservable, null, null);

		context.bindValue(SWTObservables
				.observeText(pvCompareValue, SWT.Modify),
				pvCompareValueTextObservable, null, null);

		// context.bindValue(SWTObservables.observeSelection(pvSuggestedType),
		// pvTypeObservable, null, null);
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

		// IObservableValue stringJunctorObservable = BeansObservables
		// .observeValue(specificBeans
		// .get(SupportedFilterTypes.JUNCTOR_CONDITION),
		// JunctorConditionBean.PropertyNames.junctor.name());

		// bind observables
		// context.bindValue(SWTObservables.observeSelection(junctorTypeCombo),
		// stringJunctorObservable, new UpdateValueStrategy() {
		// @Override
		// public Object convert(Object value) {
		// return JunctorConditionType.valueOf((String) value);
		// }
		// }, new UpdateValueStrategy() {
		// @Override
		// public Object convert(Object value) {
		// return ((JunctorConditionType) value).name();
		// }
		// });

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
		// IObservableValue keyComboObservable = BeansObservables.observeValue(
		// specificBeans.get(SupportedFilterTypes.STRING_CONDITION),
		// StringFilterConditionBean.PropertyNames.keyValue.name());

		IObservableValue stringCompareValueTextObservable = BeansObservables
				.observeValue(specificBeans
						.get(SupportedFilterTypes.STRING_CONDITION),
						StringFilterConditionBean.PropertyNames.compValue
								.name());

		// IObservableValue stringOperatorObservable = BeansObservables
		// .observeValue(specificBeans
		// .get(SupportedFilterTypes.STRING_CONDITION),
		// StringFilterConditionBean.PropertyNames.operator.name());

		// bind observables
		// context.bindValue(SWTObservables.observeSelection(stringOperatorCombo),
		// stringOperatorObservable,
		// new StringRegelOperatorToModelStrategy(),
		// new StringRegelOperatorToGuiStrategy());

		// context.bindValue(SWTObservables
		// .observeSelection(stringCompareKeyCombo), keyComboObservable,
		// new MessageKeyToModelStrategy(), null);

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
		initTimeBasedAddOnBeanDataBinding(context);

		context.bindValue(SWTObservables.observeSelection(_rubrikComboEntry),
				rubrikTextObservable, null, null);

	}

	@Override
	public void setFocus() {
		_nameTextEntry.setFocus();
	}

	public static void staticInject(
			IProcessVariableConnectionService pvConnectionService) {
		FilterbedingungEditor.pvConnectionService = pvConnectionService;
	}

}
